package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Version;

class Scanner implements Policy {
    private DefaultType access;
    private StructureBuilder builder;
    private String name;
    private boolean required;
    private ClassScanner scanner;
    private Structure structure;
    private Class type;

    public Scanner(Class type) throws Exception {
        this.scanner = new ClassScanner(type);
        this.builder = new StructureBuilder(this, type);
        this.type = type;
        scan(type);
    }

    public Class getType() {
        return this.type;
    }

    public Creator getCreator() {
        return this.scanner.getCreator();
    }

    public Decorator getDecorator() {
        return this.scanner.getDecorator();
    }

    public Caller getCaller(Context context) {
        return new Caller(this, context);
    }

    public Section getSection(Context context) {
        return this.structure.getSection(context);
    }

    public Version getRevision() {
        return this.structure.getRevision();
    }

    public Order getOrder() {
        return this.scanner.getOrder();
    }

    public Label getVersion() {
        return this.structure.getVersion();
    }

    public Label getText() {
        return this.structure.getText();
    }

    public String getName() {
        return this.name;
    }

    public Function getCommit() {
        return this.scanner.getCommit();
    }

    public Function getValidate() {
        return this.scanner.getValidate();
    }

    public Function getPersist() {
        return this.scanner.getPersist();
    }

    public Function getComplete() {
        return this.scanner.getComplete();
    }

    public Function getReplace() {
        return this.scanner.getReplace();
    }

    public Function getResolve() {
        return this.scanner.getResolve();
    }

    public boolean isPrimitive() {
        return this.structure.isPrimitive();
    }

    public boolean isEmpty() {
        return this.scanner.getRoot() == null;
    }

    public boolean isStrict() {
        return this.scanner.isStrict();
    }

    private void scan(Class type) throws Exception {
        root(type);
        order(type);
        access(type);
        field(type);
        method(type);
        validate(type);
        commit(type);
    }

    private void commit(Class type) throws Exception {
        if (this.structure == null) {
            this.structure = this.builder.build(type);
        }
        this.builder = null;
    }

    private void order(Class<?> type) throws Exception {
        this.builder.assemble(type);
    }

    private void validate(Class type) throws Exception {
        this.builder.validate(type);
    }

    private void root(Class<?> type) {
        String real = type.getSimpleName();
        Root root = this.scanner.getRoot();
        String text = real;
        if (root != null) {
            text = root.name();
            if (isEmpty(text)) {
                text = Reflector.getName(real);
            }
            this.name = text.intern();
        }
    }

    private void access(Class<?> cls) {
        Default holder = this.scanner.getDefault();
        if (holder != null) {
            this.required = holder.required();
            this.access = holder.value();
        }
    }

    private boolean isEmpty(String value) {
        return value.length() == 0;
    }

    private void field(Class type) throws Exception {
        Iterator i$ = new FieldScanner(type, this.access, this.required).iterator();
        while (i$.hasNext()) {
            Contact contact = (Contact) i$.next();
            Annotation label = contact.getAnnotation();
            if (label != null) {
                this.builder.process(contact, label);
            }
        }
    }

    public void method(Class type) throws Exception {
        Iterator i$ = new MethodScanner(type, this.access, this.required).iterator();
        while (i$.hasNext()) {
            Contact contact = (Contact) i$.next();
            Annotation label = contact.getAnnotation();
            if (label != null) {
                this.builder.process(contact, label);
            }
        }
    }
}
