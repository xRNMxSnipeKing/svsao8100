package com.microsoft.xbox.smartglass.canvas;

import android.util.Log;

public class RunnableScript implements Runnable {
    private String _script;
    private CanvasView _view;

    public RunnableScript(CanvasView view, String script) {
        this._view = view;
        this._script = script;
    }

    public void run() {
        try {
            this._view.removeScript(this);
            this._view.loadUrl(this._script);
        } catch (Exception e) {
            Log.e("RunnableScript", e.toString());
        }
    }

    public String getScript() {
        return this._script;
    }
}
