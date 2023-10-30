package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Set;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementMapUnion;
import org.simpleframework.xml.strategy.Type;

class ElementMapUnionLabel implements Label {
    private final Contact contact;
    private final GroupExtractor extractor;
    private final Label label;

    public ElementMapUnionLabel(Contact contact, ElementMapUnion union, ElementMap element) throws Exception {
        this.extractor = new GroupExtractor(contact, union);
        this.label = new ElementMapLabel(contact, element);
        this.contact = contact;
    }

    public Contact getContact() {
        return this.contact;
    }

    public Annotation getAnnotation() {
        return this.label.getAnnotation();
    }

    public Type getType(Class type) {
        return getContact();
    }

    public Label getLabel(Class type) {
        return this;
    }

    public Converter getConverter(Context context) throws Exception {
        Type type = getContact();
        if (type != null) {
            return new CompositeMapUnion(context, this.extractor, type);
        }
        throw new UnionException("Union %s was not declared on a field or method", this.label);
    }

    public Set<String> getUnion() throws Exception {
        return this.extractor.getNames();
    }

    public Set<String> getUnion(Context context) throws Exception {
        return this.extractor.getNames(context);
    }

    public Object getEmpty(Context context) throws Exception {
        return this.label.getEmpty(context);
    }

    public String getName(Context context) throws Exception {
        return this.label.getName(context);
    }

    public Decorator getDecorator() throws Exception {
        return this.label.getDecorator();
    }

    public Type getDependent() throws Exception {
        return this.label.getDependent();
    }

    public String getEntry() throws Exception {
        return this.label.getEntry();
    }

    public String getName() throws Exception {
        return this.label.getName();
    }

    public String getOverride() {
        return this.label.getOverride();
    }

    public String getPath() {
        return this.label.getPath();
    }

    public Class getType() {
        return this.label.getType();
    }

    public boolean isAttribute() {
        return this.label.isAttribute();
    }

    public boolean isCollection() {
        return this.label.isCollection();
    }

    public boolean isData() {
        return this.label.isData();
    }

    public boolean isInline() {
        return this.label.isInline();
    }

    public boolean isRequired() {
        return this.label.isRequired();
    }

    public String toString() {
        return this.label.toString();
    }
}
