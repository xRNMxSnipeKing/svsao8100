package org.simpleframework.xml.core;

import org.simpleframework.xml.Version;
import org.simpleframework.xml.filter.Filter;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NodeMap;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Style;

class Source implements Context {
    private TemplateEngine engine = new TemplateEngine(this.filter);
    private Filter filter;
    private Session session;
    private Strategy strategy;
    private Style style;
    private Support support;

    public Source(Strategy strategy, Support support, Style style, Session session) {
        this.filter = new TemplateFilter(this, support);
        this.strategy = strategy;
        this.support = support;
        this.session = session;
        this.style = style;
    }

    public boolean isStrict() {
        return this.session.isStrict();
    }

    public Session getSession() {
        return this.session;
    }

    public Support getSupport() {
        return this.support;
    }

    public Style getStyle() {
        if (this.style == null) {
            this.style = new DefaultStyle();
        }
        return this.style;
    }

    public boolean isFloat(Class type) throws Exception {
        return this.support.isFloat(type);
    }

    public boolean isFloat(Type type) throws Exception {
        return isFloat(type.getType());
    }

    public boolean isPrimitive(Class type) throws Exception {
        return this.support.isPrimitive(type);
    }

    public boolean isPrimitive(Type type) throws Exception {
        return isPrimitive(type.getType());
    }

    public Instance getInstance(Class type) {
        return this.support.getInstance(type);
    }

    public Instance getInstance(Value value) {
        return this.support.getInstance(value);
    }

    public String getName(Class type) throws Exception {
        return this.support.getName(type);
    }

    public Version getVersion(Class type) throws Exception {
        return getScanner(type).getRevision();
    }

    private Scanner getScanner(Class type) throws Exception {
        return this.support.getScanner(type);
    }

    public Decorator getDecorator(Class type) throws Exception {
        return getScanner(type).getDecorator();
    }

    public Caller getCaller(Class type) throws Exception {
        return getScanner(type).getCaller(this);
    }

    public Schema getSchema(Class type) throws Exception {
        Scanner schema = getScanner(type);
        if (schema != null) {
            return new ClassSchema(schema, this);
        }
        throw new PersistenceException("Invalid schema class %s", type);
    }

    public Object getAttribute(Object key) {
        return this.session.get(key);
    }

    public Value getOverride(Type type, InputNode node) throws Exception {
        NodeMap<InputNode> map = node.getAttributes();
        if (map != null) {
            return this.strategy.read(type, map, this.session);
        }
        throw new PersistenceException("No attributes for %s", node);
    }

    public boolean setOverride(Type type, Object value, OutputNode node) throws Exception {
        NodeMap<OutputNode> map = node.getAttributes();
        if (map != null) {
            return this.strategy.write(type, value, map, this.session);
        }
        throw new PersistenceException("No attributes for %s", node);
    }

    public Class getType(Type type, Object value) {
        if (value != null) {
            return value.getClass();
        }
        return type.getType();
    }

    public String getProperty(String text) {
        return this.engine.process(text);
    }
}
