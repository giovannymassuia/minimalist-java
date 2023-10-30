package io.giovannymassuia.app.service;

import io.giovannymassuia.app.database.DatabaseConnection;
import io.giovannymassuia.app.domain.Todo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TodoServiceImpl implements TodoService {

    String id;

    public TodoServiceImpl() {
        System.out.println("TodoServiceImpl created");
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public List<Todo> getAll() throws SQLException {
        System.out.println("TodoServiceImpl.getAll() called: " + this.id);

        List<Todo> todos = new ArrayList<>();
        String query = "SELECT * FROM todos";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                var id = resultSet.getString("id");
                var task = resultSet.getString("task");
                var done = resultSet.getBoolean("done");
                todos.add(new Todo(UUID.fromString(id), task, done));
            }
        }

        return todos;
    }

    @Override
    public Todo getById(String id) throws SQLException {
        String query = "SELECT * FROM todos WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, UUID.fromString(id));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                var task = resultSet.getString("task");
                var done = resultSet.getBoolean("done");
                return new Todo(UUID.fromString(id), task, done);
            } else {
                throw new SQLException("No todo found with the given ID.");
            }
        }
    }

    @Override
    public Todo create(String task) throws SQLException {
        UUID id = UUID.randomUUID();
        String query = "INSERT INTO todos (id, task, done) VALUES (?, ?, false)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, id);
            statement.setString(2, task);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 1) {
                return new Todo(id, task, false);
            } else {
                throw new SQLException("Creating todo failed, no row inserted.");
            }
        }
    }

    @Override
    public void markAsDone(String id) throws SQLException {
        String query = "UPDATE todos SET done = true WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Marking todo as done failed, no todo found with the given ID.");
            }
        }
    }
}
