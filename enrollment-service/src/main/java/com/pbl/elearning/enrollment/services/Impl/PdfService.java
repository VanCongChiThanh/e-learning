package com.pbl.elearning.enrollment.services.Impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PdfService {

    private static final Logger log = LoggerFactory.getLogger(PdfService.class);
    private volatile String lastRegisteredFont = null;

    public String getLastRegisteredFont() {
        return lastRegisteredFont;
    }

    public byte[] generatePdfFromHtml(String htmlContent) throws IOException {
        // 1. Convert HTML string → XHTML using jsoup
        Document document = Jsoup.parse(htmlContent);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.outputSettings().charset(StandardCharsets.UTF_8);

        // Inject meta charset if not exists
        if (document.selectFirst("meta[charset]") == null && 
            document.selectFirst("meta[http-equiv=Content-Type]") == null) {
            document.head().prepend("<meta charset=\"UTF-8\" />");
        }

        String xhtml = document.html();

        // 2. Generate PDF with Flying Saucer + OpenPDF
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();

            // Register fonts BEFORE setting document
            registerFonts(renderer);

            // Set document and render
            renderer.setDocumentFromString(xhtml);
            renderer.layout();
            renderer.createPDF(baos);
            return baos.toByteArray();
        }
    }

    private void registerFonts(ITextRenderer renderer) {
        // Candidate fonts with better Vietnamese support
        String[] candidateFonts = new String[] {
                // Windows fonts
                "C:/Windows/Fonts/arial.ttf",
                "C:/Windows/Fonts/arialuni.ttf",
                "C:/Windows/Fonts/times.ttf",
                "C:/Windows/Fonts/DejaVuSans.ttf",
                // Linux fonts
                "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
                "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf",
                // Mac fonts
                "/System/Library/Fonts/Supplemental/Arial Unicode.ttf",
                "/Library/Fonts/Arial Unicode.ttf"
        };

        boolean registered = false;
        
        // Try system fonts first
        for (String fontPath : candidateFonts) {
            try {
                Path fp = Paths.get(fontPath);
                if (Files.exists(fp)) {
                    String used = fp.toAbsolutePath().toString();
                    // BaseFont.IDENTITY_H supports Unicode/UTF-8
                    renderer.getFontResolver().addFont(
                        used, 
                        BaseFont.IDENTITY_H, 
                        BaseFont.EMBEDDED
                    );
                    lastRegisteredFont = used;
                    log.info("✓ Registered system font: {}", used);
                    registered = true;
                    break;
                }
            } catch (Exception ex) {
                log.debug("Could not register font {}: {}", fontPath, ex.getMessage());
            }
        }

        // If no system font found, use bundled font from classpath
        if (!registered) {
            registered = registerBundledFont(renderer, "fonts/NotoSans-Regular.ttf");
        }

        // Fallback: try alternative bundled fonts
        if (!registered) {
            registered = registerBundledFont(renderer, "fonts/DejaVuSans.ttf");
        }

        if (!registered) {
            log.warn("⚠ No Unicode font registered! Vietnamese characters may not display correctly.");
        }
    }

    private boolean registerBundledFont(ITextRenderer renderer, String resourcePath) {
        try (InputStream fis = this.getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (fis != null) {
                // Create temp file for font
                Path tmp = Files.createTempFile("pdf-font-", ".ttf");
                Files.copy(fis, tmp, StandardCopyOption.REPLACE_EXISTING);
                
                String used = tmp.toAbsolutePath().toString();
                renderer.getFontResolver().addFont(
                    used, 
                    BaseFont.IDENTITY_H, 
                    BaseFont.EMBEDDED
                );
                
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

    public String loadTemplate(String name) throws IOException {
        // Try file system first (for development)
        Path p = Paths.get("src/main/resources/templates", name);
        if (Files.exists(p)) {
            return Files.readString(p, StandardCharsets.UTF_8);
        }
        
        // Fallback to classpath (for production)
        try (InputStream is = this.getClass().getClassLoader()
                .getResourceAsStream("templates/" + name)) {
            if (is == null) {
                throw new IOException("Template not found: " + name);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}