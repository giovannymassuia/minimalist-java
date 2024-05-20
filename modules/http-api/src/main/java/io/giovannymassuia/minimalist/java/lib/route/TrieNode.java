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
