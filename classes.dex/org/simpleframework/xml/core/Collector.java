package org.simpleframework.xml.core;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

class Collector implements Criteria {
    private final Registry alias = new Registry();
    private final Context context;
    private final Registry registry = new Registry();

    private class Registry extends LinkedHashMap<String, Variable> {
        private Registry() {
        }

        public Iterator<String> iterator() {
            return keySet().iterator();
        }
    }

    public Collector(Context context) {
        this.context = context;
    }

    public Variable get(String name) {
        return (Variable) this.registry.get(name);
    }

    public Variable resolve(String name) {
        return (Variable) this.alias.get(name);
    }

    public Variable remove(String name) throws Exception {
        Variable variable = (Variable) this.alias.remove(name);
        if (variable != null) {
            for (String option : variable.getUnion(this.context)) {
                this.registry.remove(option);
                this.alias.remove(option);
            }
            this.registry.remove(name);
        }
        return variable;
    }

    public Iterator<String> iterator() {
        return this.registry.iterator();
    }

    public void set(Label label, Object value) throws Exception {
        Variable variable = new Variable(label, value);
        if (label != null) {
            Set<String> options = label.getUnion(this.context);
            String name = label.getName(this.context);
            if (!this.registry.containsKey(name)) {
                this.registry.put(name, variable);
                this.alias.put(name, variable);
            }
            for (String option : options) {
                this.alias.put(option, variable);
            }
        }
    }

    public void commit(Object source) throws Exception {
        for (Variable entry : this.registry.values()) {
            entry.getContact().set(source, entry.getValue());
        }
    }
}
