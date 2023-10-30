package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.view.AvatarViewActor;
import com.microsoft.xbox.avatar.view.AvatarViewEditor;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithAvatar;
import com.microsoft.xbox.xle.viewmodel.MessageDetailsActivityViewModel;

public class MessageDetailsActivityAdapter extends AdapterBaseWithAvatar {
    private TextView dateView;
    private AvatarViewActor meActor;
    private TextView messageView;
    private TextView senderView;
    private ImageView shadowtar;
    private MessageDetailsActivityViewModel viewModel;

    public MessageDetailsActivityAdapter(MessageDetailsActivityViewModel vm) {
        this.screenBody = findViewById(R.id.messagedetails_activity_body);
        this.viewModel = vm;
        this.senderView = (TextView) findViewById(R.id.messagedetails_sender);
        this.messageView = (TextView) findViewById(R.id.messagedetails_message);
        this.dateView = (TextView) findViewById(R.id.messagedetails_date);
        this.meActor = (AvatarViewActor) findViewById(R.id.messagedetails_avatar);
        this.avatarView = (AvatarViewEditor) findViewById(R.id.messagedetails_avatar_view);
        this.shadowtar = (ImageView) findViewById(R.id.you_profile_avatar_shadowtar);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.message_delete, new OnClickListener() {
            public void onClick(View v) {
                MessageDetailsActivityAdapter.this.viewModel.deleteCurrentMessage();
            }
        });
        setAppBarButtonClickListener(R.id.message_reply, new OnClickListener() {
            public void onClick(View v) {
                MessageDetailsActivityAdapter.this.viewModel.navigateToReplyCurrentMessage();
            }
        });
        setAppBarButtonClickListener(R.id.message_block, new OnClickListener() {
            public void onClick(View v) {
                MessageDetailsActivityAdapter.this.viewModel.blockCurrentSender();
            }
        });
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                MessageDetailsActivityAdapter.this.viewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.viewModel.isBusy());
        this.senderView.setText(this.viewModel.getSenderGamerTag());
        this.messageView.setText(this.viewModel.getMessageBody());
        this.dateView.setText(this.viewModel.getMessageDate());
        setAppBarButtonEnabled(R.id.message_delete, this.viewModel.getCanDelete());
        setBlocking(this.viewModel.isBlockingBusy(), this.viewModel.getBlockingStatusText());
        this.shadowtar.setVisibility(this.viewModel.getIsShadowtarVisible() ? 0 : 8);
        this.avatarView.setAvatarViewVM(this.viewModel.getAvatarViewVM());
        this.meActor.setActorVM(this.viewModel.getActorVM());
    }
}
