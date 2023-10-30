package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.ui.PieChartView;
import com.microsoft.xbox.xle.viewmodel.AchievementsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public class AchievementsListHeaderPhoneModule extends ScreenModuleLayout {
    private View achievementDetailsView;
    private AchievementsActivityViewModel achievementViewModel;
    private TextView gameAchievementsView;
    private TextView gameEarnedPercentage;
    private TextView gameScoreView;
    private View gamerscoreDetailsView;
    private PieChartView pieChartView;

    public AchievementsListHeaderPhoneModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.achievements_list_header_content);
    }

    protected void onFinishInflate() {
        this.pieChartView = (PieChartView) findViewById(R.id.pie_chart_view);
        this.gameEarnedPercentage = (TextView) findViewById(R.id.achievements_earned_percentage);
        this.gameAchievementsView = (TextView) findViewById(R.id.achievements_game_achievements);
        this.gameScoreView = (TextView) findViewById(R.id.achievements_game_score);
        this.gamerscoreDetailsView = findViewById(R.id.achievements_gamerscore_details);
        this.achievementDetailsView = findViewById(R.id.achievements_achievement_details);
    }

    public void updateView() {
        int i = 0;
        if (this.achievementViewModel.getGameTitle() != null && this.achievementViewModel.getGameTitle().length() > 0) {
            this.gameEarnedPercentage.setText(this.achievementViewModel.getGameEarnedPercentage() + "%");
            this.gamerscoreDetailsView.setVisibility(this.achievementViewModel.getHasAchievements() ? 0 : 4);
            View view = this.achievementDetailsView;
            if (!this.achievementViewModel.getHasAchievements()) {
                i = 4;
            }
            view.setVisibility(i);
            this.gameAchievementsView.setText(this.achievementViewModel.getGameAchievementText());
            this.gameScoreView.setText(this.achievementViewModel.getGameScoreText());
            this.pieChartView.setPercentage(this.achievementViewModel.getGameEarnedPercentage());
        }
    }

    public void setViewModel(ViewModelBase vm) {
        this.achievementViewModel = (AchievementsActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.achievementViewModel;
    }
}
