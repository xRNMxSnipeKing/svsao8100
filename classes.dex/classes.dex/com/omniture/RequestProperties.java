package com.omniture;

import java.util.Hashtable;

public class RequestProperties {
    protected Hashtable headers;
    protected String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setHeaders(Hashtable headers) {
        this.headers = headers;
    }

    public Hashtable getHeaders() {
        return this.headers;
    }

    public static RequestProperties parseRequestProperties(String request) {
        RequestProperties requestProperties = new RequestProperties();
        Hashtable headers = null;
        String[] requestParts = AppMeasurementBase.splitString("\t", request);
        if (requestParts.length > 0 && requestParts[0].length() > 0) {
            requestProperties.setUrl(requestParts[0]);
            int requestPartNum = 1;
            while (requestPartNum < requestParts.length) {
                String key = requestParts[requestPartNum];
                if (!(key == null || key == "" || requestPartNum >= requestParts.length - 1)) {
                    String value = requestParts[requestPartNum + 1];
                    if (!(value == null || value.trim().length() == 0)) {
                        if (headers == null) {
                            headers = new Hashtable();
                        }
                        headers.put(key, value);
                    }
                }
                requestPartNum += 2;
            }
        }
        requestProperties.setHeaders(headers);
        return requestProperties;
    }
}
