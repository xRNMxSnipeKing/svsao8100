package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

class SetPart implements MethodPart {
    private final Annotation label;
    private final Method method;
    private final String name;
    private final MethodType type;

    public SetPart(MethodName method, Annotation label) {
        this.method = method.getMethod();
        this.name = method.getName();
        this.type = method.getType();
        this.label = label;
    }

    public String getName() {
        return this.name;
    }

    public Class getType() {
        return this.method.getParameterTypes()[0];
    }

    public Class getDependent() {
        return Reflector.getParameterDependent(this.method, 0);
    }

    public Class[] getDependents() {
        return Reflector.getParameterDependents(this.method, 0);
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return this.method.getAnnotation(type);
    }

    public MethodType getMethodType() {
        return this.type;
    }

    public Method getMethod() {
        if (!this.method.isAccessible()) {
            this.method.setAccessible(true);
        }
        return this.method;
    }

    public String toString() {
        return this.method.toGenericString();
    }
}
