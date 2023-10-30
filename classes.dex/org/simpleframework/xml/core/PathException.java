package org.simpleframework.xml.core;

public class PathException extends PersistenceException {
    public PathException(String text, Object... list) {
        super(text, list);
    }
}
