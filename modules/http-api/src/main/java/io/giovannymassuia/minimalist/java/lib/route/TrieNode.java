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
package io.giovannymassuia.minimalist.java.lib.route;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.giovannymassuia.minimalist.java.lib.HttpContext;
import io.giovannymassuia.minimalist.java.lib.ResponseEntity;

class TrieNode {
    public Map<String, TrieNode> staticChildren = new HashMap<>();
    public TrieNode parameterChild = null;
    public Function<HttpContext, ResponseEntity<?>> handler;
    public String parameterName = null; // If null, it's a static node. Otherwise, it's a parameterized node.
}
