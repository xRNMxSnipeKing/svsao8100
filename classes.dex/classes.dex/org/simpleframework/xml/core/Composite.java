package org.simpleframework.xml.core;

import java.util.Iterator;
import org.simpleframework.xml.Version;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NamespaceMap;
import org.simpleframework.xml.stream.NodeMap;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Position;

class Composite implements Converter {
    private final Context context;
    private final Criteria criteria;
    private final ObjectFactory factory;
    private final Primitive primitive;
    private final Revision revision;
    private final Type type;

    public Composite(Context context, Type type) {
        this(context, type, null);
    }

    public Composite(Context context, Type type, Class override) {
        this.factory = new ObjectFactory(context, type, override);
        this.primitive = new Primitive(context, type);
        this.criteria = new Collector(context);
        this.revision = new Revision();
        this.context = context;
        this.type = type;
    }

    public Object read(InputNode node) throws Exception {
        Instance value = this.factory.getInstance(node);
        Class type = value.getType();
        if (value.isReference()) {
            return value.getInstance();
        }
        if (this.context.isPrimitive(type)) {
            return readPrimitive(node, value);
        }
        return read(node, value, type);
    }

    public Object read(InputNode node, Object source) throws Exception {
        Schema schema = this.context.getSchema(source.getClass());
        Caller caller = schema.getCaller();
        read(node, source, schema);
        this.criteria.commit(source);
        caller.validate(source);
        caller.commit(source);
        return readResolve(node, source, caller);
    }

    private Object read(InputNode node, Instance value, Class real) throws Exception {
        Schema schema = this.context.getSchema(real);
        Caller caller = schema.getCaller();
        Object source = read(node, schema, value);
        caller.validate(source);
        caller.commit(source);
        value.setInstance(source);
        return readResolve(node, source, caller);
    }

    private Object read(InputNode node, Schema schema, Instance value) throws Exception {
        if (schema.getCreator().isDefault()) {
            return readDefault(node, schema, value);
        }
        read(node, null, schema);
        return readConstructor(node, schema, value);
    }

    private Object readDefault(InputNode node, Schema schema, Instance value) throws Exception {
        Object source = value.getInstance();
        if (value != null) {
            value.setInstance(source);
            read(node, source, schema);
            this.criteria.commit(source);
        }
        return source;
    }

    private Object readConstructor(InputNode node, Schema schema, Instance value) throws Exception {
        Object source = schema.getCreator().getInstance(this.context, this.criteria);
        if (value != null) {
            value.setInstance(source);
            this.criteria.commit(source);
        }
        return source;
    }

    private Object readPrimitive(InputNode node, Instance value) throws Exception {
        Class type = value.getType();
        Object result = this.primitive.read(node, type);
        if (type != null) {
            value.setInstance(result);
        }
        return result;
    }

    private Object readResolve(InputNode node, Object source, Caller caller) throws Exception {
        if (source == null) {
            return source;
        }
        Position line = node.getPosition();
        Object resolve = caller.resolve(source);
        if (this.type.getType().isAssignableFrom(resolve.getClass())) {
            return resolve;
        }
        throw new ElementException("Type %s does not match %s at %s", resolve.getClass(), this.type.getType(), line);
    }

    private void read(InputNode node, Object source, Schema schema) throws Exception {
        Section section = schema.getSection();
        readVersion(node, source, schema);
        readText(node, source, schema);
        readSection(node, source, section);
    }

    private void readSection(InputNode node, Object source, Section section) throws Exception {
        readAttributes(node, source, section);
        readElements(node, source, section);
    }

    private void readVersion(InputNode node, Object source, Schema schema) throws Exception {
        Label label = schema.getVersion();
        Class expect = this.type.getType();
        if (label != null) {
            InputNode value = (InputNode) node.getAttributes().remove(label.getName());
            if (value != null) {
                readVersion(value, source, label);
                return;
            }
            Version version = this.context.getVersion(expect);
            Double start = Double.valueOf(this.revision.getDefault());
            Double expected = Double.valueOf(version.revision());
            this.criteria.set(label, start);
            this.revision.compare(expected, start);
        }
    }

    private void readVersion(InputNode node, Object source, Label label) throws Exception {
        Object value = readInstance(node, source, label);
        Class expect = this.type.getType();
        if (value != null) {
            Double actual = Double.valueOf(this.context.getVersion(expect).revision());
            if (!value.equals(this.revision)) {
                this.revision.compare(actual, value);
            }
        }
    }

    private void readAttributes(InputNode node, Object source, Section section) throws Exception {
        NodeMap<InputNode> list = node.getAttributes();
        LabelMap map = section.getAttributes();
        Iterator i$ = list.iterator();
        while (i$.hasNext()) {
            readAttribute(node.getAttribute((String) i$.next()), source, map);
        }
        validate(node, map, source);
    }

    private void readElements(InputNode node, Object source, Section section) throws Exception {
        LabelMap map = section.getElements();
        InputNode child = node.getNext();
        while (child != null) {
            Section block = section.getSection(child.getName());
            if (block != null) {
                readSection(child, source, block);
            } else {
                readElement(child, source, map);
            }
            child = node.getNext();
        }
        validate(node, map, source);
    }

    private void readText(InputNode node, Object source, Schema schema) throws Exception {
        Label label = schema.getText();
        if (label != null) {
            readInstance(node, source, label);
        }
    }

    private void readAttribute(InputNode node, Object source, LabelMap map) throws Exception {
        Label label = map.take(node.getName());
        if (label == null) {
            Position line = node.getPosition();
            Class expect = this.context.getType(this.type, source);
            if (map.isStrict(this.context) && this.revision.isEqual()) {
                throw new AttributeException("Attribute '%s' does not have a match in %s at %s", name, expect, line);
            }
            return;
        }
        readInstance(node, source, label);
    }

    private void readElement(InputNode node, Object source, LabelMap map) throws Exception {
        String name = node.getName();
        Label label = map.take(name);
        if (label == null) {
            label = this.criteria.get(name);
        }
        if (label == null) {
            Position line = node.getPosition();
            Class expect = this.context.getType(this.type, source);
            if (map.isStrict(this.context) && this.revision.isEqual()) {
                throw new ElementException("Element '%s' does not have a match in %s at %s", name, expect, line);
            } else {
                node.skip();
                return;
            }
        }
        readUnion(node, source, map, label);
    }

    private void readUnion(InputNode node, Object source, LabelMap map, Label label) throws Exception {
        Object value = readInstance(node, source, label);
        for (String key : label.getUnion(this.context)) {
            Label union = map.take(key);
            if (label.isInline()) {
                this.criteria.set(union, value);
            }
        }
    }

    private Object readInstance(InputNode node, Object source, Label label) throws Exception {
        Object object = readVariable(node, source, label);
        if (object == null) {
            Position line = node.getPosition();
            Class expect = this.context.getType(this.type, source);
            if (label.isRequired() && this.revision.isEqual()) {
                throw new ValueRequiredException("Empty value for %s in %s at %s", label, expect, line);
            }
        } else if (object != label.getEmpty(this.context)) {
            this.criteria.set(label, object);
        }
        return object;
    }

    private Object readVariable(InputNode node, Object source, Label label) throws Exception {
        Converter reader = label.getConverter(this.context);
        String name = label.getName(this.context);
        if (label.isCollection()) {
            Variable variable = this.criteria.get(name);
            Contact contact = label.getContact();
            if (variable != null) {
                return reader.read(node, variable.getValue());
            }
            if (source != null) {
                Object value = contact.get(source);
                if (value != null) {
                    return reader.read(node, value);
                }
            }
        }
        return reader.read(node);
    }

    private void validate(InputNode node, LabelMap map, Object source) throws Exception {
        Class expect = this.context.getType(this.type, source);
        Position line = node.getPosition();
        Iterator i$ = map.iterator();
        while (i$.hasNext()) {
            Label label = (Label) i$.next();
            if (label.isRequired() && this.revision.isEqual()) {
                throw new ValueRequiredException("Unable to satisfy %s for %s at %s", label, expect, line);
            }
            Object value = label.getEmpty(this.context);
            if (value != null) {
                this.criteria.set(label, value);
            }
        }
    }

    public boolean validate(InputNode node) throws Exception {
        Instance value = this.factory.getInstance(node);
        if (value.isReference()) {
            return true;
        }
        Object result = value.setInstance(null);
        return validate(node, value.getType());
    }

    private boolean validate(InputNode node, Class type) throws Exception {
        Schema schema = this.context.getSchema(type);
        Section section = schema.getSection();
        validateText(node, schema);
        validateSection(node, section);
        return node.isElement();
    }

    private void validateSection(InputNode node, Section section) throws Exception {
        validateAttributes(node, section);
        validateElements(node, section);
    }

    private void validateAttributes(InputNode node, Section section) throws Exception {
        NodeMap<InputNode> list = node.getAttributes();
        LabelMap map = section.getAttributes();
        Iterator i$ = list.iterator();
        while (i$.hasNext()) {
            validateAttribute(node.getAttribute((String) i$.next()), map);
        }
        validate(node, map);
    }

    private void validateElements(InputNode node, Section section) throws Exception {
        LabelMap map = section.getElements();
        InputNode next = node.getNext();
        while (next != null) {
            Section child = section.getSection(next.getName());
            if (child != null) {
                validateSection(next, child);
            } else {
                validateElement(next, map);
            }
            next = node.getNext();
        }
        validate(node, map);
    }

    private void validateText(InputNode node, Schema schema) throws Exception {
        Label label = schema.getText();
        if (label != null) {
            validate(node, label);
        }
    }

    private void validateAttribute(InputNode node, LabelMap map) throws Exception {
        Position line = node.getPosition();
        Label label = map.take(node.getName());
        if (label == null) {
            Class expect = this.type.getType();
            if (map.isStrict(this.context) && this.revision.isEqual()) {
                throw new AttributeException("Attribute '%s' does not exist for %s at %s", name, expect, line);
            }
            return;
        }
        validate(node, label);
    }

    private void validateElement(InputNode node, LabelMap map) throws Exception {
        String name = node.getName();
        Label label = map.take(name);
        if (label == null) {
            label = this.criteria.get(name);
        }
        if (label == null) {
            Position line = node.getPosition();
            Class expect = this.type.getType();
            if (map.isStrict(this.context) && this.revision.isEqual()) {
                throw new ElementException("Element '%s' does not exist for %s at %s", name, expect, line);
            } else {
                node.skip();
                return;
            }
        }
        validateUnion(node, map, label);
    }

    private void validateUnion(InputNode node, LabelMap map, Label label) throws Exception {
        for (String key : label.getUnion(this.context)) {
            Label union = map.take(key);
            if (union != null && label.isInline()) {
                this.criteria.set(union, null);
            }
        }
        validate(node, label);
    }

    private void validate(InputNode node, Label label) throws Exception {
        Converter reader = label.getConverter(this.context);
        Position line = node.getPosition();
        Class expect = this.type.getType();
        if (reader.validate(node)) {
            this.criteria.set(label, null);
        } else {
            throw new PersistenceException("Invalid value for %s in %s at %s", label, expect, line);
        }
    }

    private void validate(InputNode node, LabelMap map) throws Exception {
        Position line = node.getPosition();
        Iterator i$ = map.iterator();
        while (i$.hasNext()) {
            Label label = (Label) i$.next();
            Class expect = this.type.getType();
            if (label.isRequired() && this.revision.isEqual()) {
                throw new ValueRequiredException("Unable to satisfy %s for %s at %s", label, expect, line);
            }
        }
    }

    public void write(OutputNode node, Object source) throws Exception {
        Schema schema = this.context.getSchema(source.getClass());
        Caller caller = schema.getCaller();
        try {
            if (schema.isPrimitive()) {
                this.primitive.write(node, source);
            } else {
                caller.persist(source);
                write(node, source, schema);
            }
            caller.complete(source);
        } catch (Throwable th) {
            caller.complete(source);
        }
    }

    private void write(OutputNode node, Object source, Schema schema) throws Exception {
        Section section = schema.getSection();
        writeVersion(node, source, schema);
        writeSection(node, source, section);
        writeText(node, source, schema);
    }

    private void writeSection(OutputNode node, Object source, Section section) throws Exception {
        NamespaceMap scope = node.getNamespaces();
        String prefix = section.getPrefix();
        if (prefix != null) {
            String reference = scope.getReference(prefix);
            if (reference == null) {
                throw new ElementException("Namespace prefix '%s' in %s is not in scope", prefix, this.type);
            }
            node.setReference(reference);
        }
        writeAttributes(node, source, section);
        writeElements(node, source, section);
    }

    private void writeVersion(OutputNode node, Object source, Schema schema) throws Exception {
        Version version = schema.getRevision();
        Label label = schema.getVersion();
        if (version != null) {
            Double start = Double.valueOf(this.revision.getDefault());
            Double value = Double.valueOf(version.revision());
            if (!this.revision.compare(value, start)) {
                writeAttribute(node, value, label);
            } else if (label.isRequired()) {
                writeAttribute(node, value, label);
            }
        }
    }

    private void writeAttributes(OutputNode node, Object source, Section section) throws Exception {
        Iterator i$ = section.getAttributes().iterator();
        while (i$.hasNext()) {
            Label label = (Label) i$.next();
            Object value = label.getContact().get(source);
            Class expect = this.context.getType(this.type, source);
            if (value == null) {
                value = label.getEmpty(this.context);
            }
            if (value == null && label.isRequired()) {
                throw new AttributeException("Value for %s is null in %s", label, expect);
            }
            writeAttribute(node, value, label);
        }
    }

    private void writeElements(OutputNode node, Object source, Section section) throws Exception {
        for (String name : section) {
            Section child = section.getSection(name);
            if (child != null) {
                writeSection(node.getChild(name), source, child);
            } else {
                Label label = section.getElement(name);
                Class expect = this.context.getType(this.type, source);
                if (this.criteria.get(name) != null) {
                    continue;
                } else if (label == null) {
                    throw new ElementException("Element '%s' not defined in %s", name, expect);
                } else {
                    writeUnion(node, source, section, label);
                }
            }
        }
    }

    private void writeUnion(OutputNode node, Object source, Section section, Label label) throws Exception {
        Object value = label.getContact().get(source);
        Class expect = this.context.getType(this.type, source);
        if (value == null && label.isRequired()) {
            throw new ElementException("Value for %s is null in %s", label, expect);
        }
        Object replace = writeReplace(value);
        if (replace != null) {
            writeElement(node, replace, label);
        }
        for (String name : label.getUnion(this.context)) {
            Label union = section.getElement(name);
            if (union != null) {
                this.criteria.set(union, replace);
            }
        }
    }

    private Object writeReplace(Object source) throws Exception {
        if (source == null) {
            return source;
        }
        return this.context.getCaller(source.getClass()).replace(source);
    }

    private void writeText(OutputNode node, Object source, Schema schema) throws Exception {
        Label label = schema.getText();
        if (label != null) {
            Object value = label.getContact().get(source);
            Class expect = this.context.getType(this.type, source);
            if (value == null) {
                value = label.getEmpty(this.context);
            }
            if (value == null && label.isRequired()) {
                throw new TextException("Value for %s is null in %s", label, expect);
            } else {
                writeText(node, value, label);
            }
        }
    }

    private void writeAttribute(OutputNode node, Object value, Label label) throws Exception {
        if (value != null) {
            label.getDecorator().decorate(node.setAttribute(label.getName(this.context), this.factory.getText(value)));
        }
    }

    private void writeElement(OutputNode node, Object value, Label label) throws Exception {
        if (value != null) {
            Class real = value.getClass();
            Label match = label.getLabel(real);
            String name = match.getName(this.context);
            Type type = label.getType(real);
            OutputNode next = node.getChild(name);
            if (!match.isInline()) {
                writeNamespaces(next, type, match);
            }
            if (match.isInline() || !isOverridden(next, value, type)) {
                Converter convert = match.getConverter(this.context);
                next.setData(match.isData());
                writeElement(next, value, convert);
            }
        }
    }

    private void writeElement(OutputNode node, Object value, Converter convert) throws Exception {
        convert.write(node, value);
    }

    private void writeNamespaces(OutputNode node, Type type, Label label) throws Exception {
        label.getDecorator().decorate(node, this.context.getDecorator(type.getType()));
    }

    private void writeText(OutputNode node, Object value, Label label) throws Exception {
        if (value != null) {
            String text = this.factory.getText(value);
            node.setData(label.isData());
            node.setValue(text);
        }
    }

    private boolean isOverridden(OutputNode node, Object value, Type type) throws Exception {
        return this.factory.setOverride(type, value, node);
    }
}
