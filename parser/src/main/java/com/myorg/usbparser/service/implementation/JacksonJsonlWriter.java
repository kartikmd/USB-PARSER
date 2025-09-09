package com.myorg.usbparser.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.myorg.usbparser.service.JsonlWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
public class JacksonJsonlWriter<T> implements JsonlWriter<T> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final ObjectWriter objectWriter = MAPPER.writer();

    @Override
    public void write(File outputFile, List<T> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            if (data != null) {
                for (T obj : data) {
                    String jsonLine = objectWriter.writeValueAsString(obj);
                    writer.write(jsonLine);
                    writer.newLine();
                }
                log.info("✅ JSONL written: {} entries -> {}", data.size(), outputFile.getAbsolutePath());
            } else {
                log.warn("⚠️ No data provided, skipping write for file: {}", outputFile.getAbsolutePath());
            }
        }
    }
}
