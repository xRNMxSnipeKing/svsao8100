package org.simpleframework.xml.core;

import java.lang.reflect.Constructor;
import org.simpleframework.xml.strategy.Value;

class Instantiator {
    private final ConstructorCache cache = new ConstructorCache();

    public Instance getInstance(Value value) {
        return new ValueInstance(this, value);
    }

    public Instance getInstance(Class type) {
        return new ClassInstance(this, type);
    }

    public Object getObject(Class type) throws Exception {
        Constructor method = (Constructor) this.cache.get(type);
        if (method == null) {
            method = type.getDeclaredConstructor(new Class[0]);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            this.cache.put(type, method);
        }
        return method.newInstance(new Object[0]);
    }
}
