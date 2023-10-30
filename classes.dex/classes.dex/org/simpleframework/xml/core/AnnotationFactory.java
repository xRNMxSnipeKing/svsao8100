package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

class AnnotationFactory {
    private final boolean required;

    public AnnotationFactory() {
        this(true);
    }

    public AnnotationFactory(boolean required) {
        this.required = required;
    }

    public Annotation getInstance(Class type) throws Exception {
        ClassLoader loader = getClassLoader();
        if (Map.class.isAssignableFrom(type)) {
            return getInstance(loader, ElementMap.class);
        }
        if (Collection.class.isAssignableFrom(type)) {
            return getInstance(loader, ElementList.class);
        }
        if (type.isArray()) {
            return getInstance(loader, ElementArray.class);
        }
        return getInstance(loader, Element.class);
    }

    private Annotation getInstance(ClassLoader loader, Class label) throws Exception {
        return (Annotation) Proxy.newProxyInstance(loader, new Class[]{label}, new AnnotationHandler(label, this.required));
    }

    private ClassLoader getClassLoader() throws Exception {
        return AnnotationFactory.class.getClassLoader();
    }
}
