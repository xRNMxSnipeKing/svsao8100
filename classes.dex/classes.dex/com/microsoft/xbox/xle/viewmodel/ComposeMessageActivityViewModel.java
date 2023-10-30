package com.microsoft.xbox.xle.viewmodel;

import android.content.res.Configuration;
import android.os.Bundle;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MessageModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.serialization.SendMessageRequest;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.FriendsSelectorActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import com.microsoft.xbox.xle.app.adapter.ComposeMessageActivityAdapter;
import java.util.ArrayList;
import java.util.EnumSet;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class ComposeMessageActivityViewModel extends ViewModelBase {
    private boolean autoFocus;
    private String busyText;
    private String messageBody;
    private final String messageBodyTag;
    private MessageModel messageModel;
    private String recipientString;
    private ArrayList<String> recipients;

    public ComposeMessageActivityViewModel() {
        this.messageBodyTag = "Compose_Message_Body";
        this.messageModel = MessageModel.getInstance();
        this.busyText = XLEApplication.Resources.getString(R.string.blocking_status_sending);
        this.adapter = AdapterFactory.getInstance().getComposeMessageAdapter(this);
        this.autoFocus = true;
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getComposeMessageAdapter(this);
    }

    protected void onStartOverride() {
        this.messageModel.addObserver(this);
        StringBuilder builder = new StringBuilder(XLEApplication.Resources.getString(R.string.message_to));
        builder.append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
        this.recipients = XLEGlobalData.getInstance().getSelectedRecipients().toArrayList();
        if (this.recipients.size() > 0) {
            for (int i = 0; i < this.recipients.size() - 1; i++) {
                builder.append((String) this.recipients.get(i));
                builder.append(XLEApplication.Resources.getString(R.string.message_seperator));
            }
            builder.append((String) this.recipients.get(this.recipients.size() - 1));
        } else {
            builder.append(XLEApplication.Resources.getString(R.string.message_to_hint_text));
        }
        this.recipientString = builder.toString();
        if (this.adapter instanceof ComposeMessageActivityAdapter) {
            this.adapter.setMessageEditText(getMessageBody());
        }
    }

    protected void onStopOverride() {
        this.adapter.updateMessageBody();
        this.messageModel.removeObserver(this);
    }

    public void updateOverride(AsyncResult<UpdateData> result) {
        switch (((UpdateData) result.getResult()).getUpdateType()) {
            case MessageSend:
                if (result.getException() == null) {
                    goBack();
                    return;
                }
                break;
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.MessageSend, XLEErrorCode.FAILED_TO_SEND_MESSAGE)) {
            showError(R.string.toast_message_send_error);
        }
        super.onUpdateFinished();
    }

    public boolean isBusy() {
        return false;
    }

    public boolean isBlockingBusy() {
        return this.messageModel.getIsSending();
    }

    public String getBlockingStatusText() {
        return this.busyText;
    }

    public boolean getIsRecipientNonEmpty() {
        return this.recipients.size() > 0;
    }

    public String getRecipients() {
        return this.recipientString;
    }

    public void setMessageBody(String body) {
        this.messageBody = body;
    }

    public String getMessageBody() {
        return this.messageBody;
    }

    public String getBusyText() {
        return this.busyText;
    }

    public boolean getShouldAutoShowKeyboard() {
        return this.autoFocus;
    }

    public int getMessageCharacterCount() {
        if (this.messageBody == null) {
            return 0;
        }
        return this.messageBody.length();
    }

    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            this.adapter.updateMessageBody();
            outState.putString("Compose_Message_Body", getMessageBody());
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String messageBody = savedInstanceState.getString("Compose_Message_Body");
            this.adapter.setMessageEditText(messageBody);
            setMessageBody(messageBody);
        }
    }

    public void load(boolean forceRefresh) {
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == 2) {
            this.autoFocus = false;
        } else {
            this.autoFocus = true;
        }
    }

    public void sendMessage() {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MessageSend));
        SendMessageRequest request = new SendMessageRequest();
        ((ComposeMessageActivityAdapter) this.adapter).updateMessageBody();
        request.MessageText = this.messageBody;
        request.Recipients = this.recipients;
        this.messageModel.sendMessage(request);
        this.adapter.updateView();
        XboxMobileOmnitureTracking.TrackMsgSend();
    }

    public void cancelSendClick() {
        ((ComposeMessageActivityAdapter) this.adapter).updateMessageBody();
        if (this.messageBody == null || this.messageBody.length() == 0) {
            super.goBack();
        } else {
            showDiscardChangesGoBack();
        }
    }

    public void onBackButtonPressed() {
        cancelSendClick();
    }

    public void navigateToFriendsPicker() {
        NavigateTo(FriendsSelectorActivity.class);
    }
}
