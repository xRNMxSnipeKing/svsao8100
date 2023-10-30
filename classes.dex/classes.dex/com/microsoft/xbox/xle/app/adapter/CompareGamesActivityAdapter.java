package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.CompareGamesActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public class CompareGamesActivityAdapter extends AdapterBaseWithList {
    private CompareGamesActivityViewModel compareGamesViewModel;
    private SwitchPanel gamesSwitchPanel;
    private TextView meGamerScore;
    private XLEImageViewFast meGamerpic;
    private TextView youGamerScore;
    private XLEImageViewFast youGamerpic;

    public CompareGamesActivityAdapter(CompareGamesActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.compare_games_activity_body);
        this.content = findViewById(R.id.compare_games_content);
        this.compareGamesViewModel = viewModel;
        View view = findViewById(R.id.me_gamer_gamerscore);
        if (view != null) {
            this.meGamerScore = (TextView) view;
            this.youGamerScore = (TextView) findViewById(R.id.you_gamer_gamerscore);
            this.meGamerpic = (XLEImageViewFast) findViewById(R.id.me_gamer_gamerpic);
            this.youGamerpic = (XLEImageViewFast) findViewById(R.id.you_gamer_gamerpic);
        }
        this.gamesSwitchPanel = (SwitchPanel) findViewById(R.id.compare_games_switch_panel);
        findAndInitializeModuleById(R.id.compare_games_vertical_list_module, this.compareGamesViewModel);
        findAndInitializeModuleById(R.id.compare_games_horizontal_list_module, this.compareGamesViewModel);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                CompareGamesActivityAdapter.this.compareGamesViewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        if (this.compareGamesViewModel.getIsActive()) {
            updateLoadingIndicator(this.compareGamesViewModel.isBusy());
        }
        this.gamesSwitchPanel.setState(this.compareGamesViewModel.getViewModelState().ordinal());
        if (this.meGamerScore != null) {
            this.meGamerScore.setText(this.compareGamesViewModel.getMeGamerScore());
            this.youGamerScore.setText(this.compareGamesViewModel.getYouGamerScore());
            this.meGamerpic.setImageURI2(this.compareGamesViewModel.getMeGamerpicUri());
            this.youGamerpic.setImageURI2(this.compareGamesViewModel.getYouGamerpicUri());
        }
    }

    protected SwitchPanel getSwitchPanel() {
        return this.gamesSwitchPanel;
    }

    protected ViewModelBase getViewModel() {
        return this.compareGamesViewModel;
    }
}
