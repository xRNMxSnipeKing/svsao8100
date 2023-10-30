package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.edsv2.EDSV2GameDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2GameMediaItem;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;

public class GameRelatedActivityViewModel extends AbstractRelatedActivityViewModel<EDSV2GameDetailModel> {
    public GameRelatedActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getGameRelatedAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getGameRelatedAdapter(this);
    }

    public ArrayList<EDSV2GameMediaItem> getRelated() {
        return ((EDSV2GameDetailModel) this.mediaModel).getRelated();
    }
}
