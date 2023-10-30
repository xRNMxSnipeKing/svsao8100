package org.simpleframework.xml.convert;

import java.lang.reflect.Constructor;

class ConverterFactory {
    private final ConverterCache cache = new ConverterCache();

    public Converter getInstance(Class type) throws Exception {
        Converter converter = (Converter) this.cache.fetch(type);
        if (converter == null) {
            return getConverter(type);
        }
        return converter;
    }

    public Converter getInstance(Convert convert) throws Exception {
        Class type = convert.value();
        if (!type.isInterface()) {
            return getInstance(type);
        }
        throw new ConvertException("Can not instantiate %s", type);
    }

    private Converter getConverter(Class type) throws Exception {
        Constructor factory = getConstructor(type);
        if (factory != null) {
            return getConverter(type, factory);
        }
        throw new ConvertException("No default constructor for %s", type);
    }

    private Converter getConverter(Class type, Constructor factory) throws Exception {
        Converter converter = (Converter) factory.newInstance(new Object[0]);
        if (converter != null) {
            this.cache.cache(type, converter);
        }
        return converter;
    }

    private Constructor getConstructor(Class type) throws Exception {
        Constructor factory = type.getDeclaredConstructor(new Class[0]);
        if (!factory.isAccessible()) {
            factory.setAccessible(true);
        }
        return factory;
    }
}
