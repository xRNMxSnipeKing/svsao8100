package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.service.model.MediaTitleState;
import com.microsoft.xbox.service.model.smartglass.TouchFrame;
import com.microsoft.xbox.toolkit.XLEException;

public interface ICompanionSession {
    void AcquireExclusiveMode();

    void Connect();

    void Disconnect();

    void EstablishTitleChannel();

    void GetActiveTitleInfo();

    void GetConsoleSettings();

    void GetText();

    void LaunchTitle(long j, int i);

    void LaunchTitle(long j, int i, String str);

    void LaunchTitleRaw(long j, String str);

    void LaunchZuneContent(String str, int i);

    void ReleaseExclusiveMode();

    void SendControlCommand(int i);

    void SendControlCommandWithMediaCenterSupport(int i);

    void SendSeekControlCommand(long j);

    void SendTitleAccelerometerData(float f, float f2, float f3);

    void SendTitleGyroscopeData(float f, float f2, float f3);

    void SendTitleMessage(String str);

    void SendTitleTouchFrame(TouchFrame touchFrame);

    void SetText(String str, String str2);

    void addCompanionSessionActiveTitleInfoListener(ICompanionSessionActiveTitleInfoListener iCompanionSessionActiveTitleInfoListener);

    void addCompanionSessionConsoleSettingsListener(ICompanionSessionConsoleSettingsListener iCompanionSessionConsoleSettingsListener);

    void addCompanionSessionDeviceJoinedListener(ICompanionSessionDeviceJoinedListener iCompanionSessionDeviceJoinedListener);

    void addCompanionSessionDeviceLeftListener(ICompanionSessionDeviceLeftListener iCompanionSessionDeviceLeftListener);

    void addCompanionSessionJsonTitleMessageListener(ICompanionSessionJsonTitleMessageListener iCompanionSessionJsonTitleMessageListener);

    void addCompanionSessionKeyboardTextListener(ICompanionSessionKeyboardTextListener iCompanionSessionKeyboardTextListener);

    void addCompanionSessionMediaTitleStateListener(ICompanionSessionMediaTitleStateListener iCompanionSessionMediaTitleStateListener);

    void addCompanionSessionRequestCompleteListener(ICompanionSessionRequestCompleteListener iCompanionSessionRequestCompleteListener);

    void addCompanionSessionStateListener(ICompanionSessionStateListener iCompanionSessionStateListener);

    void addCompanionSessionTitleChannelStateListener(ICompanionSessionTitleChannelStateListener iCompanionSessionTitleChannelStateListener);

    void addCompanionSessionTitleListener(ICompanionSessionTitleListener iCompanionSessionTitleListener);

    void getActiveTitleId() throws XLEException;

    int getCurrentCapability();

    MediaTitleState getCurrentMediaState();

    int getCurrentSessionState();

    int getCurrentTitleChannelState();

    long getCurrentTitleId();

    long getLastErrorCode();

    void getMediaState() throws XLEException;

    void initialize(int i);

    void joinSession();

    void removeCompanionSessionActiveTitleInfoListener(ICompanionSessionActiveTitleInfoListener iCompanionSessionActiveTitleInfoListener);

    void removeCompanionSessionConsoleSettingsListener(ICompanionSessionConsoleSettingsListener iCompanionSessionConsoleSettingsListener);

    void removeCompanionSessionDeviceJoinedListener(ICompanionSessionDeviceJoinedListener iCompanionSessionDeviceJoinedListener);

    void removeCompanionSessionDeviceLeftListener(ICompanionSessionDeviceLeftListener iCompanionSessionDeviceLeftListener);

    void removeCompanionSessionJsonTitleMessageListener(ICompanionSessionJsonTitleMessageListener iCompanionSessionJsonTitleMessageListener);

    void removeCompanionSessionKeyboardTextListener(ICompanionSessionKeyboardTextListener iCompanionSessionKeyboardTextListener);

    void removeCompanionSessionMediaTitleStateListener(ICompanionSessionMediaTitleStateListener iCompanionSessionMediaTitleStateListener);

    void removeCompanionSessionRequestCompleteListener(ICompanionSessionRequestCompleteListener iCompanionSessionRequestCompleteListener);

    void removeCompanionSessionStateListener(ICompanionSessionStateListener iCompanionSessionStateListener);

    void removeCompanionSessionTitleChannelStateListener(ICompanionSessionTitleChannelStateListener iCompanionSessionTitleChannelStateListener);

    void removeCompanionSessionTitleListener(ICompanionSessionTitleListener iCompanionSessionTitleListener);

    void setAuthenticationToken(String str);

    void shutdownSession(boolean z);
}
