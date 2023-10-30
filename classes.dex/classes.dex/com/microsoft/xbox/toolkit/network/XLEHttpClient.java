package com.microsoft.xbox.toolkit.network;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;

public class XLEHttpClient extends AbstractXLEHttpClient {
    DefaultHttpClient client;

    public XLEHttpClient(ClientConnectionManager connectionManager, HttpParams params) {
        this.client = new DefaultHttpClient(connectionManager, params);
    }

    protected HttpResponse execute(HttpUriRequest get) throws ClientProtocolException, IOException {
        return this.client.execute(get, new BasicHttpContext());
    }

    public CookieStore getCookieStore() {
        return this.client.getCookieStore();
    }

    public void setCookieStore(CookieStore store) {
        this.client.setCookieStore(store);
    }
}
