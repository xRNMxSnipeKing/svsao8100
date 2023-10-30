package com.microsoft.xbox.toolkit;

import java.util.ArrayList;
import java.util.HashSet;

public class MultiSelection<T> {
    private HashSet<T> selection = new HashSet();

    public void add(T object) {
        this.selection.add(object);
    }

    public void remove(T object) {
        this.selection.remove(object);
    }

    public boolean contains(T object) {
        return this.selection.contains(object);
    }

    public boolean isEmpty() {
        return this.selection.isEmpty();
    }

    public ArrayList<T> toArrayList() {
        return new ArrayList(this.selection);
    }

    public void reset() {
        this.selection.clear();
    }
}
