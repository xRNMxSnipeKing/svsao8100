package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.List;

class ClassCreator implements Creator {
    private final List<Initializer> list;
    private final Initializer primary;
    private final Signature signature;
    private final Class type;

    public ClassCreator(List<Initializer> list, Signature signature, Initializer primary) {
        this.type = signature.getType();
        this.signature = signature;
        this.primary = primary;
        this.list = list;
    }

    public boolean isDefault() {
        return this.primary != null;
    }

    public Object getInstance(Context context) throws Exception {
        return this.primary.getInstance(context);
    }

    public Object getInstance(Context context, Criteria criteria) throws Exception {
        Initializer initializer = getInitializer(context, criteria);
        if (initializer != null) {
            return initializer.getInstance(context, criteria);
        }
        throw new PersistenceException("Constructor not matched for %s", this.type);
    }

    private Initializer getInitializer(Context context, Criteria criteria) throws Exception {
        Initializer result = this.primary;
        double max = 0.0d;
        for (Initializer initializer : this.list) {
            double score = initializer.getScore(context, criteria);
            if (score > max) {
                result = initializer;
                max = score;
            }
        }
        return result;
    }

    public Parameter getParameter(String name) {
        return (Parameter) this.signature.get(name);
    }

    public List<Parameter> getParameters() {
        return this.signature.getParameters();
    }

    public List<Initializer> getInitializers() {
        return new ArrayList(this.list);
    }

    public String toString() {
        return String.format("creator for %s", new Object[]{this.type});
    }
}
