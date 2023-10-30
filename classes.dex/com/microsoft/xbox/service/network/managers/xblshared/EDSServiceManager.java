package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.authenticate.XstsToken;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.SearchTermData;
import com.microsoft.xbox.service.model.edsv2.EDSV2DiscoverData;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchResult;
import com.microsoft.xbox.service.network.managers.XstsTokenManager;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.Ready;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EDSServiceManager implements IEDSServiceManager {
    private static final String XSTS_AUDIENCE = "http://xboxlive.com";

    public static native void nativeBrowseMediaItemList(String str, int i, int i2, String str2, String str3, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    public static native void nativeGetCombinedContentRating(String str, String str2, String str3, boolean z, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    public static native void nativeGetGenreList(String str, String str2, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    public static native void nativeGetMediaItemDetail(String str, String str2, long j, int i, String str3, String str4, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    public static native void nativeGetMusicSmartDJ(String str, String str2, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    public static native void nativeGetPopularSearchTerms(String str, String str2, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    public static native void nativeGetProgrammingItems2(String str, String str2, String str3, String str4, String str5, boolean z, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    public static native void nativeGetRelated(String str, int i, int i2, String str2, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    public static native void nativeInitializeServiceManager(String str, String str2, String str3, boolean z);

    public static native void nativeSearchMediaItems(String str, int i, String str2, int i2, String str3, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    public void initializeServiceManager() {
        if (MeProfileModel.getModel().getLegalLocale() != null && MeProfileModel.getModel().getMembershipLevel() != null && MeProfileModel.getModel().getCombinedContentRating() != null) {
            final Ready ready = new Ready();
            final String legalLocale = MeProfileModel.getModel().getLegalLocale();
            final String subscriptionLevel = MeProfileModel.getModel().getMembershipLevel();
            final String combinedContentRating = MeProfileModel.getModel().getCombinedContentRating();
            final boolean adultAccount = !MeProfileModel.getModel().getIsParentallyControlled();
            XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
                public void run() {
                    EDSServiceManager.nativeInitializeServiceManager(legalLocale, subscriptionLevel, combinedContentRating, adultAccount);
                    ready.setReady();
                }
            });
            ready.waitForReady();
        }
    }

    public EDSV2DiscoverData getProgrammingItems2() throws XLEException {
        if (MeProfileModel.getModel().getLegalLocale() == null || MeProfileModel.getModel().getMembershipLevel() == null || MeProfileModel.getModel().getCombinedContentRating() == null) {
            return null;
        }
        final Ready ready = new Ready();
        final String token = getXstsToken();
        final String legalLocale = MeProfileModel.getModel().getLegalLocale();
        final String deviceLocale = MeProfileModel.getModel().getLegalLocale();
        final String subscriptionLevel = MeProfileModel.getModel().getMembershipLevel();
        final String combinedContentRating = MeProfileModel.getModel().getCombinedContentRating();
        final boolean adultAccount = !MeProfileModel.getModel().getIsParentallyControlled();
        final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                EDSServiceManager.nativeGetProgrammingItems2(token, deviceLocale, legalLocale, subscriptionLevel, combinedContentRating, adultAccount, worker);
                ready.setReady();
            }
        });
        ready.waitForReady();
        return (EDSV2DiscoverData) worker.deserialize(EDSV2DiscoverData.class, XLEErrorCode.FAILED_TO_GET_DISCOVER);
    }

    public List<SearchTermData> GetPopularSearchTerms() throws XLEException {
        if (MeProfileModel.getModel().getLegalLocale() == null) {
            return null;
        }
        final Ready ready = new Ready();
        final String token = getXstsToken();
        final String legalLocale = MeProfileModel.getModel().getLegalLocale();
        final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                EDSServiceManager.nativeGetPopularSearchTerms(legalLocale, token, worker);
                ready.setReady();
            }
        });
        ready.waitForReady();
        return new ArrayList(Arrays.asList((Object[]) worker.deserialize(SearchTermData[].class, XLEErrorCode.FAILED_TO_GET_POPULAR_SEARCH_DATA)));
    }

    private String getXstsToken() throws XLEException {
        String tokenStr = "";
        if (XboxLiveEnvironment.Instance().isUsingStub() || !XboxLiveEnvironment.Instance().isUsingToken()) {
            return tokenStr;
        }
        XstsToken token = XstsTokenManager.getInstance().getXstsToken("http://xboxlive.com");
        if (token != null) {
            return token.getToken();
        }
        throw new XLEException(XLEErrorCode.INVALID_TOKEN);
    }

    public <T extends EDSV2MediaItem> T getMediaItemDetail(String canonicalId, String partnerMediaId, long titleId, int mediaGroup, String impressionGuid) throws XLEException {
        if (MeProfileModel.getModel().getLegalLocale() == null || MeProfileModel.getModel().getMembershipLevel() == null || !MeProfileModel.getModel().getInitializeComplete() || getXstsToken() == null) {
            return null;
        }
        final Ready ready = new Ready();
        final String token = getXstsToken();
        String legalLocale = MeProfileModel.getModel().getLegalLocale();
        String subscriptionLevel = MeProfileModel.getModel().getMembershipLevel();
        if (MeProfileModel.getModel().getIsParentallyControlled()) {
        }
        final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
        final String encodedPartnerMediaId = JavaUtil.isNullOrEmpty(partnerMediaId) ? null : JavaUtil.urlEncode(partnerMediaId);
        String combinedContentRating = MeProfileModel.getModel().getCombinedContentRating();
        final String str = canonicalId;
        final long j = titleId;
        final int i = mediaGroup;
        final String str2 = impressionGuid;
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                EDSServiceManager.nativeGetMediaItemDetail(str, encodedPartnerMediaId, j, i, str2, token, worker);
                ready.setReady();
            }
        });
        ready.waitForReady();
        return (EDSV2MediaItem) worker.deserialize(EDSV2MediaItem.class, XLEErrorCode.FAILED_TO_GET_MEDIA_ITEM_DETAILS);
    }

    public <T extends EDSV2MediaItem> ArrayList<T> browseMediaItemList(String canonicalId, int desiredMediaType, int mediaItemType, String impressionGuid) throws XLEException {
        if (MeProfileModel.getModel().getLegalLocale() == null || MeProfileModel.getModel().getMembershipLevel() == null || !MeProfileModel.getModel().getInitializeComplete() || getXstsToken() == null) {
            return null;
        }
        final Ready ready = new Ready();
        final String token = getXstsToken();
        String legalLocale = MeProfileModel.getModel().getLegalLocale();
        String subscriptionLevel = MeProfileModel.getModel().getMembershipLevel();
        if (MeProfileModel.getModel().getIsParentallyControlled()) {
        }
        String combinedContentRating = MeProfileModel.getModel().getCombinedContentRating();
        final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
        final String str = canonicalId;
        final int i = desiredMediaType;
        final int i2 = mediaItemType;
        final String str2 = impressionGuid;
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                EDSServiceManager.nativeBrowseMediaItemList(str, i, i2, str2, token, worker);
                ready.setReady();
            }
        });
        ready.waitForReady();
        EDSV2MediaItem[] dataArray = (EDSV2MediaItem[]) worker.deserialize(EDSV2MediaItem[].class, XLEErrorCode.FAILED_TO_BROWSE_MEDIA_ITEM_LIST);
        if (dataArray == null) {
            return null;
        }
        ArrayList<T> data = new ArrayList();
        for (Object add : dataArray) {
            data.add(add);
        }
        return data;
    }

    public <T extends EDSV2MediaItem> ArrayList<T> getRelated(String canonicalId, int desiredMediaType, int mediaItemType) throws XLEException {
        ArrayList<T> arrayList = null;
        if (getXstsToken() != null && MeProfileModel.getModel().getInitializeComplete()) {
            final Ready ready = new Ready();
            final String token = getXstsToken();
            final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
            final String str = canonicalId;
            final int i = desiredMediaType;
            final int i2 = mediaItemType;
            XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
                public void run() {
                    EDSServiceManager.nativeGetRelated(str, i, i2, token, worker);
                    ready.setReady();
                }
            });
            ready.waitForReady();
            arrayList = null;
            EDSV2MediaItem[] dataArray = (EDSV2MediaItem[]) worker.deserialize(EDSV2MediaItem[].class, XLEErrorCode.FAILED_TO_GET_RELATED);
            if (dataArray != null) {
                arrayList = new ArrayList();
                for (Object add : dataArray) {
                    arrayList.add(add);
                }
            }
        }
        return arrayList;
    }

    public String getCombinedContentRating() throws XLEException {
        final String xststoken = getXstsToken();
        final String legalLocale = MeProfileModel.getModel().getLegalLocale();
        final String subscriptionLevel = MeProfileModel.getModel().getMembershipLevel();
        final boolean isAdultAccount = !MeProfileModel.getModel().getIsParentallyControlled();
        if (xststoken == null || JavaUtil.isNullOrEmpty(legalLocale) || JavaUtil.isNullOrEmpty(subscriptionLevel)) {
            throw new XLEException(XLEErrorCode.FAILED_TO_GET_COMBINED_CONTENT_RATING);
        }
        final Ready done = new Ready();
        final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                EDSServiceManager.nativeGetCombinedContentRating(xststoken, legalLocale, subscriptionLevel, isAdultAccount, worker);
                done.setReady();
            }
        });
        done.waitForReady();
        return worker.getJSONData();
    }

    public EDSV2SearchResult searchMediaItems(String searchTerm, int filter, String continuationToken, int resultsPerPage) throws XLEException {
        if (MeProfileModel.getModel().getLegalLocale() == null || MeProfileModel.getModel().getMembershipLevel() == null || getXstsToken() == null) {
            return null;
        }
        final Ready ready = new Ready();
        final String token = getXstsToken();
        String legalLocale = MeProfileModel.getModel().getLegalLocale();
        String subscriptionLevel = MeProfileModel.getModel().getMembershipLevel();
        if (MeProfileModel.getModel().getIsParentallyControlled()) {
        }
        final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
        final String str = searchTerm;
        final int i = filter;
        final String str2 = continuationToken;
        final int i2 = resultsPerPage;
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                EDSServiceManager.nativeSearchMediaItems(str, i, str2, i2, token, worker);
                ready.setReady();
            }
        });
        return (EDSV2SearchResult) worker.deserialize(EDSV2SearchResult.class, XLEErrorCode.FAILED_TO_GET_SEARCH_SUMMARY_DATA);
    }

    public static URI buildEDSImageURI(String baseimageurl, int width, int height) {
        return URI.create(String.format("%s?width=%d&height=%d&format=png", new Object[]{baseimageurl, Integer.valueOf(width), Integer.valueOf(height)}));
    }

    public <T extends EDSV2MediaItem> ArrayList<T> getSmartDJ(String identifier) throws XLEException {
        ArrayList<T> arrayList = null;
        if (getXstsToken() != null && MeProfileModel.getModel().getInitializeComplete()) {
            final Ready ready = new Ready();
            final String token = getXstsToken();
            final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
            final String str = identifier;
            XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
                public void run() {
                    EDSServiceManager.nativeGetMusicSmartDJ(str, token, worker);
                    ready.setReady();
                }
            });
            ready.waitForReady();
            arrayList = null;
            EDSV2MediaItem[] dataArray = (EDSV2MediaItem[]) worker.deserialize(EDSV2MediaItem[].class, XLEErrorCode.FAILED_TO_GET_MUSIC_SMARTDJ);
            if (dataArray != null) {
                arrayList = new ArrayList();
                for (Object add : dataArray) {
                    arrayList.add(add);
                }
            }
        }
        return arrayList;
    }

    public ArrayList<String> getGenreList(String mediaType) throws XLEException {
        if (MeProfileModel.getModel().getLegalLocale() == null) {
            return null;
        }
        final Ready ready = new Ready();
        final String token = getXstsToken();
        final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
        final String str = mediaType;
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                EDSServiceManager.nativeGetGenreList(str, token, worker);
                ready.setReady();
            }
        });
        ready.waitForReady();
        return new ArrayList(Arrays.asList((Object[]) worker.deserialize(String[].class, XLEErrorCode.FAILED_TO_GET_GENRE_LIST)));
    }
}
