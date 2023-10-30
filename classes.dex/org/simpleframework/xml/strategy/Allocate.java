package org.simpleframework.xml.strategy;

import java.util.Map;

class Allocate implements Value {
    private String key;
    private Map map;
    private Value value;

    public Allocate(Value value, Map map, String key) {
        this.value = value;
        this.map = map;
        this.key = key;
    }

    public Object getValue() {
        return this.map.get(this.key);
    }

    public void setValue(Object object) {
        if (this.key != null) {
            this.map.put(this.key, object);
        }
        this.value.setValue(object);
    }

    public Class getType() {
        return this.value.getType();
    }

    public int getLength() {
        return this.value.getLength();
    }

    public boolean isReference() {
        return false;
    }
}
