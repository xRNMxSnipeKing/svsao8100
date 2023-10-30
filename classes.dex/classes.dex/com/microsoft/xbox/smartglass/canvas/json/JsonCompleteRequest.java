package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonCompleteRequest extends JSONObject {
    public JsonCompleteRequest(int id, String result, JSONObject response) {
        try {
            put(Name.MARK, id);
            if (result != null) {
                put("result", result);
            }
            if (response != null) {
                put("response", response);
            }
        } catch (JSONException e) {
        }
    }
}
