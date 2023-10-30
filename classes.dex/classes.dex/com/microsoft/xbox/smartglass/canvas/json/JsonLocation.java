package com.microsoft.xbox.smartglass.canvas.json;

import android.location.Location;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonLocation extends JSONObject {
    public JsonLocation(Location location) throws JSONException {
        put("altitude", location.getAltitude());
        put("course", (double) location.getBearing());
        put("latitude", location.getLatitude());
        put("longitude", location.getLongitude());
        put("speed", (double) location.getSpeed());
        put("isUnknown", false);
    }
}
