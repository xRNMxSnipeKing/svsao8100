package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.simpleframework.xml.ElementArray;

class ElementArrayParameter implements Parameter {
    private final Contact contact;
    private final Constructor factory;
    private final int index;
    private final Label label;
    private final String name = this.label.getName();
    private final Class type = this.label.getType();

    private static class Contact extends ParameterContact<ElementArray> {
        public Contact(ElementArray label, Constructor factory, int index) {
            super(label, factory, index);
        }

        public String getName() {
            return ((ElementArray) this.label).name();
        }
    }

    public ElementArrayParameter(Constructor factory, ElementArray value, int index) throws Exception {
        this.contact = new Contact(value, factory, index);
        this.label = new ElementArrayLabel(this.contact, value);
        this.factory = factory;
        this.index = index;
    }

    public String getName() throws Exception {
        return this.name;
    }

    public String getName(Context context) throws Exception {
        return this.label.getName(context);
    }

    public Class getType() {
        return this.factory.getParameterTypes()[this.index];
    }

    public Annotation getAnnotation() {
        return this.contact.getAnnotation();
    }

    public int getIndex() {
        return this.index;
    }

    public boolean isRequired() {
        return this.label.isRequired();
    }

    public boolean isPrimitive() {
        return this.type.isPrimitive();
    }

    public String toString() {
        return this.contact.toString();
    }
}
