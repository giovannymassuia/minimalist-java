/*
 * Copyright 2023 minimalist-java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.giovannymassuia.minimalist.java.lib.servers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import io.giovannymassuia.minimalist.java.lib.HttpContext;
import io.giovannymassuia.minimalist.java.lib.ResponseEntity;
import io.giovannymassuia.minimalist.java.lib.Route;
import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;

class JavaHttpApi implements ApiServer {

    private final Logger logger = Logger.getLogger(JavaHttpApi.class.getName());

    private HttpServer server;
    private final Gson GSON = new Gson();

    @Override
    public void create(int port) {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addRoute(Route route) {
        server.createContext(route.rootPath(), exchange -> {
            String method = exchange.getRequestMethod();
            URI uri = exchange.getRequestURI();

            Map<String, String> extractedPathParams = new HashMap<>();
            Map<String, String> extractedQueryParams = extractQueryParameters(uri);

            logger.info("Received %s request for %s".formatted(method, uri));

            List<RoutePath> routePaths = route.pathsByMethod(Route.RouteMethod.valueOf(method));

            Optional<RoutePath> routePath = routePaths.stream()
                            .filter(rp -> isPathMatching(route.rootPath() + rp.pathPattern(), uri,
                                            extractedPathParams))
                            .findFirst();

            if (routePath.isEmpty()) {
                try {
                    sendResponse(exchange, "Not Found", 404);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                HttpContext httpContext =
                                new HttpContext(extractedPathParams, extractedQueryParams);
                ResponseEntity<?> response = routePath.get().handler().apply(httpContext);
                sendResponse(exchange, response);
            }

            logger.info("Response sent with status: %s".formatted(exchange.getResponseCode()));
        });
    }

    @Override
    public void start() {
        server.start();
    }

    private Map<String, String> extractQueryParameters(URI uri) {
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

    private boolean isPathMatching(String path, URI uri, Map<String, String> extractedPathParams) {
        if (path != null) {
            String regexPattern = path.replaceAll("\\{\\w+\\}", "([^/]+)");
            // remove last slash if present
            if (regexPattern.endsWith("/")) {
                regexPattern = regexPattern.substring(0, regexPattern.length() - 1);
            }
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

    private void sendResponse(HttpExchange exchange, String response, int statusCode)
                    throws IOException {
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

    private void sendResponse(HttpExchange exchange, ResponseEntity<?> responseEntity)
                    throws IOException {
        String response = responseEntity.body() != null ? toJson(responseEntity.body()) : null;

        // set json content type
        exchange.getResponseHeaders().add("Content-Type", "application/json");

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

    private String toJson(Object obj) {
        return GSON.toJson(obj);
    }

}
