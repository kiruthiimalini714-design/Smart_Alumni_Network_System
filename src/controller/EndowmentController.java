package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.EndowmentProject;
import model.Pledge;
import service.EndowmentService;
import util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class EndowmentController implements HttpHandler {

    private final EndowmentService endowmentService = new EndowmentService();

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

            if (path.contains("/pledges")) {
                if ("POST".equalsIgnoreCase(method)) {
                    if (userId <= 0) {
                        HttpHelper.sendErrorResponse(exchange, 401, "Authentication required to make a pledge.");
                        return;
                    }
                    Map<String, Object> body = HttpHelper.readJsonBody(exchange);
                    int projectId = JsonUtil.getInt(body, "projectId", 0);
                    double amount = JsonUtil.getDouble(body, "pledgeAmount", 0.0);
                    String notes = JsonUtil.getString(body, "notes", "");

                    Map<String, Object> res = endowmentService.createPledge(userId, projectId, amount, notes);
                    boolean success = Boolean.TRUE.equals(res.get("success"));
                    HttpHelper.sendJsonResponse(exchange, success ? 201 : 400, res);

                } else if ("GET".equalsIgnoreCase(method)) {
                    if (userId <= 0) {
                        HttpHelper.sendErrorResponse(exchange, 401, "Authentication required.");
                        return;
                    }
                    List<Pledge> list = endowmentService.getUserPledges(userId);
                    HttpHelper.sendJsonResponse(exchange, 200, list);
                } else {
                    HttpHelper.sendErrorResponse(exchange, 405, "Method Not Allowed");
                }
                return;
            }

            if ("GET".equalsIgnoreCase(method)) {
                String[] parts = path.split("/");
                if (parts.length >= 4 && parts[3].matches("\\d+")) {
                    int projectId = Integer.parseInt(parts[3]);
                    Map<String, Object> details = endowmentService.getProjectDetails(projectId);
                    HttpHelper.sendJsonResponse(exchange, 200, details);
                } else {
                    List<EndowmentProject> list = endowmentService.getAllProjects();
                    HttpHelper.sendJsonResponse(exchange, 200, list);
                }
            } else {
                HttpHelper.sendErrorResponse(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            HttpHelper.sendErrorResponse(exchange, 500, "Server Error: " + e.getMessage());
        }
    }
}
