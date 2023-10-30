package com.microsoft.xbox.smartglass.canvas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.smartglass.canvas.components.Accelerometer;
import com.microsoft.xbox.smartglass.canvas.components.Browser;
import com.microsoft.xbox.smartglass.canvas.components.Developer;
import com.microsoft.xbox.smartglass.canvas.components.Gyroscope;
import com.microsoft.xbox.smartglass.canvas.components.Haptic;
import com.microsoft.xbox.smartglass.canvas.components.Information;
import com.microsoft.xbox.smartglass.canvas.components.Input;
import com.microsoft.xbox.smartglass.canvas.components.Location;
import com.microsoft.xbox.smartglass.canvas.components.Media;
import com.microsoft.xbox.smartglass.canvas.components.Messaging;
import com.microsoft.xbox.smartglass.canvas.components.ServiceProxy;
import com.microsoft.xbox.smartglass.canvas.components.Touch;
import com.microsoft.xbox.smartglass.canvas.json.JsonCanvasClientInfo;
import com.microsoft.xbox.smartglass.canvas.json.JsonCanvasHostInfo;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonError;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLELog;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"SetJavaScriptEnabled"})
public class CanvasView extends WebView {
    public static boolean IsSmartGlassStudioRunning = false;
    private static final int KEYBOARD_DETECTION_THRESHOLD = 120;
    private static Object _invokeScriptLock = new Object();
    private IActivity _activity;
    private List<Integer> _allowedTitleIds;
    private List<String> _allowedUrlPrefixes;
    private CanvasViewClient _client;
    private Hashtable<String, CanvasComponent> _componentMap;
    private EnumSet<CanvasComponents> _components;
    private int _errorCode;
    private JsonCanvasHostInfo _hostInfo;
    private String _legalLocale;
    private ScriptRunner _scriptRunner;
    private List<Runnable> _scripts;
    private CanvasTokenManager _tokenManager;
    public JsonCanvasClientInfo clientInfo;
    private HandlesIme imeHandler;

    public interface IActivity {
        void onHandleRps(String str);

        void setEnvironment(String str);
    }

    public CanvasView(Context context) {
        super(context);
        initialize();
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        try {
            this._hostInfo = new JsonCanvasHostInfo(UUID.randomUUID());
        } catch (JSONException exception) {
            XLELog.Diagnostic("CanvasView", "Unable to generate JSON host info: " + exception.toString());
        }
        this._componentMap = new Hashtable();
        this._scripts = new ArrayList();
        this._scriptRunner = new ScriptRunner(this);
        setHandleImeInterface(this._scriptRunner);
        setWebViewClient(new CanvasWebViewClient());
        setWebChromeClient(new CanvasWebChromeClient());
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(getContext().getDir("database", 0).getPath());
        settings.setDomStorageEnabled(true);
        addJavascriptInterface(new JavaScriptBridge(this), "smartglass");
        this._errorCode = 0;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int diffHeight = MeasureSpec.getSize(heightMeasureSpec) - getHeight();
        if (Math.abs(diffHeight) > KEYBOARD_DETECTION_THRESHOLD) {
            if (diffHeight > 0) {
                if (this.imeHandler != null) {
                    this.imeHandler.onKeyboardDismissed();
                }
            } else if (this.imeHandler != null) {
                this.imeHandler.onKeyboardShown();
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void destroy() {
        synchronized (this._scripts) {
            for (Runnable script : this._scripts) {
                final Runnable callback = script;
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        CanvasView.this.removeCallbacks(callback);
                    }
                });
                removeCallbacks(script);
            }
            this._scripts.clear();
        }
        super.destroy();
    }

    public void setHandleImeInterface(HandlesIme handleImeInterface) {
        if (this.imeHandler != null && handleImeInterface == null) {
            this.imeHandler.onKeyboardDismissed();
        }
        this.imeHandler = handleImeInterface;
    }

    public String getLegalLocale() {
        return this._legalLocale;
    }

    public void setLegalLocale(String legalLocale) {
        this._legalLocale = legalLocale;
    }

    public void setComponents(EnumSet<CanvasComponents> components) {
        this._components = components;
        stopAllComponents();
        this._components.add(CanvasComponents.Browser);
        if (this._components.contains(CanvasComponents.Location)) {
            this._componentMap.put("Location", new Location(this));
        }
        if (this._components.contains(CanvasComponents.Messaging)) {
            this._componentMap.put("Messaging", new Messaging(this));
        }
        if (this._components.contains(CanvasComponents.Media)) {
            this._componentMap.put("Media", new Media(this));
        }
        if (this._components.contains(CanvasComponents.ServiceProxy)) {
            this._componentMap.put("ServiceProxy", new ServiceProxy(this));
        }
        if (this._components.contains(CanvasComponents.Input)) {
            this._componentMap.put("Input", new Input(this));
        }
        if (this._components.contains(CanvasComponents.Haptic)) {
            this._componentMap.put("Haptic", new Haptic(this));
        }
        if (this._components.contains(CanvasComponents.Accelerometer)) {
            this._componentMap.put("Accelerometer", new Accelerometer(this));
        }
        if (this._components.contains(CanvasComponents.Gyroscope)) {
            this._componentMap.put("Gyroscope", new Gyroscope(this));
        }
        if (this._components.contains(CanvasComponents.Information)) {
            this._componentMap.put("Information", new Information(this));
        }
        if (this._components.contains(CanvasComponents.Developer)) {
            this._componentMap.put("Developer", new Developer(this));
        }
        if (this._components.contains(CanvasComponents.Touch)) {
            this._componentMap.put("Touch", new Touch(this));
        }
        if (this._components.contains(CanvasComponents.Browser)) {
            this._componentMap.put("Browser", new Browser(this));
        }
    }

    public CanvasComponent getComponent(String className) {
        return (CanvasComponent) this._componentMap.get(className);
    }

    public List<String> getAllowedUrlPrefixes() {
        return this._allowedUrlPrefixes;
    }

    public void setAllowedUrlPrefixes(List<String> allowedUrlPrefixes) {
        this._allowedUrlPrefixes = allowedUrlPrefixes;
    }

    public List<Integer> getAllowedTitleIds() {
        return this._allowedTitleIds;
    }

    public void setAllowedTitleIds(List<Integer> allowedTitleIds) {
        this._allowedTitleIds = allowedTitleIds;
    }

    public void stopAllComponents() {
        for (CanvasComponent component : this._componentMap.values()) {
            component.stopComponent();
        }
        this._componentMap.clear();
    }

    public void setClient(CanvasViewClient client) {
        this._client = client;
    }

    public CanvasTokenManager getTokenManager() {
        return this._tokenManager;
    }

    public void setTokenManager(CanvasTokenManager tokenManager) {
        this._tokenManager = tokenManager;
    }

    public void onNavigating(String url) {
        if (this._errorCode == 0 && this._client != null) {
            this._client.onNavigating(url);
        }
        if (url.indexOf("oauth") > 0) {
            int startOfFragment = url.indexOf("#");
            if (startOfFragment > 0) {
                String target = "access_token";
                for (String split : url.substring(startOfFragment + 1).split("&")) {
                    String[] pairs = split.split("=");
                    if (pairs.length > 1 && pairs[0].equals(target)) {
                        handleRPS("t=" + pairs[1]);
                    }
                }
            }
        }
    }

    public void onLoadCompleted(String url) {
        if (this._errorCode == 0) {
            if (this._client != null) {
                this._client.onLoadCompleted(url);
            }
            invokeScript(CanvasScript.SetHostInformation, this._hostInfo.toString());
            JSONObject jsonObject = new JSONObject();
            Enumeration<CanvasComponent> e = this._componentMap.elements();
            while (e.hasMoreElements()) {
                CanvasComponent component = (CanvasComponent) e.nextElement();
                if (component.getCurrentState() != null) {
                    try {
                        jsonObject.put(component.getClass().getSimpleName(), component.getCurrentState());
                    } catch (JSONException e2) {
                    }
                }
            }
            on(CanvasEvent.Loaded, jsonObject);
        }
    }

    public void onNavigationFailed(String url, int errorCode, String description) {
        this._errorCode = errorCode;
        if (this._client != null) {
            this._client.onNavigationFailed(url, errorCode, description);
        }
    }

    public void completeRequest(JsonCompleteRequest jsonObject) {
        invokeScript(CanvasScript.CompleteRequest, jsonObject.toString());
    }

    public void on(String event, JSONObject jsonObject) {
        invokeScript(CanvasScript.OnEvent, event, jsonObject.toString());
    }

    public void on(String event, String argument) {
        invokeScript(CanvasScript.OnEvent, event, argument);
    }

    public void on(String event) {
        invokeScript(CanvasScript.OnEvent, event);
    }

    private void invokeScript(String scriptName, String... args) {
        synchronized (_invokeScriptLock) {
            try {
                System.gc();
                StringBuilder sb = new StringBuilder("javascript:");
                sb.append(scriptName);
                sb.append("(");
                for (int i = 0; i < args.length - 1; i++) {
                    sb.append("\"");
                    sb.append(args[i].replace("\\", "\\\\").replace("\"", "\\\""));
                    sb.append("\",");
                }
                if (args.length > 0) {
                    sb.append("\"");
                    sb.append(args[args.length - 1].replace("\\", "\\\\").replace("\"", "\\\""));
                    sb.append("\"");
                }
                sb.append(");");
                synchronized (this._scripts) {
                    RunnableScript script = new RunnableScript(this, sb.toString());
                    this._scripts.add(script);
                    this._scriptRunner.run(script);
                }
            } catch (OutOfMemoryError e) {
                System.gc();
                try {
                    on(CanvasEvent.Error, new JsonError(e.getLocalizedMessage()));
                } catch (OutOfMemoryError e2) {
                }
            }
        }
    }

    public void removeScript(RunnableScript script) {
        synchronized (this._scripts) {
            this._scripts.remove(script);
        }
    }

    public void loadUrl(String url) {
        this._errorCode = 0;
        super.loadUrl(url);
    }

    public void setActivity(IActivity activity) {
        this._activity = activity;
    }

    public IActivity getActivity() {
        return this._activity;
    }

    public void handleRPS(String ticket) {
        if (this._activity != null) {
            this._activity.onHandleRps(ticket);
        }
    }

    public boolean validateSession(boolean validateTitleChannel, int requestId) {
        CompanionSession session = CompanionSession.getInstance();
        if (session == null || session.getCurrentSessionState() != 2 || (validateTitleChannel && session.getCurrentTitleChannelState() != 2)) {
            if (requestId == -1) {
                return false;
            }
            completeRequest(new JsonCompleteRequest(requestId, CanvasEvent.Error, new JsonError("Session is disconnected.")));
            return false;
        } else if (IsSmartGlassStudioRunning || (this._allowedTitleIds != null && this._allowedTitleIds.contains(Integer.valueOf((int) session.getCurrentTitleId())))) {
            return true;
        } else {
            if (requestId == -1) {
                return false;
            }
            completeRequest(new JsonCompleteRequest(requestId, CanvasEvent.Error, new JsonError("The active title does not allow interaction with the current activity.")));
            return false;
        }
    }
}
