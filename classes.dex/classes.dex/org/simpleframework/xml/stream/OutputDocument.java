package org.simpleframework.xml.stream;

class OutputDocument implements OutputNode {
    private String comment;
    private Mode mode = Mode.INHERIT;
    private String name;
    private String reference;
    private OutputStack stack;
    private OutputNodeMap table = new OutputNodeMap(this);
    private String value;
    private NodeWriter writer;

    public OutputDocument(NodeWriter writer, OutputStack stack) {
        this.writer = writer;
        this.stack = stack;
    }

    public String getPrefix() {
        return null;
    }

    public String getPrefix(boolean inherit) {
        return null;
    }

    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public NamespaceMap getNamespaces() {
        return null;
    }

    public OutputNode getParent() {
        return null;
    }

    public String getName() {
        return null;
    }

    public String getValue() throws Exception {
        return this.value;
    }

    public String getComment() {
        return this.comment;
    }

    public boolean isRoot() {
        return true;
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public OutputNode setAttribute(String name, String value) {
        return this.table.put(name, value);
    }

    public NodeMap<OutputNode> getAttributes() {
        return this.table;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setData(boolean data) {
        if (data) {
            this.mode = Mode.DATA;
        } else {
            this.mode = Mode.ESCAPE;
        }
    }

    public OutputNode getChild(String name) throws Exception {
        return this.writer.writeElement(this, name);
    }

    public void remove() throws Exception {
        if (this.stack.isEmpty()) {
            throw new NodeException("No root node");
        }
        this.stack.bottom().remove();
    }

    public void commit() throws Exception {
        if (this.stack.isEmpty()) {
            throw new NodeException("No root node");
        }
        this.stack.bottom().commit();
    }

    public boolean isCommitted() {
        return this.stack.isEmpty();
    }
}
