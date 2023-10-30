package org.simpleframework.xml.transform;

import java.lang.reflect.Constructor;
import java.util.Date;

class DateFactory<T extends Date> {
    private final Constructor<T> factory;

    public DateFactory(Class<T> type) throws Exception {
        this(type, Long.TYPE);
    }

    public DateFactory(Class<T> type, Class... list) throws Exception {
        this.factory = type.getDeclaredConstructor(list);
    }

    public T getInstance(Object... list) throws Exception {
        return (Date) this.factory.newInstance(list);
    }
}
