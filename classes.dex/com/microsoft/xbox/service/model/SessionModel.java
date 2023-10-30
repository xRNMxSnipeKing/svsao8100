package com.microsoft.xbox.service.model;

import com.microsoft.xbox.authenticate.XstsToken;
import com.microsoft.xbox.service.network.managers.MulticastManager;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.XstsTokenManager;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSession;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionJsonTitleMessageListener;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionMediaTitleStateListener;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionRequestCompleteListener;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionStateListener;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionTitleListener;
import com.microsoft.xbox.service.network.managers.xblshared.XBLSharedServiceManager;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoaderTask;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XLEObservable;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;

public class SessionModel extends XLEObservable<UpdateData> implements ICompanionSessionStateListener, ICompanionSessionMediaTitleStateListener, ICompanionSessionTitleListener, ICompanionSessionJsonTitleMessageListener, ICompanionSessionRequestCompleteListener {
    private static final String TMF_AUDIENCE_URL = "http://xlink.xboxlive.com";
    private static String lastSetAuthenticationToken;
    private ICompanionSession companionSession;
    private boolean falseStart;
    private boolean isConnecting;
    private boolean isRetryConnecting;
    private Runnable queuedAction;
    private boolean shouldShowSlowConnectionDialog;

    private static class SessionModelHolder {
        public static SessionModel instance = new SessionModel();

        private SessionModelHolder() {
        }

        public static void reset() {
            XLELog.Diagnostic("SessionModel", "Reset is called");
            instance.resetSessionData();
        }
    }

    private class JoinSessionRunner extends IDataLoaderRunnable<Void> {
        private JoinSessionRunner() {
        }

        public void onPreExecute() {
        }

        public Void buildData() throws XLEException {
            if (!XboxLiveEnvironment.Instance().isUsingStub()) {
                XstsToken token = XstsTokenManager.getInstance().getXstsToken("http://xlink.xboxlive.com");
                XLELog.Diagnostic("SessionModel", "JoinSession started");
                if (token != null) {
                    String currentToken = token.getToken();
                    if (SessionModel.lastSetAuthenticationToken != currentToken) {
                        SessionModel.lastSetAuthenticationToken = currentToken;
                        SessionModel.this.companionSession.setAuthenticationToken(SessionModel.lastSetAuthenticationToken);
                    }
                } else {
                    XLELog.Error("CompanionSession", "can't get xsts token for tmf service");
                    throw new XLEException(XLEErrorCode.FAILED_TO_GET_XSTS_TOKEN);
                }
            }
            SessionModel.this.companionSession.joinSession();
            return null;
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_CONNECT_TO_CONSOLE;
        }

        public void onPostExcute(AsyncResult<Void> result) {
            SessionModel.this.onJoinSessionAsync(result);
        }
    }

    private SessionModel() {
        this.isConnecting = false;
        this.isRetryConnecting = false;
        this.shouldShowSlowConnectionDialog = false;
        this.falseStart = false;
        this.companionSession = ServiceManagerFactory.getInstance().getCompanionSession();
        this.companionSession.addCompanionSessionStateListener(this);
        this.companionSession.addCompanionSessionTitleListener(this);
        this.companionSession.addCompanionSessionMediaTitleStateListener(this);
        this.companionSession.addCompanionSessionJsonTitleMessageListener(this);
        this.companionSession.addCompanionSessionRequestCompleteListener(this);
    }

    public static SessionModel getInstance() {
        return SessionModelHolder.instance;
    }

    public static void reset(boolean shutdown) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        getInstance().clearObserver();
        getInstance().leaveSession(shutdown);
        SessionModelHolder.reset();
    }

    public boolean getIsConnecting() {
        return this.isConnecting;
    }

    public boolean getIsRetryConnecting() {
        return this.isRetryConnecting;
    }

    public void setRetryConnectingStatus(boolean value) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        this.isRetryConnecting = value;
    }

    public void setRetryFailed() {
        boolean z;
        if (ThreadManager.UIThread == Thread.currentThread()) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        this.isRetryConnecting = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.SessionRetryConnectFailed, true), this, null));
    }

    public int getDisplayedSessionState() {
        if (this.isConnecting || this.falseStart) {
            return 1;
        }
        return this.companionSession.getCurrentSessionState();
    }

    public long getLastErrorCode() {
        return this.companionSession.getLastErrorCode();
    }

    public int getCurrentCapability() {
        return this.companionSession.getCurrentCapability();
    }

    public long getCurrentTitleId() {
        return this.companionSession.getCurrentTitleId();
    }

    public MediaTitleState getCurrentMediaState() {
        return this.companionSession.getCurrentMediaState();
    }

    public void load(boolean refreshSessionData) {
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.SessionState, true), this, null));
    }

    public void leaveSession(boolean shutdown) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        XLELog.Diagnostic("SessionModel", "leave the session");
        CompanionSession.getInstance().shutdownSession(shutdown);
        MulticastManager.UnregisterMulticast();
    }

    private void resetSessionData() {
        this.shouldShowSlowConnectionDialog = false;
    }

    public void falseStart() {
        this.falseStart = true;
    }

    public void connectToConsole() {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        XLELog.Diagnostic("SessionModel", "connect to console is called");
        this.falseStart = false;
        this.companionSession.initialize(XBLSharedServiceManager.nativeGetGlobalEnvironment());
        if (this.isConnecting || this.companionSession.getCurrentSessionState() == 1 || this.companionSession.getCurrentSessionState() == 2) {
            XLELog.Diagnostic("SessionModel", "ignore connect request " + this.companionSession.getCurrentSessionState());
            return;
        }
        this.isConnecting = true;
        MulticastManager.UnregisterMulticast();
        new DataLoaderTask(new JoinSessionRunner()).execute();
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.SessionState, false), this, null));
    }

    private void onJoinSessionAsync(AsyncResult<Void> result) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        this.isConnecting = false;
        if (result.getException() != null) {
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.SessionState, true), this, result.getException()));
        }
    }

    public void sendControlCommand(LRCControlKey key) {
        sendControlCommandWithMediaCenterSupport(key);
    }

    public void sendControlCommandWithMediaCenterSupport(final LRCControlKey key) {
        runActionSafe(new Runnable() {
            public void run() {
                CompanionSession.getInstance().SendControlCommandWithMediaCenterSupport(key);
            }
        });
    }

    public void sendSeekCommand(final long seekPosition) {
        runActionSafe(new Runnable() {
            public void run() {
                CompanionSession.getInstance().SendSeekControlCommand(seekPosition);
            }
        });
    }

    public void launchTitle(final long titleId, final int titleType) {
        runActionSafe(new Runnable() {
            public void run() {
                CompanionSession.getInstance().LaunchTitle(titleId, titleType);
            }
        });
    }

    public void launchProvider(long titleId, int launchType, String deepLink) {
        if (deepLink == null || deepLink.length() == 0) {
            XLEAssert.assertTrue(false);
            return;
        }
        final long j = titleId;
        final int i = launchType;
        final String str = deepLink;
        runActionSafe(new Runnable() {
            public void run() {
                CompanionSession.getInstance().LaunchTitle(j, i, str);
            }
        });
    }

    public void launchZuneContent(final String mediaId, final int mediaType) {
        runActionSafe(new Runnable() {
            public void run() {
                CompanionSession.getInstance().LaunchZuneContent(mediaId, mediaType);
            }
        });
    }

    public void getMediaTitleState() {
        try {
            if (this.companionSession.getCurrentSessionState() == 2) {
                CompanionSession.getInstance().getMediaState();
                return;
            }
        } catch (Exception e) {
            XLELog.Error("SessionModel", "Exception from getMediaState: " + e.toString());
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.SessionState, true), this, null));
    }

    private Runnable dequeueAction() {
        Runnable action = this.queuedAction;
        this.queuedAction = null;
        return action;
    }

    private void runActionSafe(Runnable action) {
        if (this.companionSession.getCurrentSessionState() != 2) {
            queueAction(action);
            connectToConsole();
            return;
        }
        runAction(action);
    }

    private void queueAction(Runnable action) {
        if (this.queuedAction != null) {
            XLELog.Error("SessionModel", "we are overwritten queued action, only one is allowed. ");
        }
        this.queuedAction = action;
    }

    private void runAction(Runnable action) {
        action.run();
    }

    private void completeQueuedActions() {
        Runnable action = dequeueAction();
        if (action != null) {
            runAction(action);
        }
    }

    private void clearQueuedActions() {
        this.queuedAction = null;
    }

    private boolean hasQueuedActions() {
        return this.queuedAction != null;
    }

    public void onSessionStateChanged(int newSessionState, XLEException exception) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        XLELog.Diagnostic("SessionModel", "Session state changed to: " + Integer.toString(newSessionState));
        this.shouldShowSlowConnectionDialog = false;
        switch (newSessionState) {
            case 0:
            case 3:
                resetSessionData();
                this.isConnecting = false;
                break;
            case 1:
                resetSessionData();
                this.isConnecting = true;
                break;
            case 2:
                boolean z;
                if (this.isRetryConnecting || 2 != getCurrentCapability()) {
                    z = false;
                } else {
                    z = true;
                }
                this.shouldShowSlowConnectionDialog = z;
                runActionSafe(new Runnable() {
                    public void run() {
                        try {
                            CompanionSession.getInstance().getMediaState();
                        } catch (XLEException e) {
                            XLELog.Error("SessionModel", "Exception from getMediaState: " + e.toString());
                        }
                    }
                });
                this.isConnecting = false;
                break;
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.SessionState, true), this, exception));
        if (!hasQueuedActions()) {
            return;
        }
        if (newSessionState == 2) {
            completeQueuedActions();
        } else if (newSessionState == 3) {
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.SessionRequestFailure, true), this, null));
            clearQueuedActions();
        }
    }

    public void onTitleChanged(long oldTitleId, long newTitleId) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        XLELog.Diagnostic("SessionModel", String.format("OnTitleChanged %d", new Object[]{Long.valueOf(newTitleId)}));
        XLELog.Diagnostic("SessionModel", String.format("onTitleChanged: Title id changed. Previous %d, Current %d", new Object[]{Long.valueOf(oldTitleId), Long.valueOf(newTitleId)}));
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.SessionState, true), this, null));
    }

    public void onMediaTitleStateUpdated(MediaTitleState mediaState) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.SessionState, true), this, null));
    }

    public void onJsonTitleMessage(String message) {
        XLELog.Diagnostic("SessionModel", "onTitleMessage: " + message);
    }

    public void onSessionRequestCompleted(int operation, int state, long error) {
        if (operation != 2) {
            return;
        }
        if (error == 0) {
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.SessionLaunchRequestComplete, true), this, null));
        } else {
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.SessionLaunchRequestComplete, true), this, new XLEException(error)));
        }
    }

    public boolean drainShouldShowSlowConnectionDialog() {
        boolean rv = this.shouldShowSlowConnectionDialog;
        this.shouldShowSlowConnectionDialog = false;
        return rv;
    }
}
