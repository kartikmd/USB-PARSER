package com.myorg.usbparser.model;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    private int tocSectionCount;
    private int parsedSectionCount;

    @Builder.Default
    private List<String> missingSections = List.of();

    @Builder.Default
    private List<String> extraSections = List.of();

    @Builder.Default
    private Map<String, Integer> tableCounts = Map.of();

    private int missingCount;
    private int extraCount;
}
