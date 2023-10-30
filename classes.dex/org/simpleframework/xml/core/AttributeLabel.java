package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.strategy.Type;

class AttributeLabel implements Label {
    private Decorator decorator;
    private Introspector detail;
    private String empty;
    private Attribute label;
    private String name;
    private Class type;

    public AttributeLabel(Contact contact, Attribute label) {
        this.detail = new Introspector(contact, this);
        this.decorator = new Qualifier(contact);
        this.type = contact.getType();
        this.empty = label.empty();
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
        String ignore = getEmpty(context);
        Type type = getContact();
        if (context.isPrimitive(type)) {
            return new Primitive(context, type, ignore);
        }
        throw new AttributeException("Cannot use %s to represent %s", this.label, type);
    }

    public String getEmpty(Context context) {
        if (this.detail.isEmpty(this.empty)) {
            return null;
        }
        return this.empty;
    }

    public String getName(Context context) throws Exception {
        return context.getStyle().getAttribute(this.detail.getName());
    }

    public String getName() throws Exception {
        return this.detail.getName();
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public String getPath() {
        return this.detail.getPath();
    }

    public String getOverride() {
        return this.name;
    }

    public Contact getContact() {
        return this.detail.getContact();
    }

    public Class getType() {
        return this.type;
    }

    public String getEntry() {
        return null;
    }

    public Type getDependent() {
        return null;
    }

    public boolean isAttribute() {
        return true;
    }

    public boolean isRequired() {
        return this.label.required();
    }

    public boolean isCollection() {
        return false;
    }

    public boolean isData() {
        return false;
    }

    public boolean isInline() {
        return false;
    }

    public String toString() {
        return this.detail.toString();
    }
}
