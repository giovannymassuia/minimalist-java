package io.giovannymassuia.framework.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static io.giovannymassuia.framework.http.Utils.*;

public abstract class AbstractHttpHandler implements HttpHandler {
    private static final Gson GSON = new Gson();

    private static final Logger logger = LoggerFactory.getLogger(AbstractHttpHandler.class);

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();

        Map<String, String> extractedPathParams = new HashMap<>();
        Map<String, String> extractedQueryParams = extractQueryParameters(uri);

        logger.info("Received {} request for {}", method, uri);

        for (Method handlerMethod : this.getClass().getDeclaredMethods()) {
            if (isMethodMatchingHttpVerb(handlerMethod, method)
                    && isPathMatching(handlerMethod, uri, extractedPathParams)) {

                try {
                    handlerMethod.invoke(this, exchange, extractedPathParams, extractedQueryParams);
                    return;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error("Error while invoking handler method", e);
                    sendResponse(exchange, "Internal Server Error", 500);
                    return;
                }
            }
        }

        sendResponse(exchange, "Not Found", 404);

        logger.info("Response sent with status: {}", exchange.getResponseCode());
    }


}
