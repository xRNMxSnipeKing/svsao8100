package org.simpleframework.xml.core;

public class ConstructorException extends PersistenceException {
    public ConstructorException(String text, Object... list) {
        super(text, list);
    }

    public ConstructorException(Throwable cause, String text, Object... list) {
        super(cause, text, list);
    }
}
