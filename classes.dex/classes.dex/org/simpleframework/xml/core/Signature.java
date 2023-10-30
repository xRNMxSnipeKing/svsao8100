package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

class Signature extends LinkedHashMap<String, Parameter> {
    private final Class type;

    public Signature(Class type) {
        this.type = type;
    }

    public Parameter getParameter(int ordinal) {
        return (Parameter) getParameters().get(ordinal);
    }

    public Parameter getParameter(String name) {
        return (Parameter) get(name);
    }

    public List<Parameter> getParameters() {
        return new ArrayList(values());
    }

    public Signature getSignature(Context context) throws Exception {
        Signature signature = new Signature(this.type);
        for (Parameter value : values()) {
            signature.put(value.getName(context), value);
        }
        return signature;
    }

    public Class getType() {
        return this.type;
    }
}
