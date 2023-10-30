package org.simpleframework.xml.convert;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.util.WeakCache;

class ScannerBuilder extends WeakCache<Class, Scanner> {

    private static class Entry extends WeakCache<Class, Annotation> implements Scanner {
        private final Class root;

        public Entry(Class root) {
            this.root = root;
        }

        public <T extends Annotation> T scan(Class<T> type) {
            if (!contains(type)) {
                T value = find(type);
                if (type != null) {
                    cache(type, value);
                }
            }
            return (Annotation) fetch(type);
        }

        private <T extends Annotation> T find(Class<T> label) {
            for (Class<?> type = this.root; type != null; type = type.getSuperclass()) {
                T value = type.getAnnotation(label);
                if (value != null) {
                    return value;
                }
            }
            return null;
        }
    }

    public Scanner build(Class<?> type) {
        Scanner scanner = (Scanner) fetch(type);
        if (scanner != null) {
            return scanner;
        }
        scanner = new Entry(type);
        cache(type, scanner);
        return scanner;
    }
}
