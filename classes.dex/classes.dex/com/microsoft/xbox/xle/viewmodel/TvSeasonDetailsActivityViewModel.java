package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVEpisodeMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVSeasonBrowseEpisodeModel;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class TvSeasonDetailsActivityViewModel extends EDSV2MediaItemListViewModel<EDSV2TVSeasonBrowseEpisodeModel> {
    private static final String SEASON_TEXT = XLEApplication.Resources.getString(R.string.tv_season_details_season);

    public TvSeasonDetailsActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getTvSeasonDetailsAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getTvSeasonDetailsAdapter(this);
    }

    protected int getErrorStringResourceId() {
        return R.string.toast_tv_seasons_error;
    }

    public String getTvSeriesName() {
        return ((EDSV2TVSeasonBrowseEpisodeModel) this.mediaModel).getSeriesTitle();
    }

    public String getSeasonsHeader() {
        return SEASON_TEXT + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + ((EDSV2TVSeasonBrowseEpisodeModel) this.mediaModel).getSeasonNumber();
    }

    public ArrayList<EDSV2TVEpisodeMediaItem> getEpisodes() {
        return ((EDSV2TVSeasonBrowseEpisodeModel) this.mediaModel).getMediaItemListData();
    }

    public void NavigateToTvEpisodeDetails(EDSV2TVEpisodeMediaItem item) {
        navigateToAppOrMediaDetails(item);
    }
}
