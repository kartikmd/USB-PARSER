package com.example.usbpd.util;

import com.example.usbpd.model.TocEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonlWriter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void writeJsonl(List<TocEntry> entries, OutputStream out) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            for (TocEntry e : entries) {
                ObjectNode node = MAPPER.createObjectNode();
                node.put("doc_title", e.getDocTitle());
                if (e.getSectionId() != null) node.put("section_id", e.getSectionId());
                node.put("title", e.getTitle());
                if (e.getPage() != null) node.put("page", e.getPage());
                if (e.getLevel() != null) node.put("level", e.getLevel());
                if (e.getParentId() != null) node.put("parent_id", e.getParentId());
                node.put("full_path", e.getFullPath());
                bw.write(MAPPER.writeValueAsString(node));
                bw.newLine();
            }
            bw.flush();
        }
    }
}
