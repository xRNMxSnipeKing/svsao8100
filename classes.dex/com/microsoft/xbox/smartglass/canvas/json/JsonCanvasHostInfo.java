package com.microsoft.xbox.smartglass.canvas.json;

import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonCanvasHostInfo extends JSONObject {
    private final UUID requestKey;
    private final String version = "1.0.Nov2012";

    public JsonCanvasHostInfo(UUID requestKey) throws JSONException {
        this.requestKey = requestKey;
        getClass();
        put("version", "1.0.Nov2012");
        put("requestKey", this.requestKey);
    }

    public String getRequestKey() {
        return this.requestKey.toString();
    }

    public String getVersion() {
        getClass();
        return "1.0.Nov2012";
    }
}
