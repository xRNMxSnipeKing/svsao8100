package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.edsv2.EDSV2TVSeasonBrowseEpisodeModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVSeriesMediaItem;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;

public class TvSeasonRelatedActivityViewModel extends AbstractRelatedActivityViewModel<EDSV2TVSeasonBrowseEpisodeModel> {
    public TvSeasonRelatedActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getTvSeasonRelatedAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getTvSeasonRelatedAdapter(this);
    }

    public ArrayList<EDSV2TVSeriesMediaItem> getRelated() {
        return ((EDSV2TVSeasonBrowseEpisodeModel) this.mediaModel).getRelated();
    }
}
