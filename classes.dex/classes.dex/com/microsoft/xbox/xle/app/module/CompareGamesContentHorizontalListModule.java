package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.CompareGameInfo;
import com.microsoft.xbox.toolkit.ui.HorizontalListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.CompareGamesHorizontalListAdapter;
import com.microsoft.xbox.xle.app.adapter.CompareGamesListAdapter;
import com.microsoft.xbox.xle.viewmodel.CompareGamesActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;
import java.util.ArrayList;

public class CompareGamesContentHorizontalListModule extends ScreenModuleLayout {
    private ArrayList<CompareGameInfo> gamesList;
    private CompareGamesListAdapter gamesListAdapter;
    private HorizontalListView listView;
    private CompareGamesActivityViewModel viewModel;

    public CompareGamesContentHorizontalListModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.compare_games_activity_content);
    }

    protected void onFinishInflate() {
        this.listView = (HorizontalListView) findViewById(R.id.compare_games_list);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (view.getTag() != null && (view.getTag() instanceof CompareGameInfo)) {
                    CompareGameInfo game = (CompareGameInfo) view.getTag();
                    XLEGlobalData.getInstance().setSelectedCompareGameInfo(game);
                    CompareGamesContentHorizontalListModule.this.viewModel.navigateToCompareAchievements(game.getGameInfo());
                }
            }
        });
    }

    public void updateView() {
        if (this.viewModel.getGames() == null) {
            return;
        }
        if (this.gamesList != this.viewModel.getGames()) {
            this.gamesList = this.viewModel.getGames();
            this.gamesListAdapter = new CompareGamesHorizontalListAdapter(XLEApplication.getMainActivity(), R.layout.games_list_row, this.viewModel);
            this.listView.setAdapter(this.gamesListAdapter);
            restoreListPosition();
            return;
        }
        this.gamesListAdapter.notifyDataSetChanged();
    }

    public void onStop() {
        super.onStop();
        this.viewModel.setListPosition(this.listView.getFirstVisiblePosition(), this.listView.getCurrentX());
    }

    protected void restoreListPosition() {
        if (this.viewModel != null && this.listView != null) {
            this.listView.scrollTo(this.viewModel.getAndResetListOffset());
        }
    }

    public void onDestroy() {
        this.listView.setOnItemClickListener(null);
    }

    public void setViewModel(ViewModelBase vm) {
        this.viewModel = (CompareGamesActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }
}
