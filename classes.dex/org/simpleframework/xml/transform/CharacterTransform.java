package org.simpleframework.xml.transform;

class CharacterTransform implements Transform<Character> {
    CharacterTransform() {
    }

    public Character read(String value) throws Exception {
        if (value.length() == 1) {
            return Character.valueOf(value.charAt(0));
        }
        throw new InvalidFormatException("Cannot convert '%s' to a character", value);
    }

    public String write(Character value) throws Exception {
        return value.toString();
    }
}
