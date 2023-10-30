package com.omniture;

import java.util.Enumeration;
import java.util.Hashtable;

public class DefaultRequestHandler extends RequestHandler {
    public boolean sendRequest(String url, Hashtable headers) {
        StringBuffer sb = new StringBuffer();
        sb.append(url).append('\t');
        if (headers != null && headers.size() > 0) {
            Enumeration keys = headers.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                sb.append(key).append('\t').append((String) headers.get(key));
                if (keys.hasMoreElements()) {
                    sb.append('\t');
                }
            }
        }
        System.out.println(sb.toString());
        return true;
    }
}
