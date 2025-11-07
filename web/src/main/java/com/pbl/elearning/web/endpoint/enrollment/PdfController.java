// package com.pbl.elearning.web.endpoint.enrollment;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.*;
// import org.springframework.web.bind.annotation.*;

// import com.pbl.elearning.enrollment.payload.response.CertificateResponse;
// import com.pbl.elearning.enrollment.services.CertificateService;
// import com.pbl.elearning.enrollment.services.Impl.PdfService;

// import java.io.IOException;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.UUID;

// @RestController
// @RequestMapping("/pdf")
// public class PdfController {

//     @Autowired
//     private PdfService pdfService;

//     @Autowired
//     private CertificateService certificateService;
//     // New endpoint: generate PDF from template with username and return downloadable PDF
//     @GetMapping(value = "/download-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
//     public ResponseEntity<byte[]> downloadPdf(
//             @RequestParam(name = "enrollmentId", required = false) UUID enrollmentId,
//             @RequestParam(name = "certificateId", required = false) UUID certificateId,
//             @RequestParam(name = "template", defaultValue = "simple-certificate-template.html") String templateName
//     ) {
//         try {
//             // 1) Fetch certificate/enrollment data
//             CertificateResponse cert = null;
//             if (certificateId != null) {
//                 cert = certificateService.getCertificateById(certificateId);
//             } else if (enrollmentId != null) {
//                 cert = certificateService.getCertificateByEnrollmentId(enrollmentId);
//                 // If not exists yet, you may want to generate
//                 if (cert == null) {
//                     cert = certificateService.generateCertificate(enrollmentId);
//                 }
//             } else {
//                 return ResponseEntity.badRequest().build();
//             }

//             // 2) Load template
//             String template = pdfService.loadTemplate(templateName);

//             // 3) Build variables map from CertificateResponse
//             Map<String, Object> vars = toTemplateVariables(cert);

//             // 4) Render template by replacing placeholders like {{key}}
//             String filled = renderTemplateWithMap(template, vars);

//             // 5) Generate PDF
//             byte[] pdfBytes = pdfService.generatePdfFromHtml(filled);

//             HttpHeaders headers = new HttpHeaders();
//             headers.setContentType(MediaType.APPLICATION_PDF);
//             headers.setContentDisposition(ContentDisposition.builder("attachment")
//                     .filename("certificate.pdf")
//                     .build());
//             return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
//         } catch (IOException e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }

//     private Map<String, Object> toTemplateVariables(CertificateResponse cert) {
//         Map<String, Object> m = new HashMap<>();
//         if (cert == null) return m;
//         m.put("certificateId", cert.getId() != null ? cert.getId().toString() : "");
//         m.put("enrollmentId", cert.getEnrollmentId() != null ? cert.getEnrollmentId().toString() : "");
//         m.put("userId", cert.getUserId() != null ? cert.getUserId().toString() : "");
//         m.put("courseId", cert.getCourseId() != null ? cert.getCourseId().toString() : "");
//         m.put("certificateNumber", cert.getCertificateNumber() != null ? cert.getCertificateNumber() : "");
//         m.put("issuedDate", cert.getIssuedDate() != null ? cert.getIssuedDate().toString() : "");
//         m.put("expiryDate", cert.getExpiryDate() != null ? cert.getExpiryDate().toString() : "");
//         m.put("templateUrl", cert.getTemplateUrl() != null ? cert.getTemplateUrl() : "");
//         m.put("certificateUrl", cert.getCertificateUrl() != null ? cert.getCertificateUrl() : "");
//         m.put("isVerified", cert.getIsVerified() != null ? cert.getIsVerified().toString() : "");
//         m.put("createdAt", cert.getCreatedAt() != null ? cert.getCreatedAt().toString() : "");

//         // user/course specific
//         m.put("courseName", cert.getCourseName() != null ? cert.getCourseName() : "");
//         m.put("courseCode", cert.getCourseCode() != null ? cert.getCourseCode() : "");
//         m.put("userName", cert.getUserName() != null ? cert.getUserName() : "");
//         m.put("userEmail", cert.getUserEmail() != null ? cert.getUserEmail() : "");
//         m.put("completionScore", cert.getCompletionScore() != null ? cert.getCompletionScore().toString() : "");
//         m.put("courseCompletionDate", cert.getCourseCompletionDate() != null ? cert.getCourseCompletionDate().toString() : "");

//         return m;
//     }

//     // naive but effective replacement: replace all occurrences of {{key}} with escaped value
//     private String renderTemplateWithMap(String template, Map<String, Object> vars) {
//         String out = template;
//         for (Map.Entry<String, Object> e : vars.entrySet()) {
//             String key = "{{ " + e.getKey() + " }}";
//             String value = e.getValue() == null ? "" : escapeHtml(e.getValue().toString());
//             out = out.replace(key, value);
//         }
//         return out;
//     }

//     // Simple HTML-escaping to avoid injection when replacing the template
//     private String escapeHtml(String s) {
//         if (s == null) return "";
//         return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
//                 .replace("\"", "&quot;").replace("'", "&#39;");
//     }

//     // DTO bên trong hoặc tách riêng file
//     public static class HtmlRequest {
//         private String html;
//         // getter & setter
//         public String getHtml() { return html; }
//         public void setHtml(String html) { this.html = html; }
//     }
// }

