package com.usbpd5.parser.service.implementaion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.usbpd5.parser.service.JsonlWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

@Slf4j
public class JacksonJsonlWriter<T> implements JsonlWriter<T> {

    private final ObjectWriter objectWriter;

    public JacksonJsonlWriter() {
        ObjectMapper mapper = new ObjectMapper();
        this.objectWriter = mapper.writer();
    }

    @Override
    public void write(File outputFile, List<T> data) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (T obj : data) {
                String jsonLine = objectWriter.writeValueAsString(obj);
                writer.write(jsonLine);
                writer.newLine();
            }
        }
        log.info("JSONL written: {} entries -> {}", data.size(), outputFile.getAbsolutePath());
    }
}

