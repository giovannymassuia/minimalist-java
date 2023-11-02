package io.giovannymassuia.minimalist.java.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public record Route(
    String rootPath,
    Map<RouteMethod, List<RoutePath>> paths) {

    public record RoutePath(
        String httpMethod,
        String pathPattern,
        Function<HttpContext, ResponseEntity<?>> handler) {

    }

    public enum RouteMethod {
        GET, POST, PUT, DELETE
    }

    public static Route builder(String rootPath) {
        return new Route(rootPath, new HashMap<>());
    }

    public Route path(RouteMethod method, String pathPattern,
        Function<HttpContext, ResponseEntity<?>> handler) {
        paths.computeIfAbsent(method, k -> new ArrayList<>())
            .add(new RoutePath(method.name(), pathPattern, handler));
        return this;
    }

    public List<RoutePath> pathsByMethod(RouteMethod method) {
        return paths.get(method);
    }

}
