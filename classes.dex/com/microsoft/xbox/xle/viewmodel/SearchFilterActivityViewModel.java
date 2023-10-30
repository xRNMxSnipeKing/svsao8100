package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.edsv2.EDSV2SearchFilterCount;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchFilterType;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.activity.SearchResultsActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.List;

public class SearchFilterActivityViewModel extends ViewModelBase {
    public SearchFilterActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getSearchFilterAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getSearchFilterAdapter(this);
    }

    protected void onStartOverride() {
    }

    protected void onStopOverride() {
    }

    public boolean isBusy() {
        return false;
    }

    public void load(boolean forceRefresh) {
    }

    public List<EDSV2SearchFilterCount> getSearchFiltersList() {
        return XLEGlobalData.getInstance().getSearchResultFilterCountList();
    }

    public void NavigateToFilteredSearchResults(EDSV2SearchFilterType filter) {
        XLEGlobalData.getInstance().setSelectedFilter(filter);
        try {
            NavigationManager.getInstance().GotoScreenWithPop(SearchResultsActivity.class);
        } catch (XLEException e) {
            XLELog.Error("navigate to error", e.toString());
        }
    }
}
