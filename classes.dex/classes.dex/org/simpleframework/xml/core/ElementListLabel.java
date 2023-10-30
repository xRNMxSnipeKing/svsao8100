package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.strategy.Type;

class ElementListLabel implements Label {
    private Decorator decorator;
    private Introspector detail;
    private String entry;
    private Class item;
    private ElementList label;
    private String name;
    private Class type;

    public ElementListLabel(Contact contact, ElementList label) {
        this.detail = new Introspector(contact, this);
        this.decorator = new Qualifier(contact);
        this.type = contact.getType();
        this.entry = label.entry();
        this.item = label.type();
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
        if (this.label.inline()) {
            return getInlineConverter(context, entry);
        }
        return getConverter(context, entry);
    }

    private Converter getConverter(Context context, String name) throws Exception {
        Type item = getDependent();
        Type type = getContact();
        if (context.isPrimitive(item)) {
            return new PrimitiveList(context, type, item, name);
        }
        return new CompositeList(context, type, item, name);
    }

    private Converter getInlineConverter(Context context, String name) throws Exception {
        Type item = getDependent();
        Type type = getContact();
        if (context.isPrimitive(item)) {
            return new PrimitiveInlineList(context, type, item, name);
        }
        return new CompositeInlineList(context, type, item, name);
    }

    public String getName(Context context) throws Exception {
        return context.getStyle().getElement(this.detail.getName());
    }

    private String getEntry(Context context) throws Exception {
        return context.getStyle().getElement(getEntry());
    }

    public Object getEmpty(Context context) throws Exception {
        Factory factory = new CollectionFactory(context, new ClassType(this.type));
        if (this.label.empty()) {
            return null;
        }
        return factory.getInstance();
    }

    public Type getDependent() throws Exception {
        Contact contact = getContact();
        if (this.item == Void.TYPE) {
            this.item = contact.getDependent();
        }
        if (this.item != null) {
            return new ClassType(this.item);
        }
        throw new ElementException("Unable to determine generic type for %s", contact);
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
