package org.simpleframework.xml.stream;

import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class NodeWriter {
    private final Set active;
    private final OutputStack stack;
    private final boolean verbose;
    private final Formatter writer;

    public NodeWriter(Writer result) {
        this(result, new Format());
    }

    public NodeWriter(Writer result, Format format) {
        this(result, format, false);
    }

    private NodeWriter(Writer result, Format format, boolean verbose) {
        this.writer = new Formatter(result, format);
        this.active = new HashSet();
        this.stack = new OutputStack(this.active);
        this.verbose = verbose;
    }

    public OutputNode writeRoot() throws Exception {
        OutputDocument root = new OutputDocument(this, this.stack);
        if (this.stack.isEmpty()) {
            this.writer.writeProlog();
        }
        return root;
    }

    public boolean isRoot(OutputNode node) {
        return this.stack.bottom() == node;
    }

    public boolean isCommitted(OutputNode node) {
        return !this.active.contains(node);
    }

    public void commit(OutputNode parent) throws Exception {
        if (this.stack.contains(parent)) {
            OutputNode top = this.stack.top();
            if (!isCommitted(top)) {
                writeStart(top);
            }
            while (this.stack.top() != parent) {
                writeEnd(this.stack.pop());
            }
            writeEnd(parent);
            this.stack.pop();
        }
    }

    public void remove(OutputNode node) throws Exception {
        if (this.stack.top() != node) {
            throw new NodeException("Cannot remove node");
        }
        this.stack.pop();
    }

    public OutputNode writeElement(OutputNode parent, String name) throws Exception {
        if (this.stack.isEmpty()) {
            return writeStart(parent, name);
        }
        if (!this.stack.contains(parent)) {
            return null;
        }
        OutputNode top = this.stack.top();
        if (!isCommitted(top)) {
            writeStart(top);
        }
        while (this.stack.top() != parent) {
            writeEnd(this.stack.pop());
        }
        return writeStart(parent, name);
    }

    private OutputNode writeStart(OutputNode parent, String name) throws Exception {
        OutputNode node = new OutputElement(parent, this, name);
        if (name != null) {
            return this.stack.push(node);
        }
        throw new NodeException("Can not have a null name");
    }

    private void writeStart(OutputNode node) throws Exception {
        writeComment(node);
        writeName(node);
        writeAttributes(node);
        writeNamespaces(node);
    }

    private void writeComment(OutputNode node) throws Exception {
        String comment = node.getComment();
        if (comment != null) {
            this.writer.writeComment(comment);
        }
    }

    private void writeName(OutputNode node) throws Exception {
        String prefix = node.getPrefix(this.verbose);
        String name = node.getName();
        if (name != null) {
            this.writer.writeStart(name, prefix);
        }
    }

    private void writeEnd(OutputNode node) throws Exception {
        Mode mode = node.getMode();
        Iterator i$ = this.stack.iterator();
        while (i$.hasNext()) {
            OutputNode next = (OutputNode) i$.next();
            if (mode != Mode.INHERIT) {
                break;
            }
            mode = next.getMode();
        }
        writeEnd(node, mode);
    }

    private void writeEnd(OutputNode node, Mode mode) throws Exception {
        String value = node.getValue();
        if (value != null) {
            this.writer.writeText(value, mode);
        }
        String name = node.getName();
        String prefix = node.getPrefix(this.verbose);
        if (name != null) {
            this.writer.writeEnd(name, prefix);
            this.writer.flush();
        }
    }

    private void writeAttributes(OutputNode node) throws Exception {
        NodeMap<OutputNode> map = node.getAttributes();
        Iterator i$ = map.iterator();
        while (i$.hasNext()) {
            String name = (String) i$.next();
            OutputNode entry = (OutputNode) map.get(name);
            this.writer.writeAttribute(name, entry.getValue(), entry.getPrefix(this.verbose));
        }
        this.active.remove(node);
    }

    private void writeNamespaces(OutputNode node) throws Exception {
        NamespaceMap<String> map = node.getNamespaces();
        for (String name : map) {
            this.writer.writeNamespace(name, map.getPrefix(name));
        }
    }
}
