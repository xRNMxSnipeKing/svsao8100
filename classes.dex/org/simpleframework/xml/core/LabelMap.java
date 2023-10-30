package org.simpleframework.xml.core;

import java.util.Iterator;
import java.util.LinkedHashMap;

class LabelMap extends LinkedHashMap<String, Label> implements Iterable<Label> {
    private final Policy policy;

    public LabelMap() {
        this(null);
    }

    public LabelMap(Policy policy) {
        this.policy = policy;
    }

    public Iterator<Label> iterator() {
        return values().iterator();
    }

    public Label take(String name) {
        return (Label) remove(name);
    }

    public LabelMap build(Context context) throws Exception {
        LabelMap clone = new LabelMap(this.policy);
        Iterator i$ = iterator();
        while (i$.hasNext()) {
            Label label = (Label) i$.next();
            if (label != null) {
                clone.put(label.getName(context), label);
            }
        }
        return clone;
    }

    public boolean isStrict(Context context) {
        if (this.policy == null) {
            return context.isStrict();
        }
        return context.isStrict() && this.policy.isStrict();
    }
}
