package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.edsv2.EDSV2MovieDetailModel;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.activity.MovieRelatedActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class MovieDetailsActivityViewModel extends EDSV2MediaItemDetailViewModel<EDSV2MovieDetailModel> {
    public MovieDetailsActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getMovieDetailsAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getMovieDetailsAdapter(this);
    }

    protected int getErrorStringResourceId() {
        return R.string.toast_movie_details_list_error;
    }

    public String getMovieReleaseData() {
        return JavaUtil.concatenateStringsWithDelimiter(getReleaseYear(), getParentalRating(), getDurationInMinutes() == 0 ? null : getDurationInMinutes() + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + XboxApplication.Resources.getString(R.string.details_minutes_abbreviation), XboxApplication.Resources.getString(R.string.comma_delimiter), false);
    }

    public String getMovieReleaseParentRatingStudioDuration() {
        String string = XboxApplication.Resources.getString(R.string.comma_delimiter);
        String[] strArr = new String[4];
        strArr[0] = getReleaseYear();
        strArr[1] = getParentalRating();
        strArr[2] = ((EDSV2MovieDetailModel) this.mediaModel).getStudio();
        strArr[3] = getDurationInMinutes() == 0 ? null : getDurationInMinutes() + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + XboxApplication.Resources.getString(R.string.details_minutes_abbreviation);
        return JavaUtil.concatenateStringsWithDelimiter(string, false, strArr);
    }

    public String getStudio() {
        return ((EDSV2MovieDetailModel) this.mediaModel).getStudio();
    }

    public int getMetaCriticReviewScore() {
        return (int) ((EDSV2MovieDetailModel) this.mediaModel).getMetaCriticReviewScore();
    }

    protected boolean shouldAddActivitiesPane() {
        if (getMediaType() == EDSV2MediaType.MEDIATYPE_MUSICVIDEO) {
            return false;
        }
        return true;
    }

    protected boolean shouldLoadActivities() {
        if (getMediaType() == EDSV2MediaType.MEDIATYPE_MUSICVIDEO) {
            return false;
        }
        return true;
    }

    protected void addRelatedScreenToDetailsPivot() {
        XLELog.Diagnostic("ViewModelBase", "Adding MovieRelatedActivity to details pivot");
        addScreenToDetailsPivot(MovieRelatedActivity.class);
    }
}
