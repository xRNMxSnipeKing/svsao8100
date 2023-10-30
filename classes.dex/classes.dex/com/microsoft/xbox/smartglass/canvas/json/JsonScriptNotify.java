package com.microsoft.xbox.smartglass.canvas.json;

import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonScriptNotify extends JSONObject {
    private Object args = opt("args");
    private String className = getString("className");
    private int id = getInt(Name.MARK);
    private UUID key = UUID.fromString(getString("key"));
    private String methodName = getString("methodName");

    public JsonScriptNotify(String jsonString) throws JSONException {
        super(jsonString);
    }

    public int getId() {
        return this.id;
    }

    public String getClassName() {
        return this.className;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public UUID getKey() {
        return this.key;
    }

    public Object getArgs() {
        return this.args;
    }
}
