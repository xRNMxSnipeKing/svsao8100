package org.simpleframework.xml.stream;

import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import java.io.BufferedWriter;
import java.io.Writer;

class Formatter {
    private static final char[] AND = new char[]{'&', 'a', 'm', 'p', ';'};
    private static final char[] CLOSE = new char[]{' ', '-', '-', '>'};
    private static final char[] DOUBLE = new char[]{'&', 'q', 'u', 'o', 't', ';'};
    private static final char[] GREATER = new char[]{'&', 'g', 't', ';'};
    private static final char[] LESS = new char[]{'&', 'l', 't', ';'};
    private static final char[] NAMESPACE = new char[]{'x', 'm', 'l', 'n', 's'};
    private static final char[] OPEN = new char[]{'<', '!', '-', '-', ' '};
    private static final char[] SINGLE = new char[]{'&', 'a', 'p', 'o', 's', ';'};
    private OutputBuffer buffer = new OutputBuffer();
    private Indenter indenter;
    private Tag last;
    private String prolog;
    private Writer result;

    private enum Tag {
        COMMENT,
        START,
        TEXT,
        END
    }

    public Formatter(Writer result, Format format) {
        this.result = new BufferedWriter(result, 1024);
        this.indenter = new Indenter(format);
        this.prolog = format.getProlog();
    }

    public void writeProlog() throws Exception {
        if (this.prolog != null) {
            write(this.prolog);
            write("\n");
        }
    }

    public void writeComment(String comment) throws Exception {
        String text = this.indenter.top();
        if (this.last == Tag.START) {
            append('>');
        }
        if (text != null) {
            append(text);
            append(OPEN);
            append(comment);
            append(CLOSE);
        }
        this.last = Tag.COMMENT;
    }

    public void writeStart(String name, String prefix) throws Exception {
        String text = this.indenter.push();
        if (this.last == Tag.START) {
            append('>');
        }
        flush();
        append(text);
        append('<');
        if (!isEmpty(prefix)) {
            append(prefix);
            append(':');
        }
        append(name);
        this.last = Tag.START;
    }

    public void writeAttribute(String name, String value, String prefix) throws Exception {
        if (this.last != Tag.START) {
            throw new NodeException("Start element required");
        }
        write(' ');
        write(name, prefix);
        write('=');
        write('\"');
        escape(value);
        write('\"');
    }

    public void writeNamespace(String reference, String prefix) throws Exception {
        if (this.last != Tag.START) {
            throw new NodeException("Start element required");
        }
        write(' ');
        write(NAMESPACE);
        if (!isEmpty(prefix)) {
            write(':');
            write(prefix);
        }
        write('=');
        write('\"');
        escape(reference);
        write('\"');
    }

    public void writeText(String text) throws Exception {
        writeText(text, Mode.ESCAPE);
    }

    public void writeText(String text, Mode mode) throws Exception {
        if (this.last == Tag.START) {
            write('>');
        }
        if (mode == Mode.DATA) {
            data(text);
        } else {
            escape(text);
        }
        this.last = Tag.TEXT;
    }

    public void writeEnd(String name, String prefix) throws Exception {
        String text = this.indenter.pop();
        if (this.last == Tag.START) {
            write('/');
            write('>');
        } else {
            if (this.last != Tag.TEXT) {
                write(text);
            }
            if (this.last != Tag.START) {
                write('<');
                write('/');
                write(name, prefix);
                write('>');
            }
        }
        this.last = Tag.END;
    }

    private void write(char ch) throws Exception {
        this.buffer.write(this.result);
        this.buffer.clear();
        this.result.write(ch);
    }

    private void write(char[] plain) throws Exception {
        this.buffer.write(this.result);
        this.buffer.clear();
        this.result.write(plain);
    }

    private void write(String plain) throws Exception {
        this.buffer.write(this.result);
        this.buffer.clear();
        this.result.write(plain);
    }

    private void write(String plain, String prefix) throws Exception {
        this.buffer.write(this.result);
        this.buffer.clear();
        if (!isEmpty(prefix)) {
            this.result.write(prefix);
            this.result.write(58);
        }
        this.result.write(plain);
    }

    private void append(char ch) throws Exception {
        this.buffer.append(ch);
    }

    private void append(char[] plain) throws Exception {
        this.buffer.append(plain);
    }

    private void append(String plain) throws Exception {
        this.buffer.append(plain);
    }

    private void data(String value) throws Exception {
        write("<![CDATA[");
        write(value);
        write("]]>");
    }

    private void escape(String value) throws Exception {
        int size = value.length();
        for (int i = 0; i < size; i++) {
            escape(value.charAt(i));
        }
    }

    private void escape(char ch) throws Exception {
        char[] text = symbol(ch);
        if (text != null) {
            write(text);
        } else {
            write(ch);
        }
    }

    public void flush() throws Exception {
        this.buffer.write(this.result);
        this.buffer.clear();
        this.result.flush();
    }

    private String unicode(char ch) {
        return Integer.toString(ch);
    }

    private boolean isEmpty(String value) {
        if (value == null || value.length() == 0) {
            return true;
        }
        return false;
    }

    private boolean isText(char ch) {
        switch (ch) {
            case '\t':
            case '\n':
            case CompanionSession.LRCERROR_EXPIRED_COMMAND /*13*/:
            case ' ':
                return true;
            default:
                if (ch <= ' ' || ch > '~') {
                    return false;
                }
                if (ch != '÷') {
                    return true;
                }
                return false;
        }
    }

    private char[] symbol(char ch) {
        switch (ch) {
            case EDSV2MediaType.MEDIATYPE_XBOXGAMETRAILER /*34*/:
                return DOUBLE;
            case '&':
                return AND;
            case '\'':
                return SINGLE;
            case EDSV2MediaType.MEDIATYPE_XBOXMOBILECONSUMABLE /*60*/:
                return LESS;
            case EDSV2MediaType.MEDIATYPE_METROGAME /*62*/:
                return GREATER;
            default:
                return null;
        }
    }
}
