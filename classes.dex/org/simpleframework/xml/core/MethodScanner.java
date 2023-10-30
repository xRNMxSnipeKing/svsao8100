package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

class MethodScanner extends ContactList {
    private final DefaultType access;
    private final MethodPartFactory factory;
    private final Hierarchy hierarchy;
    private final PartMap read;
    private final Class type;
    private final PartMap write;

    private class PartMap extends LinkedHashMap<String, MethodPart> implements Iterable<String> {
        private PartMap() {
        }

        public Iterator<String> iterator() {
            return keySet().iterator();
        }

        public MethodPart take(String name) {
            return (MethodPart) remove(name);
        }
    }

    public MethodScanner(Class type) throws Exception {
        this(type, null);
    }

    public MethodScanner(Class type, DefaultType access) throws Exception {
        this(type, access, true);
    }

    public MethodScanner(Class type, DefaultType access, boolean required) throws Exception {
        this.factory = new MethodPartFactory(required);
        this.hierarchy = new Hierarchy(type);
        this.write = new PartMap();
        this.read = new PartMap();
        this.access = access;
        this.type = type;
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
        validate();
    }

    private void scan(Class type, Class real) throws Exception {
        for (Method method : type.getDeclaredMethods()) {
            scan(method);
        }
    }

    private void scan(Method method) throws Exception {
        for (Annotation label : method.getDeclaredAnnotations()) {
            scan(method, label);
        }
    }

    private void scan(Class type, DefaultType access) throws Exception {
        Method[] list = type.getDeclaredMethods();
        if (access == DefaultType.PROPERTY) {
            for (Method method : list) {
                if (this.factory.getType(method) != null) {
                    process(method);
                }
            }
        }
    }

    private void scan(Method method, Annotation label) throws Exception {
        if (label instanceof Attribute) {
            process(method, label);
        }
        if (label instanceof ElementUnion) {
            process(method, label);
        }
        if (label instanceof ElementListUnion) {
            process(method, label);
        }
        if (label instanceof ElementMapUnion) {
            process(method, label);
        }
        if (label instanceof ElementList) {
            process(method, label);
        }
        if (label instanceof ElementArray) {
            process(method, label);
        }
        if (label instanceof ElementMap) {
            process(method, label);
        }
        if (label instanceof Element) {
            process(method, label);
        }
        if (label instanceof Transient) {
            remove(method, label);
        }
        if (label instanceof Version) {
            process(method, label);
        }
        if (label instanceof Text) {
            process(method, label);
        }
    }

    private void process(Method method, Annotation label) throws Exception {
        MethodPart part = this.factory.getInstance(method, label);
        MethodType type = part.getMethodType();
        if (type == MethodType.GET) {
            process(part, this.read);
        }
        if (type == MethodType.IS) {
            process(part, this.read);
        }
        if (type == MethodType.SET) {
            process(part, this.write);
        }
    }

    private void process(Method method) throws Exception {
        MethodPart part = this.factory.getInstance(method);
        MethodType type = part.getMethodType();
        if (type == MethodType.GET) {
            process(part, this.read);
        }
        if (type == MethodType.IS) {
            process(part, this.read);
        }
        if (type == MethodType.SET) {
            process(part, this.write);
        }
    }

    private void process(MethodPart method, PartMap map) {
        String name = method.getName();
        if (name != null) {
            map.put(name, method);
        }
    }

    private void remove(Method method, Annotation label) throws Exception {
        MethodPart part = this.factory.getInstance(method, label);
        MethodType type = part.getMethodType();
        if (type == MethodType.GET) {
            remove(part, this.read);
        }
        if (type == MethodType.IS) {
            remove(part, this.read);
        }
        if (type == MethodType.SET) {
            remove(part, this.write);
        }
    }

    private void remove(MethodPart part, PartMap map) throws Exception {
        String name = part.getName();
        if (name != null) {
            map.remove(name);
        }
    }

    private void build() throws Exception {
        Iterator i$ = this.read.iterator();
        while (i$.hasNext()) {
            String name = (String) i$.next();
            MethodPart part = (MethodPart) this.read.get(name);
            if (part != null) {
                build(part, name);
            }
        }
    }

    private void build(MethodPart read, String name) throws Exception {
        MethodPart match = this.write.take(name);
        if (match != null) {
            build(read, match);
        } else {
            build(read);
        }
    }

    private void build(MethodPart read) throws Exception {
        add(new MethodContact(read));
    }

    private void build(MethodPart read, MethodPart write) throws Exception {
        Annotation label = read.getAnnotation();
        String name = read.getName();
        if (write.getAnnotation().equals(label)) {
            if (read.getType() != write.getType()) {
                throw new MethodException("Method types do not match for %s in %s", name, read.getType());
            } else {
                add(new MethodContact(read, write));
                return;
            }
        }
        throw new MethodException("Annotations do not match for '%s' in %s", name, this.type);
    }

    private void validate() throws Exception {
        Iterator i$ = this.write.iterator();
        while (i$.hasNext()) {
            String name = (String) i$.next();
            MethodPart part = (MethodPart) this.write.get(name);
            if (part != null) {
                validate(part, name);
            }
        }
    }

    private void validate(MethodPart write, String name) throws Exception {
        MethodPart match = this.read.take(name);
        Method method = write.getMethod();
        if (match == null) {
            throw new MethodException("No matching get method for %s in %s", method, this.type);
        }
    }
}
