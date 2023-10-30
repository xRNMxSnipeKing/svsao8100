package org.simpleframework.xml.stream;

import java.util.Iterator;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

class StreamReader implements EventReader {
    private EventNode peek;
    private XMLEventReader reader;

    private static class End extends EventToken {
        private End() {
        }

        public boolean isEnd() {
            return true;
        }
    }

    private static class Entry extends EventAttribute {
        private final Attribute entry;

        public Entry(Attribute entry) {
            this.entry = entry;
        }

        public String getName() {
            return this.entry.getName().getLocalPart();
        }

        public String getPrefix() {
            return this.entry.getName().getPrefix();
        }

        public String getReference() {
            return this.entry.getName().getNamespaceURI();
        }

        public String getValue() {
            return this.entry.getValue();
        }

        public boolean isReserved() {
            return false;
        }

        public Object getSource() {
            return this.entry;
        }
    }

    private static class Start extends EventElement {
        private final StartElement element;
        private final Location location;

        public Start(XMLEvent event) {
            this.element = event.asStartElement();
            this.location = event.getLocation();
        }

        public int getLine() {
            return this.location.getLineNumber();
        }

        public String getName() {
            return this.element.getName().getLocalPart();
        }

        public String getPrefix() {
            return this.element.getName().getPrefix();
        }

        public String getReference() {
            return this.element.getName().getNamespaceURI();
        }

        public Iterator<Attribute> getAttributes() {
            return this.element.getAttributes();
        }

        public Object getSource() {
            return this.element;
        }
    }

    private static class Text extends EventToken {
        private final Characters text;

        public Text(XMLEvent event) {
            this.text = event.asCharacters();
        }

        public boolean isText() {
            return true;
        }

        public String getValue() {
            return this.text.getData();
        }

        public Object getSource() {
            return this.text;
        }
    }

    public StreamReader(XMLEventReader reader) {
        this.reader = reader;
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
        XMLEvent event = this.reader.nextEvent();
        if (event.isStartElement()) {
            return start(event);
        }
        if (event.isCharacters()) {
            return text(event);
        }
        if (event.isEndElement()) {
            return end();
        }
        return read();
    }

    private Start start(XMLEvent event) {
        Start node = new Start(event);
        if (node.isEmpty()) {
            return build(node);
        }
        return node;
    }

    private Start build(Start event) {
        Iterator<Attribute> list = event.getAttributes();
        while (list.hasNext()) {
            Entry entry = attribute((Attribute) list.next());
            if (!entry.isReserved()) {
                event.add(entry);
            }
        }
        return event;
    }

    private Entry attribute(Attribute entry) {
        return new Entry(entry);
    }

    private Text text(XMLEvent event) {
        return new Text(event);
    }

    private End end() {
        return new End();
    }
}
