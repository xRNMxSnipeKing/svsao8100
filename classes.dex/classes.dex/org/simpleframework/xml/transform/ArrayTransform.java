package org.simpleframework.xml.transform;

import java.lang.reflect.Array;

class ArrayTransform implements Transform {
    private final Transform delegate;
    private final Class entry;
    private final StringArrayTransform split = new StringArrayTransform();

    public ArrayTransform(Transform delegate, Class entry) {
        this.delegate = delegate;
        this.entry = entry;
    }

    public Object read(String value) throws Exception {
        String[] list = this.split.read(value);
        return read(list, list.length);
    }

    private Object read(String[] list, int length) throws Exception {
        Object array = Array.newInstance(this.entry, length);
        for (int i = 0; i < length; i++) {
            Object item = this.delegate.read(list[i]);
            if (item != null) {
                Array.set(array, i, item);
            }
        }
        return array;
    }

    public String write(Object value) throws Exception {
        return write(value, Array.getLength(value));
    }

    private String write(Object value, int length) throws Exception {
        String[] list = new String[length];
        for (int i = 0; i < length; i++) {
            Object entry = Array.get(value, i);
            if (entry != null) {
                list[i] = this.delegate.write(entry);
            }
        }
        return this.split.write(list);
    }
}
