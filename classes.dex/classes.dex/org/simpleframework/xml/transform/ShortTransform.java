package org.simpleframework.xml.transform;

class ShortTransform implements Transform<Short> {
    ShortTransform() {
    }

    public Short read(String value) {
        return Short.valueOf(value);
    }

    public String write(Short value) {
        return value.toString();
    }
}
