package org.simpleframework.xml.transform;

class BooleanTransform implements Transform<Boolean> {
    BooleanTransform() {
    }

    public Boolean read(String value) {
        return Boolean.valueOf(value);
    }

    public String write(Boolean value) {
        return value.toString();
    }
}
