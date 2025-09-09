package com.usbpd5.parser.service.implementation;



import com.usbpd5.parser.model.Section;
import com.usbpd5.parser.service.SectionExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PdfBoxSectionExtractor implements SectionExtractor {

    // Regex for headings like "2", "2.1", "2.1.2"
    private static final Pattern HEADING_PATTERN = Pattern.compile(
            "^(\\d+(?:\\.\\d+)*)(?:\\s+)(.+)$"
    );

    private final String docTitle;

    public PdfBoxSectionExtractor(String docTitle) {
        this.docTitle = docTitle;
    }

    @Override
    public List<Section> parse(File pdfFile) throws Exception {
        List<Section> sections = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(document.getNumberOfPages());

            String text = stripper.getText(document);
            String[] lines = text.split("\\r?\\n");

            for (String line : lines) {
                Matcher matcher = HEADING_PATTERN.matcher(line.trim());
                if (matcher.matches()) {
                    String sectionId = matcher.group(1);
                    String title = matcher.group(2).trim();

                    int level = sectionId.split("\\.").length;
                    String parentId = (sectionId.contains("."))
                            ? sectionId.substring(0, sectionId.lastIndexOf('.'))
                            : null;

                    Section section = Section.builder()
                            .docTitle(docTitle)
                            .sectionId(sectionId)
                            .title(title)
                            .page(-1) // optional: can extend to capture page numbers
                            .level(level)
                            .parentId(parentId)
                            .fullPath(sectionId + " " + title)
                            .tags(new ArrayList<>())
                            .build();

                    sections.add(section);
                }
            }
        }

        log.info("Extracted {} full sections", sections.size());
        return sections;
    }
}

