package com.myorg.usbparser.controller;


import com.myorg.usbparser.model.Section;
import com.myorg.usbparser.service.JsonlWriter;
import com.myorg.usbparser.service.SectionExtractor;
import com.myorg.usbparser.service.TocExtractor;
import com.myorg.usbparser.service.Validator;
import com.myorg.usbparser.service.implementation.ExcelValidator;
import com.myorg.usbparser.service.implementation.JacksonJsonlWriter;
import com.myorg.usbparser.service.implementation.PdfBoxSectionExtractor;
import com.myorg.usbparser.service.implementation.PdfBoxTocExtractor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/pdf")
public class PdfParserController {
    private final String docTitle = "USB Power Delivery Specification Rev 3.2";

    private final File outputDir = new File(System.getProperty("user.dir"), "output");

    @PostMapping("/parse")
    public ResponseEntity<String> parsePdf(@RequestParam("file") MultipartFile file) throws Exception {
        // Ensure output directory exists
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Save uploaded PDF inside output/
        File pdfFile = new File(outputDir, file.getOriginalFilename());
        file.transferTo(pdfFile);

        // Step 1: Extract ToC
        TocExtractor tocExtractor = new PdfBoxTocExtractor(docTitle);
        List<Section> tocSections = tocExtractor.parse(pdfFile);

        // Step 2: Extract all sections
        SectionExtractor sectionExtractor = new PdfBoxSectionExtractor(docTitle);
        List<Section> allSections = sectionExtractor.parse(pdfFile);

        // Step 3: Write outputs
        JsonlWriter<Section> writer = new JacksonJsonlWriter<>();
        writer.write(new File(outputDir, "usb_pd_toc.jsonl"), tocSections);
        writer.write(new File(outputDir, "usb_pd_sections.jsonl"), allSections);

        // Step 4: Validation
        File validationFile = new File(outputDir, "validation_report.xlsx");
        Validator validator = new ExcelValidator(validationFile);
        validator.validate(tocSections, allSections);

        return ResponseEntity.ok("✅ Parsing complete. Results saved in: " + outputDir.getAbsolutePath());
    }

    @GetMapping("/results/toc")
    public ResponseEntity<File> getTocJsonl() {
        File tocFile = new File(outputDir, "usb_pd_toc.jsonl");
        return tocFile.exists()
                ? ResponseEntity.ok(tocFile)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/results/sections")
    public ResponseEntity<File> getSectionsJsonl() {
        File secFile = new File(outputDir, "usb_pd_sections.jsonl");
        return secFile.exists()
                ? ResponseEntity.ok(secFile)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/results/validation")
    public ResponseEntity<File> getValidationReport() {
        File valFile = new File(outputDir, "validation_report.xlsx");
        return valFile.exists()
                ? ResponseEntity.ok(valFile)
                : ResponseEntity.notFound().build();
    }
}
