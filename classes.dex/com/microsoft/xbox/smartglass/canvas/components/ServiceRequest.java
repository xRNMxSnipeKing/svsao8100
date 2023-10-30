package com.microsoft.xbox.smartglass.canvas.components;

import android.os.AsyncTask;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.smartglass.canvas.CanvasTokenManager;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.smartglass.canvas.InputStreamUtils;
import com.microsoft.xbox.smartglass.canvas.RunnableObservableTask;
import com.microsoft.xbox.smartglass.canvas.ServiceResponse;
import com.microsoft.xbox.smartglass.canvas.TaskTracker;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonError;
import com.microsoft.xbox.smartglass.canvas.json.JsonServiceRequest;
import com.microsoft.xbox.smartglass.privateutilities.HttpClient;
import com.microsoft.xbox.smartglass.privateutilities.SGHttpResponse;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.json.JSONObject;

public class ServiceRequest extends Observable implements Runnable {
    private static final int REQUEST_TIMEOUT = 15000;
    private static final String XSTS_TOKEN_PREAMBLE = "XBL2.0 x=";
    private static CookieStore _cookieStore = new BasicCookieStore();
    private static HashSet<Integer> _successCodes = new HashSet();
    private final CanvasView _canvas;
    public int _id;
    private boolean _isInternalRequest;
    private JsonServiceRequest _request;
    public ServiceResponse _response;
    private final CanvasTokenManager _tokenManager;
    private TaskTracker _tracker;

    static {
        _successCodes.add(Integer.valueOf(200));
        _successCodes.add(Integer.valueOf(201));
        _successCodes.add(Integer.valueOf(202));
        _successCodes.add(Integer.valueOf(204));
        _successCodes.add(Integer.valueOf(301));
        _successCodes.add(Integer.valueOf(302));
        _successCodes.add(Integer.valueOf(304));
        _successCodes.add(Integer.valueOf(307));
        _successCodes.add(Integer.valueOf(308));
    }

    public ServiceRequest(CanvasView canvas, boolean isInternalRequest, int id, JsonServiceRequest request, TaskTracker tracker) {
        this._canvas = canvas;
        this._tokenManager = this._canvas == null ? null : this._canvas.getTokenManager();
        this._isInternalRequest = isInternalRequest;
        this._id = id;
        this._request = request;
        this._response = null;
        this._tracker = tracker;
    }

    public AsyncTask<Runnable, Void, Observable> runAsync() {
        RunnableObservableTask task = new RunnableObservableTask();
        if (this._tracker != null) {
            this._tracker.addTask(task);
        }
        return task.execute(new Runnable[]{this, this});
    }

    public void run() {
        HttpClient client = HttpClient.create(REQUEST_TIMEOUT);
        try {
            URL url = new URL(this._request.getUrl());
            if (url.getProtocol().equalsIgnoreCase("http") || url.getProtocol().equalsIgnoreCase("https")) {
                HttpUriRequest method;
                CookieStore cookies;
                if (!(this._isInternalRequest || CanvasView.IsSmartGlassStudioRunning)) {
                    boolean allowed = false;
                    List<String> allowedUrlPrefixes = this._canvas.getAllowedUrlPrefixes();
                    if (allowedUrlPrefixes != null) {
                        for (String allowedUrlPrefix : allowedUrlPrefixes) {
                            URL allowedUrl = new URL(allowedUrlPrefix);
                            Locale locale = Locale.getDefault();
                            if (url.getProtocol().equalsIgnoreCase(allowedUrl.getProtocol()) && url.getHost().equalsIgnoreCase(allowedUrl.getHost()) && url.getPort() == allowedUrl.getPort() && url.getFile().toLowerCase(locale).startsWith(allowedUrl.getFile().toLowerCase(locale))) {
                                allowed = true;
                                break;
                            }
                        }
                    }
                    if (!allowed) {
                        throw new IllegalArgumentException("Only urls matching the allowed url prefix list can be accessed.");
                    }
                }
                String authorizationHeader = null;
                if (this._request.getSendUserToken().booleanValue()) {
                    if (!url.getProtocol().equalsIgnoreCase("https")) {
                        throw new IllegalArgumentException("Only https requests are supported when sendUserToken is set to true.");
                    } else if (this._request.getAudienceUri() == null) {
                        throw new IllegalArgumentException("audienceUri must be specified when sendUserToken is set to true.");
                    } else {
                        String xstsToken = null;
                        if (this._tokenManager != null) {
                            xstsToken = this._tokenManager.getXstsToken(this._request.getAudienceUri(), false);
                        }
                        if (xstsToken == null) {
                            throw new IllegalArgumentException("Unable to obtain a user token for the given audienceUri.");
                        }
                        authorizationHeader = XSTS_TOKEN_PREAMBLE + xstsToken;
                    }
                }
                String data = this._request.getData();
                boolean z = data != null && data.length() > 0;
                StringEntity entity = null;
                if (Boolean.valueOf(z).booleanValue()) {
                    entity = new StringEntity(data, "UTF-8");
                    entity.setContentType(this._request.getContentType());
                }
                if (this._request.getMethod().equalsIgnoreCase("POST")) {
                    HttpPost httpPost = new HttpPost(url.toString());
                    httpPost.setEntity(entity);
                    method = httpPost;
                } else if (this._request.getMethod().equalsIgnoreCase("GET")) {
                    r0 = new HttpGet(url.toString());
                } else if (this._request.getMethod().equalsIgnoreCase("PUT")) {
                    HttpPut httpPut = new HttpPut(url.toString());
                    httpPut.setEntity(entity);
                    Object method2 = httpPut;
                } else if (this._request.getMethod().equalsIgnoreCase("DELETE")) {
                    r0 = new HttpDelete(url.toString());
                } else if (this._request.getMethod().equalsIgnoreCase("HEAD")) {
                    r0 = new HttpHead(url.toString());
                } else {
                    this._canvas.completeRequest(new JsonCompleteRequest(this._id, CanvasEvent.Error, new JsonError("Unsupported http method")));
                    return;
                }
                JSONObject headers = this._request.getHeaders();
                if (headers != null) {
                    Iterator<?> fields = headers.keys();
                    while (fields.hasNext()) {
                        String field = (String) fields.next();
                        method.addHeader(field, headers.getString(field));
                    }
                }
                if (this._request.getWithCredentials().booleanValue()) {
                    cookies = _cookieStore;
                } else {
                    cookies = new BasicCookieStore();
                }
                client.setCookieStore(cookies);
                if (authorizationHeader != null) {
                    method.addHeader("Authorization", authorizationHeader);
                }
                SGHttpResponse httpResponse = client.execute(method);
                String responseBody = "";
                InputStream in = httpResponse.getStream();
                if (in != null) {
                    String str = new String(InputStreamUtils.readToEnd(in), "UTF-8");
                }
                if (_successCodes.contains(Integer.valueOf(httpResponse.getStatusCode()))) {
                    this._response = new ServiceResponse(this._id, responseBody, httpResponse.getHeaderMap());
                    setChanged();
                    return;
                }
                this._canvas.completeRequest(new JsonCompleteRequest(this._id, CanvasEvent.Error, new JsonError(String.format("%s (%d)", new Object[]{httpResponse.getStatusString(), Integer.valueOf(httpResponse.getStatusCode())}))));
                return;
            }
            throw new UnsupportedOperationException("Only support http or https requests");
        } catch (Exception e) {
            this._canvas.completeRequest(new JsonCompleteRequest(this._id, CanvasEvent.Error, new JsonError(e.getLocalizedMessage())));
        }
    }

    public void notifyObservers(Object data) {
        if (!(data == null || !(data instanceof RunnableObservableTask) || this._tracker == null)) {
            this._tracker.removeTask((RunnableObservableTask) data);
        }
        super.notifyObservers(this._response);
    }
}
