package com.omniture;

import java.util.Hashtable;

public abstract class RequestHandler {
    public abstract boolean sendRequest(String str, Hashtable hashtable);
}
