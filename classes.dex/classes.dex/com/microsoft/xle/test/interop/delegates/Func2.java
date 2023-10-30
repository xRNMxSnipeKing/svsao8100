package com.microsoft.xle.test.interop.delegates;

public abstract class Func2<T, T1, T2> extends Func1<T, T1> {
    public abstract T invoke(T1 t1, T2 t2);

    public T invoke(T1 t1) {
        return super.invoke();
    }

    public T invoke() {
        return super.invoke();
    }
}
