package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class PathParser implements Expression {
    private boolean attribute;
    private String cache;
    private int count;
    private char[] data;
    private LinkedList<Integer> indexes = new LinkedList();
    private LinkedList<String> names = new LinkedList();
    private int off;
    private String path;
    private LinkedList<String> prefixes = new LinkedList();
    private int start;
    private Class type;

    private class PathSection implements Expression {
        private int begin;
        private List<String> cache = new ArrayList();
        private int end;
        private String path;

        public PathSection(int index, int end) {
            this.begin = index;
            this.end = end;
        }

        public boolean isPath() {
            return this.end - this.begin >= 1;
        }

        public boolean isAttribute() {
            if (!PathParser.this.attribute || this.end < PathParser.this.names.size() - 1) {
                return false;
            }
            return true;
        }

        public int getIndex() {
            return ((Integer) PathParser.this.indexes.get(this.begin)).intValue();
        }

        public String getPrefix() {
            return (String) PathParser.this.prefixes.get(this.begin);
        }

        public String getFirst() {
            return (String) PathParser.this.names.get(this.begin);
        }

        public String getLast() {
            return (String) PathParser.this.names.get(this.end);
        }

        public Expression getPath(int from) {
            return getPath(from, 0);
        }

        public Expression getPath(int from, int trim) {
            return new PathSection(this.begin + from, this.end - trim);
        }

        public Iterator<String> iterator() {
            if (this.cache.isEmpty()) {
                for (int i = this.begin; i <= this.end; i++) {
                    String segment = (String) PathParser.this.names.get(i);
                    if (segment != null) {
                        this.cache.add(segment);
                    }
                }
            }
            return this.cache.iterator();
        }

        private String getPath() {
            int last = PathParser.this.start;
            int pos = 0;
            int i = 0;
            while (i <= this.end) {
                if (last >= PathParser.this.count) {
                    last++;
                    break;
                }
                int last2 = last + 1;
                if (PathParser.this.data[last] == '/') {
                    i++;
                    if (i == this.begin) {
                        pos = last2;
                        last = last2;
                    }
                }
                last = last2;
            }
            return new String(PathParser.this.data, pos, (last - 1) - pos);
        }

        public String toString() {
            if (this.path == null) {
                this.path = getPath();
            }
            return this.path;
        }
    }

    public PathParser(Class type, String path) throws Exception {
        this.type = type;
        this.path = path;
        parse(path);
    }

    public boolean isPath() {
        return this.names.size() > 1;
    }

    public boolean isAttribute() {
        return this.attribute;
    }

    public int getIndex() {
        return ((Integer) this.indexes.getFirst()).intValue();
    }

    public String getPrefix() {
        return (String) this.prefixes.getFirst();
    }

    public String getFirst() {
        return (String) this.names.getFirst();
    }

    public String getLast() {
        return (String) this.names.getLast();
    }

    public Iterator<String> iterator() {
        return this.names.iterator();
    }

    public Expression getPath(int from) {
        return getPath(from, 0);
    }

    public Expression getPath(int from, int trim) {
        int last = this.names.size() - 1;
        if (last - trim >= from) {
            return new PathSection(from, last - trim);
        }
        return new PathSection(from, from);
    }

    private void parse(String path) throws Exception {
        if (path != null) {
            this.count = path.length();
            this.data = new char[this.count];
            path.getChars(0, this.count, this.data, 0);
        }
        path();
    }

    private void path() throws Exception {
        if (this.data[this.off] == '/') {
            throw new PathException("Path '%s' in %s references document root", this.path, this.type);
        }
        if (this.data[this.off] == '.') {
            skip();
        }
        while (this.off < this.count) {
            if (this.attribute) {
                throw new PathException("Path '%s' in %s references an invalid attribute", this.path, this.type);
            }
            segment();
        }
        truncate();
    }

    private void skip() throws Exception {
        if (this.data.length > 1) {
            if (this.data[this.off + 1] != '/') {
                throw new PathException("Path '%s' in %s has an illegal syntax", this.path, this.type);
            }
            this.off++;
        }
        int i = this.off + 1;
        this.off = i;
        this.start = i;
    }

    private void segment() throws Exception {
        char first = this.data[this.off];
        if (first == '/') {
            throw new PathException("Invalid path expression '%s' in %s", this.path, this.type);
        }
        if (first == '@') {
            attribute();
        } else {
            element();
        }
        align();
    }

    private void element() throws Exception {
        int mark = this.off;
        int size = 0;
        while (this.off < this.count) {
            char[] cArr = this.data;
            int i = this.off;
            this.off = i + 1;
            char value = cArr[i];
            if (isValid(value)) {
                size++;
            } else {
                if (value == '[') {
                    index();
                } else if (value != '/') {
                    throw new PathException("Illegal character '%s' in element for '%s' in %s", Character.valueOf(value), this.path, this.type);
                }
                insert(mark, size);
            }
        }
        insert(mark, size);
    }

    private void attribute() throws Exception {
        int mark = this.off + 1;
        this.off = mark;
        while (this.off < this.count) {
            char[] cArr = this.data;
            int i = this.off;
            this.off = i + 1;
            if (!isValid(cArr[i])) {
                throw new PathException("Illegal character '%s' in attribute for '%s' in %s", Character.valueOf(cArr[i]), this.path, this.type);
            }
        }
        if (this.off <= mark) {
            throw new PathException("Attribute reference in '%s' for %s is empty", this.path, this.type);
        }
        this.attribute = true;
        insert(mark, this.off - mark);
    }

    private void index() throws Exception {
        char[] cArr;
        int i;
        int value = 0;
        if (this.data[this.off - 1] == '[') {
            while (this.off < this.count) {
                cArr = this.data;
                i = this.off;
                this.off = i + 1;
                char digit = cArr[i];
                if (!isDigit(digit)) {
                    break;
                }
                value = ((value * 10) + digit) - 48;
            }
        }
        cArr = this.data;
        i = this.off;
        this.off = i + 1;
        if (cArr[i - 1] != ']') {
            throw new PathException("Invalid index for path '%s' in %s", this.path, this.type);
        } else {
            this.indexes.add(Integer.valueOf(value));
        }
    }

    private void truncate() throws Exception {
        if (this.off - 1 >= this.data.length) {
            this.off--;
        } else if (this.data[this.off - 1] == '/') {
            this.off--;
        }
    }

    private void align() throws Exception {
        if (this.names.size() > this.indexes.size()) {
            this.indexes.add(Integer.valueOf(1));
        }
    }

    private boolean isDigit(char value) {
        return Character.isDigit(value);
    }

    private boolean isValid(char value) {
        return isLetter(value) || isSpecial(value);
    }

    private boolean isSpecial(char value) {
        return value == '_' || value == '-' || value == ':';
    }

    private boolean isLetter(char value) {
        return Character.isLetterOrDigit(value);
    }

    private void insert(int start, int count) {
        String segment = new String(this.data, start, count);
        if (count > 0) {
            insert(segment);
        }
    }

    private void insert(String segment) {
        int index = segment.indexOf(58);
        String prefix = null;
        if (index > 0) {
            prefix = segment.substring(0, index);
            segment = segment.substring(index + 1);
        }
        this.prefixes.add(prefix);
        this.names.add(segment);
    }

    public String toString() {
        int size = this.off - this.start;
        if (this.cache == null) {
            this.cache = new String(this.data, this.start, size);
        }
        return this.cache;
    }
}
