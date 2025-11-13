package com.pbl.elearning.enrollment.services.Impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import com.lowagie.text.pdf.BaseFont;
import com.pbl.elearning.enrollment.models.Certificate;
import com.pbl.elearning.enrollment.payload.response.CertificateResponse;
import com.pbl.elearning.enrollment.repository.CertificateRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import org.xhtmlrenderer.pdf.ITextRenderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PdfService {

    private static final Logger log = LoggerFactory.getLogger(PdfService.class);
    private volatile String lastRegisteredFont = null;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private AmazonS3 getS3Client; // Hoặc wrapper service S3 của bạn

    @Value("${aws.s3.bucket}")
    private String bucket;

    public String getLastRegisteredFont() {
        return lastRegisteredFont;
    }

    /* ======================== METHOD CHỦ CHỐT ======================== */

    /**
     * Gộp: render template → generate PDF → upload S3 → update certificate record
     */
    public String generateAndUploadCertificatePdf(CertificateResponse certResp, String templateName) throws IOException {
        // 1. Load template
        String template = loadTemplate(templateName);

        // 2. Build template variables từ CertificateResponse
        Map<String, Object> vars = toTemplateVariables(certResp);

        // 3. Render HTML
        String html = renderTemplateWithMap(template, vars);

        // 4. Generate PDF bytes
        byte[] pdfBytes = generatePdfFromHtml(html);
        Files.write(Paths.get("test.pdf"), pdfBytes);
        System.out.println("PDF đã lưu: test.pdf");
        // 5. Upload PDF → get S3 URL
        String s3Url = uploadPdfToS3(pdfBytes, certResp.getCertificateNumber() + ".pdf");

        // 6. Cập nhật certificate record nếu muốn (lấy ID từ CertificateResponse)
        if (certResp.getId() != null) {
            Certificate certEntity = certificateRepository.findById(certResp.getId()).orElse(null);
            if (certEntity != null) {
                certEntity.setCertificateUrl(s3Url);
                certificateRepository.save(certEntity);
            }
        }

        return s3Url;
    }


    /* ======================== PDF GENERATION ======================== */

    public byte[] generatePdfFromHtml(String htmlContent) throws IOException {
        System.out.println("Generating PDF from HTML content...");
        // 1. Convert HTML → XHTML using Jsoup
        Document document = Jsoup.parse(htmlContent);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.outputSettings().charset(StandardCharsets.UTF_8);

        if (document.selectFirst("meta[charset]") == null &&
            document.selectFirst("meta[http-equiv=Content-Type]") == null) {
            document.head().prepend("<meta charset=\"UTF-8\" />");
        }

        String xhtml = document.html();

        // 2. Generate PDF using Flying Saucer + OpenPDF
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();

            // Register fonts BEFORE setting document
            registerFonts(renderer);

            renderer.setDocumentFromString(xhtml);
            renderer.layout();
            renderer.createPDF(baos);
            return baos.toByteArray();
        }
    }

    /* ======================== TEMPLATE HANDLING ======================== */

    public String loadTemplate(String name) throws IOException {
        System.out.println("Loading template: " + name);
        // 1. Try file system (dev)
        Path p = Paths.get("src/main/resources/templates", name);
        if (Files.exists(p)) return Files.readString(p, StandardCharsets.UTF_8);

        // 2. Fallback classpath (prod)
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("templates/" + name)) {
            if (is == null) throw new IOException("Template not found: " + name);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public String renderTemplateWithMap(String template, Map<String, Object> vars) {
        System.out.println("Rendering template with variables: " + vars.keySet());
        String out = template;
        for (Map.Entry<String, Object> e : vars.entrySet()) {
            String key = "{{ " + e.getKey() + " }}";
            String value = e.getValue() == null ? "" : escapeHtml(e.getValue().toString());
            out = out.replace(key, value);
        }
        return out;
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    public Map<String, Object> toTemplateVariables(CertificateResponse cert) {
        Map<String, Object> m = new HashMap<>();
        if (cert == null) return m;

        m.put("certificateId", cert.getId() != null ? cert.getId().toString() : "");
        m.put("enrollmentId", cert.getEnrollmentId() != null ? cert.getEnrollmentId().toString() : "");
        m.put("userId", cert.getUserId() != null ? cert.getUserId().toString() : "");
        m.put("courseId", cert.getCourseId() != null ? cert.getCourseId().toString() : "");
        m.put("certificateNumber", cert.getCertificateNumber() != null ? cert.getCertificateNumber() : "");
        m.put("issuedDate", cert.getIssuedDate() != null ? cert.getIssuedDate().toString() : "");
        m.put("expiryDate", cert.getExpiryDate() != null ? cert.getExpiryDate().toString() : "");
        m.put("templateUrl", cert.getTemplateUrl() != null ? cert.getTemplateUrl() : "");
        m.put("certificateUrl", cert.getCertificateUrl() != null ? cert.getCertificateUrl() : "");
        m.put("isVerified", cert.getIsVerified() != null ? cert.getIsVerified().toString() : "");
        m.put("createdAt", cert.getCreatedAt() != null ? cert.getCreatedAt().toString() : "");

        // user/course specific
        m.put("courseName", cert.getCourseName() != null ? cert.getCourseName() : "");
        m.put("courseCode", cert.getCourseCode() != null ? cert.getCourseCode() : "");
        m.put("userName", cert.getUserName() != null ? cert.getUserName() : "");
        m.put("userEmail", cert.getUserEmail() != null ? cert.getUserEmail() : "");
        m.put("completionScore", cert.getCompletionScore() != null ? cert.getCompletionScore().toString() : "");
        m.put("courseCompletionDate", cert.getCourseCompletionDate() != null ? cert.getCourseCompletionDate().toString() : "");

        return m;
    }

    /* ======================== FONT HANDLING ======================== */

    private void registerFonts(ITextRenderer renderer) {
        String[] candidateFonts = new String[] {
            "C:/Windows/Fonts/arial.ttf",
            "C:/Windows/Fonts/arialuni.ttf",
            "C:/Windows/Fonts/times.ttf",
            "C:/Windows/Fonts/DejaVuSans.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
            "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf",
            "/System/Library/Fonts/Supplemental/Arial Unicode.ttf",
            "/Library/Fonts/Arial Unicode.ttf"
        };

        boolean registered = false;
        for (String fontPath : candidateFonts) {
            try {
                Path fp = Paths.get(fontPath);
                if (Files.exists(fp)) {
                    String used = fp.toAbsolutePath().toString();
                    renderer.getFontResolver().addFont(used, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    lastRegisteredFont = used;
                    log.info("✓ Registered system font: {}", used);
                    registered = true;
                    break;
                }
            } catch (Exception ex) {
                log.debug("Could not register font {}: {}", fontPath, ex.getMessage());
            }
        }

        if (!registered) registered = registerBundledFont(renderer, "fonts/NotoSans-Regular.ttf");
        if (!registered) registered = registerBundledFont(renderer, "fonts/DejaVuSans.ttf");

        if (!registered) log.warn("⚠ No Unicode font registered! Vietnamese characters may not display correctly.");
    }

    private boolean registerBundledFont(ITextRenderer renderer, String resourcePath) {
        try (InputStream fis = this.getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (fis != null) {
                Path tmp = Files.createTempFile("pdf-font-", ".ttf");
                Files.copy(fis, tmp, StandardCopyOption.REPLACE_EXISTING);

                String used = tmp.toAbsolutePath().toString();
                renderer.getFontResolver().addFont(used, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                lastRegisteredFont = used;
                log.info("✓ Registered bundled font: {}", resourcePath);

                tmp.toFile().deleteOnExit();
                return true;
            }
        } catch (Exception ex) {
            log.debug("Could not register bundled font {}: {}", resourcePath, ex.getMessage());
        }
        return false;
    }

    /* ======================== S3 UPLOAD ======================== */

    public String uploadPdfToS3(byte[] pdfBytes, String fileName) {
        System.out.println("Uploading PDF to S3: " + fileName);
        System.setProperty("com.amazonaws.sdk.disableCertChecking", "true");
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/pdf");
        metadata.setContentLength(pdfBytes.length);

        ByteArrayInputStream bais = new ByteArrayInputStream(pdfBytes);
        getS3Client.putObject(bucket, fileName, bais, metadata);
        System.out.println("Uploaded PDF to S3: " + fileName);
        return getS3Client.getUrl(bucket, fileName).toString();
    }
}
