package com.usbpd5.parser.model;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    private int tocSectionCount;
    private int parsedSectionCount;
    private List<String> missingSections;
    private List<String> extraSections;
    private Map<String, Integer> tableCounts;
}
