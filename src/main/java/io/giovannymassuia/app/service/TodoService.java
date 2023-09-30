package io.giovannymassuia.app.service;

import io.giovannymassuia.app.domain.Todo;
import io.giovannymassuia.framework.di.Singleton;

import java.sql.SQLException;
import java.util.List;

@Singleton
public interface TodoService {

    List<Todo> getAll() throws SQLException;

    Todo getById(String id) throws SQLException;

    Todo create(String task) throws SQLException;

    void markAsDone(String id) throws SQLException;
}
