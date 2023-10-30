package org.simpleframework.xml.strategy;

class ArrayValue implements Value {
    private int size;
    private Class type;
    private Object value;

    public ArrayValue(Class type, int size) {
        this.type = type;
        this.size = size;
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
        return this.size;
    }

    public boolean isReference() {
        return false;
    }
}
