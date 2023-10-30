package com.microsoft.xbox.xle.viewmodel;

import android.os.Bundle;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.FriendsModel;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.YouProfileModel;
import com.microsoft.xbox.service.model.serialization.Friend;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.FullProfileActivity;
import com.microsoft.xbox.xle.app.activity.TabletProfileActivity;
import com.microsoft.xbox.xle.app.activity.YouPivotActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import com.microsoft.xbox.xle.app.adapter.SearchGamerActivityAdapter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.regex.Pattern;

public class SearchGamerActivityViewModel extends ViewModelBase {
    private static final String GAMERTAG_REGEX = "^([A-Za-z][A-Za-z0-9 ]{0,14})$";
    private static final int MAX_GAMERTAG_LENGTH = 15;
    private final String KEY_GAMERTAG;
    private String enteredGamertag;
    private ArrayList<FriendItem> filteredFriends;
    private ArrayList<FriendItem> friendsList;
    private boolean isSearchingGamer;
    private YouProfileModel youProfileModel;

    public SearchGamerActivityViewModel() {
        this.KEY_GAMERTAG = "SearchGamer_Gamertag";
        this.filteredFriends = new ArrayList();
        this.friendsList = new ArrayList();
        this.adapter = AdapterFactory.getInstance().getSearchGamerAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getSearchGamerAdapter(this);
    }

    public ArrayList<FriendItem> getFilteredFriends() {
        return this.filteredFriends;
    }

    public String getEnteredGamertag() {
        return this.enteredGamertag;
    }

    public boolean isBusy() {
        return this.youProfileModel != null ? this.youProfileModel.getIsLoading() : false;
    }

    public boolean isBlockingBusy() {
        return (this.youProfileModel != null && this.youProfileModel.getIsLoading()) || this.isSearchingGamer;
    }

    public String getBlockingStatusText() {
        return XLEApplication.Resources.getString(R.string.loading);
    }

    public void onGamertagEntryChanged(String enteredText) {
        this.enteredGamertag = enteredText;
        this.filteredFriends.clear();
        if (enteredText.length() > 0) {
            Iterator i$ = this.friendsList.iterator();
            while (i$.hasNext()) {
                FriendItem friend = (FriendItem) i$.next();
                if (friend.getGamertag().toLowerCase().startsWith(enteredText.toLowerCase())) {
                    this.filteredFriends.add(friend);
                }
            }
        }
        this.adapter.updateView();
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case YouProfileData:
                if (asyncResult.getException() == null) {
                    if (this.youProfileModel.getGamertag() != null && this.youProfileModel.getGamertag().length() > 0 && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
                        this.isSearchingGamer = false;
                        navigateToYouProfile(this.youProfileModel.getGamertag());
                        break;
                    }
                }
                this.isSearchingGamer = false;
                break;
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.YouProfileData, XLEErrorCode.FAILED_TO_GET_YOU_PROFILE)) {
            XboxMobileOmnitureTracking.TrackSearchFriendNoResults("Friend", this.enteredGamertag);
            showError(R.string.search_gamer_dialog_error_no_gamer);
        }
        super.onUpdateFinished();
    }

    private void updateFriendsList() {
        if (FriendsModel.getModel().getFriendsList() != null) {
            Iterator i$ = FriendsModel.getModel().getFriendsList().iterator();
            while (i$.hasNext()) {
                this.friendsList.add(new FriendItem((Friend) i$.next()));
            }
        }
    }

    public boolean beginSearchGamer(String youGamertag) {
        if (!isValidGamertag(youGamertag)) {
            showError(R.string.search_gamer_dialog_error_invalid_gamertag);
        } else if (!youGamertag.equalsIgnoreCase(MeProfileModel.getModel().getGamertag())) {
            this.isSearchingGamer = true;
            if (this.youProfileModel != null) {
                this.youProfileModel.removeObserver(this);
            }
            this.youProfileModel = YouProfileModel.getModel(youGamertag);
            this.youProfileModel.addObserver(this);
            setUpdateTypesToCheck(EnumSet.of(UpdateType.YouProfileData));
            this.youProfileModel.load(false);
            XboxMobileOmnitureTracking.TrackFriendSearch();
        } else if (XLEApplication.Instance.getIsTablet()) {
            navigateToYouProfile(youGamertag);
        } else {
            NavigateTo(FullProfileActivity.class, false);
        }
        return this.isSearchingGamer;
    }

    public void navigateToYouProfile(String youGamertag) {
        String str = "You gamertag must not be empty.";
        boolean z = youGamertag != null && youGamertag.length() > 0;
        XLEAssert.assertTrue(str, z);
        XLEGlobalData.getInstance().setSelectedGamertag(youGamertag);
        XLELog.Info("SearchGamerActivityViewModel", String.format("Navigating to you profile pivot for gamertag=%s", new Object[]{youGamertag}));
        if (XLEApplication.Instance.getIsTablet()) {
            NavigateTo(TabletProfileActivity.class, false);
        } else {
            NavigateTo(YouPivotActivity.class, false);
        }
    }

    protected void onStartOverride() {
        boolean z = true;
        String str = "MeProfileModel should have been loaded.";
        boolean z2 = MeProfileModel.getModel().getGamertag() != null && MeProfileModel.getModel().getGamertag().length() > 0;
        XLEAssert.assertTrue(str, z2);
        String str2 = "FriendsModel should have been loaded.";
        if (FriendsModel.getModel().getFriendsList() == null) {
            z = false;
        }
        XLEAssert.assertTrue(str2, z);
        XLEAssert.assertFalse("FriendsModels hould not be loading right now.", FriendsModel.getModel().getIsLoading());
        updateFriendsList();
    }

    protected void onStopOverride() {
        if (this.youProfileModel != null) {
            this.youProfileModel.removeObserver(this);
            this.youProfileModel = null;
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putString("SearchGamer_Gamertag", this.enteredGamertag);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.enteredGamertag = savedInstanceState.getString("SearchGamer_Gamertag");
            ((SearchGamerActivityAdapter) this.adapter).setGamertagText(this.enteredGamertag);
        }
    }

    public void load(boolean forceRefresh) {
    }

    private boolean isValidGamertag(String gamertag) {
        if (gamertag == null || gamertag.length() == 0 || gamertag.length() > 15) {
            return false;
        }
        return Pattern.matches(GAMERTAG_REGEX, gamertag);
    }
}
