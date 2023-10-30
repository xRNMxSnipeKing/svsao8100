package org.simpleframework.xml.core;

import java.util.LinkedList;

class Hierarchy extends LinkedList<Class> {
    public Hierarchy(Class type) {
        scan(type);
    }

    private void scan(Class type) {
        while (type != null) {
            addFirst(type);
            type = type.getSuperclass();
        }
        remove(Object.class);
    }
}
