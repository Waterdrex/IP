package com.tcet.pms;

import com.sun.net.httpserver.HttpServer;
import com.tcet.pms.controller.ApiHandler;
import com.tcet.pms.controller.StaticFileHandler;
import com.tcet.pms.service.ProjectManagerService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Main Application Bootstrap for TCET Project Management System.
 * Initializes the HTTP server, mounts REST controllers and static web assets,
 * and provisions a multi-threaded execution pool.
 */
public class Main {
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port provided in args, falling back to default " + DEFAULT_PORT);
            }
        } else if (System.getenv("PORT") != null && !System.getenv("PORT").trim().isEmpty()) {
            // Read cloud-assigned dynamic PORT (e.g. Render, Railway, Heroku)
            try {
                port = Integer.parseInt(System.getenv("PORT").trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid PORT env variable, falling back to default " + DEFAULT_PORT);
            }
        }

        try {
            // 1. Initialize Service Facade and Data Store
            ProjectManagerService service = new ProjectManagerService();

            // 2. Initialize Java HTTP Server on localhost
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // 3. Register Context Handlers
            server.createContext("/api", new ApiHandler(service));
            server.createContext("/", new StaticFileHandler("public"));

            // 4. Configure Multi-threaded Request Dispatcher
            server.setExecutor(Executors.newFixedThreadPool(10));

            // 5. Start Server
            server.start();

            printBanner(port);

        } catch (IOException e) {
            System.err.println("Failed to start server on port " + port + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printBanner(int port) {
        System.out.println("==================================================================");
        System.out.println("  TCET Mumbai - Industrial Program Mini-Project");
        System.out.println("  Full Stack Development using Java - Project Management System");
        System.out.println("==================================================================");
        System.out.println("  [+] Server Status : ONLINE");
        System.out.println("  [+] Web Interface : http://localhost:" + port);
        System.out.println("  [+] REST API Base : http://localhost:" + port + "/api");
        System.out.println("  [+] Health Check  : http://localhost:" + port + "/api/health");
        System.out.println("  [+] Stats Check   : http://localhost:" + port + "/api/stats");
        System.out.println("==================================================================");
        System.out.println("  Press Ctrl+C to terminate the server.\n");
    }
}
