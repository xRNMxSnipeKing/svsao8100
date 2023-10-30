package org.simpleframework.xml.core;

public class AttributeException extends PersistenceException {
    public AttributeException(String text, Object... list) {
        super(text, list);
    }

    public AttributeException(Throwable cause, String text, Object... list) {
        super(cause, text, list);
    }
}
