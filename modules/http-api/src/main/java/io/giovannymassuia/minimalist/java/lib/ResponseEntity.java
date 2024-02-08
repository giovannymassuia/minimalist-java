/*
 * Copyright 2024 minimalist-java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.giovannymassuia.minimalist.java.lib;

public record ResponseEntity<T>(
    StatusCode statusCode,
    T body) {

    public static <T> ResponseEntity<T> ok(T body) {
        return new ResponseEntity<>(StatusCode.OK, body);
    }

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
