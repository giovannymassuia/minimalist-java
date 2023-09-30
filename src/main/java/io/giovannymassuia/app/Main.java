package io.giovannymassuia.app;

import io.giovannymassuia.app.http.handlers.TodoController;
import io.giovannymassuia.app.service.TodoService;
import io.giovannymassuia.app.service.TodoServiceImpl;
import io.giovannymassuia.framework.di.SimpleDIContainer;
import io.giovannymassuia.framework.http.ResponseEntity;
import io.giovannymassuia.framework.http.Route;
import io.giovannymassuia.framework.http.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static io.giovannymassuia.framework.http.ResponseEntity.StatusCode.OK;
import static io.giovannymassuia.framework.http.Route.RouteMethod.GET;
import static io.giovannymassuia.framework.http.Route.RouteMethod.POST;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        logger.info("Starting application");

        var diContainer = SimpleDIContainer.getInstance();
        var webServer = new WebServer(8080);

        // Register dependencies
        diContainer.register(TodoService.class, TodoServiceImpl::new);
        diContainer.register(TodoController.class, () -> new TodoController(diContainer.resolve(TodoService.class)));

        // Register routes with annotations
//        webServer.route()
//                .path("/todos")
//                .handler(new TodoHandler())
//                .register();

        // Register routes with fluent API
        var todoController = diContainer.resolve(TodoController.class);
        webServer
                .addRoute(Route.builder("/todos")
                        .path(GET, "/", todoController::handleGet)
                        .path(GET, "/{id}", todoController::handleGetById)
                        .path(POST, "/", todoController::handlePost))
                .addRoute(Route.builder("/health")
                        .path(GET, "/", context -> {
                            var thread = Thread.currentThread();
                            logger.info("Health check");

                            // sleep 2 seconds to simulate a long running task
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            return new ResponseEntity<>(OK, "OK from thread " + thread.getName());
                        }));

        webServer.start();
    }
}