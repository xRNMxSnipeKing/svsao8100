package org.simpleframework.xml.convert;

import org.simpleframework.xml.strategy.Value;

class Reference implements Value {
    private Object data;
    private Value value;

    public Reference(Value value, Object data) {
        this.value = value;
        this.data = data;
    }

    public int getLength() {
        return 0;
    }

    public Class getType() {
        return this.data.getClass();
    }

    public Object getValue() {
        return this.data;
    }

    public boolean isReference() {
        return true;
    }

    public void setValue(Object data) {
        if (this.value != null) {
            this.value.setValue(data);
        }
        this.data = data;
    }
}
