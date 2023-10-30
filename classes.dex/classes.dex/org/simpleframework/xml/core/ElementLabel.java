package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.strategy.Type;

class ElementLabel implements Label {
    private Decorator decorator;
    private Introspector detail;
    private Element label;
    private String name;
    private Class override;
    private Class type;

    public ElementLabel(Contact contact, Element label) {
        this.detail = new Introspector(contact, this);
        this.decorator = new Qualifier(contact);
        this.type = contact.getType();
        this.override = label.type();
        this.name = label.name();
        this.label = label;
    }

    public Type getType(Class type) {
        Type contact = getContact();
        return this.override == Void.TYPE ? contact : new OverrideType(contact, this.override);
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
        Type type = getContact();
        if (context.isPrimitive(type)) {
            return new Primitive(context, type);
        }
        if (this.override == Void.TYPE) {
            return new Composite(context, type);
        }
        return new Composite(context, type, this.override);
    }

    public String getName(Context context) throws Exception {
        return context.getStyle().getElement(this.detail.getName());
    }

    public Object getEmpty(Context context) {
        return null;
    }

    public String getName() throws Exception {
        return this.detail.getName();
    }

    public Annotation getAnnotation() {
        return this.label;
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

    public Class getType() {
        if (this.override == Void.TYPE) {
            return this.type;
        }
        return this.override;
    }

    public String getEntry() {
        return null;
    }

    public Type getDependent() {
        return null;
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
