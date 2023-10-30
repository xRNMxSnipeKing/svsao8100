package org.simpleframework.xml.core;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.strategy.Type;

class Entry {
    private static final String DEFAULT_NAME = "entry";
    private boolean attribute;
    private Contact contact;
    private String entry;
    private String key;
    private Class keyType;
    private ElementMap label;
    private String value;
    private Class valueType;

    public Entry(Contact contact, ElementMap label) {
        this.attribute = label.attribute();
        this.entry = label.entry();
        this.value = label.value();
        this.key = label.key();
        this.contact = contact;
        this.label = label;
    }

    public Contact getContact() {
        return this.contact;
    }

    public boolean isAttribute() {
        return this.attribute;
    }

    public boolean isInline() throws Exception {
        return isAttribute();
    }

    public Converter getKey(Context context) throws Exception {
        Type type = getKeyType();
        if (context.isPrimitive(type)) {
            return new PrimitiveKey(context, this, type);
        }
        return new CompositeKey(context, this, type);
    }

    public Converter getValue(Context context) throws Exception {
        Type type = getValueType();
        if (context.isPrimitive(type)) {
            return new PrimitiveValue(context, this, type);
        }
        return new CompositeValue(context, this, type);
    }

    protected Type getKeyType() throws Exception {
        if (this.keyType == null) {
            this.keyType = this.label.keyType();
            if (this.keyType == Void.TYPE) {
                this.keyType = getDependent(0);
            }
        }
        return new ClassType(this.keyType);
    }

    protected Type getValueType() throws Exception {
        if (this.valueType == null) {
            this.valueType = this.label.valueType();
            if (this.valueType == Void.TYPE) {
                this.valueType = getDependent(1);
            }
        }
        return new ClassType(this.valueType);
    }

    private Class getDependent(int index) throws Exception {
        Class[] list = this.contact.getDependents();
        if (list.length >= index) {
            return list[index];
        }
        throw new PersistenceException("Could not find type for %s at index %s", this.contact, Integer.valueOf(index));
    }

    public String getKey() throws Exception {
        if (this.key == null) {
            return this.key;
        }
        if (isEmpty(this.key)) {
            this.key = null;
        }
        return this.key;
    }

    public String getValue() throws Exception {
        if (this.value == null) {
            return this.value;
        }
        if (isEmpty(this.value)) {
            this.value = null;
        }
        return this.value;
    }

    public String getEntry() throws Exception {
        if (this.entry == null) {
            return this.entry;
        }
        if (isEmpty(this.entry)) {
            this.entry = DEFAULT_NAME;
        }
        return this.entry;
    }

    private boolean isEmpty(String value) {
        return value.length() == 0;
    }

    public String toString() {
        return String.format("%s on %s", new Object[]{this.label, this.contact});
    }
}
