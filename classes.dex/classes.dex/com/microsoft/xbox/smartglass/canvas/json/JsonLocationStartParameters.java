package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonLocationStartParameters {
    private int maxSampleRateMilliseconds;
    private int movementThresholdMeters;

    public JsonLocationStartParameters(JSONObject jsonObject) throws JSONException {
        this.maxSampleRateMilliseconds = jsonObject.getInt("maxSampleRateMilliseconds");
        this.movementThresholdMeters = jsonObject.getInt("movementThresholdMeters");
    }

    public int getMaxSampleRateMilliseconds() {
        return this.maxSampleRateMilliseconds;
    }

    public int getMovementThresholdMeters() {
        return this.movementThresholdMeters;
    }
}
