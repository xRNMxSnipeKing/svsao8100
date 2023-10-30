package com.microsoft.xbox.xle.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.AchievementItem;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.SoundManager;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.AchievementsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;
import java.util.ArrayList;

public class AchievementsActivityAdapter extends AdapterBaseWithList {
    private SwitchPanel achievementSwitchPanel;
    private AchievementsActivityViewModel achievementViewModel;
    private ArrayList<AchievementItem> achievementsList;
    private TextView gameTitleView;
    private View headView;
    private boolean isWithHeader;
    private AchievementsListAdapter listAdapter;

    public AchievementsActivityAdapter(AchievementsActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.achievements_activity_body);
        this.content = findViewById(R.id.achievements_content);
        this.achievementViewModel = viewModel;
        this.listView = (XLEListView) findViewById(R.id.achievements_list);
        this.gameTitleView = (TextView) findViewById(R.id.achievements_game_title);
        this.achievementSwitchPanel = (SwitchPanel) findViewById(R.id.achievements_switch_panel);
        this.headView = ((LayoutInflater) XLEApplication.MainActivity.getSystemService("layout_inflater")).inflate(R.layout.achievements_list_header, null, true);
        if (!XLEGlobalData.getInstance().getIsTablet()) {
            this.listView.setOnItemClickListenerWithoutSound(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    if (AchievementsActivityAdapter.this.listView.getHeaderViewsCount() <= 0 || position != 0) {
                        SoundManager.getInstance().playSound(XboxApplication.Instance.getRawRValue("sndbuttonselectandroid"));
                    }
                    AchievementItemViewHolder viewHolder = (AchievementItemViewHolder) view.getTag();
                    if (viewHolder != null) {
                        AchievementsActivityAdapter.this.achievementViewModel.navigateToAchievement(viewHolder.getKey());
                    }
                }
            });
        }
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                AchievementsActivityAdapter.this.achievementViewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.achievementViewModel.isBusy());
        this.achievementSwitchPanel.setState(this.achievementViewModel.getViewModelState().ordinal());
        if (this.achievementViewModel.getAchievements() != null) {
            if (this.achievementsList != this.achievementViewModel.getAchievements()) {
                this.achievementsList = this.achievementViewModel.getAchievements();
                this.listAdapter = new AchievementsListAdapter(XLEApplication.getMainActivity(), R.layout.achievements_list_row, this.achievementViewModel);
                if (this.achievementViewModel.shouldShowAchievementsHeader()) {
                    this.listView.setAdapter(null);
                    if (this.isWithHeader) {
                        this.listView.removeHeaderView(this.headView);
                        this.isWithHeader = false;
                    }
                    this.listView.addHeaderView(this.headView);
                    this.isWithHeader = true;
                    findAndInitializeModuleById(R.id.achievements_List_Header_Phone_Module, this.achievementViewModel);
                    findAndInitializeModuleById(R.id.achievements_List_Header_Tablet_Module, this.achievementViewModel);
                }
                this.listView.setAdapter(this.listAdapter);
                restoreListPosition();
                this.listView.onDataUpdated();
            } else {
                this.listView.notifyDataSetChanged();
            }
        }
        if (this.achievementViewModel.getGameTitle() != null && this.achievementViewModel.getGameTitle().length() > 0) {
            XLEUtil.updateTextIfNotNull(this.gameTitleView, JavaUtil.stringToUpper(this.achievementViewModel.getGameTitle()));
        }
    }

    protected SwitchPanel getSwitchPanel() {
        return this.achievementSwitchPanel;
    }

    protected ViewModelBase getViewModel() {
        return this.achievementViewModel;
    }
}
