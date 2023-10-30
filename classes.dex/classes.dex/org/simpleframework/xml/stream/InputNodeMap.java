package org.simpleframework.xml.stream;

import java.util.Iterator;
import java.util.LinkedHashMap;

class InputNodeMap extends LinkedHashMap<String, InputNode> implements NodeMap<InputNode> {
    private final InputNode source;

    protected InputNodeMap(InputNode source) {
        this.source = source;
    }

    public InputNodeMap(InputNode source, EventNode element) {
        this.source = source;
        build(element);
    }

    private void build(EventNode element) {
        for (Attribute entry : element) {
            InputAttribute value = new InputAttribute(this.source, entry);
            if (!entry.isReserved()) {
                put(value.getName(), value);
            }
        }
    }

    public InputNode getNode() {
        return this.source;
    }

    public String getName() {
        return this.source.getName();
    }

    public InputNode put(String name, String value) {
        InputNode node = new InputAttribute(this.source, name, value);
        if (name != null) {
            put(name, node);
        }
        return node;
    }

    public InputNode remove(String name) {
        return (InputNode) super.remove(name);
    }

    public InputNode get(String name) {
        return (InputNode) super.get(name);
    }

    public Iterator<String> iterator() {
        return keySet().iterator();
    }
}
