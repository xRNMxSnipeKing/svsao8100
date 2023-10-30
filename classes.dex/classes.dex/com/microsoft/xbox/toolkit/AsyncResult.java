package com.microsoft.xbox.toolkit;

public class AsyncResult<T> {
    private final XLEException exception;
    private final T result;
    private final Object sender;

    public AsyncResult(T result, Object sender, XLEException exception) {
        this.sender = sender;
        this.exception = exception;
        this.result = result;
    }

    public Object getSender() {
        return this.sender;
    }

    public XLEException getException() {
        return this.exception;
    }

    public T getResult() {
        return this.result;
    }
}
