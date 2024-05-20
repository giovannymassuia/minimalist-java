package io.giovannymassuia.minimalist.java.lib.route;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import io.giovannymassuia.minimalist.java.lib.HttpContext;
import io.giovannymassuia.minimalist.java.lib.ResponseEntity;

public class Trie {
    private final TrieNode root = new TrieNode();

    public void insert(String rootPath, String path, Function<HttpContext, ResponseEntity<?>> handler) {
        TrieNode current = root;
        String fullPath = rootPath + path;
        fullPath = fullPath.replace("//", "/");
        String[] segments = fullPath.split("/");
        Set<String> seenParameters = new HashSet<>();

        if (fullPath.equals("/")) {
            segments = new String[]{""};
        }

        for (String segment : segments) {
            if (segment.startsWith("{") && segment.endsWith("}")) {
                String paramName = segment.substring(1).substring(0, segment.length() - 2);

                if (seenParameters.contains(paramName)) {
                    throw new IllegalArgumentException(
                            "Cannot use the same parameter name more than once in a route.");
                }
                seenParameters.add(paramName);

                if (current.parameterChild != null) {
                    // Check if the parameter is of the same type.
                    if (!current.parameterChild.parameterName.equals(paramName)) {
                        throw new IllegalArgumentException(
                                "Invalid path: " + path + ". Parameter " + paramName
                                        + " is already defined.");
                    }
                } else {
                    TrieNode paramNode = new TrieNode();
                    paramNode.parameterName = paramName;
                    current.parameterChild = paramNode;
                }
                current = current.parameterChild;
            } else {
                current.staticChildren.putIfAbsent(segment, new TrieNode());
                current = current.staticChildren.get(segment);
            }
        }
        current.handler = handler;
    }

    public SearchResult search(String path) {
        TrieNode current = root;
        String[] segments = path.split("/");
        Map<String, String> capturedParams = new HashMap<>();

        if (path.equals("/")) {
            segments = new String[]{""};
        }

        for (String segment : segments) {
            TrieNode next = current.staticChildren.get(segment);

            // If exact match is not found, check if a parameter child exists.
            if (next == null && current.parameterChild != null) {
                next = current.parameterChild;
                capturedParams.put(next.parameterName, segment);
            }

            if (next == null) {
                return null;
            }

            current = next;
        }
        return new SearchResult(current.handler, capturedParams);
    }

    public record SearchResult(Function<HttpContext, ResponseEntity<?>> handler, Map<String, String> params) {
    }

}
