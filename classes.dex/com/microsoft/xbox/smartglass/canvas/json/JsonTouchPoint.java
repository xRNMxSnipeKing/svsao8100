package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonTouchPoint extends JSONObject {
    private int action;
    private int id;
    private double x;
    private double y;

    public JsonTouchPoint(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt(Name.MARK);
        this.action = jsonObject.getInt("action");
        this.x = jsonObject.getDouble("x");
        this.y = jsonObject.getDouble("y");
    }

    public int getId() {
        return this.id;
    }

    public int getAction() {
        return this.action;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
