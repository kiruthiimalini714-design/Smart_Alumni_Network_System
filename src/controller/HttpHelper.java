package controller;

import com.sun.net.httpserver.HttpExchange;
import util.JsonUtil;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpHelper {

    public static String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    public static Map<String, Object> readJsonBody(HttpExchange exchange) {
        try {
            String body = readBody(exchange);
            return JsonUtil.parseJsonObject(body);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public static Map<String, String> parseQueryParams(HttpExchange exchange) {
        Map<String, String> params = new HashMap<>();
        String query = exchange.getRequestURI().getRawQuery();
        if (query == null || query.trim().isEmpty()) {
            return params;
        }
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                try {
                    String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                    String val = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                    params.put(key, val);
                } catch (Exception ignored) {}
            } else if (pair.length == 1) {
                try {
                    String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                    params.put(key, "");
                } catch (Exception ignored) {}
            }
        }
        return params;
    }

    public static void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String json = (data instanceof String) ? (String) data : JsonUtil.toJson(data);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-User-Id, X-User-Role");

        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        Map<String, Object> err = new HashMap<>();
        err.put("success", false);
        err.put("message", message);
        sendJsonResponse(exchange, statusCode, err);
    }

    public static int getUserIdHeader(HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst("X-User-Id");
        if (header == null || header.trim().isEmpty()) return -1;
        try {
            return Integer.parseInt(header.trim());
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getUserRoleHeader(HttpExchange exchange) {
        String role = exchange.getRequestHeaders().getFirst("X-User-Role");
        return (role != null) ? role.trim() : "Guest";
    }
}
