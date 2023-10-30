package com.microsoft.xbox.smartglass.canvas.json;

import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonServiceRequestResponse extends JSONObject {
    public JsonServiceRequestResponse(String serviceResponse, Map<String, List<String>> responseHeaders) throws JSONException {
        if (serviceResponse != null) {
            put("serviceResponse", serviceResponse);
        }
        if (responseHeaders != null) {
            JSONObject jsonObject = new JSONObject();
            for (String key : responseHeaders.keySet()) {
                if (key != null) {
                    List<String> values = (List) responseHeaders.get(key);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < values.size() - 1; i++) {
                        sb.append((String) values.get(i));
                        sb.append(", ");
                    }
                    sb.append((String) values.get(values.size() - 1));
                    jsonObject.put(key, sb.toString());
                }
            }
            put("responseHeaders", jsonObject);
        }
    }
}
