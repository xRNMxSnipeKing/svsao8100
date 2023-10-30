package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVEpisodeDetailModel;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.activity.TVEpisodeRelatedActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.IllegalFormatException;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class TvEpisodeDetailsActivityViewModel extends EDSV2MediaItemDetailViewModel<EDSV2TVEpisodeDetailModel> {
    private static final String SEASON_TEXT = XLEApplication.Resources.getString(R.string.tv_season_details_season);

    public TvEpisodeDetailsActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getTvEpisodeDetailsAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getTvEpisodeDetailsAdapter(this);
    }

    protected int getErrorStringResourceId() {
        return R.string.toast_tv_episode_details_list_error;
    }

    public String getTitle() {
        if (((EDSV2TVEpisodeDetailModel) this.mediaModel).getMediaType() == EDSV2MediaType.MEDIATYPE_TVSHOW) {
            return ((EDSV2TVEpisodeDetailModel) this.mediaModel).getTitle();
        }
        return JavaUtil.concatenateStringsWithDelimiter(((EDSV2TVEpisodeDetailModel) this.mediaModel).getSeriesTitle(), String.format("%s %s", new Object[]{SEASON_TEXT, Integer.valueOf(((EDSV2TVEpisodeDetailModel) this.mediaModel).getSeasonNumber())}), null, XboxApplication.Resources.getString(R.string.colon_delimiter));
    }

    public String getTvEpisodeName() {
        return ((EDSV2TVEpisodeDetailModel) this.mediaModel).getTitle();
    }

    public String getYearRatingDuration() {
        try {
            return JavaUtil.concatenateStringsWithDelimiter(getReleaseYear(), getParentalRating(), getDurationInMinutes() == 0 ? null : getDurationInMinutes() + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + XboxApplication.Resources.getString(R.string.details_minutes_abbreviation), XboxApplication.Resources.getString(R.string.comma_delimiter), false);
        } catch (IllegalFormatException e) {
            return "";
        }
    }

    public String getMonthDateYearRatingDuration() {
        try {
            return JavaUtil.concatenateStringsWithDelimiter(getReleaseDate(), getParentalRating(), getDurationInMinutes() == 0 ? null : getDurationInMinutes() + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + XboxApplication.Resources.getString(R.string.details_minutes_abbreviation), XboxApplication.Resources.getString(R.string.comma_delimiter), false);
        } catch (IllegalFormatException e) {
            return "";
        }
    }

    protected boolean shouldAddActivitiesPane() {
        return true;
    }

    protected boolean shouldLoadActivities() {
        return true;
    }

    protected void addRelatedScreenToDetailsPivot() {
        XLELog.Diagnostic("ViewModelBase", "Adding TVEpisodeRelatedActivity to details pivot");
        addScreenToDetailsPivot(TVEpisodeRelatedActivity.class);
    }
}
