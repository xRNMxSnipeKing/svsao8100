package org.simpleframework.xml.transform;

class StringTransform implements Transform<String> {
    StringTransform() {
    }

    public String read(String value) {
        return value;
    }

    public String write(String value) {
        return value;
    }
}
