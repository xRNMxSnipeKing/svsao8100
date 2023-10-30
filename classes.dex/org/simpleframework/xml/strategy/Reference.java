package org.simpleframework.xml.strategy;

class Reference implements Value {
    private Class type;
    private Object value;

    public Reference(Object value, Class type) {
        this.value = value;
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
        return true;
    }
}
