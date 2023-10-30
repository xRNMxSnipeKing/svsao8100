package org.simpleframework.xml.core;

import java.lang.reflect.Method;
import java.util.Map;

class Function {
    private final boolean contextual;
    private final Method method;

    public Function(Method method) {
        this(method, false);
    }

    public Function(Method method, boolean contextual) {
        this.contextual = contextual;
        this.method = method;
    }

    public Object call(Context context, Object source) throws Exception {
        if (source == null) {
            return null;
        }
        Map table = context.getSession().getMap();
        if (!this.contextual) {
            return this.method.invoke(source, new Object[0]);
        }
        return this.method.invoke(source, new Object[]{table});
    }
}
