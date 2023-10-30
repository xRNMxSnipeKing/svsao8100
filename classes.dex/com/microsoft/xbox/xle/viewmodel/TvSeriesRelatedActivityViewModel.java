package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.edsv2.EDSV2TVSeriesBrowseSeasonModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVSeriesMediaItem;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;

public class TvSeriesRelatedActivityViewModel extends AbstractRelatedActivityViewModel<EDSV2TVSeriesBrowseSeasonModel> {
    public TvSeriesRelatedActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getTvSeriesRelatedAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getTvSeriesRelatedAdapter(this);
    }

    public ArrayList<EDSV2TVSeriesMediaItem> getRelated() {
        return ((EDSV2TVSeriesBrowseSeasonModel) this.mediaModel).getRelated();
    }
}
