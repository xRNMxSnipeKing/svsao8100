package org.simpleframework.xml.convert;

class RegistryBinder {
    private final ClassCache cache = new ClassCache();
    private final ConverterFactory factory = new ConverterFactory();

    public Converter lookup(Class type) throws Exception {
        Class result = (Class) this.cache.fetch(type);
        if (result != null) {
            return create(result);
        }
        return null;
    }

    private Converter create(Class type) throws Exception {
        return this.factory.getInstance(type);
    }

    public void bind(Class type, Class converter) throws Exception {
        this.cache.cache(type, converter);
    }
}
