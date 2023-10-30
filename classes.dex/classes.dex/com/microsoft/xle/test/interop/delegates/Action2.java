package com.microsoft.xle.test.interop.delegates;

public abstract class Action2<T1, T2> extends Action1<T1> {
    public T2 parameterData2;

    public abstract void invoke(T1 t1, T2 t2);

    public void invoke() {
    }

    public void invoke(T1 t1) {
    }
}
