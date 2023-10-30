package io.giovannymassuia.app.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import io.giovannymassuia.app.domain.Todo;
import io.giovannymassuia.app.service.TodoService;
import io.giovannymassuia.framework.di.SimpleDIContainer;
import io.giovannymassuia.framework.http.AbstractHttpHandler;
import io.giovannymassuia.framework.http.annotations.Get;
import io.giovannymassuia.framework.http.annotations.Path;
import io.giovannymassuia.framework.http.annotations.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.giovannymassuia.framework.http.Utils.sendResponse;
import static io.giovannymassuia.framework.http.Utils.toJson;

public class TodoHandler extends AbstractHttpHandler {

    private final Logger logger = LoggerFactory.getLogger(TodoHandler.class);

    private final TodoService todoService;

    public TodoHandler() {
        var diContainer = SimpleDIContainer.getInstance();
        todoService = diContainer.resolve(TodoService.class);
    }

    @Get
    @Path("/todos")
    public void handleGet(HttpExchange exchange, Map<String, String> pathParams, Map<String, String> queryParams) throws IOException {
        try {
            List<Todo> todos = todoService.getAll();
            String jsonResponse = toJson(todos);
            sendResponse(exchange, jsonResponse, 200);
        } catch (SQLException e) {
            sendResponse(exchange, "Database error", 500);
        }
    }

    @Get
    @Path("/todos/{id}")
    public void handleGetById(HttpExchange exchange, Map<String, String> pathParams, Map<String, String> queryParams) throws IOException {
        try {
            Todo todo = todoService.getById(pathParams.get("id"));
            String jsonResponse = toJson(todo);
            sendResponse(exchange, jsonResponse, 200);
        } catch (SQLException e) {
            logger.error("Error while getting todo by ID", e);
            sendResponse(exchange, "Database error", 500);
        }
    }

    @Post
    public void handlePost(HttpExchange exchange) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            NewTodoRequest newTodoRequest = new Gson().fromJson(requestBody, NewTodoRequest.class);

            Todo createdTodo = todoService.create(newTodoRequest.task());

            String jsonResponse = toJson(createdTodo);
            sendResponse(exchange, jsonResponse, 201);
        } catch (SQLException e) {
            sendResponse(exchange, "Database error", 500);
        } catch (JsonSyntaxException e) {
            sendResponse(exchange, "Invalid JSON format", 400);
        }
    }

    public record NewTodoRequest(String task) {
    }

    private void handleMarkAsDone(HttpExchange exchange, String path) throws IOException {
        try {
            // Extract UUID from the path
            String uuidStr = path.split("/")[2]; // Split by '/' and take the third element
            UUID todoId = UUID.fromString(uuidStr);

            todoService.markAsDone(todoId.toString());

            sendResponse(exchange, null, 200);
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, "Invalid UUID format", 400);
        } catch (SQLException e) {
            sendResponse(exchange, "Database error or Todo not found", 500);
        }
    }
}


