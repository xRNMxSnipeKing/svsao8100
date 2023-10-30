package com.omniture;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import java.io.File;
import java.util.Hashtable;
import java.util.Locale;
import java.util.UUID;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class AppMeasurement extends AppMeasurementBaseSE {
    private static final String PREF_FILE_NAME = "APP_MEASUREMENT_CACHE";
    private static final String PREF_KEY = "APP_MEASUREMENT_VISITOR_ID";
    public static ChurnMeasurement churn = null;
    private static String visId = null;
    protected Context context;
    public DoPlugins doPlugins;
    public DoRequest doRequest;

    public interface DoPlugins {
        void doPlugins(AppMeasurement appMeasurement);
    }

    public interface DoRequest {
        boolean doRequest(AppMeasurement appMeasurement, String str, Hashtable hashtable);
    }

    protected boolean _hasDoPlugins() {
        return this.doPlugins != null;
    }

    protected void _doPlugins() {
        if (this.doPlugins != null) {
            this.doPlugins.doPlugins(this);
        }
    }

    protected boolean _hasDoRequest() {
        return this.doRequest != null;
    }

    protected boolean _doRequest(String url, Hashtable headers) {
        if (this.doRequest != null) {
            return this.doRequest.doRequest(this, url, headers);
        }
        return true;
    }

    public AppMeasurement() {
        this.doPlugins = null;
        this.doRequest = null;
        this.context = null;
        this.target = "AN";
    }

    public AppMeasurement(Context context) {
        this();
        this.context = context;
        if (this.context != null && s.offlineFilename == null) {
            s.offlineFilename = new File(s.context.getCacheDir(), "AppMeasurement.offline").getPath();
        }
        if (churn == null) {
            churn = new ChurnMeasurement(this);
        }
    }

    public void startActivity(Activity activity) {
        if (churn == null) {
            churn = new ChurnMeasurement(this);
        }
        churn.startActivity(this.context);
    }

    public void stopActivity() {
        churn.stopActivity(this.context);
    }

    public void logDebug(String msg) {
        Log.d("AppMeasurement", msg);
    }

    protected void handleTechnology() {
        try {
            DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
            s.resolution = displayMetrics.widthPixels + "x" + displayMetrics.heightPixels;
        } catch (Exception e) {
        }
    }

    protected String getDefaultUserAgent() {
        String acceptLanguage = getDefaultAcceptLanguage();
        String applicationID = getApplicationID();
        StringBuilder append = new StringBuilder().append("Mozilla/5.0 (Linux; U; Android ").append(VERSION.RELEASE).append("; ");
        if (!isSet(acceptLanguage)) {
            acceptLanguage = "en_US";
        }
        return append.append(acceptLanguage).append("; ").append(Build.MODEL).append(" Build/").append(Build.ID).append(")").append(isSet(applicationID) ? MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + applicationID : "").toString();
    }

    protected String getDefaultVisitorID() {
        if (visId == null) {
            if (loadPref(this.context) == null) {
                savePref(this.context, getUUID());
            }
            visId = loadPref(this.context);
        }
        if (visId == null && this.debugTracking) {
            logDebug("Unable to generate Visitor ID");
        }
        return visId;
    }

    static String loadPref(Context c) {
        return c.getSharedPreferences(PREF_FILE_NAME, 0).getString(PREF_KEY, null);
    }

    static void savePref(Context c, String uuid) {
        Editor prefs = c.getSharedPreferences(PREF_FILE_NAME, 0).edit();
        prefs.putString(PREF_KEY, uuid);
        prefs.commit();
    }

    private String getUUID() {
        String visUUID = null;
        try {
            TelephonyManager telephony = (TelephonyManager) this.context.getSystemService("phone");
            if (telephony != null) {
                visUUID = telephony.getSubscriberId();
                if (visUUID == null) {
                    visUUID = telephony.getDeviceId();
                }
            }
        } catch (Exception e) {
            visUUID = null;
        }
        if (visUUID == null || visUUID.length() < 1) {
            return UUID.randomUUID().toString().replace("-", "");
        }
        return visUUID;
    }

    protected String getDefaultAcceptLanguage() {
        try {
            Locale currentLocale = this.context.getResources().getConfiguration().locale;
            return currentLocale.getLanguage() + '-' + currentLocale.getCountry().toLowerCase();
        } catch (Exception e) {
            return null;
        }
    }

    protected String getApplicationID() {
        try {
            PackageManager packageManager = this.context.getPackageManager();
            String applicationName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(s.context.getPackageName(), 0));
            String applicationVersion = packageManager.getPackageInfo(s.context.getPackageName(), 0).versionName;
            if (isSet(applicationName)) {
                return applicationName + (isSet(applicationVersion) ? "/" + applicationVersion : "");
            }
        } catch (Exception e) {
        }
        return null;
    }
}
