package com.tcet.pms.util;

import java.util.*;

/**
 * Lightweight, zero-dependency JSON parser and serializer written in pure Core Java.
 * Eliminates external Maven/Gradle jar requirements (Jackson/Gson) for seamless
 * out-of-the-box execution during academic viva evaluations.
 */
public class SimpleJson {

    // ================= SERIALIZATION =================

    public static String stringify(Object obj) {
        StringBuilder sb = new StringBuilder();
        serializeValue(obj, sb);
        return sb.toString();
    }

    private static void serializeValue(Object obj, StringBuilder sb) {
        if (obj == null) {
            sb.append("null");
        } else if (obj instanceof String) {
            sb.append('"').append(escapeString((String) obj)).append('"');
        } else if (obj instanceof Number || obj instanceof Boolean) {
            sb.append(obj.toString());
        } else if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            sb.append("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                sb.append('"').append(escapeString(String.valueOf(entry.getKey()))).append("\":");
                serializeValue(entry.getValue(), sb);
                first = false;
            }
            sb.append("}");
        } else if (obj instanceof Collection) {
            Collection<?> col = (Collection<?>) obj;
            sb.append("[");
            boolean first = true;
            for (Object item : col) {
                if (!first) sb.append(",");
                serializeValue(item, sb);
                first = false;
            }
            sb.append("]");
        } else {
            // Fallback to string representation
            sb.append('"').append(escapeString(obj.toString())).append('"');
        }
    }

    private static String escapeString(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    // ================= PARSING =================

    public static Object parse(String json) {
        if (json == null) return null;
        Parser parser = new Parser(json.trim());
        return parser.parseValue();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String json) {
        Object res = parse(json);
        if (res instanceof Map) {
            return (Map<String, Object>) res;
        }
        return new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public static List<Object> parseArray(String json) {
        Object res = parse(json);
        if (res instanceof List) {
            return (List<Object>) res;
        }
        return new ArrayList<>();
    }

    private static class Parser {
        private final String src;
        private int pos = 0;

        public Parser(String src) {
            this.src = src;
        }

        public Object parseValue() {
            skipWhitespace();
            if (pos >= src.length()) return null;

            if (src.charAt(pos) == '\\' && pos + 1 < src.length() && src.charAt(pos + 1) == '"') {
                pos++;
            }

            char c = src.charAt(pos);
            if (c == '{') {
                return parseObject();
            } else if (c == '[') {
                return parseArray();
            } else if (c == '"') {
                return parseString();
            } else if (c == 't' || c == 'f') {
                return parseBoolean();
            } else if (c == 'n') {
                return parseNull();
            } else if (c == '-' || Character.isDigit(c)) {
                return parseNumber();
            } else {
                throw new IllegalArgumentException("Unexpected character at pos " + pos + ": " + c);
            }
        }

        private Map<String, Object> parseObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            expect('{');
            skipWhitespace();
            if (peek('}')) {
                pos++;
                return map;
            }

            while (true) {
                skipWhitespace();
                if (pos < src.length() && src.charAt(pos) == '\\' && pos + 1 < src.length() && src.charAt(pos + 1) == '"') {
                    pos++;
                }
                String key;
                if (peek('"')) {
                    key = parseString();
                } else {
                    // Support unquoted keys for maximum compatibility
                    int start = pos;
                    while (pos < src.length() && src.charAt(pos) != ':' && !Character.isWhitespace(src.charAt(pos))) {
                        pos++;
                    }
                    key = src.substring(start, pos).trim();
                }
                skipWhitespace();
                expect(':');
                Object val = parseValue();
                map.put(key, val);

                skipWhitespace();
                if (peek('}')) {
                    pos++;
                    break;
                }
                expect(',');
            }
            return map;
        }

        private List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            expect('[');
            skipWhitespace();
            if (peek(']')) {
                pos++;
                return list;
            }

            while (true) {
                list.add(parseValue());
                skipWhitespace();
                if (peek(']')) {
                    pos++;
                    break;
                }
                expect(',');
            }
            return list;
        }

        private String parseString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (pos < src.length()) {
                char c = src.charAt(pos++);
                if (c == '"') {
                    return sb.toString();
                } else if (c == '\\') {
                    if (pos >= src.length()) break;
                    char esc = src.charAt(pos++);
                    switch (esc) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'u':
                            if (pos + 4 <= src.length()) {
                                String hex = src.substring(pos, pos + 4);
                                sb.append((char) Integer.parseInt(hex, 16));
                                pos += 4;
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

        private Object parseNumber() {
            int start = pos;
            if (peek('-')) pos++;
            while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
            boolean isFloat = false;
            if (pos < src.length() && src.charAt(pos) == '.') {
                isFloat = true;
                pos++;
                while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
            }
            if (pos < src.length() && (src.charAt(pos) == 'e' || src.charAt(pos) == 'E')) {
                isFloat = true;
                pos++;
                if (pos < src.length() && (src.charAt(pos) == '+' || src.charAt(pos) == '-')) pos++;
                while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
            }
            String numStr = src.substring(start, pos);
            if (isFloat) {
                return Double.parseDouble(numStr);
            } else {
                try {
                    return Long.parseLong(numStr);
                } catch (NumberFormatException e) {
                    return Double.parseDouble(numStr);
                }
            }
        }

        private Boolean parseBoolean() {
            if (src.startsWith("true", pos)) {
                pos += 4;
                return Boolean.TRUE;
            } else if (src.startsWith("false", pos)) {
                pos += 5;
                return Boolean.FALSE;
            }
            throw new IllegalArgumentException("Expected boolean at pos " + pos);
        }

        private Object parseNull() {
            if (src.startsWith("null", pos)) {
                pos += 4;
                return null;
            }
            throw new IllegalArgumentException("Expected null at pos " + pos);
        }

        private void skipWhitespace() {
            while (pos < src.length()) {
                char c = src.charAt(pos);
                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    pos++;
                } else {
                    break;
                }
            }
        }

        private void expect(char c) {
            if (pos >= src.length() || src.charAt(pos) != c) {
                throw new IllegalArgumentException("Expected '" + c + "' at pos " + pos + " but found '" + (pos < src.length() ? src.charAt(pos) : "EOF") + "'");
            }
            pos++;
        }

        private boolean peek(char c) {
            return pos < src.length() && src.charAt(pos) == c;
        }
    }
}
