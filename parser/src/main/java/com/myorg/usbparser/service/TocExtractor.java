package com.myorg.usbparser.service;

import com.myorg.usbparser.model.Section;

import java.io.File;
import java.util.List;

public interface TocExtractor extends PdfParser<Section> {
    List<Section> parse(File pdfFile) throws Exception;
}

