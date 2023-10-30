package com.microsoft.xbox.toolkit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.UUID;

public class ApplicationSettingManager {
    private static final String GUID = "GUID";
    private static final String autoLaunchSmartGlassStatus = "autoLaunchSmartGlassStatus";
    private static final String fileName = "xlesetting";
    private static ApplicationSettingManager instance = new ApplicationSettingManager();
    private static final String lastShowWhatsNewVersionCode = "showWhatsNewVersionCode";
    private static final String soundStatus = "hasSound";
    private static final String systemCheckStatus = "hasSystemCheck";
    private Context context = XboxApplication.Instance.getApplicationContext();
    private SharedPreferences preferences;

    private ApplicationSettingManager() {
    }

    public static ApplicationSettingManager getInstance() {
        return instance;
    }

    public void saveSoundStatus(boolean status) {
        this.preferences = this.context.getSharedPreferences(fileName, 0);
        Editor editor = this.preferences.edit();
        editor.putBoolean(soundStatus, status);
        editor.commit();
    }

    public boolean getSoundStatus() {
        this.preferences = this.context.getSharedPreferences(fileName, 0);
        return this.preferences.getBoolean(soundStatus, true);
    }

    public void saveSystemCheckStatus(boolean isFirst) {
        this.preferences = this.context.getSharedPreferences(fileName, 0);
        Editor editor = this.preferences.edit();
        editor.putBoolean(systemCheckStatus, isFirst);
        editor.commit();
    }

    public boolean getSystemCheckStatus() {
        this.preferences = this.context.getSharedPreferences(fileName, 0);
        return this.preferences.getBoolean(systemCheckStatus, false);
    }

    public void setShowWhatsNewLastVersionCode(int versionCode) {
        this.preferences = this.context.getSharedPreferences(fileName, 0);
        Editor editor = this.preferences.edit();
        editor.putInt(lastShowWhatsNewVersionCode, versionCode);
        editor.commit();
    }

    public boolean getAutoLaunchSmartGlassStatus() {
        this.preferences = this.context.getSharedPreferences(fileName, 0);
        return this.preferences.getBoolean(autoLaunchSmartGlassStatus, true);
    }

    public void setAutoLaunchSmartGlassStatus(boolean flag) {
        this.preferences = this.context.getSharedPreferences(fileName, 0);
        Editor editor = this.preferences.edit();
        editor.putBoolean(autoLaunchSmartGlassStatus, flag);
        editor.commit();
    }

    public int getShowWhatsNewLastVersionCode() {
        this.preferences = this.context.getSharedPreferences(fileName, 0);
        return this.preferences.getInt(lastShowWhatsNewVersionCode, -1);
    }

    public String getGUID() {
        this.preferences = this.context.getSharedPreferences(fileName, 0);
        String guid = this.preferences.getString(GUID, null);
        if (guid != null) {
            return guid;
        }
        createAndSaveGUID();
        return this.preferences.getString(GUID, null);
    }

    private void createAndSaveGUID() {
        this.preferences = this.context.getSharedPreferences(fileName, 0);
        Editor editor = this.preferences.edit();
        editor.putString(GUID, UUID.randomUUID().toString());
        editor.commit();
    }
}
