package com.myorg.usbparser.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // 👈 This hides null fields in JSON
public class Section {
    private String docTitle;
    private String sectionId;
    private String title;
    private int page;
    private int level;
    private String parentId;
    private String fullPath;
    private List<String> tags;
    private String content; // Will not appear in JSON if null
}
