package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.StatsService;

import java.io.IOException;
import java.util.Map;

public class StatsController implements HttpHandler {

    private final StatsService statsService = new StatsService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            HttpHelper.sendJsonResponse(exchange, 200, Map.of("status", "ok"));
            return;
        }

        if ("GET".equalsIgnoreCase(method)) {
            try {
                Map<String, Object> stats = statsService.getDashboardStats();
                HttpHelper.sendJsonResponse(exchange, 200, stats);
            } catch (Exception e) {
                HttpHelper.sendErrorResponse(exchange, 500, "Server Error: " + e.getMessage());
            }
        } else {
            HttpHelper.sendErrorResponse(exchange, 405, "Method Not Allowed");
        }
    }
}
