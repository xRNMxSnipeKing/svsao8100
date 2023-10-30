package org.simpleframework.xml.stream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class DocumentReader implements EventReader {
    private static final String RESERVED = "xml";
    private EventNode peek;
    private NodeExtractor queue;
    private NodeStack stack = new NodeStack();

    private static class End extends EventToken {
        private End() {
        }

        public boolean isEnd() {
            return true;
        }
    }

    private static class Entry extends EventAttribute {
        private final Node node;

        public Entry(Node node) {
            this.node = node;
        }

        public String getName() {
            return this.node.getLocalName();
        }

        public String getValue() {
            return this.node.getNodeValue();
        }

        public String getPrefix() {
            return this.node.getPrefix();
        }

        public String getReference() {
            return this.node.getNamespaceURI();
        }

        public boolean isReserved() {
            String prefix = getPrefix();
            String name = getName();
            if (prefix != null) {
                return prefix.startsWith(DocumentReader.RESERVED);
            }
            return name.startsWith(DocumentReader.RESERVED);
        }

        public Object getSource() {
            return this.node;
        }
    }

    private static class Start extends EventElement {
        private final Element element;

        public Start(Node element) {
            this.element = (Element) element;
        }

        public String getName() {
            return this.element.getLocalName();
        }

        public String getPrefix() {
            return this.element.getPrefix();
        }

        public String getReference() {
            return this.element.getNamespaceURI();
        }

        public NamedNodeMap getAttributes() {
            return this.element.getAttributes();
        }

        public Object getSource() {
            return this.element;
        }
    }

    private static class Text extends EventToken {
        private final Node node;

        public Text(Node node) {
            this.node = node;
        }

        public boolean isText() {
            return true;
        }

        public String getValue() {
            return this.node.getNodeValue();
        }

        public Object getSource() {
            return this.node;
        }
    }

    public DocumentReader(Document document) {
        this.queue = new NodeExtractor(document);
        this.stack.push(document);
    }

    public EventNode peek() throws Exception {
        if (this.peek == null) {
            this.peek = next();
        }
        return this.peek;
    }

    public EventNode next() throws Exception {
        EventNode next = this.peek;
        if (next == null) {
            return read();
        }
        this.peek = null;
        return next;
    }

    private EventNode read() throws Exception {
        Node node = (Node) this.queue.peek();
        if (node == null) {
            return end();
        }
        return read(node);
    }

    private EventNode read(Node node) throws Exception {
        Node top = (Node) this.stack.top();
        if (node.getParentNode() != top) {
            if (top != null) {
                this.stack.pop();
            }
            return end();
        }
        if (node != null) {
            this.queue.poll();
        }
        return convert(node);
    }

    private EventNode convert(Node node) throws Exception {
        if (node.getNodeType() != (short) 1) {
            return text(node);
        }
        if (node != null) {
            this.stack.push(node);
        }
        return start(node);
    }

    private Start start(Node node) {
        Start event = new Start(node);
        if (event.isEmpty()) {
            return build(event);
        }
        return event;
    }

    private Start build(Start event) {
        NamedNodeMap list = event.getAttributes();
        int length = list.getLength();
        for (int i = 0; i < length; i++) {
            Attribute value = attribute(list.item(i));
            if (!value.isReserved()) {
                event.add(value);
            }
        }
        return event;
    }

    private Entry attribute(Node node) {
        return new Entry(node);
    }

    private Text text(Node node) {
        return new Text(node);
    }

    private End end() {
        return new End();
    }
}
