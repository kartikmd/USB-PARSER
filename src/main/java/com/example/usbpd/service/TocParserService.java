package com.example.usbpd.service;

import com.example.usbpd.model.TocEntry;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TocParserService {

    // 1) "2.1.2  Title ............ 53"
    private static final Pattern TOC_PATTERN_DOTS = Pattern.compile(
            "^\\s*(\\d+(?:\\.\\d+)*)\\s+(.+?)\\s*(?:\\.{2,}|\\s+)\\s*(\\d{1,4})\\s*$"
    );

    // 2) "2.1 Title    53" or with multiple spaces/tabs
    private static final Pattern TOC_PATTERN_TABS = Pattern.compile(
            "^\\s*(\\d+(?:\\.\\d+)*)\\s+(.+?)\\s+(\\d{1,4})\\s*$"
    );

    // Section ID only (e.g. "2.1.3")
    private static final Pattern SECTION_ID = Pattern.compile("^(\\d+(?:\\.\\d+)*)$");

    public List<TocEntry> parseFrontMatterToC(InputStream pdfStream, String docTitle, int frontPages) throws IOException {
        List<TocEntry> out = new ArrayList<>();
        try (PDDocument doc = PDDocument.load(pdfStream)) {
            int pages = Math.min(frontPages, doc.getNumberOfPages());
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(pages);
            String text = stripper.getText(doc);

            String[] lines = text.split("\\R");
            for (String raw : lines) {
                String line = raw.strip();
                if (line.isEmpty()) continue;

                // Skip headers
                String lower = line.toLowerCase();
                if (lower.contains("table of contents") || lower.matches(".*chapter\\s+\\d+.*")) {
                    continue;
                }

                TocEntry entry = matchLine(line, docTitle);
                if (entry != null) {
                    out.add(entry);
                }
            }
        }
        return out;
    }

    private TocEntry matchLine(String line, String docTitle) {
        Matcher m = TOC_PATTERN_DOTS.matcher(line);
        if (m.matches()) {
            return buildEntry(docTitle, m.group(1), m.group(2), m.group(3));
        }
        m = TOC_PATTERN_TABS.matcher(line);
        if (m.matches()) {
            return buildEntry(docTitle, m.group(1), m.group(2), m.group(3));
        }

        // Fallback: last token = page number
        String[] tokens = line.split("\\s+");
        if (tokens.length >= 3) {
            String last = tokens[tokens.length - 1];
            if (last.matches("\\d{1,4}")) {
                StringBuilder titleBuilder = new StringBuilder();
                String sectionId = null;
                for (int i = 0; i < tokens.length - 1; i++) {
                    if (sectionId == null && SECTION_ID.matcher(tokens[i]).matches()) {
                        sectionId = tokens[i];
                        continue;
                    }
                    if (titleBuilder.length() > 0) titleBuilder.append(' ');
                    titleBuilder.append(tokens[i]);
                }
                if (sectionId != null) {
                    return buildEntry(docTitle, sectionId, titleBuilder.toString(), last);
                }
            }
        }
        return null;
    }

    private TocEntry buildEntry(String docTitle, String sectionId, String title, String pageStr) {
        Integer page = null;
        try { page = Integer.parseInt(pageStr); } catch (Exception ignored) {}
        int level = 1 + (int) sectionId.chars().filter(ch -> ch == '.').count();
        String parentId = null;
        int idx = sectionId.lastIndexOf('.');
        if (idx > 0) parentId = sectionId.substring(0, idx);
        return new TocEntry(docTitle, sectionId, title.trim(), page, level, parentId);
    }
}
