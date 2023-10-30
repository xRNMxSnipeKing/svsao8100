package org.simpleframework.xml.stream;

import java.util.ArrayList;

class Stack<T> extends ArrayList<T> {
    public Stack(int size) {
        super(size);
    }

    public T pop() {
        int size = size();
        if (size <= 0) {
            return null;
        }
        return remove(size - 1);
    }

    public T top() {
        int size = size();
        if (size <= 0) {
            return null;
        }
        return get(size - 1);
    }

    public T bottom() {
        if (size() <= 0) {
            return null;
        }
        return get(0);
    }

    public T push(T value) {
        add(value);
        return value;
    }
}
