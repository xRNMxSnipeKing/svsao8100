package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Text;

class ConstructorScanner {
    private List<Initializer> list = new ArrayList();
    private Initializer primary;
    private Signature signature;
    private Class type;

    public ConstructorScanner(Class type) throws Exception {
        this.signature = new Signature(type);
        this.type = type;
        scan(type);
    }

    public Creator getCreator() {
        return new ClassCreator(this.list, this.signature, this.primary);
    }

    private void scan(Class type) throws Exception {
        Constructor[] array = type.getDeclaredConstructors();
        if (isInstantiable(type)) {
            for (Constructor factory : array) {
                Signature index = new Signature(type);
                if (!type.isPrimitive()) {
                    scan(factory, index);
                }
            }
            return;
        }
        throw new ConstructorException("Can not construct inner %s", type);
    }

    private void scan(Constructor factory, Signature map) throws Exception {
        Annotation[][] labels = factory.getParameterAnnotations();
        Class[] types = factory.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            for (Annotation process : labels[i]) {
                Parameter value = process(factory, process, i);
                if (value != null) {
                    String name = value.getName();
                    if (map.containsKey(name)) {
                        throw new PersistenceException("Parameter '%s' is a duplicate in %s", name, factory);
                    } else {
                        this.signature.put(name, value);
                        map.put(name, value);
                    }
                }
            }
        }
        if (types.length == map.size()) {
            build(factory, map);
        }
    }

    private void build(Constructor factory, Signature signature) throws Exception {
        Initializer initializer = new Initializer(factory, signature);
        if (initializer.isDefault()) {
            this.primary = initializer;
        }
        this.list.add(initializer);
    }

    private Parameter process(Constructor factory, Annotation label, int ordinal) throws Exception {
        if (label instanceof Attribute) {
            return create(factory, label, ordinal);
        }
        if (label instanceof ElementList) {
            return create(factory, label, ordinal);
        }
        if (label instanceof ElementArray) {
            return create(factory, label, ordinal);
        }
        if (label instanceof ElementMap) {
            return create(factory, label, ordinal);
        }
        if (label instanceof Element) {
            return create(factory, label, ordinal);
        }
        if (label instanceof Text) {
            return create(factory, label, ordinal);
        }
        return null;
    }

    private Parameter create(Constructor factory, Annotation label, int ordinal) throws Exception {
        Parameter value = ParameterFactory.getInstance(factory, label, ordinal);
        String name = value.getName();
        if (this.signature.containsKey(name)) {
            validate(value, name);
        }
        return value;
    }

    private void validate(Parameter parameter, String name) throws Exception {
        Parameter other = (Parameter) this.signature.get(name);
        if (!parameter.getAnnotation().equals(other.getAnnotation())) {
            throw new MethodException("Annotations do not match for '%s' in %s", name, this.type);
        } else if (other.getType() != parameter.getType()) {
            throw new MethodException("Method types do not match for '%s' in %s", name, this.type);
        }
    }

    private boolean isInstantiable(Class type) {
        if (!Modifier.isStatic(type.getModifiers()) && type.isMemberClass()) {
            return false;
        }
        return true;
    }
}
