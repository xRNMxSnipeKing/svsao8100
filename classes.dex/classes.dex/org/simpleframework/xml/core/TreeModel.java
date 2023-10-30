package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class TreeModel implements Model {
    private final LabelMap attributes;
    private final LabelMap elements;
    private final int index;
    private final ModelMap models;
    private final String name;
    private final OrderList order;
    private final Policy policy;
    private final String prefix;

    private static class OrderList extends ArrayList<String> {
    }

    public TreeModel(Policy policy) {
        this(policy, null, null, 1);
    }

    public TreeModel(Policy policy, String name, String prefix, int index) {
        this.attributes = new LabelMap(policy);
        this.elements = new LabelMap(policy);
        this.order = new OrderList();
        this.models = new ModelMap();
        this.policy = policy;
        this.prefix = prefix;
        this.index = index;
        this.name = name;
    }

    public Model lookup(Expression path) {
        Model model = lookup(path.getFirst(), path.getIndex());
        if (!path.isPath()) {
            return model;
        }
        path = path.getPath(1, 0);
        if (model != null) {
            return model.lookup(path);
        }
        return model;
    }

    public void registerElement(String name) throws Exception {
        if (!this.order.contains(name)) {
            this.order.add(name);
        }
        this.elements.put(name, null);
    }

    public void registerAttribute(String name) throws Exception {
        this.attributes.put(name, null);
    }

    public void registerAttribute(Label label) throws Exception {
        String name = label.getName();
        if (this.attributes.get(name) != null) {
            throw new PersistenceException("Duplicate annotation of name '%s' on %s", name, label);
        } else {
            this.attributes.put(name, label);
        }
    }

    public void registerElement(Label label) throws Exception {
        String name = label.getName();
        if (this.elements.get(name) != null) {
            throw new PersistenceException("Duplicate annotation of name '%s' on %s", name, label);
        }
        if (!this.order.contains(name)) {
            this.order.add(name);
        }
        this.elements.put(name, label);
    }

    public ModelMap buildModels(Context context) throws Exception {
        return this.models.build(context);
    }

    public LabelMap buildAttributes(Context context) throws Exception {
        return this.attributes.build(context);
    }

    public LabelMap buildElements(Context context) throws Exception {
        return this.elements.build(context);
    }

    public boolean isModel(String name) {
        return this.models.containsKey(name);
    }

    public boolean isElement(String name) {
        return this.elements.containsKey(name);
    }

    public boolean isAttribute(String name) {
        return this.attributes.containsKey(name);
    }

    public Iterator<String> iterator() {
        List<String> list = new ArrayList();
        Iterator i$ = this.order.iterator();
        while (i$.hasNext()) {
            list.add((String) i$.next());
        }
        return list.iterator();
    }

    public void validate(Class type) throws Exception {
        validateAttributes(type);
        validateElements(type);
        validateModels(type);
    }

    private void validateModels(Class type) throws Exception {
        Iterator it = this.models.iterator();
        while (it.hasNext()) {
            int count = 1;
            Iterator i$ = ((ModelList) it.next()).iterator();
            while (i$.hasNext()) {
                Model model = (Model) i$.next();
                if (model != null) {
                    String name = model.getName();
                    int count2 = count + 1;
                    if (model.getIndex() != count) {
                        throw new ElementException("Path section '%s[%s]' is out of sequence in %s", name, Integer.valueOf(model.getIndex()), type);
                    } else {
                        model.validate(type);
                        count = count2;
                    }
                }
            }
        }
    }

    private void validateAttributes(Class type) throws Exception {
        for (String name : this.attributes.keySet()) {
            if (((Label) this.attributes.get(name)) == null) {
                throw new AttributeException("Ordered attribute '%s' does not exist in %s", (String) i$.next(), type);
            }
        }
    }

    private void validateElements(Class type) throws Exception {
        for (String name : this.elements.keySet()) {
            ModelList list = (ModelList) this.models.get(name);
            Label label = (Label) this.elements.get(name);
            if (list == null && label == null) {
                throw new ElementException("Ordered element '%s' does not exist in %s", name, type);
            } else if (list != null && label != null && !list.isEmpty()) {
                throw new ElementException("Element '%s' is also a path name in %s", name, type);
            }
        }
    }

    public void register(Label label) throws Exception {
        if (label.isAttribute()) {
            registerAttribute(label);
        } else {
            registerElement(label);
        }
    }

    public Model lookup(String name, int index) {
        return this.models.lookup(name, index);
    }

    public Model register(String name, String prefix, int index) throws Exception {
        Model model = this.models.lookup(name, index);
        if (model == null) {
            return create(name, prefix, index);
        }
        return model;
    }

    private Model create(String name, String prefix, int index) throws Exception {
        Model model = new TreeModel(this.policy, name, prefix, index);
        if (name != null) {
            this.models.register(name, model);
            this.order.add(name);
        }
        return model;
    }

    public boolean isComposite() {
        Iterator it = this.models.iterator();
        while (it.hasNext()) {
            Iterator i$ = ((ModelList) it.next()).iterator();
            while (i$.hasNext()) {
                Model model = (Model) i$.next();
                if (model != null && !model.isEmpty()) {
                    return true;
                }
            }
        }
        if (this.models.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isEmpty() {
        if (this.elements.isEmpty() && this.attributes.isEmpty() && !isComposite()) {
            return true;
        }
        return false;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public String toString() {
        return String.format("model '%s[%s]'", new Object[]{this.name, Integer.valueOf(this.index)});
    }
}
