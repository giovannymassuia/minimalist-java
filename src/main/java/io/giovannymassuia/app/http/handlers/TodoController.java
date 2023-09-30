package io.giovannymassuia.app.http.handlers;

import com.google.gson.Gson;
import io.giovannymassuia.app.domain.Todo;
import io.giovannymassuia.app.service.TodoService;
import io.giovannymassuia.framework.di.SimpleDIContainer;
import io.giovannymassuia.framework.di.Singleton;
import io.giovannymassuia.framework.http.HttpContext;
import io.giovannymassuia.framework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import static io.giovannymassuia.framework.http.ResponseEntity.StatusCode.*;
import static io.giovannymassuia.framework.http.Utils.sendResponse;

@Singleton
public class TodoController {

    private final Logger logger = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    public ResponseEntity<?> handleGet(HttpContext context) {
        try {
            var diContainer = SimpleDIContainer.getInstance();
            var service = diContainer.resolve(TodoService.class);

            System.out.println(service.toString());

            List<Todo> todos = service.getAll();
            return new ResponseEntity<>(OK, todos);
        } catch (SQLException e) {
            logger.error("Error while getting all todos", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    public ResponseEntity<?> handleGetById(HttpContext context) {
        try {
            Todo todo = todoService.getById(context.pathParams().get("id"));
            return new ResponseEntity<>(OK, todo);
        } catch (SQLException e) {
            logger.error("Error while getting todo by ID", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    public ResponseEntity<?> handlePost(HttpContext context) {
        try {
            String requestBody = null;
            try {
                requestBody = new String(context.exchange().getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            NewTodoRequest newTodoRequest = new Gson().fromJson(requestBody, NewTodoRequest.class);

            Todo createdTodo = todoService.create(newTodoRequest.task());

            return new ResponseEntity<>(CREATED, createdTodo);
        } catch (SQLException e) {
            logger.error("Error while creating todo", e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    public record NewTodoRequest(String task) {
    }

    private void handleMarkAsDone(HttpContext context) throws IOException {
        try {
            todoService.markAsDone(context.pathParams().get("id"));

            sendResponse(context.exchange(), null, 200);
        } catch (IllegalArgumentException e) {
            sendResponse(context.exchange(), "Invalid UUID format", 400);
        } catch (SQLException e) {
            sendResponse(context.exchange(), "Database error or Todo not found", 500);
        }
    }
}


