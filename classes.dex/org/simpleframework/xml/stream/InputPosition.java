package org.simpleframework.xml.stream;

class InputPosition implements Position {
    private EventNode source;

    public InputPosition(EventNode source) {
        this.source = source;
    }

    public int getLine() {
        return this.source.getLine();
    }

    public String toString() {
        return String.format("line %s", new Object[]{Integer.valueOf(getLine())});
    }
}
