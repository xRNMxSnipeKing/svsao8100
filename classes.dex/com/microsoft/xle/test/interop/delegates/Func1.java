package com.microsoft.xle.test.interop.delegates;

public abstract class Func1<T, T1> extends Func<T> {
    public abstract T invoke(T1 t1) throws Throwable;

    public T invoke() {
        return null;
    }
}
