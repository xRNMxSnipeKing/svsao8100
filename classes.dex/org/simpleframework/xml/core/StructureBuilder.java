package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementMapUnion;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.Version;

class StructureBuilder {
    private ModelAssembler assembler;
    private LabelMap attributes;
    private ExpressionBuilder builder;
    private Comparer comparer = new Comparer();
    private LabelMap elements;
    private boolean primitive;
    private Model root;
    private Scanner scanner;
    private Label text;
    private Class type;
    private Label version;

    public StructureBuilder(Scanner scanner, Class type) throws Exception {
        this.builder = new ExpressionBuilder(type);
        this.assembler = new ModelAssembler(this.builder, type);
        this.attributes = new LabelMap(scanner);
        this.elements = new LabelMap(scanner);
        this.root = new TreeModel(scanner);
        this.scanner = scanner;
        this.type = type;
    }

    public void assemble(Class type) throws Exception {
        Order order = this.scanner.getOrder();
        if (order != null) {
            this.assembler.assemble(this.root, order);
        }
    }

    public void process(Contact field, Annotation label) throws Exception {
        if (label instanceof Attribute) {
            process(field, label, this.attributes);
        }
        if (label instanceof ElementUnion) {
            union(field, label, this.elements);
        }
        if (label instanceof ElementListUnion) {
            union(field, label, this.elements);
        }
        if (label instanceof ElementMapUnion) {
            union(field, label, this.elements);
        }
        if (label instanceof ElementList) {
            process(field, label, this.elements);
        }
        if (label instanceof ElementArray) {
            process(field, label, this.elements);
        }
        if (label instanceof ElementMap) {
            process(field, label, this.elements);
        }
        if (label instanceof Element) {
            process(field, label, this.elements);
        }
        if (label instanceof Version) {
            version(field, label);
        }
        if (label instanceof Text) {
            text(field, label);
        }
    }

    private void union(Contact field, Annotation type, LabelMap map) throws Exception {
        for (Annotation value : extract(type)) {
            Label label = LabelFactory.getInstance(field, type, value);
            String name = label.getName();
            if (map.get(name) != null) {
                throw new PersistenceException("Duplicate annotation of name '%s' on %s", name, label);
            }
            process(field, label, map);
            validate(label, name);
        }
    }

    private void process(Contact field, Annotation type, LabelMap map) throws Exception {
        Label label = LabelFactory.getInstance(field, type);
        String name = label.getName();
        if (map.get(name) != null) {
            throw new PersistenceException("Duplicate annotation of name '%s' on %s", name, field);
        }
        process(field, label, map);
        validate(label, name);
    }

    private void process(Contact field, Label label, LabelMap map) throws Exception {
        String name = label.getName();
        String path = label.getPath();
        Model model = this.root;
        if (path != null) {
            model = register(path);
        }
        model.register(label);
        map.put(name, label);
    }

    private void text(Contact field, Annotation type) throws Exception {
        Label label = LabelFactory.getInstance(field, type);
        if (this.text != null) {
            throw new TextException("Multiple text annotations in %s", type);
        } else {
            this.text = label;
        }
    }

    private void version(Contact field, Annotation type) throws Exception {
        Label label = LabelFactory.getInstance(field, type);
        if (this.version != null) {
            throw new AttributeException("Multiple version annotations in %s", type);
        } else {
            this.version = label;
        }
    }

    private Annotation[] extract(Annotation label) throws Exception {
        Method[] list = label.annotationType().getDeclaredMethods();
        if (list.length == 1) {
            return (Annotation[]) list[0].invoke(label, new Object[0]);
        }
        throw new UnionException("Annotation '%s' is not a valid union for %s", label, this.type);
    }

    public Structure build(Class type) {
        return new Structure(this.root, this.version, this.text, this.primitive);
    }

    private boolean isElement(String path) throws Exception {
        Expression target = this.builder.build(path);
        Model model = lookup(target);
        if (model == null) {
            return false;
        }
        String name = target.getLast();
        if (model.isElement(name)) {
            return true;
        }
        return model.isModel(name);
    }

    private boolean isAttribute(String path) throws Exception {
        Expression target = this.builder.build(path);
        Model model = lookup(target);
        if (model != null) {
            return model.isAttribute(target.getLast());
        }
        return false;
    }

    private Model lookup(Expression path) throws Exception {
        Expression target = path.getPath(0, 1);
        if (path.isPath()) {
            return this.root.lookup(target);
        }
        return this.root;
    }

    private Model register(String path) throws Exception {
        Expression expression = this.builder.build(path);
        Model model = this.root.lookup(expression);
        return model != null ? model : create(expression);
    }

    private Model create(Expression path) throws Exception {
        Model model = this.root;
        while (model != null) {
            String prefix = path.getPrefix();
            String name = path.getFirst();
            int index = path.getIndex();
            if (name != null) {
                model = model.register(name, prefix, index);
            }
            if (!path.isPath()) {
                break;
            }
            path = path.getPath(1);
        }
        return model;
    }

    public void validate(Class type) throws Exception {
        Creator creator = this.scanner.getCreator();
        Order order = this.scanner.getOrder();
        validateUnions(type);
        validateElements(type, order);
        validateAttributes(type, order);
        validateParameters(creator);
        validateConstructors(type);
        validateModel(type);
        validateText(type);
    }

    private void validateModel(Class type) throws Exception {
        if (!this.root.isEmpty()) {
            this.root.validate(type);
        }
    }

    private void validateText(Class type) throws Exception {
        if (this.text != null) {
            if (!this.elements.isEmpty()) {
                throw new TextException("Elements used with %s in %s", this.text, type);
            } else if (this.root.isComposite()) {
                throw new TextException("Paths used with %s in %s", this.text, type);
            }
        } else if (this.scanner.isEmpty()) {
            this.primitive = isEmpty();
        }
    }

    private void validateUnions(Class type) throws Exception {
        Iterator it = this.elements.iterator();
        while (it.hasNext()) {
            Label label = (Label) it.next();
            Set<String> options = label.getUnion();
            Contact contact = label.getContact();
            for (String option : options) {
                Annotation union = contact.getAnnotation();
                Label other = (Label) this.elements.get(option);
                if (label.isInline() != other.isInline()) {
                    throw new UnionException("Inline must be consistent in %s for %s", union, contact);
                } else if (label.isRequired() != other.isRequired()) {
                    throw new UnionException("Required must be consistent in %s for %s", union, contact);
                }
            }
        }
    }

    private void validateElements(Class type, Order order) throws Exception {
        if (order != null) {
            String[] arr$ = order.elements();
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                if (isElement(arr$[i$])) {
                    i$++;
                } else {
                    throw new ElementException("Ordered element '%s' missing for %s", arr$[i$], type);
                }
            }
        }
    }

    private void validateAttributes(Class type, Order order) throws Exception {
        if (order != null) {
            String[] arr$ = order.attributes();
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                if (isAttribute(arr$[i$])) {
                    i$++;
                } else {
                    throw new AttributeException("Ordered attribute '%s' missing in %s", arr$[i$], type);
                }
            }
        }
    }

    private void validateConstructors(Class type) throws Exception {
        Creator creator = this.scanner.getCreator();
        List<Initializer> list = creator.getInitializers();
        if (creator.isDefault()) {
            validateConstructors(this.elements);
            validateConstructors(this.attributes);
        }
        if (!list.isEmpty()) {
            validateConstructors(this.elements, list);
            validateConstructors(this.attributes, list);
        }
    }

    private void validateConstructors(LabelMap map) throws Exception {
        Iterator i$ = map.iterator();
        while (i$.hasNext()) {
            Label label = (Label) i$.next();
            if (label != null && label.getContact().isReadOnly()) {
                throw new ConstructorException("Default constructor can not accept read only %s in %s", label, this.type);
            }
        }
    }

    private void validateConstructors(LabelMap map, List<Initializer> list) throws Exception {
        Iterator i$ = map.iterator();
        while (i$.hasNext()) {
            Label label = (Label) i$.next();
            if (label != null) {
                validateConstructor(label, list);
            }
        }
        if (list.isEmpty()) {
            throw new ConstructorException("No constructor accepts all read only values in %s", this.type);
        }
    }

    private void validateConstructor(Label label, List<Initializer> list) throws Exception {
        Iterator<Initializer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Initializer initializer = (Initializer) iterator.next();
            Contact contact = label.getContact();
            String name = label.getName();
            if (contact.isReadOnly()) {
                Parameter value = initializer.getParameter(name);
                for (String option : label.getUnion()) {
                    if (value == null) {
                        value = initializer.getParameter(option);
                    }
                }
                if (value == null) {
                    iterator.remove();
                }
            }
        }
    }

    private void validateParameters(Creator creator) throws Exception {
        for (Parameter parameter : creator.getParameters()) {
            String name = parameter.getName();
            Label label = (Label) this.elements.get(name);
            if (isEmpty(name)) {
                label = this.text;
            }
            if (label == null) {
                label = (Label) this.attributes.get(name);
                continue;
            }
            if (label == null) {
                throw new ConstructorException("Parameter '%s' does not have a match in %s", name, this.type);
            }
        }
    }

    private void validate(Label label, String name) throws Exception {
        Parameter parameter = this.scanner.getCreator().getParameter(name);
        if (parameter != null) {
            validate(label, parameter);
        }
    }

    private void validate(Label label, Parameter parameter) throws Exception {
        Set<String> options = label.getUnion();
        Contact contact = label.getContact();
        String name = parameter.getName();
        if (contact.getType() != parameter.getType()) {
            throw new ConstructorException("Type does not match %s for '%s' in %s", label, name, parameter);
        }
        if (!options.contains(name)) {
            String require = label.getName();
            if (name != require) {
                if (name == null || require == null) {
                    throw new ConstructorException("Annotation does not match %s for '%s' in %s", label, name, parameter);
                } else if (!name.equals(require)) {
                    throw new ConstructorException("Annotation does not match %s for '%s' in %s", label, name, parameter);
                }
            }
        }
        validateAnnotations(label, parameter);
    }

    private void validateAnnotations(Label label, Parameter parameter) throws Exception {
        Annotation field = label.getAnnotation();
        Annotation argument = parameter.getAnnotation();
        String name = parameter.getName();
        if (!this.comparer.equals(field, argument)) {
            if (!field.annotationType().equals(argument.annotationType())) {
                throw new ConstructorException("Annotation %s does not match %s for '%s' in %s", argument.annotationType(), field.annotationType(), name, parameter);
            }
        }
    }

    private boolean isEmpty(String value) {
        return value.length() == 0;
    }

    private boolean isEmpty() {
        if (this.text != null) {
            return false;
        }
        return this.root.isEmpty();
    }
}
