package com.microsoft.xbox.toolkit;

import java.util.Collection;
import java.util.LinkedList;

public class ThreadSafeLinkedList<T> {
    private boolean forceReturnWhenEmpty = false;
    private LinkedList<T> queue = new LinkedList();
    private Object syncObject = new Object();

    public void addLast(T obj) {
        synchronized (this.syncObject) {
            this.queue.addLast(obj);
            this.syncObject.notifyAll();
        }
    }

    public void forceReturnWhenEmpty(boolean value) {
        synchronized (this.syncObject) {
            this.forceReturnWhenEmpty = value;
            this.syncObject.notifyAll();
        }
    }

    public void addAll(Collection<? extends T> collection) {
        synchronized (this.syncObject) {
            this.queue.addAll(collection);
            this.syncObject.notifyAll();
        }
    }

    public T removeFirst() {
        T t = null;
        try {
            synchronized (this.syncObject) {
                while (this.queue.isEmpty() && !this.forceReturnWhenEmpty) {
                    this.syncObject.wait();
                }
                if (!this.queue.isEmpty()) {
                    t = this.queue.removeFirst();
                }
            }
        } catch (Exception e) {
        }
        return t;
    }

    public T pop() {
        return removeFirst();
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }
}
