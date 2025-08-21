package com.example.usbpd.model;

public class TocEntry {
    private String docTitle;
    private String sectionId;
    private String title;
    private Integer page;
    private Integer level;
    private String parentId;
    private String fullPath;

    public TocEntry() {}

    public TocEntry(String docTitle, String sectionId, String title, Integer page, Integer level, String parentId) {
        this.docTitle = docTitle;
        this.sectionId = sectionId;
        this.title = title;
        this.page = page;
        this.level = level;
        this.parentId = parentId;
        this.fullPath = (sectionId != null && !sectionId.isEmpty())
                ? sectionId + " " + title
                : title;
    }

    public String getDocTitle() { return docTitle; }
    public void setDocTitle(String docTitle) { this.docTitle = docTitle; }

    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getFullPath() { return fullPath; }
    public void setFullPath(String fullPath) { this.fullPath = fullPath; }
}
