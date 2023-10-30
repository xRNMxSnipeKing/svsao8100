package com.microsoft.xbox.toolkit.network;

import java.io.InputStream;
import org.apache.http.Header;

public class XLEHttpStatusAndStream {
    public Header[] headers = new Header[0];
    public String redirectUrl = null;
    public int statusCode = -1;
    public String statusLine = null;
    public InputStream stream = null;
}
