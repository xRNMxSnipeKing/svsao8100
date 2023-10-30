package org.simpleframework.xml.convert;

import java.util.Map;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.strategy.TreeStrategy;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NodeMap;
import org.simpleframework.xml.stream.OutputNode;

public class AnnotationStrategy implements Strategy {
    private final ConverterScanner scanner;
    private final Strategy strategy;

    public AnnotationStrategy() {
        this(new TreeStrategy());
    }

    public AnnotationStrategy(Strategy strategy) {
        this.scanner = new ConverterScanner();
        this.strategy = strategy;
    }

    public Value read(Type type, NodeMap<InputNode> node, Map map) throws Exception {
        Value value = this.strategy.read(type, node, map);
        return isReference(value) ? value : read(type, (NodeMap) node, value);
    }

    private Value read(Type type, NodeMap<InputNode> node, Value value) throws Exception {
        Converter converter = this.scanner.getConverter(type, value);
        InputNode parent = (InputNode) node.getNode();
        if (converter == null) {
            return value;
        }
        Object data = converter.read(parent);
        if (value != null) {
            value.setValue(data);
        }
        return new Reference(value, data);
    }

    public boolean write(Type type, Object value, NodeMap<OutputNode> node, Map map) throws Exception {
        boolean reference = this.strategy.write(type, value, node, map);
        if (reference) {
            return reference;
        }
        return write(type, value, node);
    }

    private boolean write(Type type, Object value, NodeMap<OutputNode> node) throws Exception {
        Converter converter = this.scanner.getConverter(type, value);
        OutputNode parent = (OutputNode) node.getNode();
        if (converter == null) {
            return false;
        }
        converter.write(parent, value);
        return true;
    }

    private boolean isReference(Value value) {
        return value != null && value.isReference();
    }
}
