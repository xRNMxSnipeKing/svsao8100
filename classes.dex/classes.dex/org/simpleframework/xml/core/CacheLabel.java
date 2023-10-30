package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Set;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Style;

class CacheLabel implements Label {
    private final Annotation annotation;
    private final boolean attribute;
    private final boolean collection;
    private final Contact contact;
    private final boolean data;
    private final Decorator decorator;
    private final Type depend;
    private final String entry;
    private final boolean inline;
    private final Label label;
    private final String name;
    private final String override;
    private final String path;
    private final boolean required;
    private final Class type;

    public CacheLabel(Label label) throws Exception {
        this.annotation = label.getAnnotation();
        this.decorator = label.getDecorator();
        this.attribute = label.isAttribute();
        this.collection = label.isCollection();
        this.contact = label.getContact();
        this.depend = label.getDependent();
        this.required = label.isRequired();
        this.override = label.getOverride();
        this.inline = label.isInline();
        this.path = label.getPath();
        this.type = label.getType();
        this.name = label.getName();
        this.entry = label.getEntry();
        this.data = label.isData();
        this.label = label;
    }

    public Type getType(Class type) throws Exception {
        return this.label.getType(type);
    }

    public Label getLabel(Class type) throws Exception {
        return this.label.getLabel(type);
    }

    public Set<String> getUnion() throws Exception {
        return this.label.getUnion();
    }

    public Set<String> getUnion(Context context) throws Exception {
        return this.label.getUnion(context);
    }

    public Annotation getAnnotation() {
        return this.annotation;
    }

    public Contact getContact() {
        return this.contact;
    }

    public Decorator getDecorator() throws Exception {
        return this.decorator;
    }

    public Converter getConverter(Context context) throws Exception {
        return this.label.getConverter(context);
    }

    public String getName(Context context) throws Exception {
        Style style = context.getStyle();
        if (this.attribute) {
            return style.getAttribute(this.name);
        }
        return style.getElement(this.name);
    }

    public Object getEmpty(Context context) throws Exception {
        return this.label.getEmpty(context);
    }

    public Type getDependent() throws Exception {
        return this.depend;
    }

    public String getEntry() throws Exception {
        return this.entry;
    }

    public String getName() throws Exception {
        return this.name;
    }

    public String getPath() {
        return this.path;
    }

    public String getOverride() {
        return this.override;
    }

    public Class getType() {
        return this.type;
    }

    public boolean isData() {
        return this.data;
    }

    public boolean isInline() {
        return this.inline;
    }

    public boolean isAttribute() {
        return this.attribute;
    }

    public boolean isCollection() {
        return this.collection;
    }

    public boolean isRequired() {
        return this.required;
    }

    public String toString() {
        return this.label.toString();
    }
}
