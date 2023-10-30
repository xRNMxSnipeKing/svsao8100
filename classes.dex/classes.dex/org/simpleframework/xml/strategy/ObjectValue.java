package org.simpleframework.xml.strategy;

class ObjectValue implements Value {
    private Class type;
    private Object value;

    public ObjectValue(Class type) {
        this.type = type;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class getType() {
        return this.type;
    }

    public int getLength() {
        return 0;
    }

    public boolean isReference() {
        return false;
    }
}
