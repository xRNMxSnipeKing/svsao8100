package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.CompareGameInfo;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.CompareGameItemViewHolder;
import com.microsoft.xbox.xle.app.adapter.CompareGamesListAdapter;
import com.microsoft.xbox.xle.viewmodel.CompareGamesActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;

public class CompareGamesContentVerticalListModule extends ScreenModuleWithList {
    private ArrayList<CompareGameInfo> gamesList;
    private CompareGamesListAdapter gamesListAdapter;
    private XLEListView listView;
    private CompareGamesActivityViewModel viewModel;

    public CompareGamesContentVerticalListModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.compare_games_activity_content);
    }

    protected void onFinishInflate() {
        this.listView = (XLEListView) findViewById(R.id.compare_games_list);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (view.getTag() != null && (view.getTag() instanceof CompareGameItemViewHolder)) {
                    CompareGamesContentVerticalListModule.this.viewModel.navigateToCompareAchievements((GameInfo) ((CompareGameItemViewHolder) view.getTag()).getKey());
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
            this.gamesListAdapter = new CompareGamesListAdapter(XLEApplication.getMainActivity(), R.layout.games_list_row, this.viewModel);
            this.listView.setAdapter(this.gamesListAdapter);
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
        this.viewModel = (CompareGamesActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }

    public XLEListView getListView() {
        return this.listView;
    }
}
