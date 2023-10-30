package org.simpleframework.xml.core;

enum MethodType {
    GET(3),
    IS(2),
    SET(3),
    NONE(0);
    
    private int prefix;

    private MethodType(int prefix) {
        this.prefix = prefix;
    }

    public int getPrefix() {
        return this.prefix;
    }
}
