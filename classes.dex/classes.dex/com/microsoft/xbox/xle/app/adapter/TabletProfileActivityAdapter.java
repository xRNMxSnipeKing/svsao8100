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
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.ui.SimpleGridLayout;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.toolkit.ui.appbar.AppBarMenuButton;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithAvatar;
import com.microsoft.xbox.xle.viewmodel.TabletProfileActivityViewModel;
import java.util.ArrayList;
import java.util.List;

public class TabletProfileActivityAdapter extends AdapterBaseWithAvatar {
    private static final int MOTTO_ANIMATION_DURATION_MS = 500;
    private XLEButton acceptRequestButton;
    private XLEButton addFriendButton;
    private XLEButton cancelRequestButton;
    private XLEButton compareGamesButton;
    private XLEButton declineRequestButton;
    private XLEButton editAvatarButton;
    private XLEButton editProfileButton;
    private TextView gamerBio;
    private TextView gamerLocation;
    private TextView gamerName;
    private TextView gamerScore;
    private TextView gamerTag;
    private XLEImageViewFast gamerpic;
    private ArrayList gamesList;
    private AvatarViewActor meAvatar;
    private TextView memberLevel;
    private AlphaAnimation mottoAppearAnimation;
    private TextView mottoBubble;
    private AlphaAnimation mottoDisappearAnimation;
    private TabletProfileActivityViewModel profileViewModel;
    private SimpleGridLayout recentGamesGrid;
    private SwitchPanel recentGamesSwitchPanel;
    private XLEButton removeFriendButton;
    private XLEButton sendMessageButton;
    private ImageView shadowtar;
    private XLEButton viewAllGamesButton;

    public TabletProfileActivityAdapter(TabletProfileActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.tablet_profile_activity_body);
        this.content = findViewById(R.id.tablet_profile_content);
        this.profileViewModel = viewModel;
        this.gamerName = (TextView) findViewById(R.id.tablet_profile_name);
        this.gamerLocation = (TextView) findViewById(R.id.tablet_profile_location);
        this.gamerBio = (TextView) findViewById(R.id.tablet_profile_bio);
        this.gamerTag = (TextView) findViewById(R.id.tablet_profile_gamertag);
        this.gamerScore = (TextView) findViewById(R.id.tablet_profile_gamerscore);
        this.memberLevel = (TextView) findViewById(R.id.tablet_profile_memberlevel);
        this.gamerpic = (XLEImageViewFast) findViewById(R.id.tablet_profile_gamerpic);
        this.acceptRequestButton = (XLEButton) findViewById(R.id.tablet_profile_accept_request);
        this.declineRequestButton = (XLEButton) findViewById(R.id.tablet_profile_decline_request);
        this.sendMessageButton = (XLEButton) findViewById(R.id.tablet_profile_send_message);
        this.compareGamesButton = (XLEButton) findViewById(R.id.tablet_profile_compare_games);
        this.removeFriendButton = (XLEButton) findViewById(R.id.tablet_profile_remove_friend);
        this.addFriendButton = (XLEButton) findViewById(R.id.tablet_profile_add_friend);
        this.cancelRequestButton = (XLEButton) findViewById(R.id.tablet_profile_cancel_request);
        this.viewAllGamesButton = (XLEButton) findViewById(R.id.tablet_profile_view_all_games);
        this.editAvatarButton = (XLEButton) findViewById(R.id.tablet_profile_edit_avatar);
        this.editProfileButton = (XLEButton) findViewById(R.id.tablet_profile_edit_profile);
        this.mottoBubble = (TextView) findViewById(R.id.tablet_profile_motto_bubble);
        this.mottoBubble.setVisibility(4);
        this.acceptRequestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TabletProfileActivityAdapter.this.profileViewModel.acceptFriendRequest();
            }
        });
        this.declineRequestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TabletProfileActivityAdapter.this.profileViewModel.declineFriendRequest();
            }
        });
        this.sendMessageButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TabletProfileActivityAdapter.this.profileViewModel.sendMessage();
            }
        });
        this.compareGamesButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TabletProfileActivityAdapter.this.profileViewModel.navigeteToCompareGamesPage();
            }
        });
        this.removeFriendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                XLELog.Diagnostic("YouProfileActivityAdapter", "Remove friend button clicked");
                TabletProfileActivityAdapter.this.profileViewModel.showRemoveFriendDialog();
            }
        });
        this.addFriendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                XLELog.Diagnostic("YouProfileActivityAdapter", "Add friend button clicked");
                TabletProfileActivityAdapter.this.profileViewModel.sendFriendRequest();
            }
        });
        this.cancelRequestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                XLELog.Diagnostic("YouProfileActivityAdapter", "Cancel request button clicked");
                TabletProfileActivityAdapter.this.profileViewModel.showRemoveFriendDialog();
            }
        });
        this.viewAllGamesButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                XLELog.Diagnostic("YouProfileActivityAdapter", "Cancel request button clicked");
                TabletProfileActivityAdapter.this.profileViewModel.navigateToViewAllGames();
            }
        });
        this.editAvatarButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                XLELog.Diagnostic("YouProfileActivityAdapter", "Cancel request button clicked");
                TabletProfileActivityAdapter.this.profileViewModel.navigateToEditAvatar();
            }
        });
        this.editProfileButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                XLELog.Diagnostic("YouProfileActivityAdapter", "Cancel request button clicked");
                TabletProfileActivityAdapter.this.profileViewModel.navigateToEditProfile();
            }
        });
        this.mottoAppearAnimation = new AlphaAnimation(0.0f, 1.0f);
        this.mottoAppearAnimation.setDuration(500);
        this.mottoAppearAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                TabletProfileActivityAdapter.this.mottoBubble.setVisibility(0);
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
                TabletProfileActivityAdapter.this.mottoBubble.setVisibility(4);
            }
        });
        this.avatarView = (AvatarViewEditor) findViewById(R.id.tablet_profile_avatar_view);
        this.meAvatar = (AvatarViewActor) findViewById(R.id.tablet_profile_avatar);
        this.shadowtar = (ImageView) findViewById(R.id.tablet_profile_avatar_shadowtar);
        this.recentGamesSwitchPanel = (SwitchPanel) findViewById(R.id.tablet_profile_recent_games_switch_panel);
        this.recentGamesGrid = (SimpleGridLayout) findViewById(R.id.you_card_recent_games_grid);
        if (this.recentGamesGrid != null) {
            this.recentGamesGrid.setGridAdapter(new TabletProfileRecentGamesGridAdapter(XLEApplication.getMainActivity(), R.layout.tablet_profile_recent_games_empty, R.layout.tablet_profile_recent_games_empty, null, null, this.recentGamesGrid.getColumnCount() * this.recentGamesGrid.getRowCount()));
        }
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View v) {
                TabletProfileActivityAdapter.this.profileViewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        int i = 8;
        setBlocking(this.profileViewModel.isBlockingBusy(), this.profileViewModel.getBlockingStatusText());
        if (this.profileViewModel.getIsActive()) {
            updateLoadingIndicator(this.profileViewModel.isBusy());
        }
        if (this.profileViewModel.getGamertag() != null && this.profileViewModel.getGamertag().length() > 0) {
            if (JavaUtil.isNullOrEmpty(this.profileViewModel.getGamerName())) {
                this.gamerName.setVisibility(8);
            } else {
                this.gamerName.setVisibility(0);
                this.gamerName.setText(this.profileViewModel.getGamerName());
            }
            if (JavaUtil.isNullOrEmpty(this.profileViewModel.getGamerLocation())) {
                this.gamerLocation.setVisibility(8);
            } else {
                this.gamerLocation.setVisibility(0);
                this.gamerLocation.setText(this.profileViewModel.getGamerLocation());
            }
            XLEUtil.updateTextIfNotNull(this.gamerBio, this.profileViewModel.getGamerBio());
            this.gamerTag.setText(this.profileViewModel.getGamertag());
            this.gamerScore.setText(this.profileViewModel.getGamerScore());
            this.gamerpic.setImageURI2(this.profileViewModel.getGamerpicUri());
            if (this.profileViewModel.isGold()) {
                this.memberLevel.setVisibility(0);
            } else {
                this.memberLevel.setVisibility(4);
            }
        }
        this.mottoBubble.setText(this.profileViewModel.getMotto());
        if (this.mottoBubble.getVisibility() != 0 && this.profileViewModel.getShowMotto()) {
            this.mottoBubble.startAnimation(this.mottoAppearAnimation);
        }
        if (this.mottoBubble.getVisibility() == 0 && !this.profileViewModel.getShowMotto()) {
            this.mottoBubble.startAnimation(this.mottoDisappearAnimation);
        }
        ImageView imageView = this.shadowtar;
        if (this.profileViewModel.getIsShadowtarVisible()) {
            i = 0;
        }
        imageView.setVisibility(i);
        this.avatarView.setAvatarViewVM(this.profileViewModel.getAvatarViewVM());
        this.meAvatar.setActorVM(this.profileViewModel.getActorVM());
        this.recentGamesSwitchPanel.setState(this.profileViewModel.getViewModelState().ordinal());
        if (this.profileViewModel.getGames() != null) {
            if (this.gamesList != this.profileViewModel.getGames()) {
                this.gamesList = this.profileViewModel.getGames();
                this.recentGamesGrid.setGridAdapter(new TabletProfileRecentGamesGridAdapter(XLEApplication.getMainActivity(), R.layout.tablet_profile_recent_games_row, R.layout.tablet_profile_recent_games_empty, this.gamesList, this.profileViewModel, this.recentGamesGrid.getColumnCount() * this.recentGamesGrid.getRowCount()));
            } else {
                this.recentGamesGrid.notifyDataChanged();
            }
        }
        if (this.profileViewModel.getIsMeProfile()) {
            XLEUtil.updateVisibilityIfNotNull(this.viewAllGamesButton, 0);
            XLEUtil.updateVisibilityIfNotNull(this.editAvatarButton, 0);
            XLEUtil.updateVisibilityIfNotNull(this.editProfileButton, 0);
            return;
        }
        XLEUtil.updateVisibilityIfNotNull(this.acceptRequestButton, this.profileViewModel.getFriendRequestViewVisibility());
        XLEUtil.updateVisibilityIfNotNull(this.declineRequestButton, this.profileViewModel.getFriendRequestViewVisibility());
        XLEUtil.updateVisibilityIfNotNull(this.sendMessageButton, this.profileViewModel.getSendMessageButtonVisibility());
        XLEUtil.updateVisibilityIfNotNull(this.removeFriendButton, this.profileViewModel.getRemoveFriendButtonVisibility());
        XLEUtil.updateVisibilityIfNotNull(this.cancelRequestButton, this.profileViewModel.getCancelRequestButtonVisibility());
        XLEUtil.updateVisibilityIfNotNull(this.addFriendButton, this.profileViewModel.getAddFriendButtonVisibility());
        XLEUtil.updateVisibilityIfNotNull(this.compareGamesButton, 0);
    }

    public void onResume() {
        super.onResume();
        this.mottoBubble.setVisibility(4);
    }

    protected List<AppBarMenuButton> getTestMenuButtons() {
        return super.getTestMenuButtons();
    }

    protected AppBarMenuButton createMenuButton(String text, int state) {
        return null;
    }
}
