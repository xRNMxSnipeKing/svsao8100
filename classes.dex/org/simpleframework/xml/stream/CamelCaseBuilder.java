package org.simpleframework.xml.stream;

class CamelCaseBuilder implements Style {
    private final boolean attribute;
    private final boolean element;

    private class Attribute extends Splitter {
        private Attribute(String source) {
            super(source);
        }

        protected void parse(char[] text, int off, int len) {
            if (CamelCaseBuilder.this.attribute) {
                text[off] = toUpper(text[off]);
            }
        }

        protected void commit(char[] text, int off, int len) {
            this.builder.append(text, off, len);
        }
    }

    private class Element extends Attribute {
        private Element(String source) {
            super(source);
        }

        protected void parse(char[] text, int off, int len) {
            if (CamelCaseBuilder.this.element) {
                text[off] = toUpper(text[off]);
            }
        }
    }

    public CamelCaseBuilder(boolean element, boolean attribute) {
        this.attribute = attribute;
        this.element = element;
    }

    public String getAttribute(String name) {
        if (name != null) {
            return new Attribute(name).process();
        }
        return null;
    }

    public String getElement(String name) {
        if (name != null) {
            return new Element(name).process();
        }
        return null;
    }
}
