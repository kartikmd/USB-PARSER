package com.usbpd5.parser.service;


import java.io.File;
import java.util.List;

public interface PdfParser<T> {
    List<T> parse(File pdfFile) throws Exception;
}

