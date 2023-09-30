package io.giovannymassuia.framework.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.giovannymassuia.framework.http.annotations.Get;
import io.giovannymassuia.framework.http.annotations.Path;
import io.giovannymassuia.framework.http.annotations.Post;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Gson GSON = new Gson();

    static boolean isMethodMatchingHttpVerb(Method method, String httpVerb) {
        return switch (httpVerb) {
            case "GET" -> method.isAnnotationPresent(Get.class);
            case "POST" -> method.isAnnotationPresent(Post.class);
            // ... other HTTP verbs
            default -> false;
        };
    }

    static boolean isPathMatching(Method method, URI uri, Map<String, String> extractedPathParams) {
        Path pathAnnotation = method.getAnnotation(Path.class);
        if (pathAnnotation != null) {
            String annotatedPath = pathAnnotation.value();
            String regexPattern = annotatedPath.replaceAll("\\{\\w+\\}", "([^/]+)");
            Pattern pattern = Pattern.compile(regexPattern);
            Matcher matcher = pattern.matcher(uri.getPath());

            if (matcher.matches()) {
                Pattern namePattern = Pattern.compile("\\{(\\w+)\\}");
                Matcher nameMatcher = namePattern.matcher(annotatedPath);
                int groupIndex = 1;
                while (nameMatcher.find()) {
                    String paramName = nameMatcher.group(1);
                    String paramValue = matcher.group(groupIndex++);
                    extractedPathParams.put(paramName, paramValue);
                }
                return true;
            }
        }
        return false;
    }

    static boolean isPathMatching(String path, URI uri, Map<String, String> extractedPathParams) {
        if (path != null) {
            String regexPattern = path.replaceAll("\\{\\w+\\}", "([^/]+)");
            // remove last slash if present
            if (regexPattern.endsWith("/")) regexPattern = regexPattern.substring(0, regexPattern.length() - 1);
            Pattern pattern = Pattern.compile(regexPattern);
            Matcher matcher = pattern.matcher(uri.getPath());

            if (matcher.matches()) {
                Pattern namePattern = Pattern.compile("\\{(\\w+)\\}");
                Matcher nameMatcher = namePattern.matcher(path);
                int groupIndex = 1;
                while (nameMatcher.find()) {
                    String paramName = nameMatcher.group(1);
                    String paramValue = matcher.group(groupIndex++);
                    extractedPathParams.put(paramName, paramValue);
                }
                return true;
            }
        }
        return false;
    }

    static Map<String, String> extractQueryParameters(URI uri) {
        Map<String, String> queryParameters = new HashMap<>();
        String query = uri.getRawQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length > 1) {
                    queryParameters.put(keyValue[0], keyValue[1]);
                } else {
                    queryParameters.put(keyValue[0], "");
                }
            }
        }
        return queryParameters;
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        if (response == null) {
            exchange.sendResponseHeaders(statusCode, -1);
            return;
        }
        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void sendResponse(HttpExchange exchange, ResponseEntity<?> responseEntity) throws IOException {
        String response = responseEntity.body() != null ? toJson(responseEntity.body()) : null;

        if (response == null) {
            exchange.sendResponseHeaders(responseEntity.statusCode().code(), -1);
            return;
        }

        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(responseEntity.statusCode().code(), bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

}
