package com.microsoft.xbox.toolkit;

import java.util.HashSet;
import java.util.PriorityQueue;

public class ThreadSafePriorityQueue<T> {
    private HashSet<T> hashSet = new HashSet();
    private PriorityQueue<T> queue = new PriorityQueue();
    private Object syncObject = new Object();

    public void push(T v) {
        synchronized (this.syncObject) {
            if (!this.hashSet.contains(v)) {
                this.queue.add(v);
                this.hashSet.add(v);
                this.syncObject.notifyAll();
            }
        }
    }

    public T pop() {
        T t = null;
        try {
            synchronized (this.syncObject) {
                while (this.queue.isEmpty()) {
                    this.syncObject.wait();
                }
                t = this.queue.remove();
                this.hashSet.remove(t);
            }
        } catch (InterruptedException e) {
        }
        return t;
    }
}
