package io.giovannymassuia.framework.http;

public record ResponseEntity<T>(
        StatusCode statusCode,
        T body) {

    public enum StatusCode {
        OK(200, "OK"),
        CREATED(201, "Created"),
        BAD_REQUEST(400, "Bad Request"),
        NOT_FOUND(404, "Not Found"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error");

        private final int code;
        private final String description;

        StatusCode(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int code() {
            return code;
        }

        public String description() {
            return description;
        }
    }
}
