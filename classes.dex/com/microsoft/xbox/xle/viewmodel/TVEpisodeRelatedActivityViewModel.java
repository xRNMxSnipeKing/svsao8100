package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.edsv2.EDSV2TVEpisodeDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVSeriesMediaItem;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;

public class TVEpisodeRelatedActivityViewModel extends AbstractRelatedActivityViewModel<EDSV2TVEpisodeDetailModel> {
    public TVEpisodeRelatedActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getTVEpisodeRelatedAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getTVEpisodeRelatedAdapter(this);
    }

    public ArrayList<EDSV2TVSeriesMediaItem> getRelated() {
        return ((EDSV2TVEpisodeDetailModel) this.mediaModel).getRelated();
    }
}
