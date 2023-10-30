package org.simpleframework.xml.strategy;

class Loader {
    Loader() {
    }

    public Class load(String type) throws Exception {
        ClassLoader loader = getClassLoader();
        if (loader == null) {
            loader = getCallerClassLoader();
        }
        return loader.loadClass(type);
    }

    private static ClassLoader getCallerClassLoader() throws Exception {
        return Loader.class.getClassLoader();
    }

    private static ClassLoader getClassLoader() throws Exception {
        return Thread.currentThread().getContextClassLoader();
    }
}
