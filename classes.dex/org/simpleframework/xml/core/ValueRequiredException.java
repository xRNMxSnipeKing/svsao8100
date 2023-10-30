package org.simpleframework.xml.core;

public class ValueRequiredException extends PersistenceException {
    public ValueRequiredException(String text, Object... list) {
        super(text, list);
    }

    public ValueRequiredException(Throwable cause, String text, Object... list) {
        super(cause, text, list);
    }
}
