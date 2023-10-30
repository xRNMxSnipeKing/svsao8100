package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2GameContentDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2RatingDescriptor;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;

public class GameContentDetailActivityViewModel extends EDSV2MediaItemDetailViewModel<EDSV2GameContentDetailModel> {
    public GameContentDetailActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getGameContentActivityAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getGameContentActivityAdapter(this);
    }

    protected int getErrorStringResourceId() {
        return R.string.toast_game_content_error;
    }

    public String getPublisher() {
        return ((EDSV2GameContentDetailModel) this.mediaModel).getPublisher();
    }

    public float getAverageUserRating() {
        return ((EDSV2GameContentDetailModel) this.mediaModel).getAverageUserRating();
    }

    public int getUserRatingCount() {
        return ((EDSV2GameContentDetailModel) this.mediaModel).getUserRatingCount();
    }

    public String getDeveloper() {
        return ((EDSV2GameContentDetailModel) this.mediaModel).getDeveloper();
    }

    public ArrayList<EDSV2RatingDescriptor> getRatingDescriptors() {
        return ((EDSV2GameContentDetailModel) this.mediaModel).getRatingDescriptors();
    }

    public String getRatingId() {
        return ((EDSV2GameContentDetailModel) this.mediaModel).getRatingId();
    }

    public boolean shouldShowMediaProgressBar() {
        return false;
    }

    public boolean shouldShowProviderButtons() {
        return true;
    }

    protected boolean shouldAddActivitiesPane() {
        return false;
    }
}
