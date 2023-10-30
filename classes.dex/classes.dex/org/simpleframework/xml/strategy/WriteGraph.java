package org.simpleframework.xml.strategy;

import java.lang.reflect.Array;
import java.util.IdentityHashMap;
import org.simpleframework.xml.stream.NodeMap;

class WriteGraph extends IdentityHashMap<Object, String> {
    private final String label;
    private final String length;
    private final String mark;
    private final String refer;

    public WriteGraph(Contract contract) {
        this.refer = contract.getReference();
        this.mark = contract.getIdentity();
        this.length = contract.getLength();
        this.label = contract.getLabel();
    }

    public boolean write(Type type, Object value, NodeMap node) {
        Class actual = value.getClass();
        Class expect = type.getType();
        Class real = actual;
        if (actual.isArray()) {
            real = writeArray(actual, value, node);
        }
        if (actual != expect) {
            node.put(this.label, real.getName());
        }
        return writeReference(value, node);
    }

    private boolean writeReference(Object value, NodeMap node) {
        String name = (String) get(value);
        int size = size();
        if (name != null) {
            node.put(this.refer, name);
            return true;
        }
        String unique = String.valueOf(size);
        node.put(this.mark, unique);
        put(value, unique);
        return false;
    }

    private Class writeArray(Class field, Object value, NodeMap node) {
        int size = Array.getLength(value);
        if (!containsKey(value)) {
            node.put(this.length, String.valueOf(size));
        }
        return field.getComponentType();
    }
}
