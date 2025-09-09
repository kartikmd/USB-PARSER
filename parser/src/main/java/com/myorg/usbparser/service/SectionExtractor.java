package com.myorg.usbparser.service;



import com.myorg.usbparser.model.Section;
import com.myorg.usbparser.service.PdfParser;

import java.io.File;
import java.util.List;

public interface SectionExtractor extends PdfParser<Section> {
    List<Section> parse(File pdfFile) throws Exception;
}

