package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.InputNode;

class ObjectFactory extends PrimitiveFactory {
    public ObjectFactory(Context context, Type type, Class override) {
        super(context, type, override);
    }

    public Instance getInstance(InputNode node) throws Exception {
        Value value = getOverride(node);
        Class expect = getType();
        if (value != null) {
            return new ObjectInstance(this.context, value);
        }
        if (Factory.isInstantiable(expect)) {
            return this.context.getInstance(expect);
        }
        throw new InstantiationException("Cannot instantiate %s for %s", expect, this.type);
    }
}
