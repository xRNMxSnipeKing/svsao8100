package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUserInfo extends JSONObject {
    public JsonUserInfo(String jsonString) throws JSONException {
        super(jsonString);
        remove("gamerTag");
    }
}
