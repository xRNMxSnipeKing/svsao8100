package org.simpleframework.xml.stream;

abstract class Splitter {
    protected StringBuilder builder = new StringBuilder();
    protected int count;
    protected int off;
    protected char[] text;

    protected abstract void commit(char[] cArr, int i, int i2);

    protected abstract void parse(char[] cArr, int i, int i2);

    public Splitter(String source) {
        this.text = source.toCharArray();
        this.count = this.text.length;
    }

    public String process() {
        while (this.off < this.count) {
            while (this.off < this.count && isSpecial(this.text[this.off])) {
                this.off++;
            }
            if (!acronym()) {
                token();
                number();
            }
        }
        return this.builder.toString();
    }

    private void token() {
        int mark = this.off;
        while (mark < this.count) {
            char ch = this.text[mark];
            if (!isLetter(ch) || (mark > this.off && isUpper(ch))) {
                break;
            }
            mark++;
        }
        if (mark > this.off) {
            parse(this.text, this.off, mark - this.off);
            commit(this.text, this.off, mark - this.off);
        }
        this.off = mark;
    }

    private boolean acronym() {
        int mark = this.off;
        int size = 0;
        while (mark < this.count && isUpper(this.text[mark])) {
            size++;
            mark++;
        }
        if (size > 1) {
            if (mark < this.count && isUpper(this.text[mark - 1])) {
                mark--;
            }
            commit(this.text, this.off, mark - this.off);
            this.off = mark;
        }
        if (size > 1) {
            return true;
        }
        return false;
    }

    private boolean number() {
        int mark = this.off;
        int size = 0;
        while (mark < this.count && isDigit(this.text[mark])) {
            size++;
            mark++;
        }
        if (size > 0) {
            commit(this.text, this.off, mark - this.off);
        }
        this.off = mark;
        return size > 0;
    }

    private boolean isLetter(char ch) {
        return Character.isLetter(ch);
    }

    private boolean isSpecial(char ch) {
        return !Character.isLetterOrDigit(ch);
    }

    private boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    private boolean isUpper(char ch) {
        return Character.isUpperCase(ch);
    }

    protected char toUpper(char ch) {
        return Character.toUpperCase(ch);
    }

    protected char toLower(char ch) {
        return Character.toLowerCase(ch);
    }
}
