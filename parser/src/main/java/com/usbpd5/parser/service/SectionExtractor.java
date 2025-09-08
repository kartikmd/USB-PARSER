package com.usbpd5.parser.service;



import com.usbpd5.parser.model.Section;

import java.io.File;
import java.util.List;

public interface SectionExtractor extends PdfParser<Section> {
    List<Section> parse(File pdfFile) throws Exception;
}

