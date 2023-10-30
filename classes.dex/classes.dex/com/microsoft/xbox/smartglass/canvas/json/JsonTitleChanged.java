package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonTitleChanged extends JSONObject {
    public JsonTitleChanged(long newTitleId) throws JSONException {
        put("newTitleId", newTitleId);
    }
}
