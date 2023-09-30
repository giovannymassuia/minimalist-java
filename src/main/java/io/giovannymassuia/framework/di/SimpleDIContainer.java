package io.giovannymassuia.framework.di;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SimpleDIContainer {

    private final Map<Class<?>, Supplier<?>> registry = new HashMap<>();

    private static final SimpleDIContainer instance = new SimpleDIContainer();

    private SimpleDIContainer() {
//        ClassScanner scanner = new ClassScanner();
//        scanner.scan("io.giovannymassuia.app").forEach(clazz -> {
//            try {
//                register(clazz.clazz(), clazz::instance);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
    }

    public static SimpleDIContainer getInstance() {
        return instance;
    }

    public void register(Class<?> type, Supplier<?> supplier) {
        registry.put(type, supplier);
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> type) {
        return (T) registry.get(type).get();
    }
}
