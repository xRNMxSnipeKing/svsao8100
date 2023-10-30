package org.simpleframework.xml.transform;

import org.simpleframework.xml.util.Cache;
import org.simpleframework.xml.util.WeakCache;

public class Transformer {
    private final TransformCache cache = new TransformCache();
    private final Cache error = new WeakCache();
    private final Matcher matcher;

    public Transformer(Matcher matcher) {
        this.matcher = new DefaultMatcher(matcher);
    }

    public Object read(String value, Class type) throws Exception {
        Transform transform = lookup(type);
        if (transform != null) {
            return transform.read(value);
        }
        throw new TransformException("Transform of %s not supported", type);
    }

    public String write(Object value, Class type) throws Exception {
        Transform transform = lookup(type);
        if (transform != null) {
            return transform.write(value);
        }
        throw new TransformException("Transform of %s not supported", type);
    }

    public boolean valid(Class type) throws Exception {
        return lookup(type) != null;
    }

    private Transform lookup(Class type) throws Exception {
        Transform transform = (Transform) this.cache.fetch(type);
        if (transform != null) {
            return transform;
        }
        if (this.error.contains(type)) {
            return null;
        }
        return match(type);
    }

    private Transform match(Class type) throws Exception {
        Transform transform = this.matcher.match(type);
        if (transform != null) {
            this.cache.cache(type, transform);
        } else {
            this.error.cache(type, this);
        }
        return transform;
    }
}
