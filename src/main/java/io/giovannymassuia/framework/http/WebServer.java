package io.giovannymassuia.framework.http;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.giovannymassuia.framework.http.Utils.*;

public class WebServer {
    private final Logger logger = LoggerFactory.getLogger(WebServer.class);

    private final HttpServer server;
    private final List<Route> routes = new ArrayList<>();

    public WebServer(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        Executor executor = Executors.newFixedThreadPool(5);
//        Executor executor = Executors.newVirtualThreadPerTaskExecutor();

        this.server.setExecutor(executor);
    }

    public WebServer addRoute(Route route) {
        routes.add(route);
        return this;
    }

    public void start() {
        routes.forEach(route -> {
            server.createContext(route.rootPath(), exchange -> {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();

                // /todos/123/done
                // /todos/{id}/done

                Map<String, String> extractedPathParams = new HashMap<>();
                Map<String, String> extractedQueryParams = extractQueryParameters(uri);

                logger.debug("Received {} request for {}", method, uri);

                List<Route.RoutePath> routePaths = route.pathsByMethod(Route.RouteMethod.valueOf(method));

                Optional<Route.RoutePath> routePath = routePaths.stream()
                        .filter(rp -> isPathMatching(route.rootPath() + rp.pathPattern(), uri, extractedPathParams))
                        .findFirst();

                if (routePath.isEmpty()) {
                    try {
                        sendResponse(exchange, "Not Found", 404);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    HttpContext httpContext = new HttpContext(exchange, extractedPathParams, extractedQueryParams);
                    ResponseEntity<?> response = routePath.get().handler().apply(httpContext);
                    sendResponse(exchange, response);
                }

                logger.debug("Response sent with status: {}", exchange.getResponseCode());
            });
        });

        server.start();
        logger.info("Server started on port {}", server.getAddress().getPort());
    }

}
