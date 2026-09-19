package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.MentorshipRequest;
import service.MentorshipService;
import util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MentorshipController implements HttpHandler {

    private final MentorshipService mentorshipService = new MentorshipService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            HttpHelper.sendJsonResponse(exchange, 200, Map.of("status", "ok"));
            return;
        }

        try {
            int userId = HttpHelper.getUserIdHeader(exchange);
            String role = HttpHelper.getUserRoleHeader(exchange);

            if (userId <= 0) {
                HttpHelper.sendErrorResponse(exchange, 401, "Authentication required.");
                return;
            }

            if ("GET".equalsIgnoreCase(method)) {
                List<MentorshipRequest> list = mentorshipService.getSessionsForUser(userId, role);
                HttpHelper.sendJsonResponse(exchange, 200, list);

            } else if ("POST".equalsIgnoreCase(method)) {
                if (!"Student".equalsIgnoreCase(role)) {
                    HttpHelper.sendErrorResponse(exchange, 403, "Only students can book mentorship sessions.");
                    return;
                }
                Map<String, Object> body = HttpHelper.readJsonBody(exchange);
                int alumniId = JsonUtil.getInt(body, "alumniId", 0);
                String topic = JsonUtil.getString(body, "topic", "");
                String message = JsonUtil.getString(body, "message", "");
                String prefDate = JsonUtil.getString(body, "preferredDate", "");
                String prefTime = JsonUtil.getString(body, "preferredTime", "");

                Map<String, Object> res = mentorshipService.requestSession(userId, alumniId, topic, message, prefDate, prefTime);
                boolean success = Boolean.TRUE.equals(res.get("success"));
                HttpHelper.sendJsonResponse(exchange, success ? 201 : 400, res);

            } else if ("PUT".equalsIgnoreCase(method) && path.contains("/status")) {
                if (!"Alumni".equalsIgnoreCase(role)) {
                    HttpHelper.sendErrorResponse(exchange, 403, "Only alumni mentors can accept or reject requests.");
                    return;
                }
                // Path format: /api/mentorship/3/status
                String[] parts = path.split("/");
                int sessionId = 0;
                for (int i = 0; i < parts.length; i++) {
                    if ("mentorship".equalsIgnoreCase(parts[i]) && i + 1 < parts.length) {
                        sessionId = Integer.parseInt(parts[i + 1]);
                        break;
                    }
                }
                if (sessionId <= 0) {
                    HttpHelper.sendErrorResponse(exchange, 400, "Invalid session ID.");
                    return;
                }

                Map<String, Object> body = HttpHelper.readJsonBody(exchange);
                String status = JsonUtil.getString(body, "status", "");
                String responseNotes = JsonUtil.getString(body, "responseNotes", "");

                Map<String, Object> res = mentorshipService.updateSessionStatus(userId, sessionId, status, responseNotes);
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
