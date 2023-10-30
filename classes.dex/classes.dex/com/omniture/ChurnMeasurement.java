package com.omniture;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;

public class ChurnMeasurement extends ChurnMeasurementBase {
    private static final String PREFS_NAME = "APP_MEASUREMENT_CACHE";
    private static String currentContextString = null;
    protected Editor editor;
    private AppMeasurement measurement = null;
    protected SharedPreferences prefs;

    protected ChurnMeasurement(AppMeasurement measurementObj) {
        super(measurementObj);
        this.measurement = measurementObj;
    }

    protected static void setCurrentContextString(String contextString) {
        currentContextString = contextString;
    }

    protected void startActivity(Context context) {
        this.prefs = this.measurement.context.getSharedPreferences(PREFS_NAME, 0);
        this.editor = this.prefs.edit();
        setCurrentContextString(prefsGetString("currentContext", null));
        prefsPutString("currentContext", context.toString());
        String string = context.getApplicationInfo().packageName + "open";
        if (prefsGetBool(string, false) && !context.toString().equals(currentContextString)) {
            prefsPutBool("ADMS_SuccessfulClose", false);
        }
        prefsPutBool(string, true);
        startSession(this.measurement.account);
    }

    protected void stopActivity(Context context) {
        prefsPutBool(context.getApplicationInfo().packageName + "open", false);
        stopSession(this.measurement.account);
    }

    protected void setGenericVariables() {
        super.setGenericVariables();
        setVariable("Android " + getAndroidVersion(), this.appEnvironmentEvar);
    }

    protected int prefsGetInt(String key, int defaultValue) {
        return this.prefs.getInt(key, defaultValue);
    }

    protected void prefsPutInt(String key, int value) {
        this.editor.putInt(key, value);
        this.editor.commit();
    }

    protected long prefsGetLong(String key, long defaultValue) {
        return this.prefs.getLong(key, defaultValue);
    }

    protected void prefsPutLong(String key, long value) {
        this.editor.putLong(key, value);
        this.editor.commit();
    }

    protected boolean prefsGetBool(String key, boolean defaultValue) {
        return this.prefs.getBoolean(key, defaultValue);
    }

    protected void prefsPutBool(String key, boolean value) {
        this.editor.putBoolean(key, value);
        this.editor.commit();
    }

    protected String prefsGetString(String key, String defaultValue) {
        return this.prefs.getString(key, defaultValue);
    }

    protected void prefsPutString(String key, String value) {
        this.editor.putString(key, value);
        this.editor.commit();
    }

    protected boolean prefsContains(String key) {
        return this.prefs.contains(key);
    }

    protected String getApplicationVersion() {
        try {
            return this.measurement.context.getPackageManager().getPackageInfo(this.measurement.context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    protected String getApplicationName() {
        try {
            PackageManager packageManager = this.measurement.context.getPackageManager();
            return (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.measurement.context.getPackageName(), 0));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void removeObjectFromPrefsWithKey(String key) {
        this.editor.remove(key);
        this.editor.commit();
    }

    private String getAndroidVersion() {
        switch (VERSION.SDK_INT) {
            case 0:
                return "BASE";
            case 1:
                return "1.0";
            case 2:
                return "1.1";
            case 3:
                return "1.5";
            case 4:
                return "1.6";
            case 5:
                return "2.0";
            case 6:
                return "2.0.1";
            case 7:
                return "2.1";
            case 8:
                return "2.2";
            case 9:
            case 10:
                return "2.3";
            case 11:
                return "3.0";
            case CompanionSession.LRCERROR_TOO_MANY_CLIENTS /*12*/:
                return "3.1";
            case CompanionSession.LRCERROR_EXPIRED_COMMAND /*13*/:
                return "3.2";
            case CompanionSession.LRCERROR_NO_EXCLUSIVE /*14*/:
            case 15:
                return "4.0";
            default:
                return "Unknown";
        }
    }
}
