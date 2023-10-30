package com.omniture;

import com.microsoft.xbox.service.network.managers.ServiceCommon;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

public class RequestHandlerSe13 extends RequestHandler {
    Method java15_HttpURLConnection_setConnectTimeout = null;
    Method java15_HttpURLConnection_setReadTimeout = null;

    public RequestHandlerSe13() {
        try {
            this.java15_HttpURLConnection_setConnectTimeout = Class.forName("HttpURLConnection").getMethod("setConnectTimeout", new Class[]{Integer.TYPE});
        } catch (Exception e) {
            this.java15_HttpURLConnection_setConnectTimeout = null;
        }
        try {
            this.java15_HttpURLConnection_setReadTimeout = Class.forName("HttpURLConnection").getMethod("setReadTimeout", new Class[]{Integer.TYPE});
        } catch (Exception e2) {
            this.java15_HttpURLConnection_setReadTimeout = null;
        }
    }

    public boolean sendRequest(String url, Hashtable headers) {
        boolean z = false;
        if (url != null) {
            HttpURLConnection connection = null;
            z = false;
            try {
                connection = requestConnect(url);
                if (connection != null) {
                    if (this.java15_HttpURLConnection_setConnectTimeout != null) {
                        this.java15_HttpURLConnection_setConnectTimeout.invoke(connection, new Object[]{new Integer(ServiceCommon.TcpSocketTimeout)});
                    }
                    if (this.java15_HttpURLConnection_setReadTimeout != null) {
                        this.java15_HttpURLConnection_setReadTimeout.invoke(connection, new Object[]{new Integer(ServiceCommon.TcpSocketTimeout)});
                    }
                    if (headers != null) {
                        Enumeration keys = headers.keys();
                        while (keys.hasMoreElements()) {
                            String key = (String) keys.nextElement();
                            String value = (String) headers.get(key);
                            if (!(value == null || value.trim().length() == 0)) {
                                connection.setRequestProperty(key, value);
                            }
                        }
                    }
                    connection.getResponseCode();
                    z = true;
                }
            } catch (Exception e) {
            }
            if (connection != null) {
                try {
                    connection.getInputStream().close();
                } catch (Exception e2) {
                }
            }
        }
        return z;
    }

    protected HttpURLConnection requestConnect(String url) {
        try {
            return (HttpURLConnection) new URL(url).openConnection();
        } catch (Exception e) {
            return null;
        }
    }
}
