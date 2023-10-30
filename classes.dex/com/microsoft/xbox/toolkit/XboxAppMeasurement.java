package com.microsoft.xbox.toolkit;

import android.app.Application;
import com.omniture.AppMeasurement;

public class XboxAppMeasurement {
    private static XboxAppMeasurement instance = null;
    private static Object lock = new Object();
    private static final String visitEvent = "event1";
    private String account;
    private AppMeasurement appMeasurementInstance;
    private boolean isInitialized = false;
    private String server;

    private XboxAppMeasurement() {
    }

    public static XboxAppMeasurement getInstance() {
        XboxAppMeasurement xboxAppMeasurement;
        synchronized (lock) {
            if (instance == null) {
                instance = new XboxAppMeasurement();
            }
            xboxAppMeasurement = instance;
        }
        return xboxAppMeasurement;
    }

    public boolean getIsInitialized() {
        return this.isInitialized;
    }

    public void initialize(Application app, String newAccount, String newServer) {
        boolean z = true;
        if (!this.isInitialized) {
            try {
                boolean z2;
                this.appMeasurementInstance = new AppMeasurement(app);
                this.account = newAccount;
                if (this.account == null || this.account.length() <= 0) {
                    z2 = false;
                } else {
                    z2 = true;
                }
                XLEAssert.assertTrue(z2);
                this.appMeasurementInstance.account = this.account;
                this.server = newServer;
                if (this.server == null || this.server.length() <= 0) {
                    z = false;
                }
                XLEAssert.assertTrue(z);
                this.appMeasurementInstance.server = this.server;
                this.appMeasurementInstance.currencyCode = "USD";
                this.appMeasurementInstance.trackingServer = "o.xbox.com";
                this.appMeasurementInstance.debugTracking = false;
                this.isInitialized = true;
            } catch (Exception e) {
                XLELog.Error("XboxAppMeasurement", e.toString());
            }
        }
    }

    public AppMeasurement getAppMeasurement() {
        return this.appMeasurementInstance;
    }

    public void trackVisit(String channel, String pageName) {
        try {
            trackVisitWithEvent(channel, pageName, null);
        } catch (Exception e) {
            XLELog.Error("XboxAppMeasurement", e.toString());
        }
    }

    public void trackVisitWithEvent(String channel, String pageName, String eventName) {
        try {
            this.appMeasurementInstance.channel = String.format("%s:%s", new Object[]{this.server, channel});
            this.appMeasurementInstance.pageName = String.format("%s:%s", new Object[]{this.appMeasurementInstance.channel, pageName});
            if (eventName == null || eventName.length() == 0) {
                this.appMeasurementInstance.events = visitEvent;
            } else {
                this.appMeasurementInstance.events = String.format("%s,%s", new Object[]{visitEvent, eventName});
            }
            this.appMeasurementInstance.eVar8 = "D=pageName";
            this.appMeasurementInstance.track();
            this.appMeasurementInstance.events = "";
            this.appMeasurementInstance.eVar34 = "";
            this.appMeasurementInstance.eVar35 = "";
            this.appMeasurementInstance.eVar36 = "";
        } catch (Exception e) {
            XLELog.Error("XboxAppMeasurement", e.toString());
        }
    }

    public void trackEvent(String event, String eventName) {
        try {
            trackEvent(event, eventName, "events");
        } catch (Exception e) {
            XLELog.Error("XboxAppMeasurement", e.toString());
        }
    }

    public void trackEvent(String event, String eventName, String linkVars) {
        try {
            this.appMeasurementInstance.pageName = "";
            this.appMeasurementInstance.linkTrackEvents = event;
            this.appMeasurementInstance.linkTrackVars = linkVars;
            this.appMeasurementInstance.events = event;
            this.appMeasurementInstance.trackLink(null, "o", eventName);
            this.appMeasurementInstance.events = "";
        } catch (Exception e) {
            XLELog.Error("XboxAppMeasurement", e.toString());
        }
    }
}
