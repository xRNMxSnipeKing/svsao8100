package org.simpleframework.xml.stream;

import java.util.Iterator;

abstract class EventToken implements EventNode {
    EventToken() {
    }

    public int getLine() {
        return -1;
    }

    public String getName() {
        return null;
    }

    public String getValue() {
        return null;
    }

    public String getReference() {
        return null;
    }

    public String getPrefix() {
        return null;
    }

    public Object getSource() {
        return null;
    }

    public Iterator<Attribute> iterator() {
        return null;
    }

    public boolean isEnd() {
        return false;
    }

    public boolean isStart() {
        return false;
    }

    public boolean isText() {
        return false;
    }
}
