package org.simpleframework.xml.convert;

import java.util.Map;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.strategy.TreeStrategy;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NodeMap;
import org.simpleframework.xml.stream.OutputNode;

public class RegistryStrategy implements Strategy {
    private final Registry registry;
    private final Strategy strategy;

    public RegistryStrategy(Registry registry) {
        this(registry, new TreeStrategy());
    }

    public RegistryStrategy(Registry registry, Strategy strategy) {
        this.registry = registry;
        this.strategy = strategy;
    }

    public Value read(Type type, NodeMap<InputNode> node, Map map) throws Exception {
        Value value = this.strategy.read(type, node, map);
        return isReference(value) ? value : read(type, (NodeMap) node, value);
    }

    private Value read(Type type, NodeMap<InputNode> node, Value value) throws Exception {
        Converter converter = lookup(type, value);
        InputNode source = (InputNode) node.getNode();
        if (converter == null) {
            return value;
        }
        Object data = converter.read(source);
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
        Converter converter = lookup(type, value);
        OutputNode source = (OutputNode) node.getNode();
        if (converter == null) {
            return false;
        }
        converter.write(source, value);
        return true;
    }

    private Converter lookup(Type type, Value value) throws Exception {
        Class real = type.getType();
        if (value != null) {
            real = value.getType();
        }
        return this.registry.lookup(real);
    }

    private Converter lookup(Type type, Object value) throws Exception {
        Class real = type.getType();
        if (value != null) {
            real = value.getClass();
        }
        return this.registry.lookup(real);
    }

    private boolean isReference(Value value) {
        return value != null && value.isReference();
    }
}
