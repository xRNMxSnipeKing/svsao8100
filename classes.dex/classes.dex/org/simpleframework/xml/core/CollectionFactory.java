package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.InputNode;

class CollectionFactory extends Factory {
    public CollectionFactory(Context context, Type type) {
        super(context, type);
    }

    public Object getInstance() throws Exception {
        Class expect = getType();
        Class real = expect;
        if (!Factory.isInstantiable(real)) {
            real = getConversion(expect);
        }
        if (isCollection(real)) {
            return real.newInstance();
        }
        throw new InstantiationException("Invalid collection %s for %s", expect, this.type);
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
        if (isCollection(expect)) {
            return this.context.getInstance(expect);
        }
        throw new InstantiationException("Invalid collection %s for %s", expect, this.type);
    }

    public Instance getInstance(Value value) throws Exception {
        Class expect = value.getType();
        if (!Factory.isInstantiable(expect)) {
            expect = getConversion(expect);
        }
        if (isCollection(expect)) {
            return new ConversionInstance(this.context, value, expect);
        }
        throw new InstantiationException("Invalid collection %s for %s", expect, this.type);
    }

    public Class getConversion(Class require) throws Exception {
        if (require.isAssignableFrom(ArrayList.class)) {
            return ArrayList.class;
        }
        if (require.isAssignableFrom(HashSet.class)) {
            return HashSet.class;
        }
        if (require.isAssignableFrom(TreeSet.class)) {
            return TreeSet.class;
        }
        throw new InstantiationException("Cannot instantiate %s for %s", require, this.type);
    }

    private boolean isCollection(Class type) {
        return Collection.class.isAssignableFrom(type);
    }
}
