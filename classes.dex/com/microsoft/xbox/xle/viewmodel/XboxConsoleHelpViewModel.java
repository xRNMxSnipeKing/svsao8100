package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.SessionModel;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;

public class XboxConsoleHelpViewModel extends ViewModelBase {
    private static final String CONNECTING = XLEApplication.Resources.getString(R.string.connecting_to_xbox_blocking);

    public XboxConsoleHelpViewModel() {
        this.adapter = AdapterFactory.getInstance().getXboxConsoleHelpAdapter(this);
    }

    protected void onStartOverride() {
        SessionModel.getInstance().addObserver(this);
    }

    protected void onStopOverride() {
        SessionModel.getInstance().removeObserver(this);
    }

    public boolean isBusy() {
        return false;
    }

    public void load(boolean forceRefresh) {
        SessionModel.getInstance().load(false);
    }

    public String getBlockingStatusText() {
        return CONNECTING;
    }

    public void retryConnectXbox() {
        XboxMobileOmnitureTracking.TrackConsoleConnectAttempt("Manual", "Helppage retry");
        AutoConnectAndLaunchViewModel.getInstance().manualConnectAndLaunch();
    }

    public boolean isBlockingBusy() {
        return false;
    }

    public void cancelToConnectXbox() {
        goBack();
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getXboxConsoleHelpAdapter(this);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateOverride(com.microsoft.xbox.toolkit.AsyncResult<com.microsoft.xbox.service.model.UpdateData> r5) {
        /*
        r4 = this;
        r1 = r5.getResult();
        r1 = (com.microsoft.xbox.service.model.UpdateData) r1;
        r0 = r1.getUpdateType();
        r1 = "XboxConsoleHelpVM";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Received update: ";
        r2 = r2.append(r3);
        r3 = r0.toString();
        r2 = r2.append(r3);
        r2 = r2.toString();
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r1, r2);
        r1 = com.microsoft.xbox.xle.viewmodel.XboxConsoleHelpViewModel.AnonymousClass1.$SwitchMap$com$microsoft$xbox$service$model$UpdateType;
        r2 = r0.ordinal();
        r1 = r1[r2];
        switch(r1) {
            case 1: goto L_0x0053;
            default: goto L_0x0031;
        };
    L_0x0031:
        r1 = "XboxConsoleHelpVM";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Unexpected update type ";
        r2 = r2.append(r3);
        r3 = r0.toString();
        r2 = r2.append(r3);
        r2 = r2.toString();
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r1, r2);
    L_0x004d:
        r1 = r4.adapter;
        r1.updateView();
        return;
    L_0x0053:
        r1 = com.microsoft.xbox.service.model.SessionModel.getInstance();
        r1 = r1.getDisplayedSessionState();
        r2 = 2;
        if (r1 != r2) goto L_0x0031;
    L_0x005e:
        r4.goBack();
        goto L_0x004d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.xle.viewmodel.XboxConsoleHelpViewModel.updateOverride(com.microsoft.xbox.toolkit.AsyncResult):void");
    }
}
