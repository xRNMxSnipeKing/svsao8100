package org.simpleframework.xml.stream;

public class HyphenStyle implements Style {
    private final Builder builder = new Builder(this.style);
    private final Style style = new HyphenBuilder();

    public String getAttribute(String name) {
        return this.builder.getAttribute(name);
    }

    public void setAttribute(String name, String value) {
        this.builder.setAttribute(name, value);
    }

    public String getElement(String name) {
        return this.builder.getElement(name);
    }

    public void setElement(String name, String value) {
        this.builder.setElement(name, value);
    }
}
