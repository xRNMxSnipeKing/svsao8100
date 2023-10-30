package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.strategy.Type;

class TextLabel implements Label {
    private Contact contact;
    private Introspector detail;
    private String empty;
    private Text label;
    private Class type;

    public TextLabel(Contact contact, Text label) {
        this.detail = new Introspector(contact, this);
        this.type = contact.getType();
        this.empty = label.empty();
        this.contact = contact;
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
        return null;
    }

    public Converter getConverter(Context context) throws Exception {
        String ignore = getEmpty(context);
        Type type = getContact();
        if (context.isPrimitive(type)) {
            return new Primitive(context, type, ignore);
        }
        throw new TextException("Cannot use %s to represent %s", type, this.label);
    }

    public String getName(Context context) {
        return getName();
    }

    public String getEmpty(Context context) {
        if (this.detail.isEmpty(this.empty)) {
            return null;
        }
        return this.empty;
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public Contact getContact() {
        return this.contact;
    }

    public String getPath() {
        return null;
    }

    public String getName() {
        return "";
    }

    public String getOverride() {
        return this.contact.toString();
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
        return true;
    }

    public String toString() {
        return this.detail.toString();
    }
}
