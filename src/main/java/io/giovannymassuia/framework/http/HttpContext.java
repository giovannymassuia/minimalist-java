package io.giovannymassuia.framework.http;

import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public record HttpContext(HttpExchange exchange, Map<String, String> pathParams, Map<String, String> queryParams) {
}
