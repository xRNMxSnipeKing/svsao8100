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
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.ComposeMessageActivity;
import com.microsoft.xbox.xle.app.adapter.MessageDetailsActivityAdapter;
import java.util.EnumSet;

public class MessageDetailsActivityViewModel extends ViewModelBase {
    private AvatarManifestModel avatarModel;
    private AvatarViewVM avatarViewVM;
    private String blockingText;
    private AvatarViewActorVMDefault meActorVM;
    private String messageBody;
    private MessageDetails messageDetails;
    private MessageModel messageModel;
    private MessageSummary messageSummary;

    public MessageDetailsActivityViewModel() {
        boolean z = true;
        this.avatarViewVM = null;
        this.meActorVM = null;
        this.messageModel = MessageModel.getInstance();
        this.adapter = new MessageDetailsActivityAdapter(this);
        this.messageSummary = XLEGlobalData.getInstance().getSelectedMessageSummary();
        this.blockingText = XLEApplication.Resources.getString(R.string.blocking_status_updating);
        XLEAssert.assertTrue(this.messageSummary != null);
        if (getSenderGamerTag() == null) {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (getSenderGamerTag().equals(MeProfileModel.getModel().getGamertag())) {
            this.avatarModel = AvatarManifestModel.getPlayerModel();
        } else {
            this.avatarModel = AvatarManifestModel.getGamerModel(getSenderGamerTag());
        }
    }

    public void onRehydrate() {
        this.adapter = new MessageDetailsActivityAdapter(this);
    }

    public String getSenderGamerTag() {
        if (this.messageSummary != null) {
            return this.messageSummary.SenderGamertag;
        }
        XLELog.Error("MessageDetailsActivityViewModel", "Message summary is null!");
        return "";
    }

    public String getMessageDate() {
        if (this.messageSummary != null) {
            return JavaUtil.getLocalizedDateString(this.messageSummary.SentTime);
        }
        XLELog.Error("MessageDetailsActivityViewModel", "Message summary is null!");
        return "";
    }

    public String getMessageBody() {
        return this.messageBody;
    }

    public boolean getCanDelete() {
        if (this.messageSummary != null) {
            return this.messageSummary.CanDelete;
        }
        return this.messageDetails != null;
    }

    protected void onStartOverride() {
        this.messageModel.addObserver(this);
        this.avatarModel.addObserver(this);
        this.avatarViewVM = new AvatarViewVMDefault();
        this.meActorVM = new AvatarViewActorVMDefault();
        this.avatarViewVM.registerActor(this.meActorVM);
        this.meActorVM.setShadowtarVisibilityChangedCallback(new Runnable() {
            public void run() {
                if (MessageDetailsActivityViewModel.this.isForeground) {
                    MessageDetailsActivityViewModel.this.adapter.updateView();
                }
            }
        });
    }

    protected void onStopOverride() {
        this.messageModel.removeObserver(this);
        this.avatarModel.removeObserver(this);
        this.avatarViewVM.onDestroy();
        this.avatarViewVM = null;
        this.meActorVM = null;
    }

    public boolean isBusy() {
        return this.messageModel.getIsLoadingMessageDetails() || this.avatarModel.getIsLoading();
    }

    public boolean isBlockingBusy() {
        return this.messageModel.getIsDeleting();
    }

    public String getBlockingStatusText() {
        return this.blockingText;
    }

    public void updateOverride(AsyncResult<UpdateData> result) {
        switch (((UpdateData) result.getResult()).getUpdateType()) {
            case MessageDetailsData:
                if (result.getException() != null || !((UpdateData) result.getResult()).getIsFinal()) {
                    if (!(result.getException() == null || result.getException().getUserObject() == null || !(result.getException().getUserObject() instanceof Long))) {
                        if (this.messageSummary.MessageId != ((Long) result.getException().getUserObject()).longValue()) {
                            result.getException().setIsHandled(true);
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
                break;
            case MessageDelete:
                if (result.getException() == null && ((UpdateData) result.getResult()).getIsFinal()) {
                    goBack();
                    return;
                }
            case AvatarManifestLoad:
                if (result.getException() != null || !((UpdateData) result.getResult()).getIsFinal()) {
                    if (result.getException() != null) {
                        result.getException().setIsHandled(true);
                        this.meActorVM.setManifest(XLEAvatarManifest.SHADOWTAR);
                        break;
                    }
                }
                this.meActorVM.setManifest(this.avatarModel.getManifest());
                break;
                break;
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.MessageDetailsData, XLEErrorCode.FAILED_TO_GET_MESSAGE_DETAIL)) {
            showError(R.string.toast_message_detail_error);
        } else if (checkErrorCode(UpdateType.MessageDelete, XLEErrorCode.FAILED_TO_DELETE_MESSAGE)) {
            showError(R.string.toast_message_delete_error);
        }
        super.onUpdateFinished();
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MessageDetailsData));
        if (this.messageSummary == null) {
            XLELog.Error("MessageDetailsViewModel", "no item selected ");
        }
        this.messageModel.loadMessageDetails(this.messageSummary.MessageId);
        this.avatarModel.load(forceRefresh);
        updateMessageBody();
    }

    public void deleteCurrentMessage() {
        XLEAssert.assertTrue(this.messageSummary != null);
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MessageDelete));
        this.messageModel.deleteMessage(this.messageSummary.MessageId, false);
        this.adapter.updateView();
    }

    public void blockCurrentSender() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        showOkCancelDialog(XLEApplication.Resources.getString(R.string.dialog_areyousure_title), XLEApplication.Resources.getString(R.string.message_block_warning), XLEApplication.Resources.getString(R.string.OK), new Runnable() {
            public void run() {
                MessageDetailsActivityViewModel.this.blockCurrentSenderInternal();
            }
        }, XLEApplication.Resources.getString(R.string.Cancel), null);
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

    public XLEAvatarManifest getManifest() {
        return this.avatarModel.getManifest();
    }

    public boolean getIsShadowtarVisible() {
        return this.meActorVM.getIsShadowtarVisible();
    }

    private void blockCurrentSenderInternal() {
        XLELog.Diagnostic("MessageDetailActivity", "blocking current sender");
        XLEAssert.assertTrue(this.messageSummary != null);
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MessageDelete));
        this.messageModel.deleteMessage(this.messageSummary.MessageId, true);
        this.adapter.updateView();
    }

    public AvatarViewActorVM getActorVM() {
        return this.meActorVM;
    }

    public AvatarViewVM getAvatarViewVM() {
        return this.avatarViewVM;
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
}
