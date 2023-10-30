package org.simpleframework.xml.transform;

class LongTransform implements Transform<Long> {
    LongTransform() {
    }

    public Long read(String value) {
        return Long.valueOf(value);
    }

    public String write(Long value) {
        return value.toString();
    }
}
