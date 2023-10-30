package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

class Comparer {
    private static final String NAME = "name";
    private final String[] ignore;

    public Comparer() {
        this(NAME);
    }

    public Comparer(String... ignore) {
        this.ignore = ignore;
    }

    public boolean equals(Annotation left, Annotation right) throws Exception {
        Class type = left.annotationType();
        Class expect = right.annotationType();
        Method[] list = type.getDeclaredMethods();
        if (!type.equals(expect)) {
            return false;
        }
        for (Method method : list) {
            if (!isIgnore(method) && !method.invoke(left, new Object[0]).equals(method.invoke(right, new Object[0]))) {
                return false;
            }
        }
        return true;
    }

    private boolean isIgnore(Method method) {
        String name = method.getName();
        if (this.ignore != null) {
            for (String value : this.ignore) {
                if (name.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }
}
