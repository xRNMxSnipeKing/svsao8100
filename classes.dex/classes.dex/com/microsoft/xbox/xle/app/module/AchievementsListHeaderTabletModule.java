package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.ui.PieChartView;
import com.microsoft.xbox.xle.viewmodel.AchievementsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public class AchievementsListHeaderTabletModule extends ScreenModuleLayout {
    private AchievementsActivityViewModel achievementViewModel;
    private TextView gameEarnedAchievements;
    private TextView gameEarnedPercentage;
    private TextView gameEarnedScore;
    private TextView gameLastPlayedDate;
    private PieChartView pieChartView;

    public AchievementsListHeaderTabletModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.achievements_list_header_content);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.pieChartView = (PieChartView) findViewById(R.id.pie_chart_view);
        this.gameEarnedPercentage = (TextView) findViewById(R.id.achievements_earned_percentage);
        this.gameEarnedScore = (TextView) findViewById(R.id.achievements_game_score);
        this.gameEarnedAchievements = (TextView) findViewById(R.id.achievements_game_achievements);
        this.gameLastPlayedDate = (TextView) findViewById(R.id.achievements_game_last_played_date);
    }

    public void updateView() {
        this.gameEarnedPercentage.setText(this.achievementViewModel.getGameEarnedPercentage() + XLEApplication.Resources.getString(R.string.achievements_earned_percentage));
        this.gameEarnedScore.setText(this.achievementViewModel.getGameScoreText());
        this.gameEarnedAchievements.setText(this.achievementViewModel.getGameAchievementText());
        if (!(this.achievementViewModel.getGameLastPlayedDate() == null || JavaUtil.getLocalizedDateString(this.achievementViewModel.getGameLastPlayedDate()) == null)) {
            this.gameLastPlayedDate.setText(String.format(XLEApplication.Resources.getString(R.string.game_last_played_date), new Object[]{JavaUtil.getLocalizedDateString(this.achievementViewModel.getGameLastPlayedDate())}));
        }
        this.pieChartView.setPercentage(this.achievementViewModel.getGameEarnedPercentage());
    }

    public void setViewModel(ViewModelBase vm) {
        this.achievementViewModel = (AchievementsActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.achievementViewModel;
    }
}
