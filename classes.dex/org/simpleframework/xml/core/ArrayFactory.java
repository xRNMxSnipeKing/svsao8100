package org.simpleframework.xml.core;

import java.lang.reflect.Array;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.Position;

class ArrayFactory extends Factory {
    public ArrayFactory(Context context, Type type) {
        super(context, type);
    }

    public Object getInstance() throws Exception {
        Class type = getComponentType();
        if (type != null) {
            return Array.newInstance(type, 0);
        }
        return null;
    }

    public Instance getInstance(InputNode node) throws Exception {
        Position line = node.getPosition();
        Value value = getOverride(node);
        if (value != null) {
            return getInstance(value, value.getType());
        }
        throw new ElementException("Array length required for %s at %s", this.type, line);
    }

    private Instance getInstance(Value value, Class entry) throws Exception {
        if (getComponentType().isAssignableFrom(entry)) {
            return new ArrayInstance(value);
        }
        throw new InstantiationException("Array of type %s cannot hold %s for %s", getComponentType(), entry, this.type);
    }

    private Class getComponentType() throws Exception {
        Class expect = getType();
        if (expect.isArray()) {
            return expect.getComponentType();
        }
        throw new InstantiationException("The %s not an array for %s", expect, this.type);
    }
}
