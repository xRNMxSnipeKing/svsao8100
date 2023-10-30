package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementMapUnion;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.Transient;
import org.simpleframework.xml.Version;

class FieldScanner extends ContactList {
    private final DefaultType access;
    private final ContactMap done;
    private final AnnotationFactory factory;
    private final Hierarchy hierarchy;

    public FieldScanner(Class type) throws Exception {
        this(type, null);
    }

    public FieldScanner(Class type, DefaultType access) throws Exception {
        this(type, access, true);
    }

    public FieldScanner(Class type, DefaultType access, boolean required) throws Exception {
        this.factory = new AnnotationFactory(required);
        this.hierarchy = new Hierarchy(type);
        this.done = new ContactMap();
        this.access = access;
        scan(type);
    }

    private void scan(Class type) throws Exception {
        Iterator i$ = this.hierarchy.iterator();
        while (i$.hasNext()) {
            scan((Class) i$.next(), this.access);
        }
        i$ = this.hierarchy.iterator();
        while (i$.hasNext()) {
            scan((Class) i$.next(), type);
        }
        build();
    }

    private void scan(Class type, Class real) {
        for (Field field : type.getDeclaredFields()) {
            scan(field);
        }
    }

    private void scan(Field field) {
        for (Annotation label : field.getDeclaredAnnotations()) {
            scan(field, label);
        }
    }

    private void scan(Class type, DefaultType access) throws Exception {
        Field[] list = type.getDeclaredFields();
        if (access == DefaultType.FIELD) {
            for (Field field : list) {
                Class real = field.getType();
                if (!isStatic(field)) {
                    process(field, real);
                }
            }
        }
    }

    private void scan(Field field, Annotation label) {
        if (label instanceof Attribute) {
            process(field, label);
        }
        if (label instanceof ElementUnion) {
            process(field, label);
        }
        if (label instanceof ElementListUnion) {
            process(field, label);
        }
        if (label instanceof ElementMapUnion) {
            process(field, label);
        }
        if (label instanceof ElementList) {
            process(field, label);
        }
        if (label instanceof ElementArray) {
            process(field, label);
        }
        if (label instanceof ElementMap) {
            process(field, label);
        }
        if (label instanceof Element) {
            process(field, label);
        }
        if (label instanceof Transient) {
            remove(field, label);
        }
        if (label instanceof Version) {
            process(field, label);
        }
        if (label instanceof Text) {
            process(field, label);
        }
    }

    private void process(Field field, Class type) throws Exception {
        Annotation label = this.factory.getInstance(type);
        if (label != null) {
            process(field, label);
        }
    }

    private void process(Field field, Annotation label) {
        Contact contact = new FieldContact(field, label);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        this.done.put(field, contact);
    }

    private void remove(Field field, Annotation label) {
        this.done.remove(field);
    }

    private void build() {
        Iterator i$ = this.done.iterator();
        while (i$.hasNext()) {
            add((Contact) i$.next());
        }
    }

    private boolean isStatic(Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            return true;
        }
        return false;
    }
}
