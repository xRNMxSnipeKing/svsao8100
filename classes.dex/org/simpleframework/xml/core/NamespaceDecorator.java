package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.stream.NamespaceMap;
import org.simpleframework.xml.stream.OutputNode;

class NamespaceDecorator implements Decorator {
    private Namespace primary;
    private List<Namespace> scope = new ArrayList();

    public void set(Namespace namespace) {
        if (namespace != null) {
            add(namespace);
        }
        this.primary = namespace;
    }

    public void add(Namespace namespace) {
        this.scope.add(namespace);
    }

    public void decorate(OutputNode node) {
        decorate(node, null);
    }

    public void decorate(OutputNode node, Decorator decorator) {
        if (decorator != null) {
            decorator.decorate(node);
        }
        scope(node);
        namespace(node);
    }

    private void scope(OutputNode node) {
        NamespaceMap map = node.getNamespaces();
        for (Namespace next : this.scope) {
            map.setReference(next.reference(), next.prefix());
        }
    }

    private void namespace(OutputNode node) {
        if (this.primary != null) {
            node.setReference(this.primary.reference());
        }
    }
}
