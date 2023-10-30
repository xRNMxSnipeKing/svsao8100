package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonLaunchTitleParameters {
    private String launchParameters;
    private int titleId;

    public JsonLaunchTitleParameters(JSONObject jsonObject) throws JSONException {
        this.titleId = jsonObject.getInt("titleId");
        this.launchParameters = jsonObject.getString("launchParameters");
    }

    public int getTitleId() {
        return this.titleId;
    }

    public String getLaunchParameters() {
        return this.launchParameters;
    }
}
