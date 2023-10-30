package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

class MethodContact implements Contact {
    private Method get;
    private Class item;
    private Class[] items;
    private Annotation label;
    private String name;
    private MethodPart set;
    private Class type;

    public MethodContact(MethodPart get) {
        this(get, null);
    }

    public MethodContact(MethodPart get, MethodPart set) {
        this.label = get.getAnnotation();
        this.items = get.getDependents();
        this.item = get.getDependent();
        this.get = get.getMethod();
        this.type = get.getType();
        this.name = get.getName();
        this.set = set;
    }

    public boolean isReadOnly() {
        return this.set == null;
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        T result = this.get.getAnnotation(type);
        if (type == this.label.annotationType()) {
            return this.label;
        }
        if (result != null || this.set == null) {
            return result;
        }
        return this.set.getAnnotation(type);
    }

    public Class getType() {
        return this.type;
    }

    public Class getDependent() {
        return this.item;
    }

    public Class[] getDependents() {
        return this.items;
    }

    public String getName() {
        return this.name;
    }

    public void set(Object source, Object value) throws Exception {
        Class type = getType();
        if (this.set == null) {
            throw new MethodException("Method %s of %s is read only", this.name, type);
        }
        this.set.getMethod().invoke(source, new Object[]{value});
    }

    public Object get(Object source) throws Exception {
        return this.get.invoke(source, new Object[0]);
    }

    public String toString() {
        return String.format("method '%s'", new Object[]{this.name});
    }
}
