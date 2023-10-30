package com.microsoft.xbox.smartglass.canvas;

import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class ScriptRunner implements HandlesIme {
    private final int SCRIPT_QUEUE_MEMORY_THRESHOLD = AvatarEditorModel.AVATAREDIT_OPTION_COLOR_SKIN;
    private final int SCRIPT_QUEUE_THRESHOLD = 32;
    private final int SCRIPT_QUEUE_TIMEOUT = 30000;
    private final CanvasView _canvas;
    private boolean _keyboardVisible;
    private int _memoryUsed;
    private long _oldestScriptInsertionTime;
    private Queue<Runnable> _scripts;
    private Timer _watchdog;

    public ScriptRunner(CanvasView canvas) {
        this._canvas = canvas;
        this._scripts = new LinkedList();
        this._memoryUsed = 0;
        this._oldestScriptInsertionTime = 0;
        this._watchdog = new Timer();
        this._watchdog.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                long timeDelta = new Date().getTime() - ScriptRunner.this._oldestScriptInsertionTime;
                synchronized (ScriptRunner.this._scripts) {
                    if (ScriptRunner.this._keyboardVisible && (ScriptRunner.this._scripts.size() > 32 || timeDelta > 30000 || ScriptRunner.this._memoryUsed > AvatarEditorModel.AVATAREDIT_OPTION_COLOR_SKIN)) {
                        ScriptRunner.this.runAllScripts();
                    }
                }
            }
        }, 0, 1000);
    }

    public void run(RunnableScript script) {
        if (this._keyboardVisible) {
            synchronized (this._scripts) {
                this._scripts.offer(script);
                this._memoryUsed += script.getScript().length() * 2;
                if (this._oldestScriptInsertionTime == 0) {
                    this._oldestScriptInsertionTime = new Date().getTime();
                }
            }
            return;
        }
        this._canvas.post(script);
    }

    private void runAllScripts() {
        synchronized (this._scripts) {
            for (Runnable script : this._scripts) {
                this._canvas.post(script);
            }
            this._scripts.clear();
            this._oldestScriptInsertionTime = 0;
            this._memoryUsed = 0;
        }
    }

    public void onKeyboardDismissed() {
        this._keyboardVisible = false;
        runAllScripts();
    }

    public void onKeyboardShown() {
        this._keyboardVisible = true;
    }
}
