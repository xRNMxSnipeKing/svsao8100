package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.view.AvatarViewActor;
import com.microsoft.xbox.avatar.view.AvatarViewEditor;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithAvatar;
import com.microsoft.xbox.xle.viewmodel.YouProfileActivityViewModel;

public class YouProfileActivityAdapter extends AdapterBaseWithAvatar {
    private static final int MOTTO_ANIMATION_DURATION_MS = 500;
    private XLEButton acceptRequestButton;
    private XLEButton declineRequestButton;
    private View friendRequestView;
    private TextView gamerName;
    private TextView gamerScore;
    private TextView gamerTag;
    private XLEImageViewFast gamerpic;
    private AvatarViewActor meAvatar;
    private TextView memberLevel;
    private AlphaAnimation mottoAppearAnimation;
    private TextView mottoBubble;
    private AlphaAnimation mottoDisappearAnimation;
    private ImageView shadowtar;
    private YouProfileActivityViewModel youViewModel;

    public YouProfileActivityAdapter(YouProfileActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.you_profile_activity_body);
        this.content = findViewById(R.id.you_profile_content);
        this.youViewModel = viewModel;
        this.gamerName = (TextView) findViewById(R.id.you_profile_name);
        this.gamerTag = (TextView) findViewById(R.id.you_profile_gamertag);
        this.gamerScore = (TextView) findViewById(R.id.you_profile_gamerscore);
        this.memberLevel = (TextView) findViewById(R.id.you_profile_memberlevel);
        this.gamerpic = (XLEImageViewFast) findViewById(R.id.you_profile_gamerpic);
        this.friendRequestView = findViewById(R.id.you_profile_friend_request);
        this.acceptRequestButton = (XLEButton) findViewById(R.id.you_profile_accept_request);
        this.declineRequestButton = (XLEButton) findViewById(R.id.you_profile_decline_request);
        this.mottoBubble = (TextView) findViewById(R.id.you_profile_motto_bubble);
        this.mottoBubble.setVisibility(4);
        this.acceptRequestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                YouProfileActivityAdapter.this.youViewModel.acceptFriendRequest();
            }
        });
        this.declineRequestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                YouProfileActivityAdapter.this.youViewModel.declineFriendRequest();
            }
        });
        this.mottoAppearAnimation = new AlphaAnimation(0.0f, 1.0f);
        this.mottoAppearAnimation.setDuration(500);
        this.mottoAppearAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                YouProfileActivityAdapter.this.mottoBubble.setVisibility(0);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
            }
        });
        this.mottoDisappearAnimation = new AlphaAnimation(1.0f, 0.0f);
        this.mottoDisappearAnimation.setDuration(500);
        this.mottoDisappearAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                YouProfileActivityAdapter.this.mottoBubble.setVisibility(4);
            }
        });
        this.avatarView = (AvatarViewEditor) findViewById(R.id.you_profile_avatar_view);
        this.meAvatar = (AvatarViewActor) findViewById(R.id.you_profile_avatar);
        this.shadowtar = (ImageView) findViewById(R.id.you_profile_avatar_shadowtar);
    }

    protected void onAppBarButtonsAdded() {
        setAppBarButtonVisibility(R.id.you_profile_add_friend, this.youViewModel.getAddFriendButtonVisibility());
        setAppBarButtonVisibility(R.id.you_profile_remove_friend, this.youViewModel.getRemoveFriendButtonVisibility());
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View v) {
                YouProfileActivityAdapter.this.youViewModel.load(true);
            }
        });
        setAppBarButtonClickListener(R.id.you_profile_send_message, new OnClickListener() {
            public void onClick(View v) {
                YouProfileActivityAdapter.this.youViewModel.sendMessage();
            }
        });
        setAppBarButtonClickListener(R.id.you_profile_add_friend, new OnClickListener() {
            public void onClick(View v) {
                XLELog.Diagnostic("YouProfileActivityAdapter", "Add friend button clicked");
                YouProfileActivityAdapter.this.youViewModel.sendFriendRequest();
            }
        });
        setAppBarButtonClickListener(R.id.you_profile_remove_friend, new OnClickListener() {
            public void onClick(View v) {
                XLELog.Diagnostic("YouProfileActivityAdapter", "Remove friend button clicked");
                YouProfileActivityAdapter.this.youViewModel.showRemoveFriendDialog();
            }
        });
        setAppBarButtonVisibility(R.id.you_profile_add_friend, this.youViewModel.getAddFriendButtonVisibility());
        setAppBarButtonVisibility(R.id.you_profile_remove_friend, this.youViewModel.getRemoveFriendButtonVisibility());
    }

    public void updateViewOverride() {
        int i = 0;
        setBlocking(this.youViewModel.isBlockingBusy(), this.youViewModel.getBlockingStatusText());
        if (this.youViewModel.getIsActive()) {
            updateLoadingIndicator(this.youViewModel.isBusy());
        }
        if (this.youViewModel.getGamertag() != null && this.youViewModel.getGamertag().length() > 0) {
            this.gamerName.setText(this.youViewModel.getGamerName());
            this.gamerTag.setText(this.youViewModel.getGamertag());
            this.gamerScore.setText(this.youViewModel.getGamerScore());
            this.gamerpic.setImageURI2(this.youViewModel.getGamerpicUri());
            if (this.youViewModel.isGold()) {
                this.memberLevel.setVisibility(0);
            } else {
                this.memberLevel.setVisibility(4);
            }
        }
        setAppBarButtonVisibility(R.id.you_profile_add_friend, this.youViewModel.getAddFriendButtonVisibility());
        setAppBarButtonVisibility(R.id.you_profile_remove_friend, this.youViewModel.getRemoveFriendButtonVisibility());
        this.friendRequestView.setVisibility(this.youViewModel.getFriendRequestViewVisibility());
        this.mottoBubble.setText(this.youViewModel.getMotto());
        if (this.mottoBubble.getVisibility() != 0 && this.youViewModel.getShowMotto()) {
            this.mottoBubble.startAnimation(this.mottoAppearAnimation);
        }
        if (this.mottoBubble.getVisibility() == 0 && !this.youViewModel.getShowMotto()) {
            this.mottoBubble.startAnimation(this.mottoDisappearAnimation);
        }
        ImageView imageView = this.shadowtar;
        if (!this.youViewModel.getIsShadowtarVisible()) {
            i = 8;
        }
        imageView.setVisibility(i);
        this.avatarView.setAvatarViewVM(this.youViewModel.getAvatarViewVM());
        this.meAvatar.setActorVM(this.youViewModel.getActorVM());
    }

    public void onResume() {
        super.onResume();
        this.mottoBubble.setVisibility(8);
    }
}
