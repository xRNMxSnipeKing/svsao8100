package com.microsoft.xbox.toolkit.network;

import android.os.Build;
import android.os.Build.VERSION;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.locale.XBLLocale;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xle.test.interop.TestInterop;
import java.util.Locale;

public class XboxLiveEnvironment {
    public static final String ACHIEVEMENT_API_PATH = "/gamedata.svc/achievements";
    public static final String ACHIEVEMENT_QUERY_PARAMS = "?gamertags=%1$s&gameId=%2$d";
    public static final String ACTIVITY_PURCHASE_PATH = "/%1$s/purchase/xbox/%2$s?modal=false";
    public static final String AVATAR_AUDIENCE_URI = "http://xboxlive.com/avatar";
    public static final String AVATAR_CLOSET_API_PATH = "/Closet.svc/GetClosetAssets";
    public static final String AVATAR_MANIFEST_PRIVATE_API_PATH = "/Manifest.svc/GetManifest";
    public static final String AVATAR_MANIFEST_PUBLIC_API_PATH = "/Manifest.svc/";
    public static final String AVATAR_MANIFEST_PUBLIC_QUERY_PARAMS = "?gt=";
    public static final String AVATAR_MANIFEST_UPDATE_API_PATH = "/Manifest.svc/Update";
    public static final String FRIEND_ACCEPT_API_PATH = "/Friend.svc/accept";
    public static final String FRIEND_ADD_API_PATH = "/Friend.svc/add";
    public static final String FRIEND_DECLINE_API_PATH = "/Friend.svc/decline";
    public static final String FRIEND_QUERY_PARAMS = "?gamertag=%s";
    public static final String FRIEND_REMOVE_API_PATH = "/Friend.svc/remove";
    public static final String GAME_API_PATH = "/gamedata.svc/games";
    public static final String GAME_QUERY_PARAMS = "?gamertags=%1$s&pageCount=%2$s&pageNumber=%3$s";
    private static final int ICS_SDK_INT = 14;
    public static final String LOGIN_REFRESH_TOKEN_PARAMS = "?client_id=%1$s&redirect_uri=%2$s&response_type=token&display=touch&scope=%3$s&locale=%4$s";
    public static final String MESSAGE_BLOCK_API_PATH = "/Message.svc/block";
    public static final String MESSAGE_DELETE_API_PATH = "/Message.svc/delete";
    public static final String MESSAGE_DETAIL_API_PATH = "/Message.svc/messagedetails";
    public static final String MESSAGE_QUERY_PARAMS = "?messageId=%d";
    public static final String MESSAGE_SEND_API_PATH = "/Message.svc/send";
    public static final String MESSAGE_SUMMARY_API_PATH = "/Message.svc/summarylist?";
    public static final String MUSIC_AUDIENCE_URI = "http://music.xboxlive.com";
    public static final String MUSIC_CLOUD_COLLECTION_API_PATH = "https://cloudcollection-ssl.xboxlive.com/%s/users/cloudcollection?syncToken=%s&deviceId=%s&maxItemCount=%d";
    public static final String PARTNER_TOKEN_API_PATH = "/tokens.svc/partnertoken";
    public static final String PARTNER_TOKEN_QUERY_PARAMS = "?gameId=1297290368&gameVersion=%d&audienceUri=";
    public static final String PDLC_AUDIENCE_URI = "http://xboxlive.com/pdlc";
    public static final String PROFILE_API_PATH = "/Profile.svc/profile";
    public static final String PROFILE_QUERY_PARAMS = "?sectionFlags=%1$s";
    public static final String PROFILE_QUERY_PARAMS_EXTENSION = "&gamerTag=%2$s";
    public static final String SLS_AUDIENCE_URI = "http://xboxlive.com";
    public static final String SUBSCRIPTION_PURCHASE_PATH = "/subscriptions";
    public static final String UDS_AUDIENCE_URI = "http://xboxlive.com/userdata";
    public static final String XLINK_AUDIENCE_URI = "http://xlink.xboxlive.com";
    private static XboxLiveEnvironment instance = new XboxLiveEnvironment();
    private Environment environment = Environment.PROD;
    private boolean isUsingToken = true;
    private Environment oldEnvironment = Environment.PROD;
    private final boolean useProxy = false;

    public enum Environment {
        STUB,
        VINT,
        CERTNET,
        PARTNERNET,
        PROD,
        DEV
    }

    public static XboxLiveEnvironment Instance() {
        return instance;
    }

    public String getLoginUrlBase() {
        switch (this.environment) {
            case VINT:
                return "http://live.rtm.vint.xbox.com";
            case PARTNERNET:
                return "http://live.part.xbox.com";
            case PROD:
                return "http://live.xbox.com";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getLoginUrlBaseSecure() {
        switch (this.environment) {
            case VINT:
                return "https://live.rtm.vint.xbox.com";
            case PARTNERNET:
                return "https://live.part.xbox.com";
            case PROD:
            case STUB:
                return "https://live.xbox.com";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getStsUrlBaseSecure() {
        switch (this.environment) {
            case VINT:
                return "https://sts.vint.xbox.com";
            case PARTNERNET:
                return "https://sts.part.xbox.com";
            case PROD:
                return "https://sts.xbox.com";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getLoginUrl() {
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
            case PROD:
                return getLoginUrlBaseSecure() + "/" + getMappedLocale() + "/Signin/Authenticate";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getAccountCreationUrl() {
        switch (this.environment) {
            case VINT:
                return "http://go.microsoft.com/fwlink/?LinkID=262081&clcid=0x409";
            case PARTNERNET:
                return "http://go.microsoft.com/fwlink/?LinkID=262081&clcid=0x409";
            case PROD:
                return "http://go.microsoft.com/fwlink/?LinkId=259789&clcid=0x409";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getPartnerTokenPrefixUrl() {
        switch (this.environment) {
            case VINT:
                return getStsUrlBaseSecure() + PARTNER_TOKEN_API_PATH + String.format(PARTNER_TOKEN_QUERY_PARAMS, new Object[]{Integer.valueOf(0)});
            case PARTNERNET:
                return getStsUrlBaseSecure() + PARTNER_TOKEN_API_PATH + "?gameId=1297290147&gameVersion=2&audienceUri=";
            case PROD:
                return getStsUrlBaseSecure() + PARTNER_TOKEN_API_PATH + String.format(PARTNER_TOKEN_QUERY_PARAMS, new Object[]{Integer.valueOf(1)});
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getXboxDomain() {
        return "xbox.com";
    }

    public String getUdsUrlBaseSecure() {
        switch (this.environment) {
            case VINT:
                return "https://uds-part.vint.xboxlive.com";
            case PARTNERNET:
                return "https://uds-part.part.xboxlive.com";
            case PROD:
                return "https://uds-part.xboxlive.com";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getVersionCheckUrl() {
        switch (this.environment) {
            case VINT:
                return "http://www.rtm.vint.xbox.com/en-US/Platform/Android/XboxLIVE/sgversion";
            case PARTNERNET:
            case PROD:
                return "http://www.xbox.com/en-US/Platform/Android/XboxLIVE/sgversion";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getClosetReadBaseSecure() {
        switch (this.environment) {
            case VINT:
                return "https://avatarcloset-part.vint.xboxlive.com";
            case PARTNERNET:
                return "https://avatarcloset-part.part.xboxlive.com";
            case PROD:
                return "https://avatarcloset-part.xboxlive.com";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getAvatarReadBaseSecure() {
        switch (this.environment) {
            case VINT:
                return "https://avatarread-part.vint.xboxlive.com";
            case PARTNERNET:
                return "https://avatarread-part.part.xboxlive.com";
            case PROD:
                return "https://avatarread-part.xboxlive.com";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getAvatarReadBaseInsecure() {
        switch (this.environment) {
            case VINT:
                return "http://avatarread.vint.xboxlive.com";
            case PARTNERNET:
                return "http://avatarread.part.xboxlive.com";
            case PROD:
                return "http://avatarread.xboxlive.com";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getAvatarWriteBaseSecure() {
        switch (this.environment) {
            case VINT:
                return "https://avatarwrite-part.vint.xboxlive.com";
            case PARTNERNET:
                return "https://avatarwrite-part.part.xboxlive.com";
            case PROD:
                return "https://avatarwrite-part.xboxlive.com";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getUserInfoUrl() {
        switch (this.environment) {
            case VINT:
                return "https://services.vint.xboxlive.com/users/me/id";
            case PARTNERNET:
                return "https://services.part.xboxlive.com/users/me/id";
            case PROD:
                return "https://services.xboxlive.com/users/me/id";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getSmartGlassOverrideUrl(String legalLocale) {
        XLEAssert.assertNotNull("Legal locale not set", legalLocale);
        switch (this.environment) {
            case VINT:
                if (XLEApplication.Instance.getIsTablet()) {
                    return "http://windowsphone-preview.rtm.vint.xbox.com/" + legalLocale + "/SG/Feeds/1.5/AndroidSlate-Override";
                }
                return "http://windowsphone-preview.rtm.vint.xbox.com/" + legalLocale + "/SG/Feeds/1.5/AndroidPhone-Override";
            case PROD:
                if (XLEApplication.Instance.getIsTablet()) {
                    return "http://windowsphone.xbox.com/" + legalLocale + "/SG/Feeds/1.5/AndroidSlate-Override";
                }
                return "http://windowsphone.xbox.com/" + legalLocale + "/SG/Feeds/1.5/AndroidPhone-Override";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getSmartGlassOverrideUserAgentString() {
        if (XLEApplication.Instance.getIsTablet()) {
            return "Android Slate";
        }
        return "Android Phone";
    }

    public String getSmartGlassOverrideXDeviceType() {
        if (XLEApplication.Instance.getIsTablet()) {
            return "Tablet";
        }
        return "Phone";
    }

    public boolean isUsingStub() {
        return this.environment == Environment.STUB;
    }

    public boolean isUsingToken() {
        return this.isUsingToken;
    }

    public void setIsUsingToken(boolean isUsingToken) {
        this.isUsingToken = isUsingToken;
    }

    public void setStub(boolean isStubEnabled) {
        boolean z = true;
        if (isStubEnabled) {
            if (this.environment != Environment.STUB) {
                this.oldEnvironment = this.environment;
            }
            this.environment = Environment.STUB;
            if (this.oldEnvironment == Environment.STUB) {
                z = false;
            }
            XLEAssert.assertTrue(z);
        } else {
            this.environment = this.oldEnvironment;
            if (this.environment == Environment.STUB) {
                z = false;
            }
            XLEAssert.assertTrue(z);
        }
        setEnvironment(this.environment);
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
        XboxApplication.Instance.setEnvironment(environment);
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public String getMappedLocale() {
        Locale sysLocale = Locale.getDefault();
        return XBLLocale.getInstance().getSupportedLocale(sysLocale.getCountry(), sysLocale.getLanguage());
    }

    private String getLanguage() {
        boolean z;
        String locale = getMappedLocale();
        if (locale.length() == 5) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        return locale.substring(0, 2);
    }

    public boolean getProxyEnabled() {
        return false;
    }

    public String getDeviceModelName() {
        return Build.MODEL;
    }

    public String getOsVersion() {
        return VERSION.RELEASE;
    }

    public String getStockAssetsPath() {
        return String.format("avatar/stockassets_%s.xml", new Object[]{getMappedLocale()});
    }

    public String getXboxTokenSite() {
        switch (this.environment) {
            case VINT:
                return "sts.vint.xbox.com";
            case PARTNERNET:
                return "sts.part.xbox.com";
            case PROD:
                return "sts.xbox.com";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getXboxTokenParam() {
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
                return "/tokens.svc/partnertoken?gameId=1297290147&gameVersion=0&audienceUri=";
            case PROD:
                return "/tokens.svc/partnertoken?gameId=1297290319&gameVersion=1&audienceUri=";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getWindowsLiveSite() {
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
                return "login.live-int.com";
            case PROD:
                return "login.live.com";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getBeaconsUrl() {
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
            case PROD:
            case STUB:
                return getLoginUrlBaseSecure() + String.format("/%1$1s/activity?skin=iosapp", new Object[]{getMappedLocale()});
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getWindowsLiveParam() {
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
                return "/ppsecure/clientpost.srf?id=281724";
            case PROD:
                return "/ppsecure/clientpost.srf?id=66262";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getWhiteListUrl() {
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
            case PROD:
                return "http://www.xbox.com/en-US/Platform/Android/XboxLIVE/takehome";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getXboxComScope() {
        switch (this.environment) {
            case VINT:
                return "service::live.rtm.vint.xbox.com::MBI_SSL";
            case PARTNERNET:
                return "service::live.part.xbox.com::MBI_SSL";
            case PROD:
                return "service::live.xbox.com::MBI_SSL";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getXboxLiveScope() {
        switch (this.environment) {
            case VINT:
                return "service::kdc.vint.xboxlive.com::MBI_SSL";
            case PARTNERNET:
                return "service::kdc.part.xboxlive.com::MBI_SSL";
            case PROD:
                return "service::kdc.xboxlive.com::MBI_SSL";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getLoginRefreshUrlBase() {
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
                return "https://login.live-int.com/oauth20_token.srf";
            case PROD:
                return "https://login.live.com/oauth20_token.srf";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getLoginAuthorizeUrlBase() {
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
                return "https://login.live-int.com/oauth20_authorize.srf";
            case PROD:
                return "https://login.live.com/oauth20_authorize.srf";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getLoginRedirectUrl() {
        if (TestInterop.getLoginReturnUrl() != null) {
            return TestInterop.getLoginReturnUrl();
        }
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
                return "https://login.live-int.com/oauth20_desktop.srf";
            case PROD:
                return "https://login.live.com/oauth20_desktop.srf";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getLoginAuthorizeUrlWithScope(String scope) {
        return getLoginAuthorizeUrlBase() + String.format(LOGIN_REFRESH_TOKEN_PARAMS, new Object[]{getClientId(), getLoginRedirectUrl(), scope, getLanguage()});
    }

    public String getClientId() {
        if (TestInterop.getClientId() != null) {
            return TestInterop.getClientId();
        }
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
                return "0000000068036303";
            case PROD:
                return "0000000048093EE3";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getXboxComSetCookieUrl() {
        switch (this.environment) {
            case VINT:
                return "https://live.rtm.vint.xbox.com/xweb/live/passport/setCookies.ashx?rru=https%3a%2f%2flive.rtm.vint.xbox.com%2f" + getMappedLocale() + "%2fsignin%2fauthenticate&wa=wsignin1.0";
            case PARTNERNET:
                return "https://live.part.xbox.com/xweb/live/passport/setCookies.ashx?rru=https%3a%2f%2flive.part.xbox.com%2f" + getMappedLocale() + "%2fsignin%2fauthenticate&wa=wsignin1.0";
            case PROD:
                return "https://live.xbox.com/xweb/live/passport/setCookies.ashx?rru=https%3a%2f%2flive.xbox.com%2f" + getMappedLocale() + "%2fsignin%2fauthenticate&wa=wsignin1.0";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getXstsTokenUrl() {
        switch (this.environment) {
            case VINT:
                return "https://auth.vint.xboxlive.com/XSts/xsts.svc/IWSTrust13";
            case PARTNERNET:
                return "https://auth.part.xboxlive.com/XSts/xsts.svc/IWSTrust13";
            case PROD:
                return "https://activeauth.xboxlive.com/XSts/xsts.svc/IWSTrust13";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getZestAccountSigninUrl() {
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
                return "https://commerce.b.aegis.zune.net/v3/account/signin";
            case PROD:
                return "https://commerce.zune.net/v3/account/signin";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getDiscoverAllMusicUrlFormat() {
        switch (this.environment) {
            case PROD:
                return "http://catalog.zune.net/v3.2/%s/hubs/music/";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getMusicImageUrlFormat() {
        switch (this.environment) {
            case PROD:
                return "http://image.catalog.zune.net/v3.2/%s/image/%s?resize=true&width=%d&height=%d&contenttype=image/jpeg";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getMusicDeliveryLocationUrl(String locale) {
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
                return String.format("https://musicdelivery-ssl.vint.xboxlive.com/v1.0/%s/asset/location", new Object[]{locale.toLowerCase()});
            case PROD:
                return String.format("https://musicdelivery-ssl.xboxlive.com/v1.0/%s/asset/location", new Object[]{locale.toLowerCase()});
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getPlaybackTrackingUrl(String locale) {
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
                return String.format("https://musicdelivery-ssl.vint.xboxlive.com/v1.0/%s/asset/playbacktracking", new Object[]{locale.toLowerCase()});
            case PROD:
                return String.format("https://musicdelivery-ssl.xboxlive.com/v1.0/%s/asset/playbacktracking", new Object[]{locale.toLowerCase()});
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getAcquireLicenseUrl(String locale) {
        switch (this.environment) {
            case VINT:
            case PARTNERNET:
                return String.format("https://musicdelivery-ssl.vint.xboxlive.com/v1.0/%s/asset/acquirelicense", new Object[]{locale.toLowerCase()});
            case PROD:
                return String.format("https://musicdelivery-ssl.xboxlive.com/v1.0/%s/asset/acquirelicense", new Object[]{locale.toLowerCase()});
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getActivityPurchaseUrl(String locale, String canonicalId) {
        return getLoginUrlBaseSecure() + String.format(ACTIVITY_PURCHASE_PATH, new Object[]{locale, canonicalId});
    }

    public String getSubscriptionPurchaseUrl() {
        return getLoginUrlBaseSecure() + SUBSCRIPTION_PURCHASE_PATH;
    }

    public String getOmnitureAccount() {
        return "msxboxandroid,msxboxsg";
    }

    public String getOmnitureServer() {
        return "xleandroid";
    }
}
