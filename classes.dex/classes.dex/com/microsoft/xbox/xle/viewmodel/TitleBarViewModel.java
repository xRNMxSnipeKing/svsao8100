package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.MessageModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel.NowPlayingState;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.MessagesActivity;
import com.microsoft.xbox.xle.app.adapter.TitleBarAdapter;
import java.util.EnumSet;

public class TitleBarViewModel extends ViewModelBase {
    private ConnectionState connectionState;
    private MessageModel messageModel;

    public enum ConnectionState {
        NotConnected,
        Connecting,
        Connected
    }

    public TitleBarViewModel() {
        this.connectionState = ConnectionState.Connecting;
        this.adapter = new TitleBarAdapter(this);
        this.messageModel = MessageModel.getInstance();
        updateConnectionState();
    }

    public void onRehydrate() {
        this.adapter = new TitleBarAdapter(this);
    }

    public boolean getHasUnreadMessages() {
        return this.messageModel != null && this.messageModel.getUnReadMessageCount() > 0;
    }

    public String getMessageCountText() {
        if (this.messageModel != null && this.messageModel.getUnReadMessageCount() > 0) {
            return Integer.toString(this.messageModel.getUnReadMessageCount());
        }
        return null;
    }

    public boolean isBusy() {
        return this.messageModel == null ? false : this.messageModel.getIsLoadingMessageList();
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        updateConnectionState();
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.MessageData, XLEErrorCode.FAILED_TO_GET_MESSAGE_SUMMARY)) {
            super.onUpdateFinished();
        } else {
            super.onUpdateFinished();
        }
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MessageData));
        if (this.messageModel != null) {
            this.messageModel.loadMessageList(forceRefresh);
        }
    }

    protected void onStartOverride() {
        if (this.messageModel != null) {
            this.messageModel.addObserver(this);
        }
        NowPlayingGlobalModel.getInstance().addObserver(this);
    }

    protected void onStopOverride() {
        if (this.messageModel != null) {
            this.messageModel.removeObserver(this);
            this.messageModel = null;
        }
        NowPlayingGlobalModel.getInstance().removeObserver(this);
    }

    public void navigateToMessagesList() {
        XLELog.Info("TitleBarViewModel", "Navigating to messages list");
        NavigateTo(MessagesActivity.class);
    }

    public void connectToConsole() {
        XboxMobileOmnitureTracking.TrackConsoleConnectAttempt("Manual", "TitleBar Connect");
        AutoConnectAndLaunchViewModel.getInstance().manualConnectAndLaunch();
    }

    public ConnectionState getConnectionState() {
        return this.connectionState;
    }

    private void updateConnectionState() {
        NowPlayingState state = NowPlayingGlobalModel.getInstance().getNowPlayingState();
        XLELog.Diagnostic("TitleBarViewModel", "ConeectState changed to  " + state);
        switch (state) {
            case Connecting:
                this.connectionState = ConnectionState.Connecting;
                return;
            case Disconnected:
                this.connectionState = ConnectionState.NotConnected;
                return;
            case ConnectedPlayingDash:
            case ConnectedPlayingDashMedia:
            case ConnectedPlayingMusic:
            case ConnectedPlayingVideo:
            case ConnectedPlayingApp:
            case ConnectedPlayingGame:
                this.connectionState = ConnectionState.Connected;
                return;
            default:
                XLEAssert.assertTrue(false);
                this.connectionState = ConnectionState.NotConnected;
                return;
        }
    }
}
