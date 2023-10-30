package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarViewActorVM;
import com.microsoft.xbox.avatar.model.AvatarViewActorVMDefault;
import com.microsoft.xbox.avatar.model.AvatarViewVM;
import com.microsoft.xbox.avatar.model.AvatarViewVMDefault;
import com.microsoft.xbox.service.model.AvatarManifestModel;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.MessageModel;
import com.microsoft.xbox.service.model.MessagingCapabilityStatus;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.serialization.MessageDetails;
import com.microsoft.xbox.service.model.serialization.MessageSummary;
import com.microsoft.xbox.service.model.serialization.XLEAvatarManifest;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.ComposeMessageActivity;
import com.microsoft.xbox.xle.app.activity.MessageDetailsActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;
import java.util.EnumSet;

public class MessagesActivityViewModel extends ViewModelBase {
    private AvatarManifestModel avatarModel;
    private AvatarViewVM avatarViewVM;
    private AvatarViewActorVMDefault meActorVM;
    private String messageBody;
    private MessageDetails messageDetails;
    private MessageModel messageModel;
    private MessageSummary messageSummary;
    private ListState viewModelState;

    public MessagesActivityViewModel() {
        this.viewModelState = ListState.LoadingState;
        this.meActorVM = null;
        this.avatarViewVM = null;
        this.messageModel = MessageModel.getInstance();
        this.adapter = AdapterFactory.getInstance().getMessagesAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getMessagesAdapter(this);
    }

    public ArrayList<MessageSummary> getMessageList() {
        return this.messageModel.getMessageList();
    }

    public int getUnreadMessageCount() {
        return this.messageModel.getUnReadMessageCount();
    }

    public ListState getListState() {
        return this.viewModelState;
    }

    public MessageModel getMessageModel() {
        return this.messageModel;
    }

    public String getMessageBody() {
        return this.messageBody;
    }

    public String getMessageSender() {
        if (this.messageSummary != null) {
            return this.messageSummary.SenderGamertag;
        }
        return null;
    }

    public void setMessageSummary(MessageSummary summary) {
        this.messageSummary = summary;
        resetAvatarManifest();
    }

    public boolean hasDetailAndAvatarUI() {
        return XLEApplication.Instance.getIsTablet();
    }

    public void loadMessageDetails() {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MessageDetailsData));
        if (hasDetailAndAvatarUI()) {
            XLEAssert.assertTrue(this.messageSummary != null);
            this.messageModel.loadMessageDetails(this.messageSummary.MessageId);
            updateMessageBody();
        }
    }

    private void blockCurrentSenderInternal() {
        XLELog.Diagnostic("MessageActivityViewModel", "blocking current sender");
        XLEAssert.assertTrue(this.messageSummary != null);
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MessageDelete));
        this.messageModel.deleteMessage(this.messageSummary.MessageId, true);
        this.adapter.updateView();
    }

    public void blockCurrentSender() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        showOkCancelDialog(XLEApplication.Resources.getString(R.string.dialog_areyousure_title), XLEApplication.Resources.getString(R.string.message_block_warning), XLEApplication.Resources.getString(R.string.OK), new Runnable() {
            public void run() {
                MessagesActivityViewModel.this.blockCurrentSenderInternal();
            }
        }, XLEApplication.Resources.getString(R.string.Cancel), null);
    }

    private void deleteCurrentMessageInternal() {
        boolean z;
        if (this.messageSummary != null) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MessageDelete));
        this.messageModel.deleteMessage(this.messageSummary.MessageId, false);
    }

    public void deleteCurrentMessage() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        showOkCancelDialog(XLEApplication.Resources.getString(R.string.dialog_areyousure_title), XLEApplication.Resources.getString(R.string.message_delete), XLEApplication.Resources.getString(R.string.OK), new Runnable() {
            public void run() {
                MessagesActivityViewModel.this.deleteCurrentMessageInternal();
            }
        }, XLEApplication.Resources.getString(R.string.Cancel), null);
    }

    protected void onStartOverride() {
        this.messageModel.addObserver(this);
        if (this.avatarViewVM == null && hasDetailAndAvatarUI()) {
            this.avatarViewVM = new AvatarViewVMDefault();
            this.meActorVM = new AvatarViewActorVMDefault();
            this.avatarViewVM.registerActor(this.meActorVM);
            this.meActorVM.setShadowtarVisibilityChangedCallback(new Runnable() {
                public void run() {
                    if (MessagesActivityViewModel.this.isForeground) {
                        MessagesActivityViewModel.this.adapter.updateView();
                    }
                }
            });
        }
        resetAvatarManifest();
    }

    public AvatarViewVM getAvatarViewVM() {
        return this.avatarViewVM;
    }

    protected void onStopOverride() {
        this.messageModel.removeObserver(this);
        if (this.avatarModel != null) {
            this.avatarModel.removeObserver(this);
        }
        this.avatarModel = null;
        if (hasDetailAndAvatarUI()) {
            this.avatarViewVM.onDestroy();
            this.avatarViewVM = null;
            this.meActorVM = null;
        }
    }

    public AvatarViewActorVM getActorVM() {
        return this.meActorVM;
    }

    public boolean getIsShadowtarVisible() {
        return (!this.meActorVM.getIsShadowtarVisible() || this.avatarModel == null || this.avatarModel.getIsLoading()) ? false : true;
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MessageData));
        this.messageModel.loadMessageList(forceRefresh);
        if (this.messageSummary != null) {
            this.avatarModel.load(forceRefresh);
        }
    }

    public boolean isBusy() {
        return this.messageModel.getIsLoadingMessageList() || (this.avatarModel != null && this.avatarModel.getIsLoading());
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        switch (type) {
            case MessageDelete:
                if (asyncResult.getException() == null || !((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
                        setMessageSummary(null);
                        break;
                    }
                }
                return;
                break;
            case MessageData:
                break;
            case MessageDetailsData:
                if (this.messageSummary != null) {
                    if (asyncResult.getException() != null || !((UpdateData) asyncResult.getResult()).getIsFinal()) {
                        if (!(asyncResult.getException() == null || asyncResult.getException().getUserObject() == null || !(asyncResult.getException().getUserObject() instanceof Long))) {
                            if (this.messageSummary.MessageId != ((Long) asyncResult.getException().getUserObject()).longValue()) {
                                asyncResult.getException().setIsHandled(true);
                                break;
                            }
                            this.messageDetails = null;
                            updateMessageBody();
                            break;
                        }
                    }
                    this.messageDetails = this.messageModel.getMessageDetails(this.messageSummary.MessageId);
                    updateMessageBody();
                    break;
                }
                break;
            case AvatarManifestLoad:
                if (asyncResult.getException() != null || !((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    if (asyncResult.getException() != null) {
                        asyncResult.getException().setIsHandled(true);
                        this.meActorVM.setManifest(XLEAvatarManifest.SHADOWTAR);
                        break;
                    }
                }
                this.meActorVM.setManifest(this.avatarModel.getManifest());
                break;
                break;
            default:
                XLELog.Diagnostic("MessageActivityViewModel", "Unexpceted update type " + type.toString());
                break;
        }
        if (asyncResult.getException() != null && ((UpdateData) asyncResult.getResult()).getIsFinal() && this.messageModel.getMessageList() == null) {
            this.viewModelState = ListState.ErrorState;
            this.adapter.updateView();
        }
        if (this.messageModel.getMessageList() == null) {
            this.viewModelState = ListState.LoadingState;
        } else if (this.messageModel.getMessageList().size() == 0) {
            this.viewModelState = ListState.NoContentState;
        } else {
            this.viewModelState = ListState.ValidContentState;
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.MessageData, XLEErrorCode.FAILED_TO_GET_MESSAGE_SUMMARY)) {
            if (getListState() == ListState.ValidContentState) {
                showError(R.string.toast_messages_error);
            } else {
                this.viewModelState = ListState.ErrorState;
                this.adapter.updateView();
            }
        }
        if (checkErrorCode(UpdateType.MessageDetailsData, XLEErrorCode.FAILED_TO_GET_MESSAGE_DETAIL)) {
            showError(R.string.toast_message_detail_error);
        } else if (checkErrorCode(UpdateType.MessageDelete, XLEErrorCode.FAILED_TO_DELETE_MESSAGE)) {
            showError(R.string.toast_message_delete_error);
        }
        super.onUpdateFinished();
    }

    public void navigateToMessageDetails(MessageSummary summary) {
        XLEGlobalData.getInstance().setSelectedMessageSummary(summary);
        XLELog.Info("MessagesActivityViewModel", String.format("Navigating to message details for messge 0x%x", new Object[]{Long.valueOf(summary.MessageId)}));
        NavigateTo(MessageDetailsActivity.class);
    }

    public void navigateToCreateMessage() {
        MessagingCapabilityStatus status = MeProfileModel.getModel().getCanComposeMessage();
        if (status.getCanSendMessage()) {
            XLELog.Info("MessagesActivityViewModel", String.format("Navigating to create message", new Object[0]));
            XLEGlobalData.getInstance().getSelectedRecipients().reset();
            NavigateTo(ComposeMessageActivity.class);
            return;
        }
        showError(status.getErrorResourceId());
    }

    public void navigateToReplyCurrentMessage() {
        XLEAssert.assertTrue(this.messageSummary != null);
        XboxMobileOmnitureTracking.TrackMsgReply();
        MessagingCapabilityStatus status = MeProfileModel.getModel().getCanComposeMessage();
        if (status.getCanSendMessage()) {
            XLEGlobalData.getInstance().getSelectedRecipients().reset();
            XLEGlobalData.getInstance().getSelectedRecipients().add(this.messageSummary.SenderGamertag);
            NavigateTo(ComposeMessageActivity.class);
            return;
        }
        showError(status.getErrorResourceId());
    }

    private void updateMessageBody() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.messageDetails != null) {
            if (this.messageDetails.MessageBody != null && this.messageDetails.MessageBody.length() > 0) {
                stringBuilder.append(this.messageDetails.MessageBody);
            }
            if (this.messageSummary.HasVoice) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("\n\n");
                }
                if (this.messageSummary.HasImage) {
                    stringBuilder.append(XLEApplication.Resources.getString(R.string.message_view_listen_on_console));
                } else {
                    stringBuilder.append(XLEApplication.Resources.getString(R.string.message_listen_on_console));
                }
            } else if (this.messageSummary.MessageType != 8) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("\n\n");
                }
                stringBuilder.append(XLEApplication.Resources.getString(R.string.message_view_on_console));
            }
        } else if (this.messageModel.getIsLoadingMessageDetails()) {
            stringBuilder.append(XLEApplication.Resources.getString(R.string.loading));
        } else {
            stringBuilder.append(XLEApplication.Resources.getString(R.string.toast_message_detail_error));
        }
        this.messageBody = stringBuilder.toString();
    }

    private void resetAvatarManifest() {
        if (hasDetailAndAvatarUI()) {
            if (this.avatarModel != null) {
                this.avatarModel.removeObserver(this);
            }
            this.meActorVM.setManifest(XLEAvatarManifest.EMPTYTAR);
            this.adapter.updateView();
            if (this.messageSummary != null) {
                this.avatarModel = AvatarManifestModel.getGamerModel(getMessageSender());
                this.avatarModel.addObserver(this);
                this.avatarModel.load(false);
                return;
            }
            this.avatarModel = null;
        }
    }
}
