package io.giovannymassuia.minimalist.java.lib;

import java.util.Map;

public record HttpContext(Map<String, String> pathParams, Map<String, String> queryParams) {

}
