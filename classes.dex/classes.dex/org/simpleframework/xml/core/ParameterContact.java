package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

abstract class ParameterContact<T extends Annotation> implements Contact {
    protected final Constructor factory;
    protected final int index;
    protected final T label;

    public abstract String getName();

    public ParameterContact(T label, Constructor factory, int index) {
        this.factory = factory;
        this.index = index;
        this.label = label;
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public Class getType() {
        return this.factory.getParameterTypes()[this.index];
    }

    public Class getDependent() {
        return Reflector.getParameterDependent(this.factory, this.index);
    }

    public Class[] getDependents() {
        return Reflector.getParameterDependents(this.factory, this.index);
    }

    public Object get(Object source) {
        return null;
    }

    public void set(Object source, Object value) {
    }

    public <A extends Annotation> A getAnnotation(Class<A> cls) {
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }

    public String toString() {
        return String.format("parameter %s of constructor %s", new Object[]{Integer.valueOf(this.index), this.factory});
    }
}
