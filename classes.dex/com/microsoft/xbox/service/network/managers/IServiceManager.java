package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.toolkit.XLEException;

public interface IServiceManager<T> {
    T getData() throws XLEException;
}
