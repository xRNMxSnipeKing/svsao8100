package com.microsoft.xbox.smartglass.canvas;

import org.json.JSONObject;

public interface CanvasComponent {
    JSONObject getCurrentState();

    void invoke(String str, int i, Object obj);

    void stopComponent();
}
