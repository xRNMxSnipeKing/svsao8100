package com.microsoft.xle.test.interop.delegates;

public abstract class Action1<T> extends Action {
    public T parameterData1;

    public abstract void invoke(T t);

    public void invoke() {
    }
}
