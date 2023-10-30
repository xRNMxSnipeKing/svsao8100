package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.ui.SimpleGridAdapter;
import com.microsoft.xbox.toolkit.ui.TouchUtil;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.TabletProfileActivityViewModel;
import java.util.ArrayList;

public class TabletProfileRecentGamesGridAdapter extends SimpleGridAdapter {
    private int totalGridCellSize;
    private TabletProfileActivityViewModel viewModel;

    public TabletProfileRecentGamesGridAdapter(Context context, int resourceId, int emptyResourceId, ArrayList recentGames, TabletProfileActivityViewModel vm, int size) {
        super(context, resourceId, emptyResourceId, recentGames);
        this.viewModel = vm;
        this.totalGridCellSize = size;
        initGridCell();
    }

    private void initGridCell() {
        for (int i = 0; i < this.totalGridCellSize; i++) {
            getGridView(i);
        }
    }

    public View getGridView(int index) {
        View gridView = super.getGridView(index);
        if (getItem(index) != null && this.viewModel.getViewModelState() == ListState.ValidContentState) {
            if (getItem(index) instanceof GameInfo) {
                updatePropertiesForYouRecentGame((GameInfo) getItem(index), gridView);
            } else {
                updatePropertiesForMeRecentGame((Title) getItem(index), gridView);
            }
        }
        return gridView;
    }

    public void onItemDestory(View view) {
        if (view != null) {
            view.setOnClickListener(null);
        }
    }

    private void updatePropertiesForYouRecentGame(final GameInfo gameInfo, View recentGameView) {
        if (recentGameView != null) {
            recentGameView.setTag(gameInfo);
            ((XLEUniformImageView) recentGameView.findViewById(R.id.recent_game_tile)).setImageURI2(Title.getImageUrl(MeProfileModel.getModel().getLegalLocale(), gameInfo.Id), XLEUtil.getMediaItemDefaultRid(1));
            recentGameView.setOnClickListener(TouchUtil.createOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    TabletProfileRecentGamesGridAdapter.this.viewModel.navigateToCompareAchievements(gameInfo);
                }
            }));
        }
    }

    private void updatePropertiesForMeRecentGame(final Title gameInfo, View recentGameView) {
        if (recentGameView != null) {
            recentGameView.setTag(gameInfo);
            ((XLEUniformImageView) recentGameView.findViewById(R.id.recent_game_tile)).setImageURI2(Title.getImageUrl(MeProfileModel.getModel().getLegalLocale(), gameInfo.titleId), XLEUtil.getMediaItemDefaultRid(1));
            recentGameView.setOnClickListener(TouchUtil.createOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    TabletProfileRecentGamesGridAdapter.this.viewModel.navigateToGameDetailsPage(gameInfo);
                }
            }));
        }
    }
}
