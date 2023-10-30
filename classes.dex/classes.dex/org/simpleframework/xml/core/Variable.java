package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Set;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Position;

class Variable implements Label {
    private final Label label;
    private final Object value;

    private class Adapter implements Repeater {
        private final Converter reader;
        private final Object value;

        public Adapter(Converter reader, Object value) {
            this.reader = reader;
            this.value = value;
        }

        public Object read(InputNode node) throws Exception {
            return read(node, this.value);
        }

        public Object read(InputNode node, Object value) throws Exception {
            Position line = node.getPosition();
            String name = node.getName();
            if (this.reader instanceof Repeater) {
                return this.reader.read(node, value);
            }
            throw new PersistenceException("Element '%s' declared twice at %s", name, line);
        }

        public boolean validate(InputNode node) throws Exception {
            Position line = node.getPosition();
            String name = node.getName();
            if (this.reader instanceof Repeater) {
                return this.reader.validate(node);
            }
            throw new PersistenceException("Element '%s' declared twice at %s", name, line);
        }

        public void write(OutputNode node, Object value) throws Exception {
            write(node, value);
        }
    }

    public Variable(Label label, Object value) {
        this.label = label;
        this.value = value;
    }

    public Label getLabel(Class type) {
        return this;
    }

    public Type getType(Class type) throws Exception {
        return this.label.getType(type);
    }

    public Set<String> getUnion() throws Exception {
        return this.label.getUnion();
    }

    public Set<String> getUnion(Context context) throws Exception {
        return this.label.getUnion(context);
    }

    public Object getValue() {
        return this.value;
    }

    public Decorator getDecorator() throws Exception {
        return this.label.getDecorator();
    }

    public Converter getConverter(Context context) throws Exception {
        Converter reader = this.label.getConverter(context);
        return reader instanceof Adapter ? reader : new Adapter(reader, this.value);
    }

    public String getName(Context context) throws Exception {
        return context.getStyle().getElement(this.label.getName(context));
    }

    public Object getEmpty(Context context) throws Exception {
        return this.label.getEmpty(context);
    }

    public Contact getContact() {
        return this.label.getContact();
    }

    public Type getDependent() throws Exception {
        return this.label.getDependent();
    }

    public String getEntry() throws Exception {
        return this.label.getEntry();
    }

    public String getName() throws Exception {
        return this.label.getName();
    }

    public Annotation getAnnotation() {
        return this.label.getAnnotation();
    }

    public String getPath() {
        return this.label.getPath();
    }

    public String getOverride() {
        return this.label.getOverride();
    }

    public Class getType() {
        return this.label.getType();
    }

    public boolean isData() {
        return this.label.isData();
    }

    public boolean isInline() {
        return this.label.isInline();
    }

    public boolean isAttribute() {
        return this.label.isAttribute();
    }

    public boolean isCollection() {
        return this.label.isCollection();
    }

    public boolean isRequired() {
        return this.label.isRequired();
    }

    public String toString() {
        return this.label.toString();
    }
}
