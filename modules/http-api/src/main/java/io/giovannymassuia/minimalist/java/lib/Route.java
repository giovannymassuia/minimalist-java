/*
 * Copyright 2023 minimalist-java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public record Route(String rootPath,Map<RouteMethod,List<RoutePath>>paths){

public record RoutePath(String httpMethod,String pathPattern,Function<HttpContext,ResponseEntity<?>>handler){

}

public enum RouteMethod {
    GET, POST, PUT, DELETE

    }

    public static Route builder(String rootPath) {
        return new Route(rootPath, new HashMap<>());
    }

    public Route path(RouteMethod method, String pathPattern,
                    Function<HttpContext, ResponseEntity<?>> handler) {
        paths.computeIfAbsent(method, k -> new ArrayList<>())
                        .add(new RoutePath(method.name(), pathPattern, handler));
        return this;
    }

    public List<RoutePath> pathsByMethod(RouteMethod method) {
        return paths.get(method);
    }

}
