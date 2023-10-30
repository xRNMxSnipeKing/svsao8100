package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonInformationCurrentState extends JSONObject {
    public JsonInformationCurrentState(long activeTitleId, boolean isPhysicalDevice, boolean isUsingLocalConnection, boolean isConnected, boolean isTitleChannelEstablished, String legalLocale) throws JSONException {
        put("activeTitleId", activeTitleId);
        put("isPhysicalDevice", isPhysicalDevice);
        put("isUsingLocalConnection", isUsingLocalConnection);
        put("isConnected", isConnected);
        put("isTitleChannelEstablished", isTitleChannelEstablished);
        put("locale", legalLocale);
    }
}
