package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.RecentGamesActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;

public class RecentGamesActivityAdapter extends AdapterBaseWithList {
    private ArrayList<GameInfo> gamesList;
    private RecentGamesListAdapter gamesListAdapter;
    private SwitchPanel gamesSwitchPanel;
    private RecentGamesActivityViewModel gamesViewModel;

    public RecentGamesActivityAdapter(RecentGamesActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.you_recent_games_activity_body);
        this.content = findViewById(R.id.recent_games_switch_panel);
        this.gamesViewModel = viewModel;
        this.listView = (XLEListView) findViewById(R.id.recent_games_list);
        this.gamesSwitchPanel = (SwitchPanel) this.content;
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RecentGamesActivityAdapter.this.gamesViewModel.navigateToCompareAchievements((GameInfo) ((SimpleListItemViewHolder) view.getTag()).getKey());
            }
        });
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                RecentGamesActivityAdapter.this.gamesViewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        if (this.gamesViewModel.getIsActive()) {
            updateLoadingIndicator(this.gamesViewModel.isBusy());
        }
        this.gamesSwitchPanel.setState(this.gamesViewModel.getViewModelState().ordinal());
        if (this.gamesViewModel.getGames() == null) {
            return;
        }
        if (this.gamesList != this.gamesViewModel.getGames()) {
            this.gamesList = this.gamesViewModel.getGames();
            this.gamesListAdapter = new RecentGamesListAdapter(XLEApplication.getMainActivity(), R.layout.games_list_row, this.gamesViewModel);
            this.listView.setAdapter(this.gamesListAdapter);
            restoreListPosition();
            this.listView.onDataUpdated();
            return;
        }
        this.listView.notifyDataSetChanged();
    }

    protected SwitchPanel getSwitchPanel() {
        return this.gamesSwitchPanel;
    }

    protected ViewModelBase getViewModel() {
        return this.gamesViewModel;
    }
}
