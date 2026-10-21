package com.tcet.pms.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.tcet.pms.model.Project;
import com.tcet.pms.model.Task;
import com.tcet.pms.model.User;
import com.tcet.pms.service.ProjectManagerService;
import com.tcet.pms.util.SimpleJson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * ApiHandler implements HttpHandler to serve RESTful API endpoints.
 * Handles HTTP GET, POST, PUT, DELETE, and OPTIONS for CORS.
 */
public class ApiHandler implements HttpHandler {
    private final ProjectManagerService service;

    public ApiHandler(ProjectManagerService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();

        // Enable CORS
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            if (path.equals("/api/health")) {
                handleHealth(exchange);
            } else if (path.equals("/api/stats")) {
                handleStats(exchange);
            } else if (path.startsWith("/api/projects")) {
                handleProjects(exchange, method, path);
            } else if (path.startsWith("/api/tasks")) {
                handleTasks(exchange, method, path, uri.getQuery());
            } else if (path.startsWith("/api/users")) {
                handleUsers(exchange, method, path);
            } else {
                sendJsonResponse(exchange, 404, errorMap("Endpoint not found: " + path));
            }
        } catch (IllegalArgumentException e) {
            sendJsonResponse(exchange, 400, errorMap(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(exchange, 500, errorMap("Internal server error: " + e.getMessage()));
        }
    }

    private void handleHealth(HttpExchange exchange) throws IOException {
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "UP");
        resp.put("server", "Java Built-in HttpServer");
        resp.put("timestamp", System.currentTimeMillis());
        sendJsonResponse(exchange, 200, resp);
    }

    private void handleStats(HttpExchange exchange) throws IOException {
        sendJsonResponse(exchange, 200, service.getDashboardStats());
    }

    private void handleProjects(HttpExchange exchange, String method, String path) throws IOException {
        String[] parts = path.split("/");
        // path: /api/projects or /api/projects/{id}
        String id = (parts.length >= 4) ? parts[3] : null;

        if ("GET".equals(method)) {
            if (id == null) {
                sendJsonResponse(exchange, 200, service.getAllProjects());
            } else {
                Optional<Project> p = service.getProjectById(id);
                if (p.isPresent()) {
                    sendJsonResponse(exchange, 200, p.get().toMap());
                } else {
                    sendJsonResponse(exchange, 404, errorMap("Project not found with id " + id));
                }
            }
        } else if ("POST".equals(method)) {
            String body = readRequestBody(exchange);
            Map<String, Object> payload = SimpleJson.parseObject(body);
            Project created = service.createProject(payload);
            sendJsonResponse(exchange, 201, created.toMap());
        } else if ("DELETE".equals(method)) {
            if (id == null) {
                sendJsonResponse(exchange, 400, errorMap("Project ID required for deletion"));
                return;
            }
            boolean deleted = service.deleteProject(id);
            if (deleted) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("message", "Project deleted successfully");
                sendJsonResponse(exchange, 200, resp);
            } else {
                sendJsonResponse(exchange, 404, errorMap("Project not found"));
            }
        } else {
            sendJsonResponse(exchange, 405, errorMap("Method not allowed"));
        }
    }

    private void handleTasks(HttpExchange exchange, String method, String path, String query) throws IOException {
        String[] parts = path.split("/");
        String id = (parts.length >= 4) ? parts[3] : null;

        if ("GET".equals(method)) {
            if (id == null) {
                String projectId = getQueryParam(query, "projectId");
                sendJsonResponse(exchange, 200, service.getAllTasks(projectId));
            } else {
                Optional<Task> t = service.getTaskById(id);
                if (t.isPresent()) {
                    sendJsonResponse(exchange, 200, t.get().toMap());
                } else {
                    sendJsonResponse(exchange, 404, errorMap("Task not found with id " + id));
                }
            }
        } else if ("POST".equals(method)) {
            String body = readRequestBody(exchange);
            Map<String, Object> payload = SimpleJson.parseObject(body);
            Task created = service.createTask(payload);
            sendJsonResponse(exchange, 201, created.toMap());
        } else if ("PUT".equals(method)) {
            if (id == null) {
                sendJsonResponse(exchange, 400, errorMap("Task ID required for update"));
                return;
            }
            String body = readRequestBody(exchange);
            Map<String, Object> payload = SimpleJson.parseObject(body);
            Optional<Task> updated = service.updateTask(id, payload);
            if (updated.isPresent()) {
                sendJsonResponse(exchange, 200, updated.get().toMap());
            } else {
                sendJsonResponse(exchange, 404, errorMap("Task not found"));
            }
        } else if ("DELETE".equals(method)) {
            if (id == null) {
                sendJsonResponse(exchange, 400, errorMap("Task ID required for deletion"));
                return;
            }
            boolean deleted = service.deleteTask(id);
            if (deleted) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("message", "Task deleted successfully");
                sendJsonResponse(exchange, 200, resp);
            } else {
                sendJsonResponse(exchange, 404, errorMap("Task not found"));
            }
        } else {
            sendJsonResponse(exchange, 405, errorMap("Method not allowed"));
        }
    }

    private void handleUsers(HttpExchange exchange, String method, String path) throws IOException {
        String[] parts = path.split("/");
        String id = (parts.length >= 4) ? parts[3] : null;

        if ("GET".equals(method)) {
            sendJsonResponse(exchange, 200, service.getAllUsers());
        } else if ("POST".equals(method)) {
            String body = readRequestBody(exchange);
            Map<String, Object> payload = SimpleJson.parseObject(body);
            User user = service.createUser(payload);
            sendJsonResponse(exchange, 201, user.toMap());
        } else if ("DELETE".equals(method)) {
            if (id == null) {
                sendJsonResponse(exchange, 400, errorMap("User ID required"));
                return;
            }
            boolean deleted = service.deleteUser(id);
            if (deleted) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                sendJsonResponse(exchange, 200, resp);
            } else {
                sendJsonResponse(exchange, 404, errorMap("User not found"));
            }
        } else {
            sendJsonResponse(exchange, 405, errorMap("Method not allowed"));
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        byte[] bytes = SimpleJson.stringify(data).getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private Map<String, Object> errorMap(String message) {
        Map<String, Object> err = new HashMap<>();
        err.put("error", true);
        err.put("message", message);
        return err;
    }

    private String getQueryParam(String query, String key) {
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals(key)) {
                return pair[1];
            }
        }
        return null;
    }
}
