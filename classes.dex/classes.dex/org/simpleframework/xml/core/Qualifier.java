package org.simpleframework.xml.core;

import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.stream.OutputNode;

class Qualifier implements Decorator {
    private NamespaceDecorator decorator = new NamespaceDecorator();

    public Qualifier(Contact contact) {
        scan(contact);
    }

    public void decorate(OutputNode node) {
        this.decorator.decorate(node);
    }

    public void decorate(OutputNode node, Decorator secondary) {
        this.decorator.decorate(node, secondary);
    }

    private void scan(Contact contact) {
        namespace(contact);
        scope(contact);
    }

    private void namespace(Contact contact) {
        Namespace primary = (Namespace) contact.getAnnotation(Namespace.class);
        if (primary != null) {
            this.decorator.set(primary);
            this.decorator.add(primary);
        }
    }

    private void scope(Contact contact) {
        NamespaceList scope = (NamespaceList) contact.getAnnotation(NamespaceList.class);
        if (scope != null) {
            for (Namespace name : scope.value()) {
                this.decorator.add(name);
            }
        }
    }
}
