package org.simpleframework.xml.core;

public class RootException extends PersistenceException {
    public RootException(String text, Object... list) {
        super(text, list);
    }

    public RootException(Throwable cause, String text, Object... list) {
        super(cause, text, list);
    }
}
