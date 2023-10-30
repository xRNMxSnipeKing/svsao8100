package org.simpleframework.xml.core;

import java.lang.reflect.Method;

class MethodName {
    private Method method;
    private String name;
    private MethodType type;

    public MethodName(Method method, MethodType type, String name) {
        this.name = name.intern();
        this.method = method;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public MethodType getType() {
        return this.type;
    }

    public Method getMethod() {
        return this.method;
    }
}
