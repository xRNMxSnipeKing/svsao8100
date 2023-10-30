package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.InputNode;

class PrimitiveFactory extends Factory {
    public PrimitiveFactory(Context context, Type type) {
        super(context, type);
    }

    public PrimitiveFactory(Context context, Type type, Class override) {
        super(context, type, override);
    }

    public Instance getInstance(InputNode node) throws Exception {
        Value value = getOverride(node);
        Class type = getType();
        if (value == null) {
            return this.context.getInstance(type);
        }
        return new ObjectInstance(this.context, value);
    }

    public Object getInstance(String text, Class type) throws Exception {
        return this.support.read(text, type);
    }

    public String getText(Object source) throws Exception {
        Class type = source.getClass();
        if (type.isEnum()) {
            return this.support.write(source, type);
        }
        return this.support.write(source, type);
    }
}
