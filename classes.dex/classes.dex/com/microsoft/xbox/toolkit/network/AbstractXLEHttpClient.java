package com.microsoft.xbox.toolkit.network;

import com.microsoft.xbox.toolkit.XLEException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

public abstract class AbstractXLEHttpClient {
    protected abstract HttpResponse execute(HttpUriRequest httpUriRequest) throws ClientProtocolException, IOException;

    public abstract CookieStore getCookieStore();

    public abstract void setCookieStore(CookieStore cookieStore);

    public XLEHttpStatusAndStream getHttpStatusAndStreamInternal(HttpUriRequest httpGet, boolean printStreamDebug) throws XLEException {
        XLEHttpStatusAndStream rv = new XLEHttpStatusAndStream();
        try {
            HttpResponse response = execute(httpGet);
            if (!(response == null || response.getStatusLine() == null)) {
                rv.statusLine = response.getStatusLine().toString();
                rv.statusCode = response.getStatusLine().getStatusCode();
            }
            if (!(response == null || response.getLastHeader("Location") == null)) {
                rv.redirectUrl = response.getLastHeader("Location").getValue();
            }
            if (response != null) {
                rv.headers = response.getAllHeaders();
            }
            HttpEntity entity = response == null ? null : response.getEntity();
            if (entity != null) {
                rv.stream = new ByteArrayInputStream(EntityUtils.toByteArray(entity));
                entity.consumeContent();
                Header contentEncodingHeader = response.getFirstHeader("Content-Encoding");
                if (contentEncodingHeader != null && contentEncodingHeader.getValue().equalsIgnoreCase("gzip")) {
                    rv.stream = new GZIPInputStream(rv.stream);
                }
            }
            return rv;
        } catch (Throwable e) {
            httpGet.abort();
            throw new XLEException(3, e);
        }
    }
}
