package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Style;

class ElementMapLabel implements Label {
    private Decorator decorator;
    private Introspector detail;
    private Entry entry;
    private Class[] items;
    private ElementMap label;
    private String name;
    private String parent;
    private Class type;

    public ElementMapLabel(Contact contact, ElementMap label) {
        this.detail = new Introspector(contact, this);
        this.decorator = new Qualifier(contact);
        this.entry = new Entry(contact, label);
        this.type = contact.getType();
        this.name = label.name();
        this.label = label;
    }

    public Label getLabel(Class type) {
        return this;
    }

    public Type getType(Class type) {
        return getContact();
    }

    public Set<String> getUnion() throws Exception {
        return Collections.emptySet();
    }

    public Set<String> getUnion(Context context) throws Exception {
        return Collections.emptySet();
    }

    public Decorator getDecorator() throws Exception {
        return this.decorator;
    }

    public Converter getConverter(Context context) throws Exception {
        Type type = getMap();
        if (this.label.inline()) {
            return new CompositeInlineMap(context, this.entry, type);
        }
        return new CompositeMap(context, this.entry, type);
    }

    public String getName(Context context) throws Exception {
        Style style = context.getStyle();
        String name = this.entry.getEntry();
        if (!this.label.inline()) {
            name = this.detail.getName();
        }
        return style.getElement(name);
    }

    public Object getEmpty(Context context) throws Exception {
        Factory factory = new MapFactory(context, new ClassType(this.type));
        if (this.label.empty()) {
            return null;
        }
        return factory.getInstance();
    }

    public Type getDependent() throws Exception {
        Contact contact = getContact();
        if (this.items == null) {
            this.items = contact.getDependents();
        }
        if (this.items == null) {
            throw new ElementException("Unable to determine type for %s", contact);
        } else if (this.items.length == 0) {
            return new ClassType(Object.class);
        } else {
            return new ClassType(this.items[0]);
        }
    }

    public String getEntry() throws Exception {
        if (this.detail.isEmpty(this.parent)) {
            this.parent = this.detail.getEntry();
        }
        return this.parent;
    }

    public String getName() throws Exception {
        if (this.label.inline()) {
            return this.entry.getEntry();
        }
        return this.detail.getName();
    }

    private Type getMap() {
        return new ClassType(this.type);
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public Class getType() {
        return this.type;
    }

    public Contact getContact() {
        return this.detail.getContact();
    }

    public String getPath() {
        return this.detail.getPath();
    }

    public String getOverride() {
        return this.name;
    }

    public boolean isData() {
        return this.label.data();
    }

    public boolean isAttribute() {
        return false;
    }

    public boolean isCollection() {
        return true;
    }

    public boolean isRequired() {
        return this.label.required();
    }

    public boolean isInline() {
        return this.label.inline();
    }

    public String toString() {
        return this.detail.toString();
    }
}
