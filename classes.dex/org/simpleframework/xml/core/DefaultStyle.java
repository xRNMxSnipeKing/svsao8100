package org.simpleframework.xml.core;

import org.simpleframework.xml.stream.Style;

class DefaultStyle implements Style {
    DefaultStyle() {
    }

    public String getAttribute(String name) {
        return name;
    }

    public String getElement(String name) {
        return name;
    }
}
