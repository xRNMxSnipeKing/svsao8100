package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.xle.app.adapter.CollectionFilterActivityAdapter;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel.CollectionFilter;

public class CollectionFilterActivityViewModel extends ViewModelBase {
    private CollectionFilter currentFilter;

    public CollectionFilterActivityViewModel() {
        this.adapter = new CollectionFilterActivityAdapter(this);
        this.currentFilter = XLEGlobalData.getInstance().getSelectedCollectionFilter();
    }

    public CollectionFilter getSelectedFilter() {
        return this.currentFilter;
    }

    public void onRehydrate() {
        this.adapter = new CollectionFilterActivityAdapter(this);
        this.currentFilter = XLEGlobalData.getInstance().getSelectedCollectionFilter();
    }

    protected void onStartOverride() {
        XLEGlobalData.getInstance().setSelectedCollectionFilter(this.currentFilter);
    }

    protected void onStopOverride() {
    }

    public boolean isBusy() {
        return false;
    }

    public void load(boolean forceRefresh) {
    }

    public void onFilterSelected(CollectionFilter filter) {
        this.currentFilter = filter;
        XLEGlobalData.getInstance().setSelectedCollectionFilter(this.currentFilter);
        goBack();
    }
}
