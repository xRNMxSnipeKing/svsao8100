package com.omniture;

import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class RequestHandlerSe extends RequestHandlerSe13 {
    protected HttpURLConnection requestConnect(String url) {
        try {
            URL requestURL = new URL(url);
            if (url.indexOf("https://") < 0) {
                return (HttpURLConnection) requestURL.openConnection();
            }
            HttpsURLConnection connectionSecure = (HttpsURLConnection) requestURL.openConnection();
            connectionSecure.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return connectionSecure;
        } catch (Exception e) {
            return null;
        }
    }
}
