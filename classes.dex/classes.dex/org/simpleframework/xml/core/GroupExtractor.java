package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

class GroupExtractor implements Group {
    private final LabelMap elements = new LabelMap();
    private final ExtractorFactory factory;
    private final Annotation label;
    private final Registry registry = new Registry(this.elements);

    private static class Registry extends LinkedHashMap<Class, Label> implements Iterable<Label> {
        private final LabelMap elements;

        public Registry(LabelMap elements) {
            this.elements = elements;
        }

        public Iterator<Label> iterator() {
            return values().iterator();
        }

        public void register(String name, Label label) {
            this.elements.put(name, label);
        }

        public void register(Class type, Label label) {
            if (!containsKey(type)) {
                put(type, label);
            }
        }
    }

    public GroupExtractor(Contact contact, Annotation label) throws Exception {
        this.factory = new ExtractorFactory(contact, label);
        this.label = label;
        extract();
    }

    public Set<String> getNames() throws Exception {
        return this.elements.keySet();
    }

    public Set<String> getNames(Context context) throws Exception {
        return getElements(context).keySet();
    }

    public LabelMap getElements(Context context) throws Exception {
        return this.elements.build(context);
    }

    public Label getLabel(Class type) {
        return (Label) this.registry.get(type);
    }

    public boolean isValid(Class type) {
        return this.registry.containsKey(type);
    }

    private void extract() throws Exception {
        Extractor extractor = this.factory.getInstance();
        if (extractor != null) {
            extract(extractor);
        }
    }

    private void extract(Extractor extractor) throws Exception {
        for (Annotation label : extractor.getAnnotations()) {
            extract(extractor, label);
        }
    }

    private void extract(Extractor extractor, Annotation value) throws Exception {
        Label label = extractor.getLabel(value);
        Class type = extractor.getType(value);
        String name = label.getName();
        if (this.registry != null) {
            this.registry.register(name, label);
            this.registry.register(type, label);
        }
    }

    public String toString() {
        return this.label.toString();
    }
}
