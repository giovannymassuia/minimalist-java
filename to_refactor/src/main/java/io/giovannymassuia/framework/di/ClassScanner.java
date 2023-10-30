package io.giovannymassuia.framework.di;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.util.List;

class ClassScanner {

    public List<InjectableClass> scan(String packageName) {
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(packageName).scan()) {
            return scanResult
                    .getClassesWithAnnotation(Singleton.class.getName()).stream()
                    .map(classInfo -> {
                        try {
                            Class<?> clazz = classInfo.loadClass();
                            var type = clazz.isInterface() ? InjectableClass.Type.INTERFACE : InjectableClass.Type.CLASS;

                            Object instance;
                            if (type == InjectableClass.Type.INTERFACE) {
                                var implClass = scanResult.getClassesImplementing(clazz.getName()).get(0).loadClass();
                                instance = implClass.getDeclaredConstructor().newInstance();
                            } else {
                                instance = clazz.getDeclaredConstructor().newInstance();
                            }

                            return new InjectableClass(clazz, type, instance);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        }
    }

    public record InjectableClass(Class<?> clazz, Type type, Object instance) {
        public enum Type {
            CLASS, INTERFACE
        }
    }

}
