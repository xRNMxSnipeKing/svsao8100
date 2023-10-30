package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.strategy.Type;

class Introspector {
    private Contact contact;
    private Label label;
    private Annotation marker;

    public Introspector(Contact contact, Label label) {
        this.marker = contact.getAnnotation();
        this.contact = contact;
        this.label = label;
    }

    public Contact getContact() {
        return this.contact;
    }

    public Type getDependent() throws Exception {
        return this.label.getDependent();
    }

    public String getEntry() throws Exception {
        Class type = getDependent().getType();
        if (type.isArray()) {
            type = type.getComponentType();
        }
        String name = getName(type);
        if (name == null) {
            return null;
        }
        return name.intern();
    }

    private String getName(Class type) throws Exception {
        String name = getRoot(type);
        if (name != null) {
            return name;
        }
        return Reflector.getName(type.getSimpleName());
    }

    private String getRoot(Class type) {
        Class real = type;
        while (type != null) {
            String name = getRoot(real, type);
            if (name != null) {
                return name;
            }
            type = type.getSuperclass();
        }
        return null;
    }

    private String getRoot(Class<?> cls, Class<?> type) {
        String name = type.getSimpleName();
        if (!type.isAnnotationPresent(Root.class)) {
            return null;
        }
        String text = ((Root) type.getAnnotation(Root.class)).name();
        if (isEmpty(text)) {
            return Reflector.getName(name);
        }
        return text;
    }

    public String getName() throws Exception {
        String entry = this.label.getEntry();
        if (!this.label.isInline()) {
            entry = getDefault();
        }
        return entry.intern();
    }

    private String getDefault() throws Exception {
        String name = this.label.getOverride();
        return !isEmpty(name) ? name : this.contact.getName();
    }

    public String getPath() {
        Path path = (Path) this.contact.getAnnotation(Path.class);
        if (path == null) {
            return null;
        }
        return path.value();
    }

    public boolean isEmpty(String value) {
        if (value == null || value.length() == 0) {
            return true;
        }
        return false;
    }

    public String toString() {
        return String.format("%s on %s", new Object[]{this.marker, this.contact});
    }
}
