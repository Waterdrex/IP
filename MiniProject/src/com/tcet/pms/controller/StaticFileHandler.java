package com.tcet.pms.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * StaticFileHandler serves frontend HTML, CSS, JavaScript, and asset files from the public folder.
 */
public class StaticFileHandler implements HttpHandler {
    private final String baseDir;
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("html", "text/html; charset=UTF-8");
        MIME_TYPES.put("htm", "text/html; charset=UTF-8");
        MIME_TYPES.put("css", "text/css; charset=UTF-8");
        MIME_TYPES.put("js", "application/javascript; charset=UTF-8");
        MIME_TYPES.put("json", "application/json; charset=UTF-8");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("ico", "image/x-icon");
    }

    public StaticFileHandler(String baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String reqPath = exchange.getRequestURI().getPath();
        if (reqPath.equals("/") || reqPath.isEmpty()) {
            reqPath = "/index.html";
        }

        // Prevent path traversal
        if (reqPath.contains("..")) {
            send404(exchange);
            return;
        }

        Path filePath = Paths.get(baseDir, reqPath.startsWith("/") ? reqPath.substring(1) : reqPath);
        File file = filePath.toFile();

        if (!file.exists() || file.isDirectory()) {
            // Also check root if someone requests index.html and it's in public
            Path altPath = Paths.get("public", reqPath.startsWith("/") ? reqPath.substring(1) : reqPath);
            file = altPath.toFile();
            if (!file.exists() || file.isDirectory()) {
                send404(exchange);
                return;
            }
        }

        String extension = getFileExtension(file.getName());
        String contentType = MIME_TYPES.getOrDefault(extension, "application/octet-stream");

        byte[] bytes = Files.readAllBytes(file.toPath());
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void send404(HttpExchange exchange) throws IOException {
        String msg = "404 Not Found";
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(404, msg.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(msg.getBytes());
        }
    }

    private String getFileExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot == -1) return "";
        return filename.substring(dot + 1).toLowerCase();
    }
}
