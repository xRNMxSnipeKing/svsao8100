package com.microsoft.xbox.xle.viewmodel;

import android.os.Bundle;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.serialization.ProfileProperties;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.activity.PrivacyActivity;
import com.microsoft.xbox.xle.app.adapter.EditProfileActivityAdapter;
import java.util.EnumSet;

public class EditProfileActivityViewModel extends ViewModelBase {
    private String bio;
    private String busyText;
    private String gamertag;
    private boolean isDirty;
    private final String keyBio;
    private final String keyLocation;
    private final String keyMotto;
    private final String keyName;
    private final String keySaveEnabled;
    private String location;
    private String motto;
    private String name;
    private String privacySettingsDescription;
    private MeProfileModel profileModel;

    public EditProfileActivityViewModel() {
        this.keyMotto = "EditProfile_Motto";
        this.keyName = "EditProfile_Name";
        this.keyLocation = "EditProfile_Location";
        this.keyBio = "EditProfile_Bio";
        this.keySaveEnabled = "EditProfile_SaveEnabled";
        this.adapter = new EditProfileActivityAdapter(this);
        this.busyText = XLEApplication.Resources.getString(R.string.blocking_status_updating);
    }

    public void onRehydrate() {
        this.adapter = new EditProfileActivityAdapter(this);
    }

    public boolean isBusy() {
        return this.profileModel.getIsLoading();
    }

    public boolean isBlockingBusy() {
        return this.profileModel.getIsSaving();
    }

    public String getBlockingStatusText() {
        return this.busyText;
    }

    public String getMotto() {
        return this.motto;
    }

    public String getGamertag() {
        return this.gamertag;
    }

    public String getName() {
        return this.name;
    }

    public String getLocation() {
        return this.location;
    }

    public String getBio() {
        return this.bio;
    }

    public String getPrivacySettingsDescription() {
        return this.privacySettingsDescription;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setIsDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public boolean getIsDirty() {
        return this.isDirty;
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case MeProfileDataSave:
                if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    goBack();
                    return;
                }
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.MeProfileDataSave, XLEErrorCode.FAILED_TO_SAVE_PROFILE)) {
            showError(R.string.toast_editprofile_save_error);
        } else if (checkErrorCode(UpdateType.MeProfileDataSave, XLEErrorCode.FAILED_TO_SAVE_PROFILE_OFFENSIVE)) {
            showError(R.string.toast_editprofile_save_offensive);
        }
        super.onUpdateFinished();
    }

    public void updatePrivacySettingsDescription() {
        String description = "";
        switch (this.profileModel.getShareProfileSetting()) {
            case 0:
                description = XLEApplication.Resources.getString(R.string.edit_profile_privacy_settings_description_everyone);
                break;
            case 1:
                description = XLEApplication.Resources.getString(R.string.edit_profile_privacy_settings_description_friends);
                break;
            case 2:
                description = XLEApplication.Resources.getString(R.string.edit_profile_privacy_settings_description_blocked);
                break;
        }
        this.privacySettingsDescription = description;
    }

    public void load(boolean forceRefresh) {
    }

    protected void onStartOverride() {
        this.profileModel = MeProfileModel.getModel();
        this.profileModel.addObserver(this);
        this.gamertag = this.profileModel.getGamertag();
        setMotto(this.profileModel.getMotto());
        setName(this.profileModel.getName());
        setLocation(this.profileModel.getLocation());
        setBio(this.profileModel.getBio());
        updatePrivacySettingsDescription();
        this.adapter.loadInitialDataFromVM();
    }

    protected void onStopOverride() {
        this.profileModel.removeObserver(this);
        this.profileModel = null;
    }

    public void onSaveInstanceState(Bundle outState) {
        this.adapter.saveEditTextToVM();
        if (outState != null) {
            outState.putString("EditProfile_Motto", getMotto());
            outState.putString("EditProfile_Name", getName());
            outState.putString("EditProfile_Location", getLocation());
            outState.putString("EditProfile_Bio", getBio());
            outState.putBoolean("EditProfile_SaveEnabled", this.isDirty);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        boolean saveEnabled = false;
        if (savedInstanceState != null) {
            setMotto(savedInstanceState.getString("EditProfile_Motto"));
            setName(savedInstanceState.getString("EditProfile_Name"));
            setLocation(savedInstanceState.getString("EditProfile_Location"));
            setBio(savedInstanceState.getString("EditProfile_Bio"));
            saveEnabled = savedInstanceState.getBoolean("EditProfile_SaveEnabled");
        }
        this.adapter.loadInitialDataFromVM();
        setIsDirty(saveEnabled);
        this.adapter.updateView();
    }

    public void save() {
        XLEAssert.assertTrue("Save button should've been disabled.", this.isDirty);
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MeProfileDataSave));
        ProfileProperties newData = new ProfileProperties();
        newData.Motto = getMotto();
        newData.Name = getName();
        newData.Location = getLocation();
        newData.Bio = getBio();
        if (this.profileModel.uploadNewProfileData(newData)) {
            this.adapter.updateView();
        }
    }

    public void onBackButtonPressed() {
        cancel();
    }

    public void cancel() {
        if (this.isDirty) {
            showDiscardChangesGoBack();
        } else {
            goBack();
        }
    }

    public void navigateToPrivacySettings() {
        if (this.profileModel.getIsParentallyControlled()) {
            showError(R.string.edit_profile_privacy_settings_parentally_controlled);
        } else if (this.isDirty) {
            showDiscardChangeNavigate(PrivacyActivity.class);
        } else {
            NavigateTo(PrivacyActivity.class);
        }
    }
}
