package org.simpleframework.xml.stream;

class OutputAttribute implements OutputNode {
    private String name;
    private String reference;
    private NamespaceMap scope;
    private OutputNode source;
    private String value;

    public OutputAttribute(OutputNode source, String name, String value) {
        this.scope = source.getNamespaces();
        this.source = source;
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public OutputNode getParent() {
        return this.source;
    }

    public NodeMap<OutputNode> getAttributes() {
        return new OutputNodeMap(this);
    }

    public OutputNode getChild(String name) {
        return null;
    }

    public String getComment() {
        return null;
    }

    public void setComment(String comment) {
    }

    public Mode getMode() {
        return Mode.INHERIT;
    }

    public void setMode(Mode mode) {
    }

    public void setData(boolean data) {
    }

    public String getPrefix() {
        return this.scope.getPrefix(this.reference);
    }

    public String getPrefix(boolean inherit) {
        return this.scope.getPrefix(this.reference);
    }

    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public NamespaceMap getNamespaces() {
        return this.scope;
    }

    public OutputNode setAttribute(String name, String value) {
        return null;
    }

    public void remove() {
    }

    public void commit() {
    }

    public boolean isRoot() {
        return false;
    }

    public boolean isCommitted() {
        return true;
    }

    public String toString() {
        return String.format("attribute %s='%s'", new Object[]{this.name, this.value});
    }
}
