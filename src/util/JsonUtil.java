package util;

import java.lang.reflect.Method;
import java.util.*;

public class JsonUtil {

    // ==========================================================
    // SERIALIZATION (Java Object -> JSON String)
    // ==========================================================

    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            return escapeString((String) obj);
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                sb.append(escapeString(String.valueOf(entry.getKey())));
                sb.append(":");
                sb.append(toJson(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof Collection<?>) {
            Collection<?> col = (Collection<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : col) {
                if (!first) sb.append(",");
                sb.append(toJson(item));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }
        if (obj.getClass().isArray()) {
            Object[] arr = (Object[]) obj;
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : arr) {
                if (!first) sb.append(",");
                sb.append(toJson(item));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }

        // POJO Serialization via Getters
        Map<String, Object> map = new LinkedHashMap<>();
        for (Method method : obj.getClass().getMethods()) {
            if (method.getParameterCount() == 0 && !method.getName().equals("getClass")) {
                String name = method.getName();
                String key = null;
                if (name.startsWith("get") && name.length() > 3) {
                    key = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                } else if (name.startsWith("is") && name.length() > 2) {
                    key = Character.toLowerCase(name.charAt(2)) + name.substring(3);
                }
                if (key != null) {
                    try {
                        Object val = method.invoke(obj);
                        map.put(key, val);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return toJson(map);
    }

    private static String escapeString(String s) {
        if (s == null) return "\"\"";
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b");  break;
                case '\f': sb.append("\\f");  break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < ' ') {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    // ==========================================================
    // DESERIALIZATION (JSON String -> Map / List / Primitives)
    // ==========================================================

    public static Map<String, Object> parseJsonObject(String json) {
        if (json == null) return new LinkedHashMap<>();
        String trimmed = json.trim();
        if (trimmed.isEmpty() || !trimmed.startsWith("{")) {
            return new LinkedHashMap<>();
        }
        Object parsed = new JsonParser(trimmed).parseValue();
        if (parsed instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> res = (Map<String, Object>) parsed;
            return res;
        }
        return new LinkedHashMap<>();
    }

    public static List<Object> parseJsonArray(String json) {
        if (json == null) return new ArrayList<>();
        String trimmed = json.trim();
        if (trimmed.isEmpty() || !trimmed.startsWith("[")) {
            return new ArrayList<>();
        }
        Object parsed = new JsonParser(trimmed).parseValue();
        if (parsed instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Object> res = (List<Object>) parsed;
            return res;
        }
        return new ArrayList<>();
    }

    // Safe extraction helpers
    public static String getString(Map<String, Object> map, String key, String defaultValue) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) return defaultValue;
        return String.valueOf(map.get(key)).trim();
    }

    public static int getInt(Map<String, Object> map, String key, int defaultValue) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) return defaultValue;
        try {
            Object val = map.get(key);
            if (val instanceof Number) return ((Number) val).intValue();
            return Integer.parseInt(val.toString().trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static double getDouble(Map<String, Object> map, String key, double defaultValue) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) return defaultValue;
        try {
            Object val = map.get(key);
            if (val instanceof Number) return ((Number) val).doubleValue();
            return Double.parseDouble(val.toString().trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) return defaultValue;
        Object val = map.get(key);
        if (val instanceof Boolean) return (Boolean) val;
        return Boolean.parseBoolean(val.toString().trim());
    }

    // Recursive descent JSON parser
    private static class JsonParser {
        private final String src;
        private int idx = 0;

        public JsonParser(String src) {
            this.src = src;
        }

        public Object parseValue() {
            skipWhitespace();
            if (idx >= src.length()) return null;
            char c = src.charAt(idx);
            if (c == '{') return parseObject();
            if (c == '[') return parseArray();
            if (c == '"') return parseString();
            if (c == 't' || c == 'f') return parseBoolean();
            if (c == 'n') return parseNull();
            if (c == '-' || Character.isDigit(c)) return parseNumber();
            throw new RuntimeException("Unexpected character at " + idx + ": " + c);
        }

        private Map<String, Object> parseObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            idx++; // skip '{'
            skipWhitespace();
            if (idx < src.length() && src.charAt(idx) == '}') {
                idx++;
                return map;
            }
            while (idx < src.length()) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                if (idx >= src.length() || src.charAt(idx) != ':') {
                    throw new RuntimeException("Expected ':' at " + idx);
                }
                idx++; // skip ':'
                Object val = parseValue();
                map.put(key, val);
                skipWhitespace();
                if (idx < src.length() && src.charAt(idx) == ',') {
                    idx++;
                    skipWhitespace();
                } else if (idx < src.length() && src.charAt(idx) == '}') {
                    idx++;
                    break;
                } else {
                    break;
                }
            }
            return map;
        }

        private List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            idx++; // skip '['
            skipWhitespace();
            if (idx < src.length() && src.charAt(idx) == ']') {
                idx++;
                return list;
            }
            while (idx < src.length()) {
                skipWhitespace();
                Object val = parseValue();
                list.add(val);
                skipWhitespace();
                if (idx < src.length() && src.charAt(idx) == ',') {
                    idx++;
                    skipWhitespace();
                } else if (idx < src.length() && src.charAt(idx) == ']') {
                    idx++;
                    break;
                } else {
                    break;
                }
            }
            return list;
        }

        private String parseString() {
            if (src.charAt(idx) != '"') throw new RuntimeException("Expected '\"' at " + idx);
            idx++; // skip initial quote
            StringBuilder sb = new StringBuilder();
            while (idx < src.length()) {
                char c = src.charAt(idx++);
                if (c == '"') {
                    return sb.toString();
                }
                if (c == '\\') {
                    if (idx >= src.length()) break;
                    char esc = src.charAt(idx++);
                    switch (esc) {
                        case '"':  sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/':  sb.append('/'); break;
                        case 'b':  sb.append('\b'); break;
                        case 'f':  sb.append('\f'); break;
                        case 'n':  sb.append('\n'); break;
                        case 'r':  sb.append('\r'); break;
                        case 't':  sb.append('\t'); break;
                        case 'u':
                            if (idx + 4 <= src.length()) {
                                String hex = src.substring(idx, idx + 4);
                                sb.append((char) Integer.parseInt(hex, 16));
                                idx += 4;
                            }
                            break;
                        default: sb.append(esc); break;
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        private Number parseNumber() {
            int start = idx;
            if (src.charAt(idx) == '-') idx++;
            while (idx < src.length() && (Character.isDigit(src.charAt(idx)) || src.charAt(idx) == '.' || src.charAt(idx) == 'e' || src.charAt(idx) == 'E' || src.charAt(idx) == '+' || src.charAt(idx) == '-')) {
                idx++;
            }
            String s = src.substring(start, idx);
            if (s.contains(".") || s.contains("e") || s.contains("E")) {
                return Double.parseDouble(s);
            } else {
                long l = Long.parseLong(s);
                if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) return (int) l;
                return l;
            }
        }

        private Boolean parseBoolean() {
            if (src.startsWith("true", idx)) {
                idx += 4;
                return Boolean.TRUE;
            }
            if (src.startsWith("false", idx)) {
                idx += 5;
                return Boolean.FALSE;
            }
            throw new RuntimeException("Invalid boolean at " + idx);
        }

        private Object parseNull() {
            if (src.startsWith("null", idx)) {
                idx += 4;
                return null;
            }
            throw new RuntimeException("Invalid null at " + idx);
        }

        private void skipWhitespace() {
            while (idx < src.length() && Character.isWhitespace(src.charAt(idx))) {
                idx++;
            }
        }
    }
}
