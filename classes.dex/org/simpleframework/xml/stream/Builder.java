package org.simpleframework.xml.stream;

import java.util.concurrent.ConcurrentHashMap;

class Builder implements Style {
    private final Cache attributes = new Cache();
    private final Cache elements = new Cache();
    private final Style style;

    private class Cache extends ConcurrentHashMap<String, String> {
    }

    public Builder(Style style) {
        this.style = style;
    }

    public String getAttribute(String name) {
        String value = (String) this.attributes.get(name);
        if (value != null) {
            return value;
        }
        value = this.style.getAttribute(name);
        if (value != null) {
            this.attributes.put(name, value);
        }
        return value;
    }

    public String getElement(String name) {
        String value = (String) this.elements.get(name);
        if (value != null) {
            return value;
        }
        value = this.style.getElement(name);
        if (value != null) {
            this.elements.put(name, value);
        }
        return value;
    }

    public void setAttribute(String name, String value) {
        this.attributes.put(name, value);
    }

    public void setElement(String name, String value) {
        this.elements.put(name, value);
    }
}
