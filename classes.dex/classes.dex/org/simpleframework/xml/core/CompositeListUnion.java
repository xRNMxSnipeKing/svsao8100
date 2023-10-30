package org.simpleframework.xml.core;

import java.util.Collection;
import java.util.Collections;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Style;

class CompositeListUnion implements Repeater {
    private final Context context;
    private final LabelMap elements;
    private final Group group;
    private final Style style;
    private final Type type;

    public CompositeListUnion(Context context, Group group, Type type) throws Exception {
        this.elements = group.getElements(context);
        this.style = context.getStyle();
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

    public void write(OutputNode node, Object value) throws Exception {
        for (Object item : (Collection) value) {
            if (item != null) {
                Label label = this.group.getLabel(item.getClass());
                if (label == null) {
                    throw new UnionException("Entry of %s not declared in %s with annotation %s", real, this.type, this.group);
                }
                write(node, item, label);
            }
        }
    }

    private void write(OutputNode node, Object item, Label label) throws Exception {
        Converter converter = label.getConverter(this.context);
        Collection list = Collections.singleton(item);
        if (!label.isInline()) {
            String root = this.style.getElement(label.getName());
            if (!node.isCommitted()) {
                node.setName(root);
            }
        }
        converter.write(node, list);
    }
}
