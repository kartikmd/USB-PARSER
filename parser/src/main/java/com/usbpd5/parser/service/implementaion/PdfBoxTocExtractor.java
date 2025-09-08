package com.usbpd5.parser.service.implementaion;

import com.usbpd5.parser.model.Section;
import com.usbpd5.parser.service.TocExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PdfBoxTocExtractor implements TocExtractor {

    // Regex to capture lines like: "2.1.2 Power Delivery Contract Negotiation ........ 53"
    private static final Pattern TOC_PATTERN = Pattern.compile(
            "^(\\d+(?:\\.\\d+)*)(?:\\s+)(.+?)\\s+\\.{3,}\\s*(\\d+)$"
    );

    private final String docTitle;

    public PdfBoxTocExtractor(String docTitle) {
        this.docTitle = docTitle;
    }

    @Override
    public List<Section> parse(File pdfFile) throws Exception {
        List<Section> sections = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();

            // Extract only first 20 pages (ToC is usually there)
            stripper.setStartPage(1);
            stripper.setEndPage(Math.min(20, document.getNumberOfPages()));

            String text = stripper.getText(document);
            String[] lines = text.split("\\r?\\n");

            for (String line : lines) {
                Matcher matcher = TOC_PATTERN.matcher(line.trim());
                if (matcher.matches()) {
                    String sectionId = matcher.group(1);
                    String title = matcher.group(2).trim();
                    int page = Integer.parseInt(matcher.group(3));

                    int level = sectionId.split("\\.").length;
                    String parentId = (sectionId.contains("."))
                            ? sectionId.substring(0, sectionId.lastIndexOf('.'))
                            : null;

                    Section section = Section.builder()
                            .docTitle(docTitle)
                            .sectionId(sectionId)
                            .title(title)
                            .page(page)
                            .level(level)
                            .parentId(parentId)
                            .fullPath(sectionId + " " + title)
                            .tags(new ArrayList<>())
                            .build();

                    sections.add(section);
                }
            }
        }

        log.info("Extracted {} ToC entries", sections.size());
        return sections;
    }
}

