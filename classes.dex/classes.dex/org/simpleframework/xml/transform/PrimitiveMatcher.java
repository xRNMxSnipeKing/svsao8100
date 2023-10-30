package org.simpleframework.xml.transform;

class PrimitiveMatcher implements Matcher {
    public Transform match(Class type) throws Exception {
        if (type == Integer.TYPE) {
            return new IntegerTransform();
        }
        if (type == Boolean.TYPE) {
            return new BooleanTransform();
        }
        if (type == Long.TYPE) {
            return new LongTransform();
        }
        if (type == Double.TYPE) {
            return new DoubleTransform();
        }
        if (type == Float.TYPE) {
            return new FloatTransform();
        }
        if (type == Short.TYPE) {
            return new ShortTransform();
        }
        if (type == Byte.TYPE) {
            return new ByteTransform();
        }
        if (type == Character.TYPE) {
            return new CharacterTransform();
        }
        return null;
    }
}
