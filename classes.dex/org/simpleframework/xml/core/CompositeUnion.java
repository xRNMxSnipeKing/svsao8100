package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

class CompositeUnion implements Repeater {
    private final Context context;
    private final LabelMap elements;
    private final Group group;
    private final Type type;

    public CompositeUnion(Context context, Group group, Type type) throws Exception {
        this.elements = group.getElements(context);
        this.context = context;
        this.group = group;
        this.type = type;
    }

    public Object read(InputNode node) throws Exception {
        return ((Label) this.elements.get(node.getName())).getConverter(this.context).read(node);
    }

    public Object read(InputNode node, Object value) throws Exception {
        return ((Label) this.elements.get(node.getName())).getConverter(this.context).read(node, value);
    }

    public boolean validate(InputNode node) throws Exception {
        return ((Label) this.elements.get(node.getName())).getConverter(this.context).validate(node);
    }

    public void write(OutputNode node, Object object) throws Exception {
        Label label = this.group.getLabel(object.getClass());
        if (label == null) {
            throw new UnionException("Value of %s not declared in %s with annotation %s", real, this.type, this.group);
        } else {
            write(node, object, label);
        }
    }

    private void write(OutputNode node, Object object, Label label) throws Exception {
        label.getConverter(this.context).write(node, object);
    }
}
