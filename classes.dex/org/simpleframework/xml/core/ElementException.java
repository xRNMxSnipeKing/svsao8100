package org.simpleframework.xml.core;

public class ElementException extends PersistenceException {
    public ElementException(String text, Object... list) {
        super(text, list);
    }

    public ElementException(Throwable cause, String text, Object... list) {
        super(cause, text, list);
    }
}
