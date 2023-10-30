package com.microsoft.xbox.xle.viewmodel;

import android.os.Bundle;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.QuickplayModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2AppMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2GameMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import com.microsoft.xbox.xle.app.adapter.SearchGameHistoryActivityAdapter;
import java.util.ArrayList;

public class SearchGameHistoryActivityViewModel extends ViewModelBase {
    private final String KEY_GAMETITLE;
    private String enteredTitle;
    private ArrayList<Title> filteredTitles;
    private QuickplayModel titleHistoryModel;

    public SearchGameHistoryActivityViewModel() {
        this.KEY_GAMETITLE = "SearchGameHistory_Title";
        this.filteredTitles = new ArrayList();
        this.adapter = AdapterFactory.getInstance().getSearchGameHistoryAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getSearchGameHistoryAdapter(this);
    }

    public ArrayList<Title> getFilteredTitles() {
        return this.filteredTitles;
    }

    public String getEnteredTitle() {
        return this.enteredTitle;
    }

    public boolean getIsEmpty() {
        return !JavaUtil.isNullOrEmpty(this.enteredTitle) && (this.filteredTitles == null || this.filteredTitles.size() == 0);
    }

    public boolean isBusy() {
        return this.titleHistoryModel != null ? this.titleHistoryModel.getIsLoading() : false;
    }

    public void onGameTitleEntryChanged(String enteredText) {
        this.enteredTitle = enteredText;
        updateTitlesList();
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case RecentsData:
                if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal() && this.titleHistoryModel.getAllQuickplayList() != null) {
                    updateTitlesList();
                    break;
                }
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.RecentsData, XLEErrorCode.FAILED_TO_GET_QUICKPLAY_SUMMARY)) {
            showError(R.string.toast_profile_error);
        }
        super.onUpdateFinished();
    }

    private void updateTitlesList() {
        if (this.titleHistoryModel.getAllQuickplayList() != null) {
            this.filteredTitles.clear();
            if (this.enteredTitle != null && this.enteredTitle.length() > 0) {
                for (Title title : this.titleHistoryModel.getAllQuickplayList()) {
                    if (title.getName() != null && title.getName().toLowerCase().startsWith(this.enteredTitle.toLowerCase())) {
                        this.filteredTitles.add(title);
                    }
                }
            }
            this.adapter.updateView();
        }
    }

    protected void onStartOverride() {
        boolean z = true;
        String str = "MeProfileModel should have been loaded.";
        boolean z2 = MeProfileModel.getModel().getGamertag() != null && MeProfileModel.getModel().getGamertag().length() > 0;
        XLEAssert.assertTrue(str, z2);
        this.titleHistoryModel = QuickplayModel.getInstance();
        String str2 = "GamesModel should have been loaded.";
        if (this.titleHistoryModel.getAllQuickplayList() == null) {
            z = false;
        }
        XLEAssert.assertTrue(str2, z);
        this.titleHistoryModel.addObserver(this);
    }

    protected void onStopOverride() {
        this.titleHistoryModel.removeObserver(this);
        this.titleHistoryModel = null;
    }

    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putString("SearchGameHistory_Title", this.enteredTitle);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.enteredTitle = savedInstanceState.getString("SearchGameHistory_Title");
            ((SearchGameHistoryActivityAdapter) this.adapter).setTitleText(this.enteredTitle);
        }
    }

    public void load(boolean forceRefresh) {
        this.titleHistoryModel.load(forceRefresh);
    }

    public void navigateToAchievementsOrTitleDetail(Title title) {
        XLELog.Info("SearchGameHistoryActivityViewModel", "Navigating to title detail.");
        if (title.IsGame()) {
            navigateToAchievements(new EDSV2GameMediaItem(title), false);
        } else {
            navigateToAppOrMediaDetails((EDSV2MediaItem) new EDSV2AppMediaItem(title), false);
        }
    }
}
