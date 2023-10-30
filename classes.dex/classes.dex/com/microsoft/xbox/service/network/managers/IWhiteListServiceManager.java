package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.toolkit.XLEException;
import java.util.HashSet;

public interface IWhiteListServiceManager {
    HashSet<String> getWhiteList() throws XLEException;
}
