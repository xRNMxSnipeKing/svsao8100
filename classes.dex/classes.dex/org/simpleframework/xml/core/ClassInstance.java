package org.simpleframework.xml.core;

class ClassInstance implements Instance {
    private Instantiator creator;
    private Class type;
    private Object value;

    public ClassInstance(Instantiator creator, Class type) {
        this.creator = creator;
        this.type = type;
    }

    public Object getInstance() throws Exception {
        if (this.value == null) {
            this.value = this.creator.getObject(this.type);
        }
        return this.value;
    }

    public Object setInstance(Object value) throws Exception {
        this.value = value;
        return value;
    }

    public Class getType() {
        return this.type;
    }

    public boolean isReference() {
        return false;
    }
}
