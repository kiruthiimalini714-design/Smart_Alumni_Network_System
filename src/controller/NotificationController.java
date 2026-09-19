package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Notification;
import service.NotificationService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class NotificationController implements HttpHandler {

    private final NotificationService notificationService = new NotificationService();

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
            if (userId <= 0) {
                HttpHelper.sendErrorResponse(exchange, 401, "Authentication required.");
                return;
            }

            if ("GET".equalsIgnoreCase(method)) {
                List<Notification> list = notificationService.getUserNotifications(userId);
                HttpHelper.sendJsonResponse(exchange, 200, list);
            } else if ("PUT".equalsIgnoreCase(method) && path.contains("/read")) {
                String[] parts = path.split("/");
                int notifId = 0;
                for (int i = 0; i < parts.length; i++) {
                    if ("notifications".equalsIgnoreCase(parts[i]) && i + 1 < parts.length) {
                        notifId = Integer.parseInt(parts[i + 1]);
                        break;
                    }
                }
                boolean updated = notificationService.markRead(notifId, userId);
                HttpHelper.sendJsonResponse(exchange, 200, Map.of("success", updated));
            } else {
                HttpHelper.sendErrorResponse(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            HttpHelper.sendErrorResponse(exchange, 500, "Server Error: " + e.getMessage());
        }
    }
}
