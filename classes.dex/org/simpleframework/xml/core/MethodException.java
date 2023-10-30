package org.simpleframework.xml.core;

public class MethodException extends PersistenceException {
    public MethodException(String text, Object... list) {
        super(text, list);
    }

    public MethodException(Throwable cause, String text, Object... list) {
        super(cause, text, list);
    }
}
