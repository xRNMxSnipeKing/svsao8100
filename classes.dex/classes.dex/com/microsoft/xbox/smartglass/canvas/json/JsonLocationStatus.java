package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonLocationStatus extends JSONObject {

    public enum Status {
        Enabled("Enabled"),
        Disabled("Disabled"),
        Available("Available"),
        OutOfService("Out Of Service"),
        TemporarilyUnavailable("Temporarily Unavailable");
        
        private String statusString;

        private Status(String statusString) {
            this.statusString = statusString;
        }

        public String toString() {
            return this.statusString;
        }
    }

    public JsonLocationStatus(Status status) throws JSONException {
        put("state", status.toString());
    }
}
