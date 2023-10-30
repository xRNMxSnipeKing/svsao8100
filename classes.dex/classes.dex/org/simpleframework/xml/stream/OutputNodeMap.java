package org.simpleframework.xml.stream;

import java.util.Iterator;
import java.util.LinkedHashMap;

class OutputNodeMap extends LinkedHashMap<String, OutputNode> implements NodeMap<OutputNode> {
    private final OutputNode source;

    public OutputNodeMap(OutputNode source) {
        this.source = source;
    }

    public OutputNode getNode() {
        return this.source;
    }

    public String getName() {
        return this.source.getName();
    }

    public OutputNode put(String name, String value) {
        OutputNode node = new OutputAttribute(this.source, name, value);
        if (this.source != null) {
            put(name, node);
        }
        return node;
    }

    public OutputNode remove(String name) {
        return (OutputNode) super.remove(name);
    }

    public OutputNode get(String name) {
        return (OutputNode) super.get(name);
    }

    public Iterator<String> iterator() {
        return keySet().iterator();
    }
}
