package com.example.usbpd.controller;

import com.example.usbpd.model.TocEntry;
import com.example.usbpd.service.TocParserService;
import com.example.usbpd.util.JsonlWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UploadController {

    private final TocParserService tocParserService;

    public UploadController(TocParserService tocParserService) {
        this.tocParserService = tocParserService;
    }

    @PostMapping(value = "/parse-toc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> parseToc(@RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "docTitle", required = false) String docTitle,
                                           @RequestParam(value = "frontPages", required = false, defaultValue = "15") int frontPages) throws IOException {
        if (docTitle == null || docTitle.isBlank()) {
            docTitle = file.getOriginalFilename() != null ? file.getOriginalFilename() : "Document";
        }
        List<TocEntry> entries = tocParserService.parseFrontMatterToC(file.getInputStream(), docTitle, frontPages);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonlWriter.writeJsonl(entries, baos);
        byte[] data = baos.toByteArray();

        String fname = "usb_pd_spec.jsonl";
        String contentDisposition = "attachment; filename*=UTF-8''" + URLEncoder.encode(fname, StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(new MediaType("application","jsonl"))
                .body(data);
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
