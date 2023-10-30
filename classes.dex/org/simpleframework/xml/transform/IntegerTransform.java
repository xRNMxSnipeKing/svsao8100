package org.simpleframework.xml.transform;

class IntegerTransform implements Transform<Integer> {
    IntegerTransform() {
    }

    public Integer read(String value) {
        return Integer.valueOf(value);
    }

    public String write(Integer value) {
        return value.toString();
    }
}
