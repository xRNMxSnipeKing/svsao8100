package com.microsoft.xbox.smartglass.privateutilities;

import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import java.io.IOException;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class HttpClient {
    private static final int CONNECTION_PER_ROUTE = 16;
    private static final int DEFAULT_TIMEOUT_IN_SECONDS = 40;
    private static final int MAX_TOTAL_CONNECTIONS = 32;
    private static HttpParams _params;
    private DefaultHttpClient _client = new DefaultHttpClient(_params);
    private SGHttpResponse _response;

    public static HttpClient create() {
        return create(DEFAULT_TIMEOUT_IN_SECONDS);
    }

    public static HttpClient create(int timeout) {
        if (_params == null) {
            initializeFactory(DEFAULT_TIMEOUT_IN_SECONDS);
        }
        return new HttpClient();
    }

    private HttpClient() {
    }

    private static void initializeFactory(int timeout) {
        _params = new BasicHttpParams();
        HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(_params, "UTF-8");
        HttpProtocolParams.setUseExpectContinue(_params, false);
        HttpClientParams.setRedirecting(_params, true);
        HttpConnectionParams.setConnectionTimeout(_params, timeout * EDSV2MediaType.MEDIATYPE_MOVIE);
        HttpConnectionParams.setSoTimeout(_params, timeout * EDSV2MediaType.MEDIATYPE_MOVIE);
        HttpConnectionParams.setSocketBufferSize(_params, AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYES);
        ConnManagerParams.setMaxConnectionsPerRoute(_params, new ConnPerRouteBean(16));
        ConnManagerParams.setMaxTotalConnections(_params, 32);
    }

    public SGHttpResponse execute(HttpUriRequest request) throws ClientProtocolException, IOException {
        this._response = new SGHttpResponse(this._client.execute(request));
        return this._response;
    }

    public SGHttpResponse getLastResponse() {
        return this._response;
    }

    public HttpParams getParams() {
        return this._client.getParams();
    }

    public CookieStore getCookieStore() {
        return this._client.getCookieStore();
    }

    public void setCookieStore(CookieStore cookieStore) {
        this._client.setCookieStore(cookieStore);
    }
}
