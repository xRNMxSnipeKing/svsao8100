package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Value;

class ObjectInstance implements Instance {
    private final Context context;
    private final Class type;
    private final Value value;

    public ObjectInstance(Context context, Value value) {
        this.type = value.getType();
        this.context = context;
        this.value = value;
    }

    public Object getInstance() throws Exception {
        if (this.value.isReference()) {
            return this.value.getValue();
        }
        Object object = getInstance(this.type);
        if (this.value == null) {
            return object;
        }
        this.value.setValue(object);
        return object;
    }

    public Object getInstance(Class type) throws Exception {
        return this.context.getInstance(type).getInstance();
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
