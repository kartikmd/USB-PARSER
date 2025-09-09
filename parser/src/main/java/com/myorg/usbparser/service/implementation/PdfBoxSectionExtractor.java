package com.myorg.usbparser.service.implementation;

import com.myorg.usbparser.model.Section;
import com.myorg.usbparser.service.SectionExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PdfBoxSectionExtractor implements SectionExtractor {

    // Regex for section headings: e.g., "1", "1.2", "2.3.4" + title
    private static final Pattern HEADING_PATTERN = Pattern.compile(
            "^\\s*(\\d+(?:\\.\\d+)*)(?:\\s+)(.+)$"
    );

    private final String docTitle;

    public PdfBoxSectionExtractor(String docTitle) {
        this.docTitle = docTitle;
    }

    @Override
    public List<Section> parse(File pdfFile) throws IOException {
        List<Section> sections = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            int totalPages = document.getNumberOfPages();

            Section currentSection = null;
            StringBuilder currentContent = new StringBuilder();

            for (int page = 1; page <= totalPages; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);

                String text = stripper.getText(document);
                String[] lines = text.split("\\r?\\n");

                for (String line : lines) {
                    String trimmed = line.trim();
                    Matcher matcher = HEADING_PATTERN.matcher(trimmed);

                    if (matcher.matches()) {
                        // ✅ Save previous section
                        if (currentSection != null) {
                            String content = currentContent.toString().trim();
                            if (content.isEmpty()) {
                                content = "[No extractable text — section may contain only figures/tables]";
                            }
                            currentSection.setContent(content);
                            sections.add(currentSection);
                            log.debug("✔ Saved section {} with {} chars of content",
                                    currentSection.getSectionId(),
                                    content.length());
                        }

                        // ✅ Start new section
                        String sectionId = matcher.group(1).trim();
                        String title = matcher.group(2).trim();
                        int level = sectionId.split("\\.").length;
                        String parentId = sectionId.contains(".")
                                ? sectionId.substring(0, sectionId.lastIndexOf('.'))
                                : null;

                        log.debug("📌 New heading found on page {}: [{}] {}", page, sectionId, title);

                        currentSection = Section.builder()
                                .docTitle(docTitle)
                                .sectionId(sectionId)
                                .title(title)
                                .page(page)
                                .level(level)
                                .parentId(parentId)
                                .fullPath(sectionId + " " + title)
                                .tags(new ArrayList<>())
                                .content("") // placeholder, will be filled
                                .build();

                        currentContent = new StringBuilder();
                    } else if (currentSection != null && !trimmed.isEmpty()) {
                        // ✅ Add body text
                        currentContent.append(trimmed).append(" ");
                    }
                }
            }

            // ✅ Save the last section
            if (currentSection != null) {
                String content = currentContent.toString().trim();
                if (content.isEmpty()) {
                    content = "[No extractable text — section may contain only figures/tables]";
                }
                currentSection.setContent(content);
                sections.add(currentSection);
                log.debug("✔ Saved final section {} with {} chars of content",
                        currentSection.getSectionId(),
                        content.length());
            }
        }

        log.info("✅ Extracted {} sections (with content or placeholders) from {}",
                sections.size(), pdfFile.getName());
        return sections;
    }
}
