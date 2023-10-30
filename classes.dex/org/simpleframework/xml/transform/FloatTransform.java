package org.simpleframework.xml.transform;

class FloatTransform implements Transform<Float> {
    FloatTransform() {
    }

    public Float read(String value) {
        return Float.valueOf(value);
    }

    public String write(Float value) {
        return value.toString();
    }
}
