package org.simpleframework.xml.core;

public class UnionException extends PersistenceException {
    public UnionException(String text, Object... list) {
        super(String.format(text, list), new Object[0]);
    }
}
