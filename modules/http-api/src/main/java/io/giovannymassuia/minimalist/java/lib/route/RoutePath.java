package io.giovannymassuia.minimalist.java.lib.route;

import java.util.function.Function;

import io.giovannymassuia.minimalist.java.lib.HttpContext;
import io.giovannymassuia.minimalist.java.lib.ResponseEntity;

public record RoutePath(String httpMethod, String pathPattern, Function<HttpContext, ResponseEntity<?>> handler){

}
