package org.simpleframework.xml.transform;

class ByteTransform implements Transform<Byte> {
    ByteTransform() {
    }

    public Byte read(String value) {
        return Byte.valueOf(value);
    }

    public String write(Byte value) {
        return value.toString();
    }
}
