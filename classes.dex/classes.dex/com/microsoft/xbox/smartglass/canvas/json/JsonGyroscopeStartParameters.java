package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonGyroscopeStartParameters {
    private int sampleRateMilliseconds;
    private boolean sendToCanvas;
    private boolean sendToConsole;

    public JsonGyroscopeStartParameters(JSONObject jsonObject) throws JSONException {
        this.sampleRateMilliseconds = jsonObject.getInt("sampleRateMilliseconds");
        this.sendToCanvas = jsonObject.getBoolean("sendToCanvas");
        this.sendToConsole = jsonObject.getBoolean("sendToConsole");
    }

    public int getSampleRateMilliseconds() {
        return this.sampleRateMilliseconds;
    }

    public boolean getSendToCanvas() {
        return this.sendToCanvas;
    }

    public boolean getSendToConsole() {
        return this.sendToConsole;
    }
}
