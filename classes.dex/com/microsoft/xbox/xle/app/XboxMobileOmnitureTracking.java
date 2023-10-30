package com.microsoft.xbox.xle.app;

import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxAppMeasurement;

public class XboxMobileOmnitureTracking {
    public static boolean isTrackingEnabled = true;

    public static void SetTrackingStatus(boolean value) {
        isTrackingEnabled = value;
    }

    public static void SetDetails(String detailType, String detailName, String detailId) {
        XboxAppMeasurement.getInstance().getAppMeasurement().eVar34 = detailType;
        XboxAppMeasurement.getInstance().getAppMeasurement().eVar35 = detailName;
        XboxAppMeasurement.getInstance().getAppMeasurement().eVar38 = detailId;
    }

    public static void TrackError(String errorName) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop7 = "D=v7";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar7 = errorName;
                XboxAppMeasurement.getInstance().trackEvent("event2", "Error", "events,eVar7,prop7");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar7 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop7 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackCreateAccount() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event3", "AccountCreation", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackSignIn(String memberType, String deviceName, String osVersion, String appVersion) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop1 = "D=v1";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar1 = memberType;
                XboxAppMeasurement.getInstance().getAppMeasurement().prop26 = "D=v26";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar26 = deviceName;
                XboxAppMeasurement.getInstance().getAppMeasurement().prop27 = "D=v27";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar27 = osVersion;
                XboxAppMeasurement.getInstance().getAppMeasurement().prop28 = "D=v28";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar28 = appVersion;
                XboxAppMeasurement.getInstance().trackEvent("event4", "SignIn", "events,eVar1,prop1,eVar26,prop26,eVar27,prop27,eVar28,prop28");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar1 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop1 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar26 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop26 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar27 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop27 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar28 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop28 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackProfileViewMy() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event26", "Profile:ViewMy", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackProfileViewOther() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event27", "Profile:ViewOther", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackMsgCheck() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event6", "Msg:Check", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackMsgRead() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event7", "Msg:Read", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackMsgReply() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event8", "Msg:Reply", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackMsgCompose() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event9", "Msg:Compose", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackMsgSend() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event10", "Msg:Send", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackFriendRequest() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event13", "Friend:Request", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackFriendAccept() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event14", "Friend:Accept", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackFriendDeny() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event15", "Friend:Decline", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackFriendSearch() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event16", "Friend:Search", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackAvatarEditStart() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event28", "AvatarEdit:Start", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackAvatarEditStart(String deviceFrameRate) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop29 = "D=v29";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar29 = deviceFrameRate;
                XboxAppMeasurement.getInstance().trackEvent("event28", "AvatarEdit:Start", "events,eVar29,prop29");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar29 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop29 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackAvatarEditItem(String itemName) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop4 = "D=v18";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar18 = itemName;
                XboxAppMeasurement.getInstance().trackEvent("event29", "AssetSelected", "events,eVar18,prop4");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar18 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop4 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackAvatarEditSave() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event30", "Save Avatar", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackAvatarEditReset() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event31", "Reset Avatar", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackGameDetailView(String gameTitle) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop22 = "D=v18";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar18 = gameTitle;
                XboxAppMeasurement.getInstance().trackEvent("event25", "Game:DetailView", "events,eVar18,prop22");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar18 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop22 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackCompareGame(String gameTitle) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop22 = "D=v18";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar18 = gameTitle;
                XboxAppMeasurement.getInstance().trackEvent("event33", "CompareGames", "events,eVar18,prop22");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar18 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop22 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackCompareAchievement(String gameTitle) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop22 = "D=v18";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar18 = gameTitle;
                XboxAppMeasurement.getInstance().trackEvent("event36", "Compare Achievement", "events,eVar18,prop22");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar18 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop22 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackSearchFriendNoResults(String searchType, String searchTerm) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop4 = "D=v4";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar4 = searchType;
                XboxAppMeasurement.getInstance().getAppMeasurement().prop5 = "D=v5";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar5 = searchTerm;
                XboxAppMeasurement.getInstance().trackEvent("event24", "FriendSearch", "events,eVar4,prop4,eVar5,prop5");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar4 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop4 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar5 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop5 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackSearchGameNoResults(String searchType, String searchTerm) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop4 = "D=v4";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar4 = searchType;
                XboxAppMeasurement.getInstance().getAppMeasurement().prop5 = "D=v5";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar5 = searchTerm;
                XboxAppMeasurement.getInstance().trackEvent("event23", "GameSearch", "events,eVar4,prop4,eVar5,prop5");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar4 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop4 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar5 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop5 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackBrowseGamesClick(String gameTitle) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop22 = "D=v18";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar18 = gameTitle;
                XboxAppMeasurement.getInstance().trackEvent("event35", "GameAllClick", "events,eVar18,prop22");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar18 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop22 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackVideoLaunch(String videoTitle, String locale) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop46 = "D=v23";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar23 = videoTitle;
                XboxAppMeasurement.getInstance().getAppMeasurement().prop10 = "D=v10";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar10 = locale;
                XboxAppMeasurement.getInstance().trackEvent("event11", "Video:Launch", "events,eVar23,prop46,eVar10,prop10");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar23 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop46 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar10 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop10 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackVideoPlay(String videoTitle, String locale) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop46 = "D=v23";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar23 = videoTitle;
                XboxAppMeasurement.getInstance().getAppMeasurement().prop10 = "D=v10";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar10 = locale;
                XboxAppMeasurement.getInstance().trackEvent("event12", "Video:Play", "events,eVar23,prop46,eVar10,prop10");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar23 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop46 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar10 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop10 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackQuickPlayConnect() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event37", "Quickplay Connect Click", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackQuickPlayConnectRetries() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event38", "Quickplay Connect Retries", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackQuickPlayContentClicked(String slotAndTitle) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop9 = "D=v9";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar9 = slotAndTitle;
                XboxAppMeasurement.getInstance().trackEvent("event39", "Quickplay Content Clicked", "events,eVar9,prop9");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar9 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop9 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackDiscoverContentClick(String slotAndTitle, String locale) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop11 = "D=v11";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar11 = slotAndTitle;
                XboxAppMeasurement.getInstance().getAppMeasurement().prop10 = "D=v10";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar10 = locale;
                XboxAppMeasurement.getInstance().trackEvent("event40", "Discover Content Clicked", "events,eVar11,prop11,eVar10,prop10");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar11 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop11 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar10 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop10 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackPlayOnXboxClick(String providerTitle) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop13 = "D=v13";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar13 = providerTitle;
                XboxAppMeasurement.getInstance().trackEvent("event41", "PlayonXbox Click", "events,eVar13,prop13");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar13 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop13 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackConsoleConnectionType(String connectionType, String autoplayStatus) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop12 = "D=v12";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar12 = connectionType;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar31 = autoplayStatus;
                XboxAppMeasurement.getInstance().trackEvent("event43", "Console Connection Success", "events,eVar12,prop12,eVar31");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar12 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop12 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar31 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackPlayControllerClicked(String controlsButtonClick) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop30 = "D=v30";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar30 = controlsButtonClick;
                XboxAppMeasurement.getInstance().trackEvent("event42", "Play Controller Clicked", "events,eVar30,prop30");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar30 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop30 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackConsoleConnectAttempt(String ActionMode, String ControlName) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar32 = ActionMode;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar33 = ControlName;
                XboxAppMeasurement.getInstance().trackEvent("event44", "Connect:Attempt", "events,eVar32,eVar33");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar32 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar33 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackLaunchActivity(String ActionMode, String ControlName, String OfferType, String OfferName, String OfferId, String ConsolePlayingMatchingTitleId) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar32 = ActionMode;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar33 = ControlName;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar34 = OfferType;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar35 = OfferName;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar38 = OfferId;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar39 = ConsolePlayingMatchingTitleId;
                XboxAppMeasurement.getInstance().trackEvent("event45", "Activity:Launch", "events,eVar32,eVar33,eVar34,eVar35,eVar38,eVar39");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar32 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar33 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar34 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar35 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar38 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar39 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackIntentToPurchase(String ControlName, String OfferType, String OfferName, String OfferId) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar33 = ControlName;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar34 = OfferType;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar35 = OfferName;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar38 = OfferId;
                XboxAppMeasurement.getInstance().trackEvent("event46", "Purchase:Activity:Intent", "events,eVar33,eVar34,eVar35,eVar38");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar33 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar34 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar35 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar38 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackIntentToPurchaseSubscription(String ControlName, String OfferType, String OfferName, String OfferId) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar33 = ControlName;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar34 = OfferType;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar35 = OfferName;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar38 = OfferId;
                XboxAppMeasurement.getInstance().trackEvent("event47", "Purchase:Subscription:Intent", "events,eVar33,eVar34,eVar35,eVar38");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar33 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar34 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar35 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar38 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackCancelAutoPlay(String OfferType, String OfferName, String OfferId) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar34 = OfferType;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar35 = OfferName;
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar38 = OfferId;
                XboxAppMeasurement.getInstance().trackEvent("event48", "AutoPlay:Cancel", "events,eVar34,eVar35,eVar38");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar34 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar35 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar38 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackSearchClick() {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().trackEvent("event49", "Search:Click", "events");
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackSearchViewMore(String LoadCount) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().prop14 = LoadCount;
                XboxAppMeasurement.getInstance().trackEvent("event50", "Search:ViewMore", "events,prop14");
                XboxAppMeasurement.getInstance().getAppMeasurement().prop14 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }

    public static void TrackChangeSetting(String ControlName, String NewSettingValue) {
        if (isTrackingEnabled) {
            try {
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar33 = ControlName;
                XboxAppMeasurement.getInstance().getAppMeasurement().prop15 = NewSettingValue;
                XboxAppMeasurement.getInstance().trackEvent("event51", "Setting:Change", "events,eVar33,prop15");
                XboxAppMeasurement.getInstance().getAppMeasurement().eVar33 = "";
                XboxAppMeasurement.getInstance().getAppMeasurement().prop15 = "";
            } catch (Exception e) {
                XLELog.Error("XboxMobileOmnitureTracking", e.toString());
            }
        }
    }
}
