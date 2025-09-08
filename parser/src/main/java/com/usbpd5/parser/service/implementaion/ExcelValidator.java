package com.usbpd5.parser.service.implementaion;


import com.usbpd5.parser.model.Section;
import com.usbpd5.parser.model.ValidationResult;
import com.usbpd5.parser.service.Validator;
import com.usbpd5.parser.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ExcelValidator implements Validator {

    private final File outputFile;

    public ExcelValidator(File outputFile) {
        this.outputFile = outputFile;
    }

    @Override
    public ValidationResult validate(List<Section> tocSections, List<Section> parsedSections) {
        try {
            // ✅ Normalize section IDs/titles before comparing
            Set<String> tocIds = tocSections.stream()
                    .map(s -> normalize(s.getTitle()))
                    .collect(Collectors.toSet());

            Set<String> parsedIds = parsedSections.stream()
                    .map(s -> normalize(s.getTitle()))
                    .collect(Collectors.toSet());

            List<String> missing = tocIds.stream()
                    .filter(id -> !parsedIds.contains(id))
                    .collect(Collectors.toList());

            List<String> extra = parsedIds.stream()
                    .filter(id -> !tocIds.contains(id))
                    .collect(Collectors.toList());

            ValidationResult result = ValidationResult.builder()
                    .tocSectionCount(tocIds.size())
                    .parsedSectionCount(parsedIds.size())
                    .missingSections(missing)
                    .extraSections(extra)
                    .tableCounts(new HashMap<>()) // extend later if needed
                    .build();

            writeExcel(result);
            return result;

        } catch (Exception e) {
            throw new ValidationException("Validation failed", e);
        }
    }

    /**
     * ✅ Normalizes section titles for comparison
     * Removes dot leaders, page numbers, trims spaces.
     */
    private String normalize(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("\\.{2,}", " ")   // remove dot fillers
                .replaceAll("\\s+\\d+$", "") // remove trailing page numbers
                .trim()
                .toLowerCase();              // case-insensitive compare
    }

    private void writeExcel(ValidationResult result) throws Exception {
        Workbook workbook = new XSSFWorkbook();

        // Sheet 1: Summary
        Sheet summarySheet = workbook.createSheet("Summary");
        Row header = summarySheet.createRow(0);
        header.createCell(0).setCellValue("Metric");
        header.createCell(1).setCellValue("Value");

        summarySheet.createRow(1).createCell(0).setCellValue("TOC Sections");
        summarySheet.getRow(1).createCell(1).setCellValue(result.getTocSectionCount());

        summarySheet.createRow(2).createCell(0).setCellValue("Parsed Sections");
        summarySheet.getRow(2).createCell(1).setCellValue(result.getParsedSectionCount());

        summarySheet.createRow(3).createCell(0).setCellValue("Missing Sections");
        summarySheet.getRow(3).createCell(1).setCellValue(result.getMissingSections().size());

        summarySheet.createRow(4).createCell(0).setCellValue("Extra Sections");
        summarySheet.getRow(4).createCell(1).setCellValue(result.getExtraSections().size());

        // Sheet 2: Missing Sections
        Sheet missingSheet = workbook.createSheet("Missing Sections");
        for (int i = 0; i < result.getMissingSections().size(); i++) {
            missingSheet.createRow(i).createCell(0).setCellValue(result.getMissingSections().get(i));
        }

        // Sheet 3: Extra Sections
        Sheet extraSheet = workbook.createSheet("Extra Sections");
        for (int i = 0; i < result.getExtraSections().size(); i++) {
            extraSheet.createRow(i).createCell(0).setCellValue(result.getExtraSections().get(i));
        }

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            workbook.write(fos);
        }
        workbook.close();
        log.info("Validation report written to {}", outputFile.getAbsolutePath());
    }
}
