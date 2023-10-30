package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.CompareAchievementInfo;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.CompareAchievementsListAdapter;
import com.microsoft.xbox.xle.app.adapter.CompareGameItemViewHolder;
import com.microsoft.xbox.xle.viewmodel.CompareAchievementsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;

public class CompareAchievementsPhoneModule extends ScreenModuleWithList {
    private ArrayList<CompareAchievementInfo> achievementList;
    private CompareAchievementsListAdapter achievementsListAdapter;
    private CompareAchievementsActivityViewModel compareAchievementsViewModel;
    private XLEListView listView;

    public CompareAchievementsPhoneModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.compare_achievements_activity_content);
    }

    protected void onFinishInflate() {
        this.listView = (XLEListView) findViewById(R.id.compare_achievements_list);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CompareAchievementsPhoneModule.this.compareAchievementsViewModel.navigateToCompareSingleAchievement(((CompareGameItemViewHolder) view.getTag()).getKey().toString());
            }
        });
    }

    public void updateView() {
        if (this.compareAchievementsViewModel.getAchievements() == null) {
            return;
        }
        if (this.achievementList != this.compareAchievementsViewModel.getAchievements()) {
            this.achievementList = this.compareAchievementsViewModel.getAchievements();
            this.achievementsListAdapter = new CompareAchievementsListAdapter(XLEApplication.getMainActivity(), R.layout.compare_games_list_row, this.compareAchievementsViewModel);
            this.listView.setAdapter(this.achievementsListAdapter);
            restoreListPosition();
            this.listView.onDataUpdated();
            return;
        }
        this.listView.notifyDataSetChanged();
    }

    public void onDestroy() {
        super.onDestroy();
        this.listView.setOnItemClickListener(null);
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
