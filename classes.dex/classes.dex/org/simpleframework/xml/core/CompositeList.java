package org.simpleframework.xml.core;

import java.util.Collection;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

class CompositeList implements Converter {
    private final Type entry;
    private final CollectionFactory factory;
    private final String name;
    private final Traverser root;
    private final Type type;

    public CompositeList(Context context, Type type, Type entry, String name) {
        this.factory = new CollectionFactory(context, type);
        this.root = new Traverser(context);
        this.entry = entry;
        this.type = type;
        this.name = name;
    }

    public Object read(InputNode node) throws Exception {
        Instance type = this.factory.getInstance(node);
        Object list = type.getInstance();
        if (type.isReference()) {
            return list;
        }
        return populate(node, list);
    }

    public Object read(InputNode node, Object result) throws Exception {
        Instance type = this.factory.getInstance(node);
        if (type.isReference()) {
            return type.getInstance();
        }
        type.setInstance(result);
        if (result != null) {
            return populate(node, result);
        }
        return result;
    }

    private Object populate(InputNode node, Object result) throws Exception {
        Collection list = (Collection) result;
        while (true) {
            InputNode next = node.getNext();
            Class expect = this.entry.getType();
            if (next == null) {
                return list;
            }
            list.add(this.root.read(next, expect));
        }
    }

    public boolean validate(InputNode node) throws Exception {
        Instance value = this.factory.getInstance(node);
        if (value.isReference()) {
            return true;
        }
        Object result = value.setInstance(null);
        return validate(node, value.getType());
    }

    private boolean validate(InputNode node, Class type) throws Exception {
        while (true) {
            InputNode next = node.getNext();
            Class expect = this.entry.getType();
            if (next == null) {
                return true;
            }
            this.root.validate(next, expect);
        }
    }

    public void write(OutputNode node, Object source) throws Exception {
        for (Object item : (Collection) source) {
            if (item != null) {
                Class expect = this.entry.getType();
                if (expect.isAssignableFrom(item.getClass())) {
                    this.root.write(node, item, expect, this.name);
                } else {
                    throw new PersistenceException("Entry %s does not match %s for %s", item.getClass(), this.entry, this.type);
                }
            }
        }
    }
}
