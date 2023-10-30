package org.simpleframework.xml.stream;

import org.xmlpull.v1.XmlPullParser;

class PullReader implements EventReader {
    private XmlPullParser parser;
    private EventNode peek;

    private static class End extends EventToken {
        private End() {
        }

        public boolean isEnd() {
            return true;
        }
    }

    private class Entry extends EventAttribute {
        private final String name;
        private final String prefix;
        private final String reference;
        private final XmlPullParser source;
        private final String value;

        public Entry(XmlPullParser source, int index) {
            this.reference = source.getAttributeNamespace(index);
            this.prefix = source.getAttributePrefix(index);
            this.value = source.getAttributeValue(index);
            this.name = source.getAttributeName(index);
            this.source = source;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public boolean isReserved() {
            return false;
        }

        public String getReference() {
            return this.reference;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public Object getSource() {
            return this.source;
        }
    }

    private static class Start extends EventElement {
        private final int line;
        private final String name;
        private final String prefix;
        private final String reference;
        private final XmlPullParser source;

        public Start(XmlPullParser source) {
            this.reference = source.getNamespace();
            this.line = source.getLineNumber();
            this.prefix = source.getPrefix();
            this.name = source.getName();
            this.source = source;
        }

        public int getLine() {
            return this.line;
        }

        public String getName() {
            return this.name;
        }

        public String getReference() {
            return this.reference;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public Object getSource() {
            return this.source;
        }
    }

    private static class Text extends EventToken {
        private final XmlPullParser source;
        private final String text;

        public Text(XmlPullParser source) {
            this.text = source.getText();
            this.source = source;
        }

        public boolean isText() {
            return true;
        }

        public String getValue() {
            return this.text;
        }

        public Object getSource() {
            return this.source;
        }
    }

    public PullReader(XmlPullParser parser) {
        this.parser = parser;
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
        int event = this.parser.next();
        if (event == 1) {
            return null;
        }
        if (event == 2) {
            return start();
        }
        if (event == 4) {
            return text();
        }
        if (event == 3) {
            return end();
        }
        return read();
    }

    private Text text() throws Exception {
        return new Text(this.parser);
    }

    private Start start() throws Exception {
        Start event = new Start(this.parser);
        if (event.isEmpty()) {
            return build(event);
        }
        return event;
    }

    private Start build(Start event) throws Exception {
        int count = this.parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            Entry entry = attribute(i);
            if (!entry.isReserved()) {
                event.add(entry);
            }
        }
        return event;
    }

    private Entry attribute(int index) throws Exception {
        return new Entry(this.parser, index);
    }

    private End end() throws Exception {
        return new End();
    }
}
