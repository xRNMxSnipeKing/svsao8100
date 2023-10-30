package com.microsoft.xbox.smartglass.privateutilities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class SGHttpResponse {
    private Map<String, List<String>> _headerMap = null;
    private HttpResponse _response;

    public SGHttpResponse(HttpResponse response) {
        this._response = response;
    }

    public Header[] getAllHeaders() {
        return this._response.getAllHeaders();
    }

    public Map<String, List<String>> getHeaderMap() {
        if (this._headerMap == null) {
            this._headerMap = new HashMap();
            for (Header header : this._response.getAllHeaders()) {
                ArrayList<String> values = new ArrayList();
                for (String value : header.getValue().split(",")) {
                    values.add(value);
                }
                this._headerMap.put(header.getName(), values);
            }
        }
        return this._headerMap;
    }

    public int getStatusCode() {
        return this._response.getStatusLine().getStatusCode();
    }

    public String getStatusString() {
        return this._response.getStatusLine().toString();
    }

    public InputStream getStream() throws IOException {
        HttpEntity entity = this._response == null ? null : this._response.getEntity();
        if (this._response == null || entity == null) {
            return null;
        }
        InputStream stream = new ByteArrayInputStream(EntityUtils.toByteArray(entity));
        entity.consumeContent();
        Header contentEncodingHeader = this._response.getFirstHeader("Content-Encoding");
        if (contentEncodingHeader == null || !contentEncodingHeader.getValue().equalsIgnoreCase("gzip")) {
            return stream;
        }
        return new GZIPInputStream(stream);
    }
}
