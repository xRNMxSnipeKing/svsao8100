package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.toolkit.ApplicationSettingManager;
import com.microsoft.xbox.toolkit.SoundManager;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.AboutActivity;
import com.microsoft.xbox.xle.app.activity.PrivacyActivity;
import com.microsoft.xbox.xle.app.activity.WhatsNewActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;

public class SettingsActivityViewModel extends ViewModelBase {
    public SettingsActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getSettingsAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getSettingsAdapter(this);
    }

    protected void onStartOverride() {
    }

    protected void onStopOverride() {
    }

    public boolean isBusy() {
        return false;
    }

    public void load(boolean forceRefresh) {
    }

    public void setSoundStatus(boolean status) {
        SoundManager.getInstance().setEnabled(status);
        ApplicationSettingManager.getInstance().saveSoundStatus(status);
        XboxMobileOmnitureTracking.TrackChangeSetting("Sound", Boolean.toString(status));
    }

    public boolean getSoundStatus() {
        return ApplicationSettingManager.getInstance().getSoundStatus();
    }

    public void setAutoLaunchSmartGlassStatus(boolean isChecked) {
        ApplicationSettingManager.getInstance().setAutoLaunchSmartGlassStatus(isChecked);
        XboxMobileOmnitureTracking.TrackChangeSetting("AutoPlay", Boolean.toString(isChecked));
    }

    public boolean getAutoLaunchSmartGlassStatus() {
        return ApplicationSettingManager.getInstance().getAutoLaunchSmartGlassStatus();
    }

    public void signOut() {
        logOut(true);
    }

    public void navigateToPrivacySettings() {
        if (MeProfileModel.getModel().getIsParentallyControlled()) {
            showError(R.string.edit_profile_privacy_settings_parentally_controlled);
        } else {
            NavigateTo(PrivacyActivity.class);
        }
    }

    public void NavigateToWhatsNew() {
        NavigateTo(WhatsNewActivity.class);
    }

    public void NavigateToAboutScreen() {
        NavigateTo(AboutActivity.class);
    }
}
