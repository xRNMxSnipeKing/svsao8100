package org.simpleframework.xml.transform;

class EnumTransform implements Transform<Enum> {
    private final Class type;

    public EnumTransform(Class type) {
        this.type = type;
    }

    public Enum read(String value) throws Exception {
        return Enum.valueOf(this.type, value);
    }

    public String write(Enum value) throws Exception {
        return value.name();
    }
}
