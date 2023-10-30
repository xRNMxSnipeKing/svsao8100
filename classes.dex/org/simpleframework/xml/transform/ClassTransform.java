package org.simpleframework.xml.transform;

class ClassTransform implements Transform<Class> {
    ClassTransform() {
    }

    public Class read(String target) throws Exception {
        ClassLoader loader = getClassLoader();
        if (loader == null) {
            loader = getCallerClassLoader();
        }
        return loader.loadClass(target);
    }

    public String write(Class target) throws Exception {
        return target.getName();
    }

    private ClassLoader getCallerClassLoader() {
        return getClass().getClassLoader();
    }

    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
