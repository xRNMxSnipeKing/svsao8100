package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.edsv2.EDSV2MovieDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MovieMediaItem;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;

public class MovieRelatedActivityViewModel extends AbstractRelatedActivityViewModel<EDSV2MovieDetailModel> {
    public MovieRelatedActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getMovieRelatedAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getMovieRelatedAdapter(this);
    }

    public ArrayList<EDSV2MovieMediaItem> getRelated() {
        return ((EDSV2MovieDetailModel) this.mediaModel).getRelated();
    }
}
