package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.CompareAchievementsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public class CompareAchievementsActivityAdapter extends AdapterBaseWithList {
    private SwitchPanel achievementsSwitchPanel;
    private CompareAchievementsActivityViewModel compareAchievementsViewModel;
    private TextView gameScoreView;
    private XLEImageViewFast gameTileView;
    private TextView gameTitleView;
    private TextView meGamerScore;
    private XLEImageViewFast meGamerpic;
    private TextView youGamerScore;
    private XLEImageViewFast youGamerpic;

    public CompareAchievementsActivityAdapter(CompareAchievementsActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.compare_achievements_activity_body);
        this.content = findViewById(R.id.compare_achievements_content);
        this.compareAchievementsViewModel = viewModel;
        this.meGamerScore = (TextView) findViewById(R.id.me_gamer_gamerscore);
        this.youGamerScore = (TextView) findViewById(R.id.you_gamer_gamerscore);
        this.meGamerpic = (XLEImageViewFast) findViewById(R.id.me_gamer_gamerpic);
        this.youGamerpic = (XLEImageViewFast) findViewById(R.id.you_gamer_gamerpic);
        this.gameTileView = (XLEImageViewFast) findViewById(R.id.compare_achievements_game_tile);
        this.gameTitleView = (TextView) findViewById(R.id.compare_achievements_game_title);
        this.gameScoreView = (TextView) findViewById(R.id.compare_achievements_game_score);
        this.achievementsSwitchPanel = (SwitchPanel) findViewById(R.id.compare_achievements_switch_panel);
        findAndInitializeModuleById(R.id.compare_achievements_list_phone_module, this.compareAchievementsViewModel);
        findAndInitializeModuleById(R.id.compare_achievements_list_tablet_module, this.compareAchievementsViewModel);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                CompareAchievementsActivityAdapter.this.compareAchievementsViewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.compareAchievementsViewModel.isBusy());
        this.achievementsSwitchPanel.setState(this.compareAchievementsViewModel.getViewModelState().ordinal());
        if (this.compareAchievementsViewModel.getAchievements() != null) {
            XLEUtil.updateTextIfNotNull(this.meGamerScore, this.compareAchievementsViewModel.getMeGamerScore());
            XLEUtil.updateTextIfNotNull(this.youGamerScore, this.compareAchievementsViewModel.getYouGamerScore());
        }
        if (this.compareAchievementsViewModel.getGameTitle() != null && this.compareAchievementsViewModel.getGameTitle().length() > 0) {
            if (this.gameTileView != null) {
                this.gameTileView.setImageURI2(this.compareAchievementsViewModel.getGameTileUri());
            }
            XLEUtil.updateTextIfNotNull(this.gameScoreView, this.compareAchievementsViewModel.getGameScoreText());
        }
        if (this.meGamerpic != null) {
            this.meGamerpic.setImageURI2(this.compareAchievementsViewModel.getMeGamerpicUri());
        }
        if (this.youGamerpic != null) {
            this.youGamerpic.setImageURI2(this.compareAchievementsViewModel.getYouGamerpicUri());
        }
        if (this.gameTitleView != null && this.compareAchievementsViewModel.getGameTitle() != null && this.compareAchievementsViewModel.getGameTitle().length() > 0) {
            this.gameTitleView.setText(this.compareAchievementsViewModel.getGameTitle());
        }
    }

    protected SwitchPanel getSwitchPanel() {
        return this.achievementsSwitchPanel;
    }

    protected ViewModelBase getViewModel() {
        return this.compareAchievementsViewModel;
    }
}
