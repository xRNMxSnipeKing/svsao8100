package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.view.AvatarViewActor;
import com.microsoft.xbox.avatar.view.AvatarViewEditor;
import com.microsoft.xbox.service.model.AchievementItem;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithAvatar;
import com.microsoft.xbox.xle.viewmodel.CompareAchievementDetailActivityViewModel;

public class CompareAchievementDetailActivityAdapter extends AdapterBaseWithAvatar {
    private TextView achievementDescriptionView;
    private TextView achievementScoreView;
    private XLEImageViewFast achievementTileView;
    private TextView achievementTitleView;
    private CompareAchievementDetailActivityViewModel achievementViewModel;
    private TextView gameTitleView;
    private AvatarViewActor meActor;
    private AvatarViewActor youActor;

    public CompareAchievementDetailActivityAdapter(CompareAchievementDetailActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.compare_achievementdetails_activity_body);
        this.achievementViewModel = viewModel;
        this.gameTitleView = (TextView) findViewById(R.id.compare_achievementdetails_gametitle);
        this.achievementTileView = (XLEImageViewFast) findViewById(R.id.compare_achievementdetails_tile);
        this.achievementTitleView = (TextView) findViewById(R.id.compare_achievementdetails_title);
        this.achievementScoreView = (TextView) findViewById(R.id.compare_achievementdetails_score);
        this.achievementDescriptionView = (TextView) findViewById(R.id.compare_achievementdetails_description);
        this.avatarView = (AvatarViewEditor) findViewById(R.id.achievementdetails_avatar);
        this.meActor = (AvatarViewActor) findViewById(R.id.achievementdetails_avatar_actor_me);
        this.youActor = (AvatarViewActor) findViewById(R.id.achievementdetails_avatar_actor_you);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View v) {
                CompareAchievementDetailActivityAdapter.this.achievementViewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.achievementViewModel.isBusy());
        this.gameTitleView.setText(this.achievementViewModel.getGameTitle());
        AchievementItem achievement = this.achievementViewModel.getAchievement();
        if (achievement != null) {
            this.achievementTileView.setImageURI2(achievement.getTileUri());
            this.achievementTitleView.setText(achievement.getName());
            this.achievementScoreView.setText(achievement.getGamerscore());
            this.achievementDescriptionView.setText(achievement.getDescription());
        }
        this.avatarView.setAvatarViewVM(this.achievementViewModel.getAvatarViewVM());
        this.meActor.setActorVM(this.achievementViewModel.getMeActorVM());
        this.youActor.setActorVM(this.achievementViewModel.getYouActorVM());
    }
}
