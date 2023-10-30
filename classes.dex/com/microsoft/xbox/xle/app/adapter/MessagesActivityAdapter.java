package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarRendererModel;
import com.microsoft.xbox.avatar.view.AvatarViewActor;
import com.microsoft.xbox.avatar.view.AvatarViewEditor;
import com.microsoft.xbox.service.model.serialization.MessageSummary;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.MAAS.MAASAnimationType;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.anim.XLEMAASAnimationPackageNavigationManager;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.MessagesActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;

public class MessagesActivityAdapter extends AdapterBaseWithList {
    private AvatarViewEditor avatarView;
    private XLEButton blockMessage;
    private XLEButton deleteMessage;
    private int lastPostion;
    private MessagesListAdapter listAdapter;
    private AvatarViewActor meAvatar;
    private CustomTypefaceTextView messageCount;
    private CustomTypefaceTextView messageDetails;
    private ArrayList<MessageSummary> messageList;
    private CustomTypefaceTextView messageSender;
    private XLEButton replyMessage;
    private SwitchPanel selectMessageSwitchPanel;
    private ImageView shadowtar;
    private SwitchPanel switchPanel;
    private MessagesActivityViewModel viewModel;
    private XLEButton writeMessage;

    private enum MessageSelectState {
        NoSelectedMessageState,
        SelectedMessageState,
        NoMessageState
    }

    public MessagesActivityAdapter(MessagesActivityViewModel messageViewModel) {
        this.lastPostion = -1;
        this.screenBody = findViewById(R.id.messages_activity_body);
        this.content = findViewById(R.id.messages_switch_panel);
        this.viewModel = messageViewModel;
        this.messageList = null;
        this.listView = (XLEListView) findViewById(R.id.messages_list);
        this.switchPanel = (SwitchPanel) this.content;
        this.selectMessageSwitchPanel = (SwitchPanel) findViewById(R.id.read_message_switch_panel);
        this.messageCount = (CustomTypefaceTextView) findViewById(R.id.messages_count);
        this.messageSender = (CustomTypefaceTextView) findViewById(R.id.message_sender);
        this.messageDetails = (CustomTypefaceTextView) findViewById(R.id.message_details);
        this.writeMessage = (XLEButton) findViewById(R.id.write_messages);
        this.replyMessage = (XLEButton) findViewById(R.id.reply_message);
        this.deleteMessage = (XLEButton) findViewById(R.id.delete_message);
        this.blockMessage = (XLEButton) findViewById(R.id.block_message);
        this.avatarView = (AvatarViewEditor) findViewById(R.id.message_details_avatar_view);
        this.meAvatar = (AvatarViewActor) findViewById(R.id.message_details_avatar);
        this.shadowtar = (ImageView) findViewById(R.id.message_details_avatar_shadowtar);
        this.listView.setChoiceMode(1);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MessageSummary summary = (MessageSummary) view.getTag();
                if (!XLEApplication.Instance.getIsTablet()) {
                    MessagesActivityAdapter.this.viewModel.navigateToMessageDetails(summary);
                } else if (MessagesActivityAdapter.this.selectMessageSwitchPanel == null) {
                } else {
                    if (MessagesActivityAdapter.this.lastPostion != position || MessagesActivityAdapter.this.lastPostion < 0) {
                        MessagesActivityAdapter.this.selectMessageSwitchPanel.setState(MessageSelectState.SelectedMessageState.ordinal());
                        MessagesActivityAdapter.this.lastPostion = position;
                        MessagesActivityAdapter.this.viewModel.setMessageSummary(summary);
                        MessagesActivityAdapter.this.viewModel.loadMessageDetails();
                        return;
                    }
                    MessagesActivityAdapter.this.selectMessageSwitchPanel.setState(MessageSelectState.NoSelectedMessageState.ordinal());
                    MessagesActivityAdapter.this.lastPostion = -1;
                    MessagesActivityAdapter.this.viewModel.setMessageSummary(null);
                }
            }
        });
        if (this.writeMessage != null) {
            this.writeMessage.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    MessagesActivityAdapter.this.lastPostion = -1;
                    MessagesActivityAdapter.this.viewModel.navigateToCreateMessage();
                }
            });
        }
        if (this.replyMessage != null) {
            this.replyMessage.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    MessagesActivityAdapter.this.lastPostion = -1;
                    MessagesActivityAdapter.this.viewModel.navigateToReplyCurrentMessage();
                }
            });
        }
        if (this.deleteMessage != null) {
            this.deleteMessage.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    MessagesActivityAdapter.this.lastPostion = -1;
                    MessagesActivityAdapter.this.viewModel.deleteCurrentMessage();
                }
            });
        }
        if (this.blockMessage != null) {
            this.blockMessage.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    MessagesActivityAdapter.this.lastPostion = -1;
                    MessagesActivityAdapter.this.viewModel.blockCurrentSender();
                }
            });
        }
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View v) {
                MessagesActivityAdapter.this.viewModel.load(true);
            }
        });
        setAppBarButtonClickListener(R.id.messages_create, new OnClickListener() {
            public void onClick(View v) {
                MessagesActivityAdapter.this.viewModel.navigateToCreateMessage();
            }
        });
    }

    public void onStart() {
        if (this.viewModel.hasDetailAndAvatarUI()) {
            MAAS.getInstance().getAnimation("AvatarView");
        }
        super.onStart();
    }

    public void onPause() {
        super.onPause();
        if (this.viewModel.hasDetailAndAvatarUI()) {
            this.avatarView.onPause();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.viewModel.hasDetailAndAvatarUI()) {
            this.avatarView.onResume();
        }
    }

    public void updateViewOverride() {
        int i = 0;
        updateLoadingIndicator(this.viewModel.isBusy());
        this.switchPanel.setState(this.viewModel.getListState().ordinal());
        if (this.viewModel.getMessageList() != null) {
            if (this.listAdapter == null || this.messageList != this.viewModel.getMessageList()) {
                this.messageList = this.viewModel.getMessageList();
                this.listAdapter = new MessagesListAdapter(XLEApplication.getMainActivity(), R.layout.messages_list_row, this.messageList);
                this.listView.setAdapter(this.listAdapter);
                restoreListPosition();
                this.listView.onDataUpdated();
            } else {
                this.listAdapter.setSelectedPos(this.lastPostion);
                this.listView.notifyDataSetChanged();
            }
            XLEUtil.updateTextIfNotNull(this.messageSender, this.viewModel.getMessageSender());
            XLEUtil.updateTextIfNotNull(this.messageDetails, this.viewModel.getMessageBody());
            if (this.messageCount != null) {
                if (this.viewModel.getMessageList().size() == 0) {
                    this.messageCount.setText(String.format(XboxApplication.Resources.getString(R.string.message_one_count), new Object[]{Integer.valueOf(this.viewModel.getMessageList().size())}));
                    this.selectMessageSwitchPanel.setState(MessageSelectState.NoMessageState.ordinal());
                } else {
                    if (this.viewModel.getMessageList().size() == 1) {
                        this.messageCount.setText(String.format(XboxApplication.Resources.getString(R.string.message_one_count), new Object[]{Integer.valueOf(this.viewModel.getMessageList().size())}));
                    } else {
                        this.messageCount.setText(String.format(XboxApplication.Resources.getString(R.string.message_multiple_count), new Object[]{Integer.valueOf(this.viewModel.getMessageList().size())}));
                    }
                    if (this.selectMessageSwitchPanel != null && this.lastPostion == -1) {
                        this.selectMessageSwitchPanel.setState(MessageSelectState.NoSelectedMessageState.ordinal());
                    }
                }
            }
        } else if (this.selectMessageSwitchPanel != null) {
            this.selectMessageSwitchPanel.setState(MessageSelectState.NoMessageState.ordinal());
        }
        if (this.viewModel.hasDetailAndAvatarUI()) {
            ImageView imageView = this.shadowtar;
            if (!this.viewModel.getIsShadowtarVisible()) {
                i = 8;
            }
            imageView.setVisibility(i);
            this.avatarView.setAvatarViewVM(this.viewModel.getAvatarViewVM());
            this.meAvatar.setActorVM(this.viewModel.getActorVM());
        }
    }

    public void onSetActive() {
        super.onSetActive();
        AvatarRendererModel.getInstance().setGLThreadRunningScreen(true);
    }

    public void onSetInactive() {
        super.onSetInactive();
        AvatarRendererModel.getInstance().setGLThreadRunningScreen(false);
    }

    public ArrayList<XLEAnimation> getAnimateIn(boolean goingBack) {
        ArrayList<XLEAnimation> animations = super.getAnimateIn(goingBack);
        if (this.viewModel.hasDetailAndAvatarUI()) {
            XLEAssert.assertNotNull(this.avatarView);
            XLEAnimation avatarViewAnimation = ((XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation("AvatarView")).compile(MAASAnimationType.ANIMATE_IN, goingBack, this.avatarView);
            if (avatarViewAnimation != null) {
                animations.add(avatarViewAnimation);
            }
        }
        return animations;
    }

    public ArrayList<XLEAnimation> getAnimateOut(boolean goingBack) {
        ArrayList<XLEAnimation> animations = super.getAnimateOut(goingBack);
        if (this.viewModel.hasDetailAndAvatarUI()) {
            XLEAssert.assertNotNull(this.avatarView);
            XLEAnimation avatarViewAnimation = ((XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation("AvatarView")).compile(MAASAnimationType.ANIMATE_OUT, goingBack, this.avatarView);
            if (avatarViewAnimation != null) {
                animations.add(avatarViewAnimation);
            }
        }
        return animations;
    }

    protected SwitchPanel getSwitchPanel() {
        return this.switchPanel;
    }

    protected ViewModelBase getViewModel() {
        return this.viewModel;
    }
}
