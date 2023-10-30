package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.strategy.Type;

class ElementArrayLabel implements Label {
    private Decorator decorator;
    private Introspector detail;
    private String entry;
    private ElementArray label;
    private String name;
    private Class type;

    public ElementArrayLabel(Contact contact, ElementArray label) {
        this.detail = new Introspector(contact, this);
        this.decorator = new Qualifier(contact);
        this.type = contact.getType();
        this.entry = label.entry();
        this.name = label.name();
        this.label = label;
    }

    public Type getType(Class type) {
        return getContact();
    }

    public Label getLabel(Class type) {
        return this;
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
        String entry = getEntry(context);
        Contact contact = getContact();
        if (this.type.isArray()) {
            return getConverter(context, entry);
        }
        throw new InstantiationException("Type is not an array %s for %s", this.type, contact);
    }

    private Converter getConverter(Context context, String name) throws Exception {
        Type entry = getDependent();
        Type type = getContact();
        if (context.isPrimitive(entry)) {
            return new PrimitiveArray(context, type, entry, name);
        }
        return new CompositeArray(context, type, entry, name);
    }

    private String getEntry(Context context) throws Exception {
        return context.getStyle().getElement(getEntry());
    }

    public String getName(Context context) throws Exception {
        return context.getStyle().getElement(this.detail.getName());
    }

    public Object getEmpty(Context context) throws Exception {
        Factory factory = new ArrayFactory(context, new ClassType(this.type));
        if (this.label.empty()) {
            return null;
        }
        return factory.getInstance();
    }

    public String getEntry() throws Exception {
        if (this.detail.isEmpty(this.entry)) {
            this.entry = this.detail.getEntry();
        }
        return this.entry;
    }

    public String getName() throws Exception {
        return this.detail.getName();
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public Type getDependent() {
        Class entry = this.type.getComponentType();
        if (entry == null) {
            return new ClassType(this.type);
        }
        return new ClassType(entry);
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

    public boolean isAttribute() {
        return false;
    }

    public boolean isCollection() {
        return false;
    }

    public boolean isRequired() {
        return this.label.required();
    }

    public boolean isData() {
        return this.label.data();
    }

    public boolean isInline() {
        return false;
    }

    public String toString() {
        return this.detail.toString();
    }
}
