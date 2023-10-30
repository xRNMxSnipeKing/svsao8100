package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Text;

final class ParameterFactory {

    private static class PameterBuilder {
        public Class create;
        public Class type;

        public PameterBuilder(Class create, Class type) {
            this.create = create;
            this.type = type;
        }

        public Constructor getConstructor() throws Exception {
            return getConstructor(Constructor.class, this.type, Integer.TYPE);
        }

        private Constructor getConstructor(Class... types) throws Exception {
            return this.create.getConstructor(types);
        }
    }

    ParameterFactory() {
    }

    public static Parameter getInstance(Constructor method, Annotation label, int index) throws Exception {
        Constructor factory = getConstructor(label);
        if (!factory.isAccessible()) {
            factory.setAccessible(true);
        }
        return (Parameter) factory.newInstance(new Object[]{method, label, Integer.valueOf(index)});
    }

    private static Constructor getConstructor(Annotation label) throws Exception {
        return getBuilder(label).getConstructor();
    }

    private static PameterBuilder getBuilder(Annotation label) throws Exception {
        if (label instanceof Element) {
            return new PameterBuilder(ElementParameter.class, Element.class);
        }
        if (label instanceof ElementList) {
            return new PameterBuilder(ElementListParameter.class, ElementList.class);
        }
        if (label instanceof ElementArray) {
            return new PameterBuilder(ElementArrayParameter.class, ElementArray.class);
        }
        if (label instanceof ElementMap) {
            return new PameterBuilder(ElementMapParameter.class, ElementMap.class);
        }
        if (label instanceof Attribute) {
            return new PameterBuilder(AttributeParameter.class, Attribute.class);
        }
        if (label instanceof Text) {
            return new PameterBuilder(TextParameter.class, Text.class);
        }
        throw new PersistenceException("Annotation %s not supported", label);
    }
}
