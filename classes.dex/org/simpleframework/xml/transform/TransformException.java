package org.simpleframework.xml.transform;

import org.simpleframework.xml.core.PersistenceException;

public class TransformException extends PersistenceException {
    public TransformException(String text, Object... list) {
        super(String.format(text, list), new Object[0]);
    }

    public TransformException(Throwable cause, String text, Object... list) {
        super(String.format(text, list), cause);
    }
}
