package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.CompareAchievementInfo;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.CompareAchievementsTabletListAdapter;
import com.microsoft.xbox.xle.ui.GamerInfoView;
import com.microsoft.xbox.xle.viewmodel.CompareAchievementsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;

public class CompareAchievementsTabletModule extends ScreenModuleWithList {
    private ArrayList<CompareAchievementInfo> achievementList;
    private CompareAchievementsTabletListAdapter achievementsListAdapter;
    private CompareAchievementsActivityViewModel compareAchievementsViewModel;
    private CustomTypefaceTextView gamerYouTitleView;
    private XLEListView listView;
    private GamerInfoView meGamerInfoView;
    private GamerInfoView youGamerInfoView;

    public CompareAchievementsTabletModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.compare_achievements_activity_content);
    }

    protected void onFinishInflate() {
        this.listView = (XLEListView) findViewById(R.id.compare_achievements_list);
        this.listView.setOnItemClickListener(null);
        this.gamerYouTitleView = (CustomTypefaceTextView) findViewById(R.id.you_gamer_me_title);
        this.meGamerInfoView = (GamerInfoView) findViewById(R.id.me_gamer_info_layout);
        this.youGamerInfoView = (GamerInfoView) findViewById(R.id.you_gamer_info_layout);
    }

    public void updateView() {
        this.gamerYouTitleView.setText(this.compareAchievementsViewModel.getCompareGamerTag());
        this.meGamerInfoView.updateGamerInfo(XLEApplication.Resources.getString(R.string.compare_me), this.compareAchievementsViewModel.getMeGamerpicUri(), this.compareAchievementsViewModel.getMeGamerAchievementsPercentValue(), this.compareAchievementsViewModel.getMeGamerAchievementsPercentText(), this.compareAchievementsViewModel.getMeGamerAchievementsWithTotalText(), this.compareAchievementsViewModel.getMeGamerScoreWithTotalText());
        this.youGamerInfoView.updateGamerInfo(this.compareAchievementsViewModel.getCompareGamerTag(), this.compareAchievementsViewModel.getYouGamerpicUri(), this.compareAchievementsViewModel.getYouGamerAchievementsPercentValue(), this.compareAchievementsViewModel.getYouGamerAchievementsPercentText(), this.compareAchievementsViewModel.getYouGamerAchievementsWithTotalText(), this.compareAchievementsViewModel.getYouGamerScoreWithTotalText());
        if (this.compareAchievementsViewModel.getAchievements() == null) {
            return;
        }
        if (this.achievementList != this.compareAchievementsViewModel.getAchievements()) {
            this.achievementList = this.compareAchievementsViewModel.getAchievements();
            this.achievementsListAdapter = new CompareAchievementsTabletListAdapter(XLEApplication.getMainActivity(), R.layout.compare_games_list_row, this.compareAchievementsViewModel);
            this.listView.setAdapter(this.achievementsListAdapter);
            restoreListPosition();
            this.listView.onDataUpdated();
            return;
        }
        this.listView.notifyDataSetChanged();
    }

    public void setViewModel(ViewModelBase vm) {
        this.compareAchievementsViewModel = (CompareAchievementsActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.compareAchievementsViewModel;
    }

    public XLEListView getListView() {
        return this.listView;
    }
}
