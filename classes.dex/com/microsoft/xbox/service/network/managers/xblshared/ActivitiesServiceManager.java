package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.authenticate.XstsToken;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2Provider;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.XstsTokenManager;
import com.microsoft.xbox.toolkit.Ready;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

public class ActivitiesServiceManager implements IActivitiesServiceManager {

    public static class ActivityProvider {
        public String name;
        public String titleId;
    }

    public static class ActivityProviders {
        @JsonProperty("Providers")
        public ArrayList<ActivityProvider> providers;
    }

    private static native void nativeGetActivityDetail(String str, String str2, int i, String str3, String str4, String str5, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    private static native void nativeGetActivityList(String str, int i, String str2, int i2, String str3, String str4, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    public ArrayList<EDSV2ActivityItem> getActivities(EDSV2MediaItem parentMediaItem) throws XLEException {
        final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
        final Ready ready = new Ready();
        final String xstsToken = getXstsToken();
        final String id = parentMediaItem.getCanonicalId();
        final String xuid = ServiceManagerFactory.getInstance().getSLSServiceManager().getAndCacheUserXuid();
        final String providerJsonString = getProviderJsonString(parentMediaItem);
        final EDSV2MediaItem eDSV2MediaItem = parentMediaItem;
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                XLELog.Diagnostic("ActivitiesServiceManager", "get activity list for " + id);
                ActivitiesServiceManager.nativeGetActivityList(id, eDSV2MediaItem.getMediaType(), providerJsonString, ActivitiesServiceManager.getSubscriptionLevel(), xstsToken, xuid, worker);
                ready.setReady();
            }
        });
        ready.waitForReady();
        return new ArrayList(Arrays.asList((Object[]) worker.deserialize(EDSV2ActivityItem[].class, XLEErrorCode.FAILED_TO_GET_ACTIVITY_SUMMARY)));
    }

    public EDSV2ActivityItem getActivityDetail(EDSV2ActivityItem activity, EDSV2MediaItem parentMediaItem) throws XLEException {
        final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
        final Ready ready = new Ready();
        final String xstsToken = getXstsToken();
        final String xuid = ServiceManagerFactory.getInstance().getSLSServiceManager().getAndCacheUserXuid();
        final String providerJsonString = getProviderJsonString(parentMediaItem);
        final EDSV2ActivityItem eDSV2ActivityItem = activity;
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                ActivitiesServiceManager.nativeGetActivityDetail(eDSV2ActivityItem.getCanonicalId(), providerJsonString, ActivitiesServiceManager.getSubscriptionLevel(), xstsToken, xuid, eDSV2ActivityItem.getImpressionGuid(), worker);
                ready.setReady();
            }
        });
        ready.waitForReady();
        return (EDSV2ActivityItem) worker.deserialize(EDSV2ActivityItem.class, XLEErrorCode.FAILED_TO_GET_ACTIVITY_DETAIL);
    }

    private String getXstsToken() throws XLEException {
        String tokenStr = "";
        if (XboxLiveEnvironment.Instance().isUsingStub() || !XboxLiveEnvironment.Instance().isUsingToken()) {
            return tokenStr;
        }
        XstsToken token = XstsTokenManager.getInstance().getXstsToken(XboxLiveEnvironment.SLS_AUDIENCE_URI);
        if (token != null) {
            return token.getToken();
        }
        throw new XLEException(XLEErrorCode.INVALID_TOKEN);
    }

    private static int getSubscriptionLevel() {
        if (MeProfileModel.getModel().getIsGold()) {
            return 1;
        }
        return 2;
    }

    private static String getProviderJsonString(EDSV2MediaItem parentMediaItem) {
        if (parentMediaItem.getProviders() != null && parentMediaItem.getProviders().size() > 0) {
            ActivityProviders providerObject = new ActivityProviders();
            providerObject.providers = new ArrayList(parentMediaItem.getProviders().size());
            Iterator i$ = parentMediaItem.getProviders().iterator();
            while (i$.hasNext()) {
                EDSV2Provider parentProvider = (EDSV2Provider) i$.next();
                XLEAssert.assertTrue("Provider title id should not be 0. Must fix!", parentProvider.getTitleId() > 0);
                ActivityProvider provider = new ActivityProvider();
                provider.name = parentProvider.getName();
                provider.titleId = Long.toString(parentProvider.getTitleId());
                providerObject.providers.add(provider);
            }
            try {
                String json = new ObjectMapper().writeValueAsString(providerObject);
                XLELog.Diagnostic("ActivitiesServiceManager", "Providers JSON string: " + json);
                return json;
            } catch (Exception e) {
                XLELog.Error("ActivitiesServiceManager", "Failed to serialize provider list to string: " + e.toString());
            }
        }
        return null;
    }
}
