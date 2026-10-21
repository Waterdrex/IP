package com.tcet.pms.repository;

import com.tcet.pms.util.SimpleJson;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FileDataStore handles disk persistence for entities using JSON files.
 * Demonstrates Java NIO File I/O, Exception Handling, and Thread Synchronization.
 */
public class FileDataStore {
    private static final String DATA_DIR = "data";

    static {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static synchronized List<Map<String, Object>> load(String filename) {
        Path path = Paths.get(DATA_DIR, filename);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim();
            if (content.isEmpty() || content.equals("[]")) {
                return new ArrayList<>();
            }
            List<Object> rawList = SimpleJson.parseArray(content);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Object item : rawList) {
                if (item instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) item;
                    result.add(map);
                }
            }
            return result;
        } catch (IOException e) {
            System.err.println("Error reading " + filename + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static synchronized void save(String filename, List<Map<String, Object>> data) {
        Path path = Paths.get(DATA_DIR, filename);
        try {
            String json = SimpleJson.stringify(data);
            Files.write(path, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Error saving to " + filename + ": " + e.getMessage());
        }
    }
}
