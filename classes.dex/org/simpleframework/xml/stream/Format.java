package org.simpleframework.xml.stream;

public class Format {
    private int indent;
    private String prolog;
    private Style style;

    public Format() {
        this(3);
    }

    public Format(int indent) {
        this(indent, null, null);
    }

    public Format(String prolog) {
        this(3, prolog);
    }

    public Format(int indent, String prolog) {
        this(indent, prolog, null);
    }

    public Format(Style style) {
        this(3, null, style);
    }

    public Format(int indent, Style style) {
        this(indent, null, style);
    }

    public Format(int indent, String prolog, Style style) {
        this.prolog = prolog;
        this.indent = indent;
        this.style = style;
    }

    public int getIndent() {
        return this.indent;
    }

    public String getProlog() {
        return this.prolog;
    }

    public Style getStyle() {
        return this.style;
    }
}
