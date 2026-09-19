import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import controller.*;
import util.DBConnection;

import java.awt.Desktop;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   SMART ALUMNI NETWORK SYSTEM - SATHYABAMA (SIST CSE AI)");
        System.out.println("   Java SE HttpServer Backend + MySQL 8.0 Relational Store");
        System.out.println("================================================================================");

        // 1. Initialize & Verify Database
        try {
            DBConnection.checkAndInitializeDatabase();
        } catch (Exception e) {
            System.err.println("[Main] Notice: Database check exception: " + e.getMessage());
        }

        // 2. Start HTTP Server
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.setExecutor(Executors.newFixedThreadPool(16));

            // Register API Handlers
            AuthController authController = new AuthController();
            AlumniController alumniController = new AlumniController();
            JobController jobController = new JobController();
            MentorshipController mentorshipController = new MentorshipController();
            EndowmentController endowmentController = new EndowmentController();
            NotificationController notificationController = new NotificationController();
            StatsController statsController = new StatsController();

            server.createContext("/api/auth", authController);
            server.createContext("/api/alumni", alumniController);
            server.createContext("/api/departments", alumniController);
            server.createContext("/api/jobs", jobController);
            server.createContext("/api/mentorship", mentorshipController);
            server.createContext("/api/endowments", endowmentController);
            server.createContext("/api/pledges", endowmentController);
            server.createContext("/api/notifications", notificationController);
            server.createContext("/api/stats", statsController);

            // Register Static Frontend File Handler
            server.createContext("/", new StaticFileHandler());

            server.start();

            System.out.println("\n[Main] Server successfully started!");
            System.out.println("[Main] URL: http://localhost:" + PORT);
            System.out.println("[Main] Static assets served from: frontend/");
            System.out.println("\n--- DEMO TEST CREDENTIALS ---");
            System.out.println("  1. Admin:   admin@gmail.com     /  admin123");
            System.out.println("  2. Alumni:  shiela@gmail.com    /  alumni123  (TCS Senior AI Engineer)");
            System.out.println("  3. Student: kiruthi@gmail.com   /  student123 (3rd Year CSE AI)");
            System.out.println("================================================================================\n");

            // Open browser automatically
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI("http://localhost:" + PORT));
                }
            } catch (Exception ignored) {}

        } catch (IOException e) {
            System.err.println("[Main] FATAL: Could not bind to port " + PORT + ": " + e.getMessage());
        }
    }

    private static class StaticFileHandler implements HttpHandler {

        private Path findFrontendDir() {
            Path p1 = Paths.get("frontend");
            if (Files.exists(p1)) return p1;
            Path p2 = Paths.get("../frontend");
            if (Files.exists(p2)) return p2;
            return Paths.get(".");
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/")) {
                path = "/index.html";
            }

            // Security check against directory traversal
            if (path.contains("..")) {
                exchange.sendResponseHeaders(403, -1);
                return;
            }

            Path frontendDir = findFrontendDir();
            Path filePath = frontendDir.resolve(path.substring(1));

            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                // Fallback to index.html for Single Page Application navigation
                filePath = frontendDir.resolve("index.html");
            }

            if (!Files.exists(filePath)) {
                String notFound = "<h1>404 Not Found</h1><p>Frontend file not found: " + path + "</p>";
                byte[] bytes = notFound.getBytes("UTF-8");
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
                return;
            }

            String mimeType = getMimeType(filePath.toString());
            exchange.getResponseHeaders().set("Content-Type", mimeType);
            exchange.getResponseHeaders().set("Cache-Control", "no-cache");

            byte[] bytes = Files.readAllBytes(filePath);
            exchange.sendResponseHeaders(200, bytes.length);
            if (!"HEAD".equalsIgnoreCase(method)) {
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
        }

        private String getMimeType(String path) {
            String lower = path.toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
            if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
            if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
            if (lower.endsWith(".gif")) return "image/gif";
            if (lower.endsWith(".svg")) return "image/svg+xml";
            if (lower.endsWith(".ico")) return "image/x-icon";
            return "application/octet-stream";
        }
    }
}
