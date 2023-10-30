package org.simpleframework.xml.core;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;

class Initializer {
    private final Constructor factory;
    private final List<Parameter> list;
    private final Signature signature;

    public Initializer(Constructor factory, Signature signature) {
        this.list = signature.getParameters();
        this.signature = signature;
        this.factory = factory;
    }

    public boolean isDefault() {
        return this.signature.size() == 0;
    }

    public Parameter getParameter(String name) {
        return (Parameter) this.signature.get(name);
    }

    public Object getInstance(Context context) throws Exception {
        if (!this.factory.isAccessible()) {
            this.factory.setAccessible(true);
        }
        return this.factory.newInstance(new Object[0]);
    }

    public Object getInstance(Context context, Criteria criteria) throws Exception {
        Object[] values = this.list.toArray();
        for (int i = 0; i < this.list.size(); i++) {
            values[i] = getVariable(context, criteria, i);
        }
        return getInstance(values);
    }

    private Object getVariable(Context context, Criteria criteria, int index) throws Exception {
        Variable variable = criteria.remove(((Parameter) this.list.get(index)).getName(context));
        if (variable != null) {
            return variable.getValue();
        }
        return null;
    }

    public double getScore(Context context, Criteria criteria) throws Exception {
        Signature match = this.signature.getSignature(context);
        for (String name : criteria) {
            Label label = criteria.resolve(name);
            if (label != null) {
                Set<String> options = label.getUnion(context);
                Parameter value = match.getParameter(name);
                Contact contact = label.getContact();
                for (String option : options) {
                    if (value == null) {
                        value = match.getParameter(option);
                    }
                }
                if (contact.isReadOnly() && value == null) {
                    return -1.0d;
                }
            }
        }
        return getPercentage(context, criteria);
    }

    private double getPercentage(Context context, Criteria criteria) throws Exception {
        double score = 0.0d;
        for (Parameter value : this.list) {
            if (criteria.resolve(value.getName(context)) != null) {
                score += 1.0d;
            } else if (value.isRequired()) {
                return -1.0d;
            } else {
                if (value.isPrimitive()) {
                    return -1.0d;
                }
            }
        }
        return getAdjustment(context, score);
    }

    private double getAdjustment(Context context, double score) {
        double adjustment = ((double) this.list.size()) / 1000.0d;
        if (score > 0.0d) {
            return (score / ((double) this.list.size())) + adjustment;
        }
        return score / ((double) this.list.size());
    }

    private Object getInstance(Object[] list) throws Exception {
        if (!this.factory.isAccessible()) {
            this.factory.setAccessible(true);
        }
        return this.factory.newInstance(list);
    }

    public String toString() {
        return this.factory.toString();
    }
}
