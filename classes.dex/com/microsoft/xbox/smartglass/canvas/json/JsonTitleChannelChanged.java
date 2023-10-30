package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonTitleChannelChanged extends JSONObject {
    public JsonTitleChannelChanged(boolean isTitleChannelEstablished) throws JSONException {
        put("isTitleChannelEstablished", isTitleChannelEstablished);
    }
}
