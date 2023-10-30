package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVSeasonMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVSeriesBrowseSeasonModel;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;

public class TvSeriesDetailsViewModel extends EDSV2MediaItemListViewModel<EDSV2TVSeriesBrowseSeasonModel> {
    public TvSeriesDetailsViewModel() {
        this.adapter = AdapterFactory.getInstance().getTvSeriesDetailsAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getTvSeriesDetailsAdapter(this);
    }

    public void load(boolean forceRefresh) {
        super.load(forceRefresh);
        if (XLEGlobalData.getInstance().getIsTablet()) {
            ((EDSV2TVSeriesBrowseSeasonModel) this.mediaModel).loadDetails(forceRefresh);
        }
    }

    protected int getErrorStringResourceId() {
        return R.string.toast_tv_series_details_list_error;
    }

    public ArrayList<EDSV2TVSeasonMediaItem> getSeasons() {
        return ((EDSV2TVSeriesBrowseSeasonModel) this.mediaModel).getMediaItemListData();
    }

    public void NavigateToTvSeasonDetails(EDSV2TVSeasonMediaItem item) {
        navigateToAppOrMediaDetails(item);
    }

    public String getNetworkName() {
        return ((EDSV2TVSeriesBrowseSeasonModel) this.mediaModel).getNetworkName();
    }
}
