package com.microsoft.xbox.smartglass.canvas.json;

import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonServiceRequest {
    private String audienceUri;
    private String contentType;
    private String data;
    private JSONObject headers;
    private String method;
    private Boolean sendUserToken;
    private String url;
    private Boolean withCredentials;

    public JsonServiceRequest(JSONObject jsonObject) throws JSONException {
        this.url = jsonObject.getString("url");
        this.method = jsonObject.optString("method", "POST");
        this.contentType = jsonObject.optString("contentType", "application/json");
        this.data = jsonObject.optString("data");
        this.headers = jsonObject.optJSONObject("headers");
        this.sendUserToken = Boolean.valueOf(jsonObject.optBoolean("sendUserToken", false));
        this.audienceUri = jsonObject.optString("audienceUri", XboxLiveEnvironment.SLS_AUDIENCE_URI);
        this.withCredentials = Boolean.valueOf(jsonObject.optBoolean("withCredentials", false));
    }

    public String getUrl() {
        return this.url;
    }

    public String getMethod() {
        return this.method;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getData() {
        return this.data;
    }

    public JSONObject getHeaders() {
        return this.headers;
    }

    public Boolean getSendUserToken() {
        return this.sendUserToken;
    }

    public String getAudienceUri() {
        return this.audienceUri;
    }

    public Boolean getWithCredentials() {
        return this.withCredentials;
    }
}
