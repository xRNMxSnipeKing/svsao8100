package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.service.model.ActiveTitleInfo;
import com.microsoft.xbox.service.model.ConsoleSettings;
import com.microsoft.xbox.service.model.KeyboardText;
import com.microsoft.xbox.service.model.LRCControlKey;
import com.microsoft.xbox.service.model.MediaTitleState;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.smartglass.TouchFrame;
import com.microsoft.xbox.toolkit.FixedSizeLinkedList;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xle.test.interop.TestInterop;
import java.util.Iterator;
import java.util.LinkedList;

public class CompanionSession implements ICompanionSession {
    public static final int LRCERROR_DUPLICATEREQ = 8;
    public static final int LRCERROR_EXCLUSIVE_CONNECTED = 15;
    public static final int LRCERROR_EXPIRED_COMMAND = 13;
    public static final int LRCERROR_FAILURE = 3;
    public static final int LRCERROR_FAIL_TO_CONNECT_TO_SESSION = 11;
    public static final int LRCERROR_FAIL_TO_GET_SESSION = 10;
    public static final int LRCERROR_INTERNAL = 5;
    public static final int LRCERROR_INVALIDARGS = 2;
    public static final int LRCERROR_INVALIDCONTENT = 7;
    public static final int LRCERROR_IN_PROGRESS = 16;
    public static final int LRCERROR_NOSESSION = 4;
    public static final int LRCERROR_NO_EXCLUSIVE = 14;
    public static final int LRCERROR_OUTOFMEMORY = 1;
    public static final int LRCERROR_REQUEST_TIMEOUT = 6;
    public static final int LRCERROR_SUCCESS = 0;
    public static final int LRCERROR_TITLECHANNEL_EXISTS = 17;
    public static final int LRCERROR_TMF_SIGNIN_FAIL = 9;
    public static final int LRCERROR_TOO_MANY_CLIENTS = 12;
    public static final int LRC_MESSAGE_TYPE_GET_ACTIVE_TITLEID = 1;
    public static final int LRC_MESSAGE_TYPE_GET_MEDIA_TITLE_STATE = 4;
    public static final int LRC_MESSAGE_TYPE_JOIN_SESSION = -2147483647;
    public static final int LRC_MESSAGE_TYPE_LAUNCH_TITLE = 2;
    public static final int LRC_MESSAGE_TYPE_LEAVE_SESSION = -2147483646;
    public static final int LRC_MESSAGE_TYPE_MEDIA_TITLE_STATE_NOTIFICATION = 6;
    public static final int LRC_MESSAGE_TYPE_NONE = 0;
    public static final int LRC_MESSAGE_TYPE_NON_MEDIA_TITLE_STATE_NOTIFICATION = 5;
    public static final int LRC_MESSAGE_TYPE_SEND_INPUT = 3;
    public static final int SESSION_CAPABILITY_CLOUD = 2;
    public static final int SESSION_CAPABILITY_DEFAULT = 1;
    public static final int SESSION_CAPABILITY_LOCALTCP = 3;
    public static final int SESSION_CAPABILITY_NOTCONNECTED = 0;
    public static final int SESSION_STATE_CONNECTED = 2;
    public static final int SESSION_STATE_CONNECTING = 1;
    public static final int SESSION_STATE_CONNECTION_FAILED = 3;
    public static final int SESSION_STATE_DISCONNECTED = 0;
    public static final int TITLE_CHANNEL_STATE_CONNECTED = 2;
    public static final int TITLE_CHANNEL_STATE_CONNECTING = 1;
    public static final int TITLE_CHANNEL_STATE_CONNECTION_FAILED = 3;
    public static final int TITLE_CHANNEL_STATE_DISCONNECTED = 0;
    private static CompanionSession instance = new CompanionSession();
    private FixedSizeLinkedList<LRCControlKey> TEST_lastSentKeys;
    private LinkedList<ICompanionSessionActiveTitleInfoListener> activeTitleInfoListenerList = new LinkedList();
    private LinkedList<ICompanionSessionConsoleSettingsListener> consoleSettingsListenerList = new LinkedList();
    private int currentCapability;
    private int currentEnvironment;
    private MediaTitleState currentMediaState;
    private int currentSessionState;
    private int currentTitleChannelState;
    private long currentTitleId;
    private Object datalock = new Object();
    private LinkedList<ICompanionSessionDeviceJoinedListener> deviceJoinedListenerList = new LinkedList();
    private LinkedList<ICompanionSessionDeviceLeftListener> deviceLeftListenerList = new LinkedList();
    public Environment environment;
    private boolean initialized;
    private LinkedList<ICompanionSessionKeyboardTextListener> keyboardTextListenerList = new LinkedList();
    private long lastErrorCode;
    private LinkedList<ICompanionSessionMediaTitleStateListener> mediaTitleStateListenerList = new LinkedList();
    private LinkedList<ICompanionSessionRequestCompleteListener> requestCompleteListenerList = new LinkedList();
    private LinkedList<ICompanionSessionStateListener> sessionStateListenerList = new LinkedList();
    private boolean shutdownLast;
    private LinkedList<ICompanionSessionTitleChannelStateListener> titleChannelStateListenerList = new LinkedList();
    private LinkedList<ICompanionSessionTitleListener> titleListenerList = new LinkedList();
    private LinkedList<ICompanionSessionJsonTitleMessageListener> titleMessageListenerList = new LinkedList();
    public String xliveToken;

    private static class LongWrapper {
        long value;

        private LongWrapper() {
        }
    }

    private native void nativeAcquireExclusiveMode();

    private native long nativeActivate(int i);

    private native void nativeConnect();

    private native long nativeDeactivate();

    private native void nativeDisconnect();

    private native long nativeDispatchCallback(int i, int[] iArr);

    private native void nativeEstablishTitleChannel();

    private native long nativeGetActiveTitleId();

    private native void nativeGetActiveTitleInfo();

    private native void nativeGetConsoleSettings();

    private native long nativeGetMediaState();

    private native void nativeGetText();

    private native long nativeInitialize(int i);

    private native long nativeJoinSession();

    private native long nativeLaunchTitle(long j, int i);

    private native long nativeLaunchTitleRaw(long j, String str);

    private native long nativeLaunchTitleWithLink(long j, int i, String str);

    private native long nativeLaunchZuneContent(String str, int i);

    private native void nativeReleaseExclusiveMode();

    private native long nativeReset();

    private native long nativeSendControlCommand(int i);

    private native long nativeSendControlCommandWithMediaCenterSupport(int i);

    private native long nativeSendSeekCommand(long j);

    private native long nativeSendTitleAccelerometerData(float f, float f2, float f3);

    private native long nativeSendTitleGyroscopeData(float f, float f2, float f3);

    private native long nativeSendTitleMessage(int i, byte[] bArr);

    private native long nativeSendTitleTouchFrame(TouchFrame touchFrame);

    private native void nativeSetAuthenicationToken(String str);

    private native void nativeSetText(String str, String str2);

    public static CompanionSession getInstance() {
        return instance;
    }

    private CompanionSession() {
        reset();
    }

    public void initialize(final int environmentType) {
        this.TEST_lastSentKeys = new FixedSizeLinkedList(20);
        this.currentEnvironment = environmentType;
        this.environment = new Environment(EnvironmentType.fromInt(this.currentEnvironment));
        this.lastErrorCode = 0;
        synchronized (this.datalock) {
            if (!this.initialized) {
                runNativeAction(new Runnable() {
                    public void run() {
                        if (CompanionSession.this.shutdownLast) {
                            XLELog.Diagnostic("CompanionSession", "initialize session is called");
                            CompanionSession.this.nativeInitialize(environmentType);
                            return;
                        }
                        XLELog.Diagnostic("CompanionSession", "not shutdown last, ignore");
                    }
                });
                this.initialized = true;
            }
        }
    }

    public void addCompanionSessionStateListener(ICompanionSessionStateListener listener) {
        synchronized (this.sessionStateListenerList) {
            this.sessionStateListenerList.add(listener);
        }
    }

    public void removeCompanionSessionStateListener(ICompanionSessionStateListener listener) {
        synchronized (this.sessionStateListenerList) {
            this.sessionStateListenerList.remove(listener);
        }
    }

    public void addCompanionSessionMediaTitleStateListener(ICompanionSessionMediaTitleStateListener listener) {
        synchronized (this.mediaTitleStateListenerList) {
            this.mediaTitleStateListenerList.add(listener);
        }
    }

    public void removeCompanionSessionMediaTitleStateListener(ICompanionSessionMediaTitleStateListener listener) {
        synchronized (this.mediaTitleStateListenerList) {
            this.mediaTitleStateListenerList.remove(listener);
        }
    }

    public void addCompanionSessionTitleListener(ICompanionSessionTitleListener listener) {
        synchronized (this.titleListenerList) {
            this.titleListenerList.add(listener);
        }
    }

    public void removeCompanionSessionTitleListener(ICompanionSessionTitleListener listener) {
        synchronized (this.titleListenerList) {
            this.titleListenerList.remove(listener);
        }
    }

    public void addCompanionSessionJsonTitleMessageListener(ICompanionSessionJsonTitleMessageListener listener) {
        synchronized (this.titleMessageListenerList) {
            this.titleMessageListenerList.add(listener);
        }
    }

    public void removeCompanionSessionJsonTitleMessageListener(ICompanionSessionJsonTitleMessageListener listener) {
        synchronized (this.titleMessageListenerList) {
            this.titleMessageListenerList.remove(listener);
        }
    }

    public void addCompanionSessionRequestCompleteListener(ICompanionSessionRequestCompleteListener listener) {
        synchronized (this.requestCompleteListenerList) {
            this.requestCompleteListenerList.add(listener);
        }
    }

    public void removeCompanionSessionRequestCompleteListener(ICompanionSessionRequestCompleteListener listener) {
        synchronized (this.requestCompleteListenerList) {
            this.requestCompleteListenerList.remove(listener);
        }
    }

    public void addCompanionSessionTitleChannelStateListener(ICompanionSessionTitleChannelStateListener listener) {
        synchronized (this.titleChannelStateListenerList) {
            this.titleChannelStateListenerList.add(listener);
        }
    }

    public void removeCompanionSessionTitleChannelStateListener(ICompanionSessionTitleChannelStateListener listener) {
        synchronized (this.titleChannelStateListenerList) {
            this.titleChannelStateListenerList.remove(listener);
        }
    }

    public void addCompanionSessionDeviceJoinedListener(ICompanionSessionDeviceJoinedListener listener) {
        synchronized (this.deviceJoinedListenerList) {
            this.deviceJoinedListenerList.add(listener);
        }
    }

    public void removeCompanionSessionDeviceJoinedListener(ICompanionSessionDeviceJoinedListener listener) {
        synchronized (this.deviceJoinedListenerList) {
            this.deviceJoinedListenerList.remove(listener);
        }
    }

    public void addCompanionSessionDeviceLeftListener(ICompanionSessionDeviceLeftListener listener) {
        synchronized (this.deviceLeftListenerList) {
            this.deviceLeftListenerList.add(listener);
        }
    }

    public void removeCompanionSessionDeviceLeftListener(ICompanionSessionDeviceLeftListener listener) {
        synchronized (this.deviceLeftListenerList) {
            this.deviceLeftListenerList.remove(listener);
        }
    }

    public void addCompanionSessionKeyboardTextListener(ICompanionSessionKeyboardTextListener listener) {
        synchronized (this.keyboardTextListenerList) {
            this.keyboardTextListenerList.add(listener);
        }
    }

    public void removeCompanionSessionKeyboardTextListener(ICompanionSessionKeyboardTextListener listener) {
        synchronized (this.keyboardTextListenerList) {
            this.keyboardTextListenerList.remove(listener);
        }
    }

    public void addCompanionSessionConsoleSettingsListener(ICompanionSessionConsoleSettingsListener listener) {
        synchronized (this.consoleSettingsListenerList) {
            this.consoleSettingsListenerList.add(listener);
        }
    }

    public void removeCompanionSessionConsoleSettingsListener(ICompanionSessionConsoleSettingsListener listener) {
        synchronized (this.consoleSettingsListenerList) {
            this.consoleSettingsListenerList.remove(listener);
        }
    }

    public void addCompanionSessionActiveTitleInfoListener(ICompanionSessionActiveTitleInfoListener listener) {
        synchronized (this.activeTitleInfoListenerList) {
            this.activeTitleInfoListenerList.add(listener);
        }
    }

    public void removeCompanionSessionActiveTitleInfoListener(ICompanionSessionActiveTitleInfoListener listener) {
        synchronized (this.activeTitleInfoListenerList) {
            this.activeTitleInfoListenerList.remove(listener);
        }
    }

    public int getCurrentSessionState() {
        return this.currentSessionState;
    }

    public long getLastErrorCode() {
        return this.lastErrorCode;
    }

    public int getCurrentTitleChannelState() {
        return this.currentTitleChannelState;
    }

    public int getCurrentCapability() {
        return this.currentCapability;
    }

    public long getCurrentTitleId() {
        return this.currentTitleId;
    }

    public MediaTitleState getCurrentMediaState() {
        return this.currentMediaState;
    }

    public FixedSizeLinkedList<LRCControlKey> TEST_getLastSentKeys() {
        return this.TEST_lastSentKeys;
    }

    public void setAuthenticationToken(final String token) {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeSetAuthenicationToken(token);
            }
        });
    }

    public void joinSession() {
        final int environment = this.currentEnvironment;
        this.currentSessionState = 1;
        this.lastErrorCode = 0;
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeActivate(environment);
            }
        });
    }

    public void reset() {
        synchronized (this.datalock) {
            this.initialized = false;
            this.shutdownLast = true;
            this.currentSessionState = 0;
            this.currentTitleId = 0;
            this.currentMediaState = null;
            this.currentCapability = 0;
            this.lastErrorCode = 0;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void shutdownSession(boolean r8) {
        /*
        r7 = this;
        r5 = r7.datalock;
        monitor-enter(r5);
        r4 = r7.initialized;	 Catch:{ all -> 0x0045 }
        if (r4 != 0) goto L_0x0010;
    L_0x0007:
        r4 = "CompanionSession";
        r6 = "companion session not initialized, ignore this shutdown call";
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r4, r6);	 Catch:{ all -> 0x0045 }
        monitor-exit(r5);	 Catch:{ all -> 0x0045 }
    L_0x000f:
        return;
    L_0x0010:
        monitor-exit(r5);	 Catch:{ all -> 0x0045 }
        r4 = getInstance();
        r4.reset();
        r7.shutdownLast = r8;
        r1 = r8;
        r4 = new com.microsoft.xbox.service.network.managers.xblshared.CompanionSession$4;
        r4.<init>(r1);
        r7.runNativeAction(r4);
        r5 = r7.sessionStateListenerList;
        monitor-enter(r5);
        r4 = r7.sessionStateListenerList;	 Catch:{ all -> 0x0042 }
        r2 = r4.iterator();	 Catch:{ all -> 0x0042 }
    L_0x002c:
        r4 = r2.hasNext();	 Catch:{ all -> 0x0042 }
        if (r4 == 0) goto L_0x0048;
    L_0x0032:
        r3 = r2.next();	 Catch:{ all -> 0x0042 }
        r3 = (com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionStateListener) r3;	 Catch:{ all -> 0x0042 }
        r0 = r3;
        r4 = new com.microsoft.xbox.service.network.managers.xblshared.CompanionSession$5;	 Catch:{ all -> 0x0042 }
        r4.<init>(r0);	 Catch:{ all -> 0x0042 }
        com.microsoft.xbox.toolkit.ThreadManager.UIThreadPost(r4);	 Catch:{ all -> 0x0042 }
        goto L_0x002c;
    L_0x0042:
        r4 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0042 }
        throw r4;
    L_0x0045:
        r4 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0045 }
        throw r4;
    L_0x0048:
        monitor-exit(r5);	 Catch:{ all -> 0x0042 }
        goto L_0x000f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.service.network.managers.xblshared.CompanionSession.shutdownSession(boolean):void");
    }

    public void getActiveTitleId() throws XLEException {
        final LongWrapper status = new LongWrapper();
        runNativeAction(new Runnable() {
            public void run() {
                synchronized (CompanionSession.this.datalock) {
                    status.value = CompanionSession.this.nativeGetActiveTitleId();
                }
            }
        });
        if (status.value != 0) {
            throw new XLEException((long) XLEErrorCode.FAILED_TO_GET_TITLE_ID, "NATIVE: GetActiveTitleId returned error code: " + Long.toString(status.value));
        }
    }

    public void getMediaState() throws XLEException {
        if (!TestInterop.onGetMediaState()) {
            final LongWrapper status = new LongWrapper();
            runNativeAction(new Runnable() {
                public void run() {
                    synchronized (CompanionSession.this.datalock) {
                        status.value = CompanionSession.this.nativeGetMediaState();
                    }
                }
            });
            if (status.value != 0) {
                throw new XLEException((long) XLEErrorCode.FAILED_TO_GET_MEDIA_STATE, "NATIVE: GetMediaState returned error code: " + Long.toString(status.value));
            }
        }
    }

    public void onSessionStateChanged(int sessionState, int capability, long errorCode) {
        XLELog.Warning("CompanionSession", "onSessionStateChanged: " + sessionState);
        synchronized (this.datalock) {
            XLEException error = null;
            if (sessionState == 3) {
                XLEException error2 = new XLEException(XLEErrorCode.FAILED_TO_CONNECT_TO_CONSOLE);
                try {
                    XLELog.Warning("CompanionSession", "Connection to console failed with error code: " + Long.toString(errorCode));
                    this.lastErrorCode = errorCode;
                    error = error2;
                } catch (Throwable th) {
                    Throwable th2 = th;
                    error = error2;
                    throw th2;
                }
            }
            try {
                this.currentCapability = capability;
                if (getCurrentSessionState() != sessionState) {
                    this.currentSessionState = sessionState;
                    final int newSessionState = sessionState;
                    final XLEException exception = error;
                    synchronized (this.sessionStateListenerList) {
                        Iterator i$ = this.sessionStateListenerList.iterator();
                        while (i$.hasNext()) {
                            final ICompanionSessionStateListener finalListener = (ICompanionSessionStateListener) i$.next();
                            ThreadManager.UIThreadPost(new Runnable() {
                                public void run() {
                                    finalListener.onSessionStateChanged(newSessionState, exception);
                                }
                            });
                        }
                    }
                }
            } catch (Throwable th3) {
                th2 = th3;
                throw th2;
            }
        }
    }

    public void onTitleChanged(long titleId) {
        XLELog.Warning("CompanionSession", "onTitleChanged: " + titleId);
        synchronized (this.datalock) {
            final long oldTitleId = this.currentTitleId;
            final long newTitleId = titleId;
            if (newTitleId != this.currentTitleId) {
                this.currentTitleId = titleId;
                this.currentMediaState = null;
            }
            synchronized (this.titleListenerList) {
                Iterator i$ = this.titleListenerList.iterator();
                while (i$.hasNext()) {
                    final ICompanionSessionTitleListener finalListener = (ICompanionSessionTitleListener) i$.next();
                    ThreadManager.UIThreadPost(new Runnable() {
                        public void run() {
                            finalListener.onTitleChanged(oldTitleId, newTitleId);
                        }
                    });
                }
            }
        }
    }

    public void onMediaTitleStateUpdated(MediaTitleState mediaState) {
        synchronized (this.datalock) {
            if (mediaState != null) {
                if (0 < mediaState.getTitleId() && mediaState.getTitleId() != this.currentTitleId) {
                    this.currentTitleId = mediaState.TitleId;
                    XLELog.Diagnostic("SessionModel", String.format("onMediaTitleStateUpdated: Received a new title id. Notifying title change. Current: %d, Received: %d", new Object[]{Long.valueOf(getCurrentTitleId()), Long.valueOf(mediaState.getTitleId())}));
                    onTitleChanged(mediaState.getTitleId());
                }
            }
            this.currentMediaState = mediaState;
            final MediaTitleState newMediaState = mediaState;
            synchronized (this.mediaTitleStateListenerList) {
                Iterator i$ = this.mediaTitleStateListenerList.iterator();
                while (i$.hasNext()) {
                    final ICompanionSessionMediaTitleStateListener finalListener = (ICompanionSessionMediaTitleStateListener) i$.next();
                    ThreadManager.UIThreadPost(new Runnable() {
                        public void run() {
                            finalListener.onMediaTitleStateUpdated(newMediaState);
                        }
                    });
                }
            }
        }
    }

    public void onGetKeyboardTextResponse(KeyboardText keyboardText) {
        synchronized (this.datalock) {
            final KeyboardText newKeyboardText = keyboardText;
            synchronized (this.keyboardTextListenerList) {
                Iterator i$ = this.keyboardTextListenerList.iterator();
                while (i$.hasNext()) {
                    final ICompanionSessionKeyboardTextListener finalListener = (ICompanionSessionKeyboardTextListener) i$.next();
                    ThreadManager.UIThreadPost(new Runnable() {
                        public void run() {
                            finalListener.OnGetKeyboardTextResponse(newKeyboardText);
                        }
                    });
                }
            }
        }
    }

    public void onGetConsoleSettingsResponse(ConsoleSettings consoleSettings) {
        synchronized (this.datalock) {
            final ConsoleSettings newConsoleSettings = consoleSettings;
            synchronized (this.consoleSettingsListenerList) {
                Iterator i$ = this.consoleSettingsListenerList.iterator();
                while (i$.hasNext()) {
                    final ICompanionSessionConsoleSettingsListener finalListener = (ICompanionSessionConsoleSettingsListener) i$.next();
                    ThreadManager.UIThreadPost(new Runnable() {
                        public void run() {
                            finalListener.OnGetConsoleSettingsResponse(newConsoleSettings);
                        }
                    });
                }
            }
        }
    }

    public void onGetActiveTitleInfoResponse(ActiveTitleInfo activeTitleInfo) {
        synchronized (this.datalock) {
            final ActiveTitleInfo newActiveTitleInfo = activeTitleInfo;
            synchronized (this.activeTitleInfoListenerList) {
                Iterator i$ = this.activeTitleInfoListenerList.iterator();
                while (i$.hasNext()) {
                    final ICompanionSessionActiveTitleInfoListener finalListener = (ICompanionSessionActiveTitleInfoListener) i$.next();
                    ThreadManager.UIThreadPost(new Runnable() {
                        public void run() {
                            finalListener.OnGetActiveTitleInfoResponse(newActiveTitleInfo);
                        }
                    });
                }
            }
        }
    }

    public void onTitleMessage(int channel, byte[] message) {
        synchronized (this.datalock) {
            if (channel == 0) {
                String stringMessage = new String(message);
                if (channel == 0 && stringMessage.endsWith("\u0000")) {
                    stringMessage = stringMessage.substring(0, stringMessage.length() - 1);
                }
                final String finalMessage = stringMessage;
                synchronized (this.titleMessageListenerList) {
                    Iterator i$ = this.titleMessageListenerList.iterator();
                    while (i$.hasNext()) {
                        final ICompanionSessionJsonTitleMessageListener finalListener = (ICompanionSessionJsonTitleMessageListener) i$.next();
                        ThreadManager.UIThreadPost(new Runnable() {
                            public void run() {
                                finalListener.onJsonTitleMessage(finalMessage);
                            }
                        });
                    }
                }
            }
        }
    }

    public void onRequestCompleted(int operation, int state, long error) {
        boolean z = true;
        synchronized (this.datalock) {
            if (ThreadManager.UIThread == Thread.currentThread()) {
                z = false;
            }
            XLEAssert.assertTrue(z);
            XLELog.Diagnostic("CompanionSession", String.format("OnRequestCompleted: operation %d, state %d, error %d", new Object[]{Integer.valueOf(operation), Integer.valueOf(state), Long.valueOf(error)}));
            synchronized (this.requestCompleteListenerList) {
                Iterator i$ = this.requestCompleteListenerList.iterator();
                while (i$.hasNext()) {
                    final ICompanionSessionRequestCompleteListener finalListener = (ICompanionSessionRequestCompleteListener) i$.next();
                    final int i = operation;
                    final int i2 = state;
                    final long j = error;
                    ThreadManager.UIThreadPost(new Runnable() {
                        public void run() {
                            finalListener.onSessionRequestCompleted(i, i2, j);
                        }
                    });
                }
            }
        }
    }

    public void onTitleChannelStateChanged(int newState) {
        synchronized (this.datalock) {
            this.currentTitleChannelState = newState;
            final int finalState = newState;
            synchronized (this.titleChannelStateListenerList) {
                Iterator i$ = this.titleChannelStateListenerList.iterator();
                while (i$.hasNext()) {
                    final ICompanionSessionTitleChannelStateListener finalListener = (ICompanionSessionTitleChannelStateListener) i$.next();
                    ThreadManager.UIThreadPost(new Runnable() {
                        public void run() {
                            finalListener.onTitleChannelStateChanged(finalState, null);
                        }
                    });
                }
            }
        }
    }

    public void onDeviceJoined(int clientId, int deviceType, String userDisplayNames) {
        synchronized (this.datalock) {
            final int finalClientId = clientId;
            final int finalDeviceType = deviceType;
            final String finalUserDisplayNames = userDisplayNames;
            synchronized (this.deviceJoinedListenerList) {
                Iterator i$ = this.deviceJoinedListenerList.iterator();
                while (i$.hasNext()) {
                    final ICompanionSessionDeviceJoinedListener finalListener = (ICompanionSessionDeviceJoinedListener) i$.next();
                    ThreadManager.UIThreadPost(new Runnable() {
                        public void run() {
                            finalListener.onDeviceJoined(finalClientId, finalDeviceType, finalUserDisplayNames);
                        }
                    });
                }
            }
        }
    }

    public void onDeviceLeft(int clientId) {
        synchronized (this.datalock) {
            final int finalClientId = clientId;
            synchronized (this.deviceLeftListenerList) {
                Iterator i$ = this.deviceLeftListenerList.iterator();
                while (i$.hasNext()) {
                    final ICompanionSessionDeviceLeftListener finalListener = (ICompanionSessionDeviceLeftListener) i$.next();
                    ThreadManager.UIThreadPost(new Runnable() {
                        public void run() {
                            finalListener.onDeviceLeft(finalClientId);
                        }
                    });
                }
            }
        }
    }

    public void OnStreamingModeChanged(boolean exclusiveModeTaken) {
        if (this.currentCapability == 2) {
            if (!exclusiveModeTaken) {
                Disconnect();
                Connect();
            }
        }
    }

    public void LaunchTitleRaw(final long titleId, final String parameters) {
        XLELog.Diagnostic("CompanionSession", "Launching title raw " + titleId);
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeLaunchTitleRaw(titleId, parameters);
            }
        });
    }

    public void LaunchTitle(final long titleId, final int titleType) {
        XLELog.Diagnostic("CompanionSession", "Launching title " + titleId);
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeLaunchTitle(titleId, titleType);
            }
        });
    }

    public void LaunchTitle(long titleId, int launchType, String deepLink) {
        XLELog.Diagnostic("CompanionSession", String.format("Launching title with deeplink %d %s", new Object[]{Long.valueOf(titleId), deepLink}));
        final long j = titleId;
        final int i = launchType;
        final String str = deepLink;
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeLaunchTitleWithLink(j, i, str);
            }
        });
    }

    public void LaunchZuneContent(final String mediaId, final int mediaType) {
        XLELog.Diagnostic("CompanionSession", String.format("Launching zune content '%s' '%d'", new Object[]{mediaId, Integer.valueOf(mediaType)}));
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeLaunchZuneContent(mediaId, mediaType);
            }
        });
    }

    public void DispatchTask(final int ptrCallback, final int[] params, final int sleepSeconds) {
        XLELog.Diagnostic("CompanionSession", "dispatch work on java thread...");
        XLEThreadPool.networkOperationsThreadPool.run(new Runnable() {
            public void run() {
                XLELog.Diagnostic("CompanionSession", "dispatch work wait thread running...");
                try {
                    Thread.sleep((long) (sleepSeconds * EDSV2MediaType.MEDIATYPE_MOVIE));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                XLELog.Diagnostic("CompanionSession", "dispatch work marshalling back to main thread...");
                XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
                    public void run() {
                        XLELog.Diagnostic("CompanionSession", "dispatch work executing on main thread...");
                        CompanionSession.this.nativeDispatchCallback(ptrCallback, params);
                    }
                });
            }
        });
        XLELog.Warning("CompanionSession", "thread calling to dispatch work is ending...");
    }

    public void SendControlCommand(final int key) {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeSendControlCommand(key);
            }
        });
    }

    public void SendControlCommand(LRCControlKey key) {
        XLELog.Diagnostic("CompanionSession", "Sending key " + key.toString());
        SendControlCommand(key.getKeyValue());
    }

    public void SendControlCommandWithMediaCenterSupport(final int key) {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeSendControlCommandWithMediaCenterSupport(key);
            }
        });
    }

    public void SendControlCommandWithMediaCenterSupport(LRCControlKey key) {
        XLELog.Diagnostic("CompanionSession", "Sending key for media center support " + key.toString());
        SendControlCommandWithMediaCenterSupport(key.getKeyValue());
    }

    public void SendSeekControlCommand(final long seekPosition) {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeSendSeekCommand(seekPosition);
            }
        });
    }

    public void SendTitleMessage(final String message) {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeSendTitleMessage(0, message.getBytes());
            }
        });
    }

    public void SendTitleTouchFrame(final TouchFrame touchFrame) {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeSendTitleTouchFrame(touchFrame);
            }
        });
    }

    public void SendTitleAccelerometerData(final float x, final float y, final float z) {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeSendTitleAccelerometerData(x, y, z);
            }
        });
    }

    public void SendTitleGyroscopeData(final float x, final float y, final float z) {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeSendTitleGyroscopeData(x, y, z);
            }
        });
    }

    public void SetText(final String text, final String previousText) {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeSetText(text, previousText);
            }
        });
    }

    public void Connect() {
        this.currentSessionState = 1;
        this.lastErrorCode = 0;
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeConnect();
            }
        });
    }

    public void Disconnect() {
        this.currentSessionState = 0;
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeDisconnect();
            }
        });
    }

    public void AcquireExclusiveMode() {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeAcquireExclusiveMode();
            }
        });
    }

    public void ReleaseExclusiveMode() {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeReleaseExclusiveMode();
            }
        });
    }

    public void GetConsoleSettings() {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeGetConsoleSettings();
            }
        });
    }

    public void GetActiveTitleInfo() {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeGetActiveTitleInfo();
            }
        });
    }

    public void GetText() {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeGetText();
            }
        });
    }

    public void EstablishTitleChannel() {
        runNativeAction(new Runnable() {
            public void run() {
                CompanionSession.this.nativeEstablishTitleChannel();
            }
        });
    }

    private void runNativeAction(Runnable action) {
        XLEThreadPool.nativeOperationsThreadPool.run(action);
    }
}
