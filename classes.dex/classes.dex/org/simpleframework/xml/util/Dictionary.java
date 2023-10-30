package org.simpleframework.xml.util;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;

public class Dictionary<E extends Entry> extends AbstractSet<E> {
    protected Table map = new Table();

    private class Table extends HashMap<String, E> {
    }

    public boolean add(E item) {
        return this.map.put(item.getName(), item) != null;
    }

    public int size() {
        return this.map.size();
    }

    public Iterator<E> iterator() {
        return this.map.values().iterator();
    }

    public E get(String name) {
        return (Entry) this.map.get(name);
    }

    public E remove(String name) {
        return (Entry) this.map.remove(name);
    }
}
