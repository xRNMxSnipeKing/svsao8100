package com.omniture;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

public class ChurnMeasurementBase {
    private static final int kADMS_ConfigTypeInstall = 0;
    private static final int kADMS_ConfigTypeLaunch = 2;
    private static final int kADMS_ConfigTypeUpgrade = 1;
    private static final int kADMS_DefaultBackgroundSessionTimeout = 15;
    private static final int kADMS_DefaultPauseSessionTimeout = 15;
    private static final String kADMS_InstallDate = "ADMS_InstallDate";
    private static final String kADMS_LastDateUsed = "ADMS_LastDateUsed";
    private static final String kADMS_LastVersion = "ADMS_LastVersion";
    private static final String kADMS_Launches = "ADMS_Launches";
    private static final String kADMS_LaunchesAfterUpgrade = "ADMS_LaunchesAfterUpgrade";
    private static final String kADMS_PauseDate = "ADMS_PauseDate";
    private static final String kADMS_SessionOpen = "ADMS_SessionOpen";
    private static final String kADMS_SessionStart = "ADMS_SessionStart";
    protected static final String kADMS_SuccessfulClose = "ADMS_SuccessfulClose";
    private static final String kADMS_UpgradeDate = "ADMS_UpgradeDate";
    private static final String kAppCrashEvent = "event7";
    private static final String kAppEnvironmentVar = "eVar9";
    private static final String kAppIDProp = "prop1";
    private static final String kAppIDVar = "eVar2";
    private static final String kAppInstallDateVar = "eVar1";
    private static final String kAppInstallEventName = "event1";
    private static final String kAppLaunchEvent = "event5";
    private static final String kAppLaunchNumberProp = "prop2";
    private static final String kAppLaunchNumberSinceLastUpgradeProp = "prop3";
    private static final String kAppLaunchNumberSinceLastUpgradeVar = "eVar11";
    private static final String kAppLaunchNumberVar = "eVar6";
    private static final String kAppUpgradeEventName = "event2";
    private static final String kDailyEngagedUserEventName = "event3";
    private static final String kDayOfWeekVar = "eVar8";
    private static final String kDaysSinceFirstUseVar = "eVar4";
    private static final String kDaysSinceLastUpgradeVar = "eVar10";
    private static final String kDaysSinceLastUseVar = "eVar5";
    private static final String kEngagedDaysLastUpgradeVar = "eVar13";
    private static final String kEngagedDaysLifetimeVar = "eVar3";
    private static final String kEngagedDaysMonthVar = "eVar12";
    private static final String kHourOfDayVar = "eVar7";
    private static final String kMonthlyEngagedUserEventName = "event4";
    protected static final String kPrevSessionLengthVar = "eVar14";
    public String appCrashEvent = kAppCrashEvent;
    public String appEnvironmentEvar = kAppEnvironmentVar;
    private String appId = null;
    public String appIdEvar = kAppIDVar;
    public String appIdProp = kAppIDProp;
    public String appInstallDateEvar = kAppInstallDateVar;
    public String appInstallEvent = kAppInstallEventName;
    public String appLaunchEvent = kAppLaunchEvent;
    public String appLaunchNumberEvar = kAppLaunchNumberVar;
    public String appLaunchNumberProp = kAppLaunchNumberProp;
    public String appLaunchNumberSinceLastUpgradeEvar = kAppLaunchNumberSinceLastUpgradeVar;
    public String appLaunchNumberSinceLastUpgradeProp = kAppLaunchNumberSinceLastUpgradeProp;
    public String appUpgradeEvent = kAppUpgradeEventName;
    public int backgroundSessionTimeout = 15;
    private String currentAppVersion = null;
    private Date currentDate = null;
    public String dailyEngagedUserEvent = kDailyEngagedUserEventName;
    private DateFormat dayMonthYearFormat = new SimpleDateFormat("M/d/yyyy");
    private DateFormat dayOfWeekDateFormat = new SimpleDateFormat("EEEE");
    public String dayOfWeekEvar = kDayOfWeekVar;
    public String daysSinceFirstUseEvar = kDaysSinceFirstUseVar;
    public String daysSinceLastUpgradeEvar = kDaysSinceLastUpgradeVar;
    public String daysSinceLastUseEvar = kDaysSinceLastUseVar;
    public String engagedDaysLastUpgradeEvar = kEngagedDaysLastUpgradeVar;
    public String engagedDaysLifetimeEvar = kEngagedDaysLifetimeVar;
    public String engagedDaysMonthEvar = kEngagedDaysMonthVar;
    private ArrayList<String> eventList = new ArrayList();
    private DateFormat hourOfDayDateFormat = new SimpleDateFormat("H");
    public String hourOfDayEvar = kHourOfDayVar;
    private AppMeasurementBase measurementBase = null;
    private DateFormat monthYearDateFormat = new SimpleDateFormat("M/yyyy");
    public String monthlyEngagedUserEvent = kMonthlyEngagedUserEventName;
    public int pauseSessionTimeout = 15;
    public String previousSessionLengthEvar = kPrevSessionLengthVar;
    private Hashtable<String, String> variables = new Hashtable();

    protected ChurnMeasurementBase(AppMeasurementBase measurementObj) {
        this.measurementBase = measurementObj;
    }

    protected void startSession(String reportSuiteID) {
        if (sessionPauseDetected(reportSuiteID)) {
            prefsPutInt(kADMS_SessionOpen, 0);
            trackSessionStart(reportSuiteID);
            prefsPutBool(kADMS_SuccessfulClose, false);
        }
        openSession();
    }

    protected void stopSession(String reportSuiteID) {
        closeSession();
        putDateIntoPrefs(new Date(), kADMS_PauseDate + reportSuiteID);
        if (prefsGetInt(kADMS_SessionOpen, 0) == 0) {
            prefsPutBool(kADMS_SuccessfulClose, true);
        }
    }

    private void trackSessionStart(String reportSuiteID) {
        cleanInstanceVariables();
        setLaunchTypeVariables();
        setGenericVariables();
        handleEvents();
        handleSessionLength(reportSuiteID);
        this.measurementBase.trackLink(null, "o", "ADMS BP Event", this.variables);
        if (!prefsContains(kADMS_SessionStart)) {
            putDateIntoPrefs(this.currentDate, kADMS_SessionStart);
        }
        prefsPutString(kADMS_LastVersion, this.currentAppVersion);
    }

    private boolean sessionPauseDetected(String reportSuiteID) {
        if (!prefsGetBool(kADMS_SuccessfulClose, false)) {
            return true;
        }
        if (prefsGetInt(kADMS_SessionOpen, 0) > 0) {
            return false;
        }
        String pausePrefKey = kADMS_PauseDate + reportSuiteID;
        if (!prefsContains(pausePrefKey)) {
            return true;
        }
        Date lastPausedDate = getDateFromPrefs(pausePrefKey);
        if (lastPausedDate == null || secondsBetween(lastPausedDate, new Date()) >= this.pauseSessionTimeout) {
            return true;
        }
        return false;
    }

    private void handleSessionLength(String reportSuiteID) {
        String pausePrefKey = kADMS_PauseDate + reportSuiteID;
        if (prefsContains(pausePrefKey)) {
            Date lastPausedDate = getDateFromPrefs(pausePrefKey);
            if (lastPausedDate != null && secondsBetween(lastPausedDate, this.currentDate) > this.backgroundSessionTimeout) {
                int sessionTime = secondsBetween(getDateFromPrefs(kADMS_SessionStart), lastPausedDate);
                if (sessionTime > 0) {
                    "" + sessionTime;
                }
                removeObjectFromPrefsWithKey(kADMS_SessionStart);
            }
        }
    }

    private void setEvent(String event) {
        if (this.measurementBase.isSet(event)) {
            this.eventList.add(event);
        }
    }

    protected void setVariable(String value, String key) {
        if (this.measurementBase.isSet(value) && this.measurementBase.isSet(key)) {
            this.variables.put(key, value);
        }
    }

    private void cleanInstanceVariables() {
        this.variables = new Hashtable();
        this.eventList = new ArrayList();
        this.currentAppVersion = getApplicationVersion();
        this.appId = getApplicationName() + "(" + this.currentAppVersion + ")";
        this.currentDate = new Date();
    }

    private void setLaunchTypeVariables() {
        if (!prefsContains(kADMS_InstallDate)) {
            setInstallVariables();
            handleUpgradeDateForConfigType(0);
        } else if (!prefsContains(kADMS_LastVersion)) {
        } else {
            if (prefsGetString(kADMS_LastVersion, null).equalsIgnoreCase(this.currentAppVersion)) {
                setLaunchVariables();
                setNotInstallVariables();
                handleUpgradeDateForConfigType(2);
                return;
            }
            setUpgradeVariables();
            setNotInstallVariables();
            handleUpgradeDateForConfigType(1);
        }
    }

    private void handleUpgradeDateForConfigType(int configType) {
        if (prefsContains(kADMS_UpgradeDate)) {
            int newLaunchesAfterUpgrade = prefsGetInt(kADMS_LaunchesAfterUpgrade, 0) + 1;
            setVariable("" + newLaunchesAfterUpgrade, this.appLaunchNumberSinceLastUpgradeEvar);
            if (configType == 2) {
                setVariable("" + daysBetween(getDateFromPrefs(kADMS_UpgradeDate), this.currentDate), this.daysSinceLastUpgradeEvar);
            }
            prefsPutInt(kADMS_LaunchesAfterUpgrade, newLaunchesAfterUpgrade);
        }
    }

    private void setInstallVariables() {
        setVariable(this.appId + " Install", "pageName");
        setVariable(dayMonthYearFromDate(this.currentDate), this.appInstallDateEvar);
        setVariable("+1", this.engagedDaysLifetimeEvar);
        setEvent(this.appInstallEvent);
        setEvent(this.dailyEngagedUserEvent);
        setEvent(this.monthlyEngagedUserEvent);
        putDateIntoPrefs(this.currentDate, kADMS_InstallDate);
    }

    private void setUpgradeVariables() {
        setVariable(this.appId + " Upgrade", "pageName");
        setEvent(this.appUpgradeEvent);
        prefsPutInt(kADMS_LaunchesAfterUpgrade, 0);
        putDateIntoPrefs(this.currentDate, kADMS_UpgradeDate);
    }

    private void setLaunchVariables() {
        setVariable(this.appId + " Launch", "pageName");
    }

    private void setNotInstallVariables() {
        Date lastUsedDate = getDateFromPrefs(kADMS_LastDateUsed);
        if (!dayMonthYearFromDate(lastUsedDate).equalsIgnoreCase(dayMonthYearFromDate(this.currentDate))) {
            setVariable("+1", this.engagedDaysLifetimeEvar);
            setEvent(this.dailyEngagedUserEvent);
        }
        if (!monthYearFromDate(lastUsedDate).equals(monthYearFromDate(this.currentDate))) {
            setEvent(this.monthlyEngagedUserEvent);
        }
        setVariable("" + daysBetween(getDateFromPrefs(kADMS_InstallDate), this.currentDate), this.daysSinceFirstUseEvar);
        setVariable("" + daysBetween(lastUsedDate, this.currentDate), this.daysSinceLastUseEvar);
        if (!prefsGetBool(kADMS_SuccessfulClose, false)) {
            setEvent(this.appCrashEvent);
        }
    }

    protected void setGenericVariables() {
        setEvent(this.appLaunchEvent);
        int newLaunches = prefsGetInt(kADMS_Launches, 0) + 1;
        setVariable("" + newLaunches, this.appLaunchNumberEvar);
        setVariable(this.appId, this.appIdEvar);
        setVariable(hourFromDate(this.currentDate), this.hourOfDayEvar);
        setVariable(dayOfWeekFromDate(this.currentDate), this.dayOfWeekEvar);
        setVariable(getDynamicEvar(this.engagedDaysLifetimeEvar), this.engagedDaysMonthEvar);
        setVariable(getDynamicEvar(this.engagedDaysLifetimeEvar), this.engagedDaysLastUpgradeEvar);
        setVariable(getDynamicEvar(this.appIdEvar), this.appIdProp);
        setVariable(getDynamicEvar(this.appLaunchNumberEvar), this.appLaunchNumberProp);
        setVariable(getDynamicEvar(this.appLaunchNumberSinceLastUpgradeEvar), this.appLaunchNumberSinceLastUpgradeProp);
        putDateIntoPrefs(this.currentDate, kADMS_LastDateUsed);
        prefsPutInt(kADMS_Launches, newLaunches);
    }

    protected String getDynamicEvar(String evar) {
        return evar.length() <= 4 ? evar : "D=v" + evar.substring(4);
    }

    private void handleEvents() {
        setVariable(join(this.eventList, ","), "events");
    }

    protected static String join(Iterable<?> elements, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (Object e : elements) {
            if (sb.length() > 0) {
                sb.append(delimiter);
            }
            sb.append(e);
        }
        return sb.toString();
    }

    private void putDateIntoPrefs(Date date, String key) {
        prefsPutLong(key, date.getTime());
    }

    private Date getDateFromPrefs(String key) {
        return new Date(prefsGetLong(key, 0));
    }

    private String dayMonthYearFromDate(Date date) {
        return this.dayMonthYearFormat.format(date);
    }

    private String hourFromDate(Date date) {
        return this.hourOfDayDateFormat.format(date);
    }

    private String dayOfWeekFromDate(Date date) {
        return this.dayOfWeekDateFormat.format(date);
    }

    private String monthYearFromDate(Date date) {
        return this.monthYearDateFormat.format(date);
    }

    private int secondsBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / 1000);
    }

    private int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / 86400000);
    }

    public static String join(Collection<String> collection, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> iter = collection.iterator();
        while (iter.hasNext()) {
            buffer.append((String) iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    private void openSession() {
        prefsPutInt(kADMS_SessionOpen, prefsGetInt(kADMS_SessionOpen, 0) + 1);
    }

    private void closeSession() {
        prefsPutInt(kADMS_SessionOpen, prefsGetInt(kADMS_SessionOpen, 0) - 1);
    }

    protected int prefsGetInt(String key, int defaultValue) {
        return 0;
    }

    protected void prefsPutInt(String key, int value) {
    }

    protected long prefsGetLong(String key, long defaultValue) {
        return 0;
    }

    protected void prefsPutLong(String key, long value) {
    }

    protected boolean prefsGetBool(String key, boolean defaultValue) {
        return false;
    }

    protected void prefsPutBool(String key, boolean value) {
    }

    protected String prefsGetString(String key, String defaultValue) {
        return "";
    }

    protected void prefsPutString(String key, String value) {
    }

    protected boolean prefsContains(String key) {
        return false;
    }

    protected String getApplicationVersion() {
        return "";
    }

    protected String getApplicationName() {
        return "";
    }

    protected void removeObjectFromPrefsWithKey(String key) {
    }
}
