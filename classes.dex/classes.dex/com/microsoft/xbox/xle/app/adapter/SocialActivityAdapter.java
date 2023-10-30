package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.view.AvatarViewActor;
import com.microsoft.xbox.avatar.view.AvatarViewEditor;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.ui.CroppedImageView;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.TextureBindingOption;
import com.microsoft.xbox.toolkit.ui.TextureManager;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.XLEClickableLayout;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithAvatar;
import com.microsoft.xbox.xle.viewmodel.SocialActivityViewModel;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

public class SocialActivityAdapter extends AdapterBaseWithAvatar {
    private static final int MOTTO_ANIMATION_DURATION_MS = 500;
    private XLEButton avatarEditButton;
    private XLEButton beaconsButton;
    private XLEButton browseMeProfileButton;
    private XLEButton findFriendsButton;
    private ArrayList<CroppedImageView> friendAvatars;
    private SwitchPanel friendSwitchPanel;
    private XLEClickableLayout friendsButton;
    private ArrayList<URI> friendsUriList;
    private TextView gamerNameView;
    private TextView gamerTagView;
    private TextView gamerscoreView;
    private AvatarViewActor meActor;
    private TextView membershipLevelView;
    private XLEButton messageButton;
    private AlphaAnimation mottoAppearAnimation;
    private AlphaAnimation mottoDisappearAnimation;
    private TextView mottoView;
    private XLEImageViewFast noFriendsImageView;
    private SocialActivityViewModel profileViewModel;
    private TextView requestCountView;
    private XLEButton searchFriendsButton;
    private RelativeLayout searchIconSet;
    private ImageView shadowtar;

    public SocialActivityAdapter(SocialActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.profile_activity_body);
        this.content = findViewById(R.id.social_content);
        this.profileViewModel = viewModel;
        this.friendSwitchPanel = (SwitchPanel) findViewById(R.id.friend_switch_panel);
        this.gamerTagView = (TextView) findViewById(R.id.profile_gamertag);
        this.gamerscoreView = (TextView) findViewById(R.id.profile_gamerscore);
        this.mottoView = (TextView) findViewById(R.id.profile_motto);
        this.gamerNameView = (TextView) findViewById(R.id.profile_name);
        this.membershipLevelView = (TextView) findViewById(R.id.profile_memberlevel);
        this.friendsButton = (XLEClickableLayout) findViewById(R.id.profile_friends);
        if (this.friendsButton != null) {
            this.friendsButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    SocialActivityAdapter.this.profileViewModel.navigateToFriendsList();
                }
            });
        }
        this.messageButton = (XLEButton) findViewById(R.id.profile_messages);
        this.messageButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SocialActivityAdapter.this.profileViewModel.navigateToMessagesList();
            }
        });
        this.beaconsButton = (XLEButton) findViewById(R.id.profile_beacons);
        this.beaconsButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SocialActivityAdapter.this.profileViewModel.navigateToBeacons();
            }
        });
        this.avatarEditButton = (XLEButton) findViewById(R.id.profile_avatar_edit);
        if (this.avatarEditButton != null) {
            this.avatarEditButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    SocialActivityAdapter.this.profileViewModel.navigateToAvatarEditor();
                }
            });
        }
        this.browseMeProfileButton = (XLEButton) findViewById(R.id.browse_me_profile);
        if (this.browseMeProfileButton != null) {
            this.browseMeProfileButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    SocialActivityAdapter.this.profileViewModel.navigateToMeProfile();
                }
            });
        }
        this.searchFriendsButton = (XLEButton) findViewById(R.id.search_friends);
        if (this.searchFriendsButton != null) {
            this.searchFriendsButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    SocialActivityAdapter.this.profileViewModel.navigateToSearchGamer();
                }
            });
        }
        this.findFriendsButton = (XLEButton) findViewById(R.id.find_friends);
        if (this.findFriendsButton != null) {
            this.findFriendsButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    SocialActivityAdapter.this.profileViewModel.navigateToSearchGamer();
                }
            });
        }
        this.searchIconSet = (RelativeLayout) findViewById(R.id.search_friends_set);
        if (this.friendsButton != null) {
            this.friendAvatars = new ArrayList();
            this.friendAvatars.add((CroppedImageView) findViewById(R.id.profile_friend_avatar_1));
            this.friendAvatars.add((CroppedImageView) findViewById(R.id.profile_friend_avatar_2));
            this.friendAvatars.add((CroppedImageView) findViewById(R.id.profile_friend_avatar_3));
            this.friendAvatars.add((CroppedImageView) findViewById(R.id.profile_friend_avatar_4));
            this.friendAvatars.add((CroppedImageView) findViewById(R.id.profile_friend_avatar_5));
        }
        this.requestCountView = (TextView) findViewById(R.id.profile_request_counter);
        this.noFriendsImageView = (XLEImageViewFast) findViewById(R.id.profile_friends_none);
        this.mottoAppearAnimation = new AlphaAnimation(0.0f, 1.0f);
        this.mottoAppearAnimation.setDuration(500);
        this.mottoAppearAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                SocialActivityAdapter.this.mottoView.setVisibility(0);
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
                SocialActivityAdapter.this.mottoView.setVisibility(4);
            }
        });
        this.avatarView = (AvatarViewEditor) findViewById(R.id.social_avatar_view);
        this.meActor = (AvatarViewActor) findViewById(R.id.social_avatar_actor);
        this.shadowtar = (ImageView) findViewById(R.id.social_avatar_shadowtar);
        findAndInitializeModuleById(R.id.friends_list_module_for_tablet, this.profileViewModel);
    }

    protected void onAppBarButtonsAdded() {
        int i;
        int i2 = 8;
        if (this.profileViewModel.getIsManifestFiltered()) {
            i = 0;
        } else {
            i = 8;
        }
        setAppBarButtonVisibility(R.id.avatar_editor_launch, i);
        if (!this.profileViewModel.getIsManifestFiltered()) {
            i2 = 0;
        }
        setAppBarButtonVisibility(R.id.avatar_editor_filtered_launch, i2);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.full_profile_launch, new OnClickListener() {
            public void onClick(View v) {
                SocialActivityAdapter.this.profileViewModel.navigateToEditProfile();
            }
        });
        setAppBarButtonClickListener(R.id.avatar_editor_launch, new OnClickListener() {
            public void onClick(View v) {
                SocialActivityAdapter.this.profileViewModel.navigateToAvatarEditor();
            }
        });
        setAppBarButtonClickListener(R.id.avatar_editor_filtered_launch, new OnClickListener() {
            public void onClick(View v) {
                SocialActivityAdapter.this.profileViewModel.navigateToAvatarEditorFiltered();
            }
        });
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                SocialActivityAdapter.this.profileViewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        int i;
        int i2 = 8;
        if (this.profileViewModel.getIsActive()) {
            updateLoadingIndicator(this.profileViewModel.isBusy());
        }
        this.gamerTagView.setText(this.profileViewModel.getGamertag());
        this.gamerscoreView.setText(this.profileViewModel.getGamerscore());
        this.mottoView.setText(this.profileViewModel.getMotto());
        if (this.gamerNameView != null) {
            this.gamerNameView.setText(this.profileViewModel.getName());
        }
        TextView textView = this.membershipLevelView;
        if (this.profileViewModel.isGold()) {
            i = 0;
        } else {
            i = 8;
        }
        textView.setVisibility(i);
        if (this.friendSwitchPanel != null) {
            this.friendSwitchPanel.setState(this.profileViewModel.getViewModelState().ordinal());
        }
        if (this.searchIconSet != null) {
            if (this.profileViewModel.getViewModelState() == ListState.ValidContentState) {
                this.searchIconSet.setVisibility(0);
            } else {
                this.searchIconSet.setVisibility(4);
            }
        }
        if (this.friendsButton != null) {
            if (!(this.profileViewModel.getFriendAvatarUrlList() == null || this.profileViewModel.getFriendAvatarUrlList() == this.friendsUriList)) {
                this.friendsUriList = this.profileViewModel.getFriendAvatarUrlList();
                XLELog.Diagnostic("SocialActivityAdapter", "Updating friends tile images");
                for (int i3 = 0; i3 < this.friendAvatars.size(); i3++) {
                    if (this.profileViewModel.getFriendAvatarUrlList().size() > i3) {
                        TextureManager.Instance().bindToView((URI) this.profileViewModel.getFriendAvatarUrlList().get(i3), (ImageView) this.friendAvatars.get(i3), TextureBindingOption.KeepAsIsBindingOption);
                        ((CroppedImageView) this.friendAvatars.get(i3)).setOnBitmapBindCompleteRunnable(new Runnable() {
                            public void run() {
                                SocialActivityAdapter.this.noFriendsImageView.setVisibility(4);
                            }
                        });
                    } else {
                        ((CroppedImageView) this.friendAvatars.get(i3)).setImageBitmap(null);
                        ((CroppedImageView) this.friendAvatars.get(i3)).setOnBitmapBindCompleteRunnable(null);
                    }
                }
            }
            if (this.profileViewModel.getFriendsRequestCount() > 0) {
                this.requestCountView.setVisibility(0);
                this.requestCountView.setText(Integer.toString(this.profileViewModel.getFriendsRequestCount()));
            } else {
                this.requestCountView.setVisibility(8);
            }
        }
        this.mottoView.setText(this.profileViewModel.getMotto());
        if (this.mottoView.getVisibility() != 0 && this.profileViewModel.shouldShowMotto()) {
            this.mottoView.startAnimation(this.mottoAppearAnimation);
        }
        if (this.mottoView.getVisibility() == 0 && !this.profileViewModel.shouldShowMotto()) {
            this.mottoView.startAnimation(this.mottoDisappearAnimation);
        }
        this.shadowtar.setVisibility(this.profileViewModel.getIsShadowtarVisible() ? 0 : 4);
        this.avatarView.setAvatarViewVM(this.profileViewModel.getAvatarViewVM());
        this.meActor.setActorVM(this.profileViewModel.getActorVM());
        if (this.profileViewModel.getIsManifestFiltered()) {
            i = 0;
        } else {
            i = 8;
        }
        setAppBarButtonVisibility(R.id.avatar_editor_filtered_launch, i);
        if (!this.profileViewModel.getIsManifestFiltered()) {
            i2 = 0;
        }
        setAppBarButtonVisibility(R.id.avatar_editor_launch, i2);
    }

    public void onPause() {
        super.onPause();
        if (this.friendsButton != null) {
            Iterator i$ = this.friendAvatars.iterator();
            while (i$.hasNext()) {
                ((CroppedImageView) i$.next()).setOnBitmapBindCompleteRunnable(null);
            }
        }
    }

    public void onResume() {
        super.onResume();
        this.mottoView.setVisibility(8);
    }
}
