package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Style;

class Traverser {
    private final Context context;
    private final Style style;

    public Traverser(Context context) {
        this.style = context.getStyle();
        this.context = context;
    }

    private Decorator getDecorator(Class type) throws Exception {
        return this.context.getDecorator(type);
    }

    public Object read(InputNode node, Class type) throws Exception {
        Object value = getComposite(type).read(node);
        if (value != null) {
            return read(node, value.getClass(), value);
        }
        return null;
    }

    public Object read(InputNode node, Object value) throws Exception {
        Class type = value.getClass();
        return read(node, type, getComposite(type).read(node, value));
    }

    private Object read(InputNode node, Class type, Object value) throws Exception {
        if (getName(type) != null) {
            return value;
        }
        throw new RootException("Root annotation required for %s", type);
    }

    public boolean validate(InputNode node, Class type) throws Exception {
        Composite factory = getComposite(type);
        if (getName(type) != null) {
            return factory.validate(node);
        }
        throw new RootException("Root annotation required for %s", type);
    }

    public void write(OutputNode node, Object source) throws Exception {
        write(node, source, source.getClass());
    }

    public void write(OutputNode node, Object source, Class expect) throws Exception {
        String root = getName(source.getClass());
        if (root == null) {
            throw new RootException("Root annotation required for %s", type);
        } else {
            write(node, source, expect, root);
        }
    }

    public void write(OutputNode node, Object source, Class expect, String name) throws Exception {
        OutputNode child = node.getChild(name);
        Type type = getType(expect);
        if (source != null) {
            Class actual = source.getClass();
            Decorator decorator = getDecorator(actual);
            if (decorator != null) {
                decorator.decorate(child);
            }
            if (!this.context.setOverride(type, source, child)) {
                getComposite(actual).write(child, source);
            }
        }
        child.commit();
    }

    private Composite getComposite(Class expect) throws Exception {
        Type type = getType(expect);
        if (expect != null) {
            return new Composite(this.context, type);
        }
        throw new RootException("Can not instantiate null class", new Object[0]);
    }

    private Type getType(Class type) {
        return new ClassType(type);
    }

    protected String getName(Class type) throws Exception {
        return this.style.getElement(this.context.getName(type));
    }
}
