package org.simpleframework.xml.core;

import java.util.HashMap;

class ExpressionBuilder {
    private final Cache cache = new Cache();
    private final Class type;

    private class Cache extends HashMap<String, Expression> {
    }

    public ExpressionBuilder(Class type) {
        this.type = type;
    }

    public Expression build(String path) throws Exception {
        Expression expression = (Expression) this.cache.get(path);
        if (expression == null) {
            return create(path);
        }
        return expression;
    }

    private Expression create(String path) throws Exception {
        Expression expression = new PathParser(this.type, path);
        if (this.cache != null) {
            this.cache.put(path, expression);
        }
        return expression;
    }
}
