package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class FieldContact implements Contact {
    private Field field;
    private Annotation label;
    private int modifier;
    private String name;

    public FieldContact(Field field, Annotation label) {
        this.modifier = field.getModifiers();
        this.label = label;
        this.field = field;
    }

    public boolean isReadOnly() {
        return !isStatic() && isFinal();
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.modifier);
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.modifier);
    }

    public Class getType() {
        return this.field.getType();
    }

    public Class getDependent() {
        return Reflector.getDependent(this.field);
    }

    public Class[] getDependents() {
        return Reflector.getDependents(this.field);
    }

    public String getName() {
        if (this.name == null) {
            this.name = getName(this.field);
        }
        return this.name;
    }

    private String getName(Field field) {
        String name = field.getName();
        if (name != null) {
            return name.intern();
        }
        return name;
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        if (type == this.label.annotationType()) {
            return this.label;
        }
        return this.field.getAnnotation(type);
    }

    public void set(Object source, Object value) throws Exception {
        if (!isFinal()) {
            this.field.set(source, value);
        }
    }

    public Object get(Object source) throws Exception {
        return this.field.get(source);
    }

    public String toString() {
        return String.format("field '%s' %s", new Object[]{getName(), this.field.toString()});
    }
}
