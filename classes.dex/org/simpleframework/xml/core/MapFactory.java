package org.simpleframework.xml.core;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.InputNode;

class MapFactory extends Factory {
    public MapFactory(Context context, Type type) {
        super(context, type);
    }

    public Object getInstance() throws Exception {
        Class expect = getType();
        Class real = expect;
        if (!Factory.isInstantiable(real)) {
            real = getConversion(expect);
        }
        if (isMap(real)) {
            return real.newInstance();
        }
        throw new InstantiationException("Invalid map %s for %s", expect, this.type);
    }

    public Instance getInstance(InputNode node) throws Exception {
        Value value = getOverride(node);
        Class expect = getType();
        if (value != null) {
            return getInstance(value);
        }
        if (!Factory.isInstantiable(expect)) {
            expect = getConversion(expect);
        }
        if (isMap(expect)) {
            return this.context.getInstance(expect);
        }
        throw new InstantiationException("Invalid map %s for %s", expect, this.type);
    }

    public Instance getInstance(Value value) throws Exception {
        Class expect = value.getType();
        if (!Factory.isInstantiable(expect)) {
            expect = getConversion(expect);
        }
        if (isMap(expect)) {
            return new ConversionInstance(this.context, value, expect);
        }
        throw new InstantiationException("Invalid map %s for %s", expect, this.type);
    }

    public Class getConversion(Class require) throws Exception {
        if (require.isAssignableFrom(HashMap.class)) {
            return HashMap.class;
        }
        if (require.isAssignableFrom(TreeMap.class)) {
            return TreeMap.class;
        }
        throw new InstantiationException("Cannot instantiate %s for %s", require, this.type);
    }

    private boolean isMap(Class type) {
        return Map.class.isAssignableFrom(type);
    }
}
