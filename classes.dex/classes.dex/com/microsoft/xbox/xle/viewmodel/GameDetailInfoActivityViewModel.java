package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.QuickplayModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2GameDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.edsv2.EDSV2RatingDescriptor;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.activity.GameRelatedActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;
import java.util.Iterator;

public class GameDetailInfoActivityViewModel extends EDSV2MediaItemDetailViewModel<EDSV2GameDetailModel> {
    private boolean isMyRecentGameAndUnavailableOnConsole;

    public GameDetailInfoActivityViewModel() {
        this.isMyRecentGameAndUnavailableOnConsole = false;
        this.adapter = AdapterFactory.getInstance().getGameDetailInfoAdapter(this);
        updateUnavailableOnConsoleFlag();
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getGameDetailInfoAdapter(this);
    }

    protected int getErrorStringResourceId() {
        return R.string.toast_game_detail_error;
    }

    public String getPublisher() {
        return ((EDSV2GameDetailModel) this.mediaModel).getPublisher();
    }

    public float getAverageUserRating() {
        return ((EDSV2GameDetailModel) this.mediaModel).getAverageUserRating();
    }

    public String getGameYearAndDeveloper() {
        return JavaUtil.concatenateStringsWithDelimiter(getReleaseYear(), getDeveloper(), null, XboxApplication.Resources.getString(R.string.comma_delimiter), false);
    }

    public String getGameYearAndPublisherAndDeveloper() {
        return JavaUtil.concatenateStringsWithDelimiter(getReleaseYear(), ((EDSV2GameDetailModel) this.mediaModel).getPublisher(), getDeveloper(), XboxApplication.Resources.getString(R.string.comma_delimiter), false);
    }

    public int getUserRatingCount() {
        return ((EDSV2GameDetailModel) this.mediaModel).getUserRatingCount();
    }

    public String getDeveloper() {
        return ((EDSV2GameDetailModel) this.mediaModel).getDeveloper();
    }

    public boolean shouldShowMediaProgressBar() {
        return false;
    }

    public boolean shouldShowProviderButtons() {
        return !NowPlayingGlobalModel.getInstance().isAppNowPlaying(getTitleId());
    }

    public boolean isNonXboxGame() {
        if (this.isMyRecentGameAndUnavailableOnConsole) {
            return true;
        }
        switch (((EDSV2GameDetailModel) this.mediaModel).getMediaType()) {
            case EDSV2MediaType.MEDIATYPE_XBOXORIGINALGAME /*21*/:
            case EDSV2MediaType.MEDIATYPE_WEBGAME /*57*/:
            case EDSV2MediaType.MEDIATYPE_MOBILEGAME /*58*/:
            case EDSV2MediaType.MEDIATYPE_METROGAME /*62*/:
                return true;
            default:
                return false;
        }
    }

    public String getDescription() {
        if (isNonXboxGame()) {
            return XboxApplication.Resources.getString(R.string.game_details_general_no_data_description);
        }
        return ((EDSV2GameDetailModel) this.mediaModel).getDescription();
    }

    public ArrayList<EDSV2RatingDescriptor> getRatingDescriptors() {
        return ((EDSV2GameDetailModel) this.mediaModel).getRatingDescriptors();
    }

    public String getRatingId() {
        return ((EDSV2GameDetailModel) this.mediaModel).getRatingId();
    }

    protected void onStartOverride() {
        super.onStartOverride();
        QuickplayModel.getInstance().addObserver(this);
    }

    protected void onStopOverride() {
        super.onStopOverride();
        QuickplayModel.getInstance().removeObserver(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        if (asyncResult.getException() != null && ((UpdateData) asyncResult.getResult()).getIsFinal() && ((UpdateData) asyncResult.getResult()).getUpdateType() == UpdateType.RecentsData) {
            updateUnavailableOnConsoleFlag();
        }
        super.updateOverride(asyncResult);
    }

    protected void addRelatedScreenToDetailsPivot() {
        XLELog.Diagnostic("ViewModelBase", "Adding GameRelatedActivity to details pivot");
        addScreenToDetailsPivot(GameRelatedActivity.class);
    }

    protected boolean shouldAddActivitiesPane() {
        return getHasActivities();
    }

    private void updateUnavailableOnConsoleFlag() {
        ArrayList<Title> allRecentGames = QuickplayModel.getInstance().getGamesQuickplayList();
        if (allRecentGames != null) {
            Iterator i$ = allRecentGames.iterator();
            while (i$.hasNext()) {
                Title myGame = (Title) i$.next();
                if (myGame.getTitleId() == getTitleId() && !myGame.isTitleAvailableOnConsole) {
                    this.isMyRecentGameAndUnavailableOnConsole = true;
                    return;
                }
            }
        }
        this.isMyRecentGameAndUnavailableOnConsole = false;
    }
}
