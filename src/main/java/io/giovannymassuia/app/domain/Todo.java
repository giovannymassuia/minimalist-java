package io.giovannymassuia.app.domain;

import java.util.UUID;

public record Todo(UUID id, String task, boolean done) {
}
