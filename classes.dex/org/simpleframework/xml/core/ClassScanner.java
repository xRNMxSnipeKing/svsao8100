package org.simpleframework.xml.core;

import java.lang.reflect.Method;
import java.util.Map;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

class ClassScanner {
    private Default access;
    private Function commit;
    private Function complete;
    private NamespaceDecorator decorator = new NamespaceDecorator();
    private Namespace namespace;
    private Order order;
    private Function persist;
    private Function replace;
    private Function resolve;
    private Root root;
    private ConstructorScanner scanner;
    private Function validate;

    public ClassScanner(Class type) throws Exception {
        this.scanner = new ConstructorScanner(type);
        scan(type);
    }

    public Creator getCreator() {
        return this.scanner.getCreator();
    }

    public Decorator getDecorator() {
        return this.decorator;
    }

    public Default getDefault() {
        return this.access;
    }

    public Order getOrder() {
        return this.order;
    }

    public Root getRoot() {
        return this.root;
    }

    public Function getCommit() {
        return this.commit;
    }

    public Function getValidate() {
        return this.validate;
    }

    public Function getPersist() {
        return this.persist;
    }

    public Function getComplete() {
        return this.complete;
    }

    public Function getReplace() {
        return this.replace;
    }

    public Function getResolve() {
        return this.resolve;
    }

    public boolean isStrict() {
        if (this.root != null) {
            return this.root.strict();
        }
        return true;
    }

    private void scan(Class type) throws Exception {
        Class real = type;
        while (type != null) {
            global(type);
            scope(type);
            scan(real, type);
            type = type.getSuperclass();
        }
        process(real);
    }

    private void global(Class type) throws Exception {
        if (this.namespace == null) {
            namespace(type);
        }
        if (this.root == null) {
            root(type);
        }
        if (this.order == null) {
            order(type);
        }
        if (this.access == null) {
            access(type);
        }
    }

    private void scan(Class real, Class type) throws Exception {
        Method[] method = type.getDeclaredMethods();
        for (Method scan : method) {
            scan(scan);
        }
    }

    private void root(Class<?> type) {
        if (type.isAnnotationPresent(Root.class)) {
            this.root = (Root) type.getAnnotation(Root.class);
        }
    }

    private void order(Class<?> type) {
        if (type.isAnnotationPresent(Order.class)) {
            this.order = (Order) type.getAnnotation(Order.class);
        }
    }

    private void access(Class<?> type) {
        if (type.isAnnotationPresent(Default.class)) {
            this.access = (Default) type.getAnnotation(Default.class);
        }
    }

    private void namespace(Class<?> type) {
        if (type.isAnnotationPresent(Namespace.class)) {
            this.namespace = (Namespace) type.getAnnotation(Namespace.class);
            if (this.namespace != null) {
                this.decorator.add(this.namespace);
            }
        }
    }

    private void scope(Class<?> type) {
        if (type.isAnnotationPresent(NamespaceList.class)) {
            for (Namespace name : ((NamespaceList) type.getAnnotation(NamespaceList.class)).value()) {
                this.decorator.add(name);
            }
        }
    }

    private void process(Class type) throws Exception {
        if (this.namespace != null) {
            this.decorator.set(this.namespace);
        }
    }

    private void scan(Method method) {
        if (this.commit == null) {
            commit(method);
        }
        if (this.validate == null) {
            validate(method);
        }
        if (this.persist == null) {
            persist(method);
        }
        if (this.complete == null) {
            complete(method);
        }
        if (this.replace == null) {
            replace(method);
        }
        if (this.resolve == null) {
            resolve(method);
        }
    }

    private void replace(Method method) {
        if (method.getAnnotation(Replace.class) != null) {
            this.replace = getFunction(method);
        }
    }

    private void resolve(Method method) {
        if (method.getAnnotation(Resolve.class) != null) {
            this.resolve = getFunction(method);
        }
    }

    private void commit(Method method) {
        if (method.getAnnotation(Commit.class) != null) {
            this.commit = getFunction(method);
        }
    }

    private void validate(Method method) {
        if (method.getAnnotation(Validate.class) != null) {
            this.validate = getFunction(method);
        }
    }

    private void persist(Method method) {
        if (method.getAnnotation(Persist.class) != null) {
            this.persist = getFunction(method);
        }
    }

    private void complete(Method method) {
        if (method.getAnnotation(Complete.class) != null) {
            this.complete = getFunction(method);
        }
    }

    private Function getFunction(Method method) {
        boolean contextual = isContextual(method);
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        return new Function(method, contextual);
    }

    private boolean isContextual(Method method) {
        Class[] list = method.getParameterTypes();
        if (list.length == 1) {
            return Map.class.equals(list[0]);
        }
        return false;
    }
}
