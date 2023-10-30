package org.simpleframework.xml.core;

public class InstantiationException extends PersistenceException {
    public InstantiationException(String text, Object... list) {
        super(text, list);
    }

    public InstantiationException(Throwable cause, String text, Object... list) {
        super(cause, text, list);
    }
}
