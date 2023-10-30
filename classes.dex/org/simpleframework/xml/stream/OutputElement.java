package org.simpleframework.xml.stream;

class OutputElement implements OutputNode {
    private String comment;
    private Mode mode = Mode.INHERIT;
    private String name;
    private OutputNode parent;
    private String reference;
    private NamespaceMap scope;
    private OutputNodeMap table = new OutputNodeMap(this);
    private String value;
    private NodeWriter writer;

    public OutputElement(OutputNode parent, NodeWriter writer, String name) {
        this.scope = new PrefixResolver(parent);
        this.writer = writer;
        this.parent = parent;
        this.name = name;
    }

    public String getPrefix() {
        return getPrefix(true);
    }

    public String getPrefix(boolean inherit) {
        String prefix = this.scope.getPrefix(this.reference);
        if (inherit && prefix == null) {
            return this.parent.getPrefix();
        }
        return prefix;
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

    public OutputNode getParent() {
        return this.parent;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String getComment() {
        return this.comment;
    }

    public boolean isRoot() {
        return this.writer.isRoot(this);
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public OutputNodeMap getAttributes() {
        return this.table;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(boolean data) {
        if (data) {
            this.mode = Mode.DATA;
        } else {
            this.mode = Mode.ESCAPE;
        }
    }

    public OutputNode setAttribute(String name, String value) {
        return this.table.put(name, value);
    }

    public OutputNode getChild(String name) throws Exception {
        return this.writer.writeElement(this, name);
    }

    public void remove() throws Exception {
        this.writer.remove(this);
    }

    public void commit() throws Exception {
        this.writer.commit(this);
    }

    public boolean isCommitted() {
        return this.writer.isCommitted(this);
    }

    public String toString() {
        return String.format("element %s", new Object[]{this.name});
    }
}
