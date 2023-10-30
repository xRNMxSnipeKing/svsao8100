package org.simpleframework.xml.core;

import java.lang.reflect.Array;
import org.simpleframework.xml.strategy.Value;

class ArrayInstance implements Instance {
    private final int length;
    private final Class type;
    private final Value value;

    public ArrayInstance(Value value) {
        this.length = value.getLength();
        this.type = value.getType();
        this.value = value;
    }

    public Object getInstance() throws Exception {
        if (this.value.isReference()) {
            return this.value.getValue();
        }
        Object array = Array.newInstance(this.type, this.length);
        if (this.value == null) {
            return array;
        }
        this.value.setValue(array);
        return array;
    }

    public Object setInstance(Object array) {
        if (this.value != null) {
            this.value.setValue(array);
        }
        return array;
    }

    public Class getType() {
        return this.type;
    }

    public boolean isReference() {
        return this.value.isReference();
    }
}
