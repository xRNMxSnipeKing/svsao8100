package com.microsoft.xbox.smartglass.canvas.json;

import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonBrowserParameters {
    public Uri uri;

    public JsonBrowserParameters(JSONObject jsonObject) throws JSONException {
        this.uri = Uri.parse(jsonObject.getString("uri"));
    }

    public Uri getUri() {
        return this.uri;
    }
}
