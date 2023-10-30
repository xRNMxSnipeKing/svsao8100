package com.microsoft.xbox.smartglass.canvas.json;

import java.util.Collection;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonClientsUpdated extends JSONObject {
    public JsonClientsUpdated(Collection<String> connectedUsers, Collection<String> disconnectedUsers) throws JSONException {
        put("connectedUsers", connectedUsers);
        put("disconnectedUsers", disconnectedUsers);
    }
}
