package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

class Primitive implements Converter {
    private final Context context;
    private final String empty;
    private final Class expect;
    private final PrimitiveFactory factory;
    private final Type type;

    public Primitive(Context context, Type type) {
        this(context, type, null);
    }

    public Primitive(Context context, Type type, String empty) {
        this.factory = new PrimitiveFactory(context, type);
        this.expect = type.getType();
        this.context = context;
        this.empty = empty;
        this.type = type;
    }

    public Object read(InputNode node) throws Exception {
        if (node.isElement()) {
            return readElement(node);
        }
        return read(node, this.expect);
    }

    public Object read(InputNode node, Object value) throws Exception {
        if (value == null) {
            return read(node);
        }
        throw new PersistenceException("Can not read existing %s for %s", this.expect, this.type);
    }

    public Object read(InputNode node, Class type) throws Exception {
        String value = node.getValue();
        if (value == null) {
            return null;
        }
        if (this.empty == null || !value.equals(this.empty)) {
            return readTemplate(value, type);
        }
        return this.empty;
    }

    private Object readElement(InputNode node) throws Exception {
        Instance value = this.factory.getInstance(node);
        if (value.isReference()) {
            return value.getInstance();
        }
        return readElement(node, value);
    }

    private Object readElement(InputNode node, Instance value) throws Exception {
        Object result = read(node, this.expect);
        if (value != null) {
            value.setInstance(result);
        }
        return result;
    }

    private Object readTemplate(String value, Class type) throws Exception {
        String text = this.context.getProperty(value);
        if (text != null) {
            return this.factory.getInstance(text, type);
        }
        return null;
    }

    public boolean validate(InputNode node) throws Exception {
        if (node.isElement()) {
            validateElement(node);
        } else {
            node.getValue();
        }
        return true;
    }

    private boolean validateElement(InputNode node) throws Exception {
        Instance type = this.factory.getInstance(node);
        if (!type.isReference()) {
            type.setInstance(null);
        }
        return true;
    }

    public void write(OutputNode node, Object source) throws Exception {
        String text = this.factory.getText(source);
        if (text != null) {
            node.setValue(text);
        }
    }
}
