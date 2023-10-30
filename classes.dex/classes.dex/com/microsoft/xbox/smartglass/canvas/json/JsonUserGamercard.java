package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUserGamercard extends JSONObject {
    public JsonUserGamercard(String jsonString) throws JSONException {
        super(jsonString);
        remove("avatarManifest");
    }
}
