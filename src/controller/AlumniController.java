package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Alumni;
import model.Department;
import service.AlumniService;
import util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AlumniController implements HttpHandler {

    private final AlumniService alumniService = new AlumniService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            HttpHelper.sendJsonResponse(exchange, 200, Map.of("status", "ok"));
            return;
        }

        try {
            if (path.startsWith("/api/departments")) {
                List<Department> depts = alumniService.getAllDepartments();
                HttpHelper.sendJsonResponse(exchange, 200, depts);
                return;
            }

            if ("GET".equalsIgnoreCase(method)) {
                // Check if requesting specific ID /api/alumni/5
                String[] parts = path.split("/");
                if (parts.length == 4 && !parts[3].isEmpty() && parts[3].matches("\\d+")) {
                    int alumniId = Integer.parseInt(parts[3]);
                    Alumni a = alumniService.getAlumniById(alumniId);
                    if (a != null) {
                        HttpHelper.sendJsonResponse(exchange, 200, a);
                    } else {
                        HttpHelper.sendErrorResponse(exchange, 404, "Alumnus not found.");
                    }
                    return;
                }

                // Query search
                Map<String, String> query = HttpHelper.parseQueryParams(exchange);
                String search = query.get("search");
                String company = query.get("company");
                Integer deptId = null;
                Integer batchYear = null;
                Boolean mentorOnly = null;

                try {
                    if (query.containsKey("deptId") && !query.get("deptId").isEmpty()) {
                        deptId = Integer.parseInt(query.get("deptId"));
                    }
                    if (query.containsKey("batchYear") && !query.get("batchYear").isEmpty()) {
                        batchYear = Integer.parseInt(query.get("batchYear"));
                    }
                    if (query.containsKey("mentorOnly")) {
                        mentorOnly = Boolean.parseBoolean(query.get("mentorOnly"));
                    }
                } catch (Exception ignored) {}

                List<Alumni> list = alumniService.searchAlumni(search, deptId, batchYear, company, mentorOnly);
                HttpHelper.sendJsonResponse(exchange, 200, list);

            } else if ("PUT".equalsIgnoreCase(method) && path.endsWith("/profile")) {
                int userId = HttpHelper.getUserIdHeader(exchange);
                if (userId <= 0) {
                    HttpHelper.sendErrorResponse(exchange, 401, "Authentication required.");
                    return;
                }
                Map<String, Object> body = HttpHelper.readJsonBody(exchange);
                String company = JsonUtil.getString(body, "company", "");
                String designation = JsonUtil.getString(body, "designation", "");
                String location = JsonUtil.getString(body, "location", "");
                String phone = JsonUtil.getString(body, "phone", "");
                String linkedinUrl = JsonUtil.getString(body, "linkedinUrl", "");
                boolean isMentorAvailable = JsonUtil.getBoolean(body, "isMentorAvailable", true);

                Map<String, Object> res = alumniService.updateProfile(userId, company, designation, location, phone, linkedinUrl, isMentorAvailable);
                boolean success = Boolean.TRUE.equals(res.get("success"));
                HttpHelper.sendJsonResponse(exchange, success ? 200 : 400, res);

            } else {
                HttpHelper.sendErrorResponse(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            HttpHelper.sendErrorResponse(exchange, 500, "Server Error: " + e.getMessage());
        }
    }
}
