package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Value;

class ValueInstance implements Instance {
    private final Instantiator creator;
    private final Class type;
    private final Value value;

    public ValueInstance(Instantiator creator, Value value) {
        this.type = value.getType();
        this.creator = creator;
        this.value = value;
    }

    public Object getInstance() throws Exception {
        if (this.value.isReference()) {
            return this.value.getValue();
        }
        Object object = this.creator.getObject(this.type);
        if (this.value == null) {
            return object;
        }
        this.value.setValue(object);
        return object;
    }

    public Object setInstance(Object object) {
        if (this.value != null) {
            this.value.setValue(object);
        }
        return object;
    }

    public boolean isReference() {
        return this.value.isReference();
    }

    public Class getType() {
        return this.type;
    }
}
