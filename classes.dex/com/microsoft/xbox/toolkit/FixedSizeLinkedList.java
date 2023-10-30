package com.microsoft.xbox.toolkit;

import java.util.LinkedList;

public class FixedSizeLinkedList<T> {
    private LinkedList<T> data = new LinkedList();
    private int size = 0;

    public FixedSizeLinkedList(int size) {
        boolean z = false;
        if (size > 0) {
            z = true;
        }
        XLEAssert.assertTrue(z);
        this.size = size;
    }

    public void push(T val) {
        this.data.add(val);
        while (this.data.size() > this.size) {
            this.data.remove();
        }
        XLEAssert.assertTrue(this.data.size() <= this.size);
    }

    public T pop() {
        T rv = this.data.remove();
        XLEAssert.assertTrue(this.data.size() <= this.size);
        return rv;
    }

    public T[] toArray(T[] name) {
        return this.data.toArray(name);
    }

    public void clear() {
        this.data.clear();
    }
}
