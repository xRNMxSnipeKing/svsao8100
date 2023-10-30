package com.microsoft.xle.test.interop.delegates;

public abstract class Func3<T, T1, T2, T3> extends Func2<T, T1, T2> {
    public abstract T invoke(T1 t1, T2 t2, T3 t3);

    public T invoke(T1 t1, T2 t2) {
        return super.invoke();
    }

    public T invoke(T1 t1) {
        return super.invoke();
    }

    public T invoke() {
        return super.invoke();
    }
}
