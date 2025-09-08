package com.usbpd5.parser.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    private String docTitle;
    private String sectionId;
    private String title;
    private int page;
    private int level;
    private String parentId;
    private String fullPath;
    private List<String> tags;
}

