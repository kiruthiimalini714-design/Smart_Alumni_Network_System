package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.AuthService;
import util.JsonUtil;

import java.io.IOException;
import java.util.Map;

public class AuthController implements HttpHandler {

    private final AuthService authService = new AuthService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            HttpHelper.sendJsonResponse(exchange, 200, Map.of("status", "ok"));
            return;
        }

        try {
            if ("POST".equalsIgnoreCase(method) && path.endsWith("/login")) {
                handleLogin(exchange);
            } else if ("POST".equalsIgnoreCase(method) && path.endsWith("/register")) {
                handleRegister(exchange);
            } else if ("GET".equalsIgnoreCase(method) && path.contains("/verify")) {
                handleVerify(exchange);
            } else if ("GET".equalsIgnoreCase(method) && path.endsWith("/me")) {
                handleMe(exchange);
            } else {
                HttpHelper.sendErrorResponse(exchange, 404, "Endpoint not found: " + path);
            }
        } catch (Exception e) {
            HttpHelper.sendErrorResponse(exchange, 500, "Server Error: " + e.getMessage());
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        Map<String, Object> body = HttpHelper.readJsonBody(exchange);
        String email = JsonUtil.getString(body, "email", "");
        String password = JsonUtil.getString(body, "password", "");
        String role = JsonUtil.getString(body, "role", "");

        Map<String, Object> res = authService.login(email, password, role);
        boolean success = Boolean.TRUE.equals(res.get("success"));
        HttpHelper.sendJsonResponse(exchange, success ? 200 : 401, res);
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        Map<String, Object> body = HttpHelper.readJsonBody(exchange);
        String role = JsonUtil.getString(body, "role", "Student");
        String email = JsonUtil.getString(body, "email", "");
        String password = JsonUtil.getString(body, "password", "");
        String name = JsonUtil.getString(body, "name", "");
        String registerNo = JsonUtil.getString(body, "registerNo", "");
        int deptId = JsonUtil.getInt(body, "deptId", 1);
        String phone = JsonUtil.getString(body, "phone", "");

        Map<String, Object> res;
        if ("Alumni".equalsIgnoreCase(role)) {
            int batchYear = JsonUtil.getInt(body, "batchYear", 2024);
            String company = JsonUtil.getString(body, "company", "");
            String designation = JsonUtil.getString(body, "designation", "");
            String location = JsonUtil.getString(body, "location", "");
            String linkedinUrl = JsonUtil.getString(body, "linkedinUrl", "");
            boolean isMentorAvailable = JsonUtil.getBoolean(body, "isMentorAvailable", true);

            res = authService.registerAlumni(email, password, name, registerNo, deptId, batchYear,
                    company, designation, location, phone, linkedinUrl, isMentorAvailable);
        } else {
            int currentYear = JsonUtil.getInt(body, "currentYear", 3);
            String bio = JsonUtil.getString(body, "bio", "");

            res = authService.registerStudent(email, password, name, registerNo, deptId, currentYear, phone, bio);
        }

        boolean success = Boolean.TRUE.equals(res.get("success"));
        HttpHelper.sendJsonResponse(exchange, success ? 201 : 400, res);
    }

    private void handleVerify(HttpExchange exchange) throws IOException {
        Map<String, String> query = HttpHelper.parseQueryParams(exchange);
        String regNo = query.getOrDefault("regNo", "");
        int deptId = 0;
        int batchYear = 0;
        try {
            deptId = Integer.parseInt(query.getOrDefault("deptId", "0"));
            batchYear = Integer.parseInt(query.getOrDefault("batchYear", "0"));
        } catch (Exception ignored) {}

        Map<String, Object> res = authService.verifyAlumniEligibility(regNo, deptId, batchYear);
        HttpHelper.sendJsonResponse(exchange, 200, res);
    }

    private void handleMe(HttpExchange exchange) throws IOException {
        int userId = HttpHelper.getUserIdHeader(exchange);
        String role = HttpHelper.getUserRoleHeader(exchange);
        if (userId <= 0) {
            HttpHelper.sendErrorResponse(exchange, 401, "No authenticated session.");
            return;
        }
        HttpHelper.sendJsonResponse(exchange, 200, Map.of(
                "success", true,
                "userId", userId,
                "role", role
        ));
    }
}
