package com.microsoft.xbox.service.network.managers;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import com.microsoft.xbox.authenticate.PartnerToken;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.StreamUtil;
import com.microsoft.xbox.toolkit.TimeMonitor;
import com.microsoft.xbox.toolkit.UrlUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.HttpClientFactory;
import com.microsoft.xbox.toolkit.network.XLEHttpResponse;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.xle.viewmodel.SearchResultsActivityViewModel;
import com.microsoft.xle.test.interop.TestInterop;
import com.microsoft.xle.test.interop.TestInterop.ServiceManagerActivityStateChange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;

public class ServiceCommon {
    public static final String AuthenticationHeader = "X-PartnerAuthorization";
    public static final String AuthenticationPrefix = "XBL1.0 x=";
    public static final String CacheControl = "Cache-Control";
    public static final String LocaleHeader = "X-Locale";
    public static final String MobilePlatformType = "5";
    public static final String PlatformTypeHeader = "X-Platform-Type";
    public static final int TcpSocketTimeout = 5000;

    public enum HttpOperation {
        GET,
        POST,
        DELETE
    }

    public static native void onGetHttpDataCompleted(XLEHttpResponse xLEHttpResponse);

    public static void AddWebHeaders(HttpUriRequest httpRequest, List<Header> headers) {
        if (headers != null) {
            for (Header header : headers) {
                XLELog.Diagnostic("ServiceCommon", "Adding header: " + header);
                httpRequest.addHeader(header);
            }
        }
    }

    public static List<Header> GetLivenWebHeaders(String token) {
        ArrayList<Header> headers = new ArrayList();
        if (JavaUtil.isNullOrEmpty(MeProfileModel.getModel().getLegalLocale())) {
            headers.add(new BasicHeader(LocaleHeader, XboxLiveEnvironment.Instance().getMappedLocale()));
        } else {
            headers.add(new BasicHeader(LocaleHeader, MeProfileModel.getModel().getLegalLocale()));
        }
        headers.add(new BasicHeader(PlatformTypeHeader, MobilePlatformType));
        headers.add(new BasicHeader(CacheControl, "no-store, no-cache, must-revalidate"));
        headers.add(new BasicHeader("PRAGMA", "no-cache"));
        if (token != null) {
            headers.add(new BasicHeader(AuthenticationHeader, AuthenticationPrefix + token));
        }
        return headers;
    }

    public static InputStream getStream(String url) throws XLEException {
        return getStream(url, GetLivenWebHeaders(null));
    }

    public static InputStream getLivenStream(String audience, String url) throws XLEException {
        PartnerToken token = null;
        if (audience != null) {
            token = PartnerTokenManager.getInstance().getPartnerToken(audience);
            checkToken(token, audience);
        }
        if (token != null) {
            XLEAssert.assertTrue("Secure token leaked to public internet", url.startsWith("https"));
        }
        return getStream(url, GetLivenWebHeaders(token == null ? null : token.getToken()));
    }

    public static boolean postLivenStream(String audience, String url, String xml) throws XLEException {
        PartnerToken token = PartnerTokenManager.getInstance().getPartnerToken(audience);
        checkToken(token, audience);
        List<Header> postHeaders = GetLivenWebHeaders(token == null ? null : token.getToken());
        postHeaders.add(new BasicHeader("Content-type", "application/xml; charset=utf-8"));
        return postStream(url, postHeaders, xml);
    }

    public static boolean deleteLiven(String audience, String url) throws XLEException {
        PartnerToken token = PartnerTokenManager.getInstance().getPartnerToken(audience);
        checkToken(token, audience);
        return delete(url, GetLivenWebHeaders(token == null ? null : token.getToken()));
    }

    private static InputStream getStream(String url, List<Header> headers) throws XLEException {
        TimeMonitor stopwatch = new TimeMonitor();
        XLEHttpStatusAndStream responseAndStream = getStreamAndStatus(url, headers);
        TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Completed);
        ParseHttpStatusAndThrowIfError(url, responseAndStream.statusCode, responseAndStream.statusLine);
        return responseAndStream.stream;
    }

    public static boolean postStream(String url, List<Header> headers, String xml) throws XLEException {
        TimeMonitor stopwatch = new TimeMonitor();
        XLEHttpStatusAndStream statusAndStream = postStringWithStatus(url, headers, xml);
        TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Completed);
        ParseHttpStatusAndThrowIfError(url, statusAndStream.statusCode, statusAndStream.statusLine);
        return statusAndStream.statusCode == 200;
    }

    private static void ParseHttpStatusAndThrowIfError(String url, int statusCode, String statusLine) throws XLEException {
        boolean success;
        boolean z;
        if (statusCode < 200 || statusCode >= 400) {
            success = false;
        } else {
            success = true;
        }
        String str = "Got a non-200 status code. Make sure it's expected";
        if (statusCode == 200) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(str, success == z);
        if (!success) {
            XLELog.Error("ServiceCommon", String.format("ParseHttpStatusAndThrowIfError %s for url '%s'", new Object[]{statusLine, url}));
            if (statusCode == 401) {
                TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
                throw new XLEException(XLEErrorCode.INVALID_TOKEN);
            } else if (statusCode == 400) {
                TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
                throw new XLEException(15);
            } else if (statusCode == SearchResultsActivityViewModel.MAX_SEARCH_RESULT_ITEMS) {
                TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
                throw new XLEException(13);
            } else {
                TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
                throw new XLEException(3);
            }
        }
    }

    public static boolean delete(String url, List<Header> headers) throws XLEException {
        URI uri = UrlUtil.getEncodedUri(url);
        url = uri.toString();
        TimeMonitor stopwatch = new TimeMonitor();
        XLELog.Info("ServiceCommon", String.format("Network delete started for url '%s'", new Object[]{url}));
        TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Started);
        XLEHttpStatusAndStream statusAndStream = excuteHttpRequest(new HttpDelete(uri), url, headers, false, 0);
        TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Completed);
        ParseHttpStatusAndThrowIfError(url, statusAndStream.statusCode, statusAndStream.statusLine);
        if (statusAndStream.statusCode == 200) {
            return true;
        }
        return false;
    }

    private static void ParseHttpResponseForStatus(String url, int statusCode, String statusLine) throws XLEException {
        boolean success;
        if (statusCode < 200 || statusCode >= 300) {
            success = false;
        } else {
            success = true;
        }
        if (success) {
            XLELog.Diagnostic("ServiceCommon", String.format("%s for url: %s", new Object[]{statusLine, url}));
            return;
        }
        XLELog.Error("ServiceCommon", String.format("%s for url: %s", new Object[]{statusLine, url}));
        if (statusCode == -1) {
            XLELog.Error("ServiceCommon", "Timeout attempting response: " + url);
            TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
            throw new XLEException(2);
        }
        TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
    }

    public static XLEHttpStatusAndStream getStreamAndStatus(String url, List<Header> headers) throws XLEException {
        return getStreamAndStatus(url, headers, true, 0);
    }

    private static XLEHttpStatusAndStream getStreamAndStatus(String url, List<Header> headers, boolean urlEncode, int timeoutOverride) throws XLEException {
        URI uri = null;
        if (urlEncode) {
            uri = UrlUtil.getEncodedUri(url);
        } else {
            try {
                uri = new URI(url);
            } catch (URISyntaxException e) {
                XLELog.Error("ServiceCommon", "Failed to encode url: " + url);
            }
        }
        url = uri.toString();
        XLELog.Info("ServiceCommon", String.format("Network getStream started for url '%s'", new Object[]{url}));
        TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Started);
        return excuteHttpRequest(new HttpGet(uri), url, headers, true, timeoutOverride);
    }

    public static XLEHttpStatusAndStream postStringWithStatus(String url, List<Header> headers, String body) throws XLEException {
        try {
            XLELog.Diagnostic("ServiceCommon", "post data " + body);
            return postStreamWithStatus(url, headers, body.getBytes("UTF-8"));
        } catch (Throwable e) {
            XLELog.Error("ServiceCommon", "can't encode string to utf8");
            throw new XLEException(4, e);
        }
    }

    public static XLEHttpStatusAndStream postStreamWithStatus(String url, List<Header> headers, byte[] body) throws XLEException {
        URI uri = UrlUtil.getEncodedUri(url);
        url = uri.toString();
        XLELog.Info("ServiceCommon", String.format("Network postStream started for url '%s'", new Object[]{url}));
        String str = "ServiceCommon";
        String str2 = "Network postStream started for body length '%d'";
        Object[] objArr = new Object[1];
        objArr[0] = Integer.valueOf(body == null ? 0 : body.length);
        XLELog.Info(str, String.format(str2, objArr));
        TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Started);
        HttpPost post = new HttpPost(uri);
        if (body != null && body.length > 0) {
            try {
                post.setEntity(new ByteArrayEntity(body));
            } catch (Throwable e) {
                XLELog.Error("ServiceCommon", e.toString());
                TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
                throw new XLEException(4, e);
            }
        }
        return excuteHttpRequest(post, url, headers, false, 0);
    }

    private static XLEHttpStatusAndStream excuteHttpRequest(HttpUriRequest request, String url, List<Header> headers, boolean expectResponseEntity, int timeoutOverride) throws XLEException {
        AddWebHeaders(request, headers);
        XLELog.Diagnostic("ServiceCommon", "headers added");
        XLEHttpStatusAndStream rv = new XLEHttpStatusAndStream();
        rv = HttpClientFactory.networkOperationsFactory.getHttpClient(timeoutOverride).getHttpStatusAndStreamInternal(request, true);
        ParseHttpResponseForStatus(url, rv.statusCode, rv.statusLine);
        if (rv.stream != null || !expectResponseEntity) {
            return rv;
        }
        XLELog.Error("ServiceCommon", "No entity for " + url);
        TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
        throw new XLEException(6);
    }

    private static void checkToken(PartnerToken token, String audience) throws XLEException {
        if (token == null || token.getToken() == null) {
            XLELog.Error("ServiceCommon", "token is empty or null for audience " + audience);
            throw new XLEException(XLEErrorCode.INVALID_TOKEN);
        } else if (token.isExpired()) {
            XLELog.Error("ServiceCommon", "token is expired");
            throw new XLEException(XLEErrorCode.INVALID_TOKEN);
        }
    }

    public static void checkConnectivity() throws XLEException {
        TimeMonitor stopwatch = new TimeMonitor();
        boolean connected = false;
        ConnectivityManager cm = (ConnectivityManager) XboxApplication.Instance.getSystemService("connectivity");
        for (int networkType : new int[]{0, 1, 6}) {
            NetworkInfo networkInfo = cm.getNetworkInfo(networkType);
            if ((networkInfo == null ? State.UNKNOWN : networkInfo.getState()) == State.CONNECTED) {
                XLELog.Diagnostic("ServiceCommon", "network connected for " + networkType);
                connected = true;
                break;
            }
            XLELog.Warning("ServiceCommon", "network not connected for " + networkType);
        }
        if (!connected) {
            throw new XLEException(1);
        }
    }

    public static void getHttpData(String url, String[] headerArray, int callbackPtr, int requestTypeTag, int timeoutOverride) {
        XLELog.Error("ServiceCommon", "JNI calling java for getting data " + url);
        final List<Header> headers = convertHttpHeader(headerArray);
        final String str = url;
        final int i = timeoutOverride;
        final int i2 = callbackPtr;
        final int i3 = requestTypeTag;
        XLEThreadPool.networkOperationsThreadPool.run(new Runnable() {
            public void run() {
                XLEHttpResponse response;
                try {
                    response = ServiceCommon.getXLEHttpResponse(ServiceCommon.getStreamAndStatus(str, headers, false, i));
                } catch (Exception e) {
                    response = new XLEHttpResponse();
                    response.statusCode = SearchResultsActivityViewModel.MAX_SEARCH_RESULT_ITEMS;
                    response.responseBytes = null;
                }
                response.callbackPtr = i2;
                response.requestTypeTag = i3;
                XLELog.Error("ServiceCommon", "getHttpData background thread calling back into C++...");
                XLELog.Error("ServiceCommon", "getHttpData callback is " + i2);
                final XLEHttpResponse finalResponse = response;
                XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
                    public void run() {
                        XLELog.Warning("ServiceCommon", "running task on " + Thread.currentThread().getId());
                        ServiceCommon.onGetHttpDataCompleted(finalResponse);
                    }
                });
                XLELog.Error("ServiceCommon", "getHttpData background thread stopping...");
            }
        });
    }

    public static void postHttpData(final String url, String[] headerArray, final byte[] body, final int callbackPtr) {
        XLELog.Error("ServiceCommon", "JNI calling java for getting data " + url);
        final List<Header> headers = convertHttpHeader(headerArray);
        XLEThreadPool.networkOperationsThreadPool.run(new Runnable() {
            public void run() {
                XLEHttpResponse response;
                try {
                    response = ServiceCommon.getXLEHttpResponse(ServiceCommon.postStreamWithStatus(url, headers, body));
                } catch (Exception e) {
                    response = new XLEHttpResponse();
                    response.statusCode = SearchResultsActivityViewModel.MAX_SEARCH_RESULT_ITEMS;
                    response.responseBytes = null;
                }
                response.callbackPtr = callbackPtr;
                response.requestTypeTag = 0;
                XLELog.Error("ServiceCommon", "postHttpData background thread calling back into C++...");
                XLELog.Error("ServiceCommon", "postHttpData callback is " + callbackPtr);
                final XLEHttpResponse finalResponse = response;
                XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
                    public void run() {
                        XLELog.Warning("ServiceCommon", "running task on " + Thread.currentThread().getId());
                        ServiceCommon.onGetHttpDataCompleted(finalResponse);
                    }
                });
                XLELog.Error("ServiceCommon", "postHttpData background thread stopping...");
            }
        });
    }

    private static List<Header> convertHttpHeader(String[] headerArray) {
        XLEAssert.assertTrue(headerArray.length % 2 == 0);
        ArrayList<Header> headers = new ArrayList();
        for (int i = 0; i < headerArray.length; i += 2) {
            if (!headerArray[i].startsWith("Content-Length")) {
                headers.add(new BasicHeader(headerArray[i], headerArray[i + 1]));
            }
        }
        return headers;
    }

    private static XLEHttpResponse getXLEHttpResponse(XLEHttpStatusAndStream statusAndStream) throws IOException {
        XLEHttpResponse response = new XLEHttpResponse();
        int rvStatusCode = statusAndStream.statusCode;
        byte[] rvResponseBytes = new byte[0];
        if (statusAndStream.stream != null) {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            StreamUtil.CopyStream(byteOutputStream, statusAndStream.stream);
            rvResponseBytes = byteOutputStream.toByteArray();
        }
        response.statusCode = rvStatusCode;
        response.responseBytes = rvResponseBytes;
        response.headerArray = new String[(statusAndStream.headers.length * 2)];
        for (int i = 0; i < statusAndStream.headers.length; i++) {
            Header header = statusAndStream.headers[i];
            response.headerArray[i * 2] = header.getName();
            response.headerArray[(i * 2) + 1] = header.getValue();
        }
        return response;
    }
}
