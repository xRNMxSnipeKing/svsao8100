package org.simpleframework.xml.transform;

class DoubleTransform implements Transform<Double> {
    DoubleTransform() {
    }

    public Double read(String value) {
        return Double.valueOf(value);
    }

    public String write(Double value) {
        return value.toString();
    }
}
