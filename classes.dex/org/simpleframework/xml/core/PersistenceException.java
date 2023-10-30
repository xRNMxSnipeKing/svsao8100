package org.simpleframework.xml.core;

public class PersistenceException extends Exception {
    public PersistenceException(String text, Object... list) {
        super(String.format(text, list));
    }

    public PersistenceException(Throwable cause, String text, Object... list) {
        super(String.format(text, list), cause);
    }
}
