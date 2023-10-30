package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.view.AvatarEditorOption;
import com.microsoft.xbox.service.model.CompareGameInfo;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchFilterCount;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchFilterType;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.service.model.serialization.MessageSummary;
import com.microsoft.xbox.toolkit.MultiSelection;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.app.activity.PivotActivity;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel.CollectionFilter;
import java.util.HashMap;
import java.util.List;

public class XLEGlobalData {
    private static final int MAX_SEARCH_TEXT_LENGTH = 120;
    private HashMap<Class<? extends PivotActivity>, Class<? extends ScreenLayout>> activePivotPaneIndexMap;
    private EDSV2MediaItem activityParentMediaItemData;
    private boolean autoLoginStarted;
    private boolean avatarEditorAssetApplyError;
    private boolean avatarEditorCrashed;
    private boolean avatarEditorPreviewNeedsApply;
    private AvatarEditorSelectType avatarSelectedMenu;
    private Class<? extends ActivityBase> defaultScreenClass;
    private DetailPivotPaneData[] detailPivotData;
    private boolean forceRefreshProfile;
    private boolean friendListUpdated;
    private boolean hideCollectionFilter;
    private boolean isAutoLaunch;
    private boolean isLoggedIn;
    private boolean launchTitleIsBrowser;
    private String pivotTitle;
    private List<EDSV2SearchFilterCount> searchResultFilterCountList;
    private String searchTag;
    private String selectedAchievementKey;
    private EDSV2ActivityItem selectedActivityData;
    private AvatarEditorOption selectedAsset;
    private CollectionFilter selectedCollectionFilter;
    private CompareGameInfo selectedCompareGameInfo;
    private String selectedDataSource;
    private EDSV2SearchFilterType selectedFilter;
    private GameInfo selectedGame;
    private String selectedGamertag;
    private EDSV2MediaItem selectedMediaItemData;
    private MessageSummary selectedMessageSummary;
    private MultiSelection<String> selectedRecipients;
    private boolean showLoginError;
    private boolean versionChecked;

    private static class XLEGlobalDataHolder {
        public static final XLEGlobalData instance = new XLEGlobalData();

        private XLEGlobalDataHolder() {
        }
    }

    private XLEGlobalData() {
        this.avatarSelectedMenu = null;
        this.selectedAsset = null;
        this.avatarEditorCrashed = false;
        this.avatarEditorAssetApplyError = false;
        this.avatarEditorPreviewNeedsApply = false;
        this.activePivotPaneIndexMap = new HashMap();
        this.friendListUpdated = false;
        this.launchTitleIsBrowser = false;
        this.hideCollectionFilter = false;
    }

    public static XLEGlobalData getInstance() {
        return XLEGlobalDataHolder.instance;
    }

    public String getSelectedGamertag() {
        return this.selectedGamertag;
    }

    public void setSelectedGamertag(String gamertag) {
        this.selectedGamertag = gamertag;
    }

    public MessageSummary getSelectedMessageSummary() {
        return this.selectedMessageSummary;
    }

    public MultiSelection<String> getSelectedRecipients() {
        if (this.selectedRecipients == null) {
            this.selectedRecipients = new MultiSelection();
        }
        return this.selectedRecipients;
    }

    public void setSelectedMessageSummary(MessageSummary summary) {
        this.selectedMessageSummary = summary;
    }

    public GameInfo getSelectedGame() {
        return this.selectedGame;
    }

    public void setSelectedGame(GameInfo game) {
        this.selectedGame = game;
    }

    public String getSelectedAchievementKey() {
        return this.selectedAchievementKey;
    }

    public void setSelectedAchievementKey(String key) {
        this.selectedAchievementKey = key;
    }

    public String getSelectedDataSource() {
        return this.selectedDataSource;
    }

    public void setSelectedDataSource(String dataSource) {
        this.selectedDataSource = dataSource;
    }

    public boolean getIsLoggedIn() {
        return this.isLoggedIn;
    }

    public void setLoggedIn(boolean value) {
        this.isLoggedIn = value;
    }

    public boolean getShowLoginError() {
        return this.showLoginError;
    }

    public void setShowLoginError(boolean value) {
        this.showLoginError = value;
    }

    public void resetGlobalParameters() {
        this.selectedGamertag = null;
        this.selectedGame = null;
        this.selectedAchievementKey = null;
        this.selectedDataSource = null;
        this.isLoggedIn = false;
        this.showLoginError = false;
        this.versionChecked = false;
        this.selectedMessageSummary = null;
        this.selectedRecipients = null;
        this.avatarSelectedMenu = null;
        this.selectedAsset = null;
        this.activePivotPaneIndexMap = new HashMap();
        this.searchTag = null;
    }

    public AvatarEditorSelectType getSelectedMenu() {
        return this.avatarSelectedMenu;
    }

    public void setSelectedMenu(AvatarEditorSelectType type) {
        this.avatarSelectedMenu = type;
    }

    public AvatarEditorOption getSelectedAsset() {
        return this.selectedAsset;
    }

    public void setSelectedAsset(AvatarEditorOption selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    public void setAvatarEditorCrashed(boolean crashed) {
        this.avatarEditorCrashed = crashed;
    }

    public void setAvatarEditorPreviewNeedsApply(boolean needsApply) {
        this.avatarEditorPreviewNeedsApply = needsApply;
    }

    public boolean getAvatarEditorCrashed() {
        return this.avatarEditorCrashed;
    }

    public void setAvatarEditorAssetApplyError(boolean value) {
        this.avatarEditorAssetApplyError = value;
    }

    public boolean getAvatarEditorAssetApplyError() {
        return this.avatarEditorAssetApplyError;
    }

    public boolean getAvatarEditorPreviewNeedsApply() {
        return this.avatarEditorPreviewNeedsApply;
    }

    public boolean getIsVersionChecked() {
        return this.versionChecked;
    }

    public void setVersionChecked(boolean value) {
        this.versionChecked = value;
    }

    public void setActivePivotPane(Class<? extends PivotActivity> pivotClass, Class<? extends ScreenLayout> screenClass) {
        this.activePivotPaneIndexMap.put(pivotClass, screenClass);
    }

    public Class<? extends ScreenLayout> getAndResetActivePivotPaneClass(Class<? extends PivotActivity> pivotClass) {
        if (!this.activePivotPaneIndexMap.containsKey(pivotClass)) {
            return null;
        }
        Class<? extends ScreenLayout> screenClass = (Class) this.activePivotPaneIndexMap.get(pivotClass);
        this.activePivotPaneIndexMap.remove(pivotClass);
        return screenClass;
    }

    public void setFriendListUpdated(boolean updated) {
        this.friendListUpdated = updated;
    }

    public boolean getFriendListUpdated() {
        return this.friendListUpdated;
    }

    public void setSelectedMediaItemData(EDSV2MediaItem item) {
        this.selectedMediaItemData = item;
    }

    public EDSV2MediaItem getSelectedMediaItemData() {
        return this.selectedMediaItemData;
    }

    public void setActivityParentMediaItemData(EDSV2MediaItem item) {
        this.activityParentMediaItemData = item;
    }

    public EDSV2MediaItem getActivityParentMediaItemData() {
        return this.activityParentMediaItemData;
    }

    public void setDetailPivotData(DetailPivotPaneData[] data) {
        this.detailPivotData = data;
    }

    public DetailPivotPaneData[] getDetailPivotData() {
        return this.detailPivotData;
    }

    public void setSelectedActivityData(EDSV2ActivityItem data) {
        this.selectedActivityData = data;
    }

    public EDSV2ActivityItem getSelectedActivityData() {
        return this.selectedActivityData;
    }

    public void setIsAutoLaunch(boolean isAutoLaunch) {
        this.isAutoLaunch = isAutoLaunch;
    }

    public boolean getIsAutoLaunch() {
        return this.isAutoLaunch;
    }

    public void setForceRefreshProfile(boolean forceRefresh) {
        this.forceRefreshProfile = forceRefresh;
    }

    public boolean getForceRefreshProfile() {
        return this.forceRefreshProfile;
    }

    public String getSearchTag() {
        return this.searchTag;
    }

    public void setSearchTag(String searchTag) {
        if (searchTag == null || searchTag.length() <= MAX_SEARCH_TEXT_LENGTH) {
            this.searchTag = searchTag;
        } else {
            this.searchTag = searchTag.substring(0, MAX_SEARCH_TEXT_LENGTH);
        }
    }

    public List<EDSV2SearchFilterCount> getSearchResultFilterCountList() {
        return this.searchResultFilterCountList;
    }

    public void setSearchResultFilterCountList(List<EDSV2SearchFilterCount> searchResultFilterCountList) {
        this.searchResultFilterCountList = searchResultFilterCountList;
    }

    public EDSV2SearchFilterType getSelectedFilter() {
        return this.selectedFilter;
    }

    public void setSelectedFilter(EDSV2SearchFilterType selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    public void setAutoLoginStarted(boolean autoLoginStarted) {
        this.autoLoginStarted = autoLoginStarted;
    }

    public boolean getAutoLoginStarted() {
        return this.autoLoginStarted;
    }

    public Class<? extends ActivityBase> getDefaultScreenClass() {
        return this.defaultScreenClass;
    }

    public void setDefaultScreenClass(Class<? extends ActivityBase> screenClass) {
        this.defaultScreenClass = screenClass;
    }

    public CollectionFilter getSelectedCollectionFilter() {
        return this.selectedCollectionFilter;
    }

    public void setSelectedCollectionFilter(CollectionFilter filter) {
        this.selectedCollectionFilter = filter;
    }

    public void setLaunchTitleIsBrowser(boolean v) {
        this.launchTitleIsBrowser = v;
    }

    public boolean getLaunchTitleIsBrowser() {
        return this.launchTitleIsBrowser;
    }

    public boolean getIsTablet() {
        return XLEApplication.Resources.getBoolean(R.bool.isLandscapeOnly);
    }

    public String getPivotTitle() {
        return this.pivotTitle;
    }

    public void setPivotTitle(String pivotTitle) {
        this.pivotTitle = pivotTitle;
    }

    public CompareGameInfo getSelectedCompareGameInfo() {
        return this.selectedCompareGameInfo;
    }

    public void setSelectedCompareGameInfo(CompareGameInfo selectedCompareGameInfo) {
        this.selectedCompareGameInfo = selectedCompareGameInfo;
    }

    public void setHideCollectionFilter(boolean isHide) {
        this.hideCollectionFilter = isHide;
    }

    public boolean getHideCollectionFilter() {
        return this.hideCollectionFilter;
    }
}
