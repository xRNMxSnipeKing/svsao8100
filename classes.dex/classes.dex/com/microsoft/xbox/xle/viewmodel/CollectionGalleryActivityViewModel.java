package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.QuickplayModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.edsv2.EDSV2AppMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2GameMediaItem;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.activity.CollectionFilterActivity;
import com.microsoft.xbox.xle.app.activity.SearchGameHistoryActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel.CollectionFilter;
import java.util.ArrayList;
import java.util.List;

public class CollectionGalleryActivityViewModel extends ViewModelBase {
    private ListState allTitleListState;
    private QuickplayModel allTitleModel;
    private ListState appListState;
    private CollectionFilter currentCollectionFilter;
    private ListState gameListState;
    private boolean isViewAllGamesPage;

    public CollectionGalleryActivityViewModel() {
        this.allTitleListState = ListState.LoadingState;
        this.gameListState = ListState.LoadingState;
        this.appListState = ListState.LoadingState;
        this.currentCollectionFilter = CollectionFilter.All;
        this.isViewAllGamesPage = false;
        this.adapter = AdapterFactory.getInstance().getCollectionGalleryAdapter(this);
        if (XLEGlobalData.getInstance().getHideCollectionFilter()) {
            this.isViewAllGamesPage = true;
            this.currentCollectionFilter = CollectionFilter.Games;
            return;
        }
        this.isViewAllGamesPage = false;
    }

    public ListState getViewModelState() {
        switch (this.currentCollectionFilter) {
            case Games:
                return this.gameListState;
            case Apps:
                return this.appListState;
            default:
                return this.allTitleListState;
        }
    }

    public CollectionFilter getCollectionFilter() {
        return this.currentCollectionFilter;
    }

    public ArrayList<Title> getAllTitleList() {
        return this.allTitleModel.getAllQuickplayList();
    }

    public ArrayList<Title> getGamesList() {
        return this.allTitleModel.getGamesQuickplayList();
    }

    public ArrayList<Title> getAppsList() {
        return this.allTitleModel.getAppsQuickplayList();
    }

    protected void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case RecentsData:
                if (asyncResult.getException() == null) {
                    if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
                        this.allTitleListState = getListState(this.allTitleModel.getAllQuickplayList());
                        this.gameListState = getListState(this.allTitleModel.getGamesQuickplayList());
                        this.appListState = getListState(this.allTitleModel.getAppsQuickplayList());
                        break;
                    }
                }
                if (this.allTitleModel.getAllQuickplayList() == null || this.allTitleModel.getAllQuickplayList().size() == 0) {
                    this.allTitleListState = ListState.ErrorState;
                }
                if (this.allTitleModel.getGamesQuickplayList() == null || this.allTitleModel.getGamesQuickplayList().size() == 0) {
                    this.gameListState = ListState.ErrorState;
                }
                if (this.allTitleModel.getAppsQuickplayList() == null || this.allTitleModel.getAppsQuickplayList().size() == 0) {
                    this.appListState = ListState.ErrorState;
                    break;
                }
                break;
        }
        this.adapter.updateView();
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getCollectionGalleryAdapter(this);
        if (!this.isViewAllGamesPage) {
            if (this.currentCollectionFilter != XLEGlobalData.getInstance().getSelectedCollectionFilter()) {
                setListPosition(0, 0);
            }
            this.currentCollectionFilter = XLEGlobalData.getInstance().getSelectedCollectionFilter();
        }
    }

    public boolean isBusy() {
        return this.allTitleModel.getIsLoading();
    }

    public void load(boolean forceRefresh) {
        this.allTitleModel.load(forceRefresh);
    }

    protected void onStartOverride() {
        this.allTitleModel = QuickplayModel.getInstance();
        this.allTitleModel.addObserver(this);
        if (!this.isViewAllGamesPage) {
            this.currentCollectionFilter = XLEGlobalData.getInstance().getSelectedCollectionFilter();
        }
    }

    protected void onStopOverride() {
        this.allTitleModel.removeObserver(this);
        this.allTitleModel = null;
    }

    public void navigateToCollectionFilter() {
        XLEGlobalData.getInstance().setSelectedCollectionFilter(this.currentCollectionFilter);
        NavigateTo(CollectionFilterActivity.class);
    }

    public void navigateToGameAchievements(GameInfo game) {
        XLELog.Info("CollectionActivityViewModel", "Navigating to achievements.");
        navigateToAchievements(game);
    }

    public void navigateToAchievementsOrTitleDetail(Title title) {
        XLELog.Info("CollectionActivityViewModel", "Navigating to title detail.");
        if (title.IsGame()) {
            navigateToAchievements(new EDSV2GameMediaItem(title));
        } else {
            navigateToAppOrMediaDetails(new EDSV2AppMediaItem(title));
        }
    }

    public void navigateToSearchTitles() {
        XLELog.Info("CollectionGalleryActivityViewModel", "Navigating to search title history");
        NavigateTo(SearchGameHistoryActivity.class);
    }

    public static ListState getListState(List list) {
        ListState newState = ListState.LoadingState;
        if (list == null) {
            return ListState.LoadingState;
        }
        if (list.size() == 0) {
            return ListState.NoContentState;
        }
        return ListState.ValidContentState;
    }

    public int getFilterContainerVisibility() {
        return this.isViewAllGamesPage ? 4 : 0;
    }
}
