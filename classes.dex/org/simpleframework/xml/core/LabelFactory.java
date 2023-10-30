package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementMapUnion;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.Version;

final class LabelFactory {

    private static class LabelBuilder {
        public Class entry;
        public Class label;
        public Class type;

        public LabelBuilder(Class type, Class label) {
            this(type, label, null);
        }

        public LabelBuilder(Class type, Class label, Class entry) {
            this.entry = entry;
            this.label = label;
            this.type = type;
        }

        public Constructor getConstructor() throws Exception {
            if (this.entry != null) {
                return getConstructor(this.label, this.entry);
            }
            return getConstructor(this.label);
        }

        private Constructor getConstructor(Class label) throws Exception {
            return this.type.getConstructor(new Class[]{Contact.class, label});
        }

        private Constructor getConstructor(Class label, Class entry) throws Exception {
            return this.type.getConstructor(new Class[]{Contact.class, label, entry});
        }
    }

    LabelFactory() {
    }

    public static Label getInstance(Contact contact, Annotation label) throws Exception {
        return getInstance(contact, label, null);
    }

    public static Label getInstance(Contact contact, Annotation label, Annotation entry) throws Exception {
        Label value = getLabel(contact, label, entry);
        return value == null ? value : new CacheLabel(value);
    }

    private static Label getLabel(Contact contact, Annotation label, Annotation entry) throws Exception {
        Constructor factory = getConstructor(label);
        if (entry != null) {
            return (Label) factory.newInstance(new Object[]{contact, label, entry});
        }
        return (Label) factory.newInstance(new Object[]{contact, label});
    }

    private static Constructor getConstructor(Annotation label) throws Exception {
        Constructor factory = getBuilder(label).getConstructor();
        if (!factory.isAccessible()) {
            factory.setAccessible(true);
        }
        return factory;
    }

    private static LabelBuilder getBuilder(Annotation label) throws Exception {
        if (label instanceof Element) {
            return new LabelBuilder(ElementLabel.class, Element.class);
        }
        if (label instanceof ElementList) {
            return new LabelBuilder(ElementListLabel.class, ElementList.class);
        }
        if (label instanceof ElementArray) {
            return new LabelBuilder(ElementArrayLabel.class, ElementArray.class);
        }
        if (label instanceof ElementMap) {
            return new LabelBuilder(ElementMapLabel.class, ElementMap.class);
        }
        if (label instanceof ElementUnion) {
            return new LabelBuilder(ElementUnionLabel.class, ElementUnion.class, Element.class);
        }
        if (label instanceof ElementListUnion) {
            return new LabelBuilder(ElementListUnionLabel.class, ElementListUnion.class, ElementList.class);
        }
        if (label instanceof ElementMapUnion) {
            return new LabelBuilder(ElementMapUnionLabel.class, ElementMapUnion.class, ElementMap.class);
        }
        if (label instanceof Attribute) {
            return new LabelBuilder(AttributeLabel.class, Attribute.class);
        }
        if (label instanceof Version) {
            return new LabelBuilder(VersionLabel.class, Version.class);
        }
        if (label instanceof Text) {
            return new LabelBuilder(TextLabel.class, Text.class);
        }
        throw new PersistenceException("Annotation %s not supported", label);
    }
}
