package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ApplicationSettingManager;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.activity.MainPivotActivity;
import com.microsoft.xbox.xle.app.activity.NowPlayingActivity;
import com.microsoft.xbox.xle.app.activity.VideoPlayerActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;

public class WhatsNewActivityViewModel extends ViewModelBase {
    private static ArrayList<WhatsNew> whatsNewList = new ArrayList();

    public static class WhatsNew {
        public int descriptionId;
        public int imageId;
        public int tittleId;
        public String vedioURL;

        public WhatsNew(int imageId, int tittleId, int descriptionId, String url) {
            this.imageId = imageId;
            this.tittleId = tittleId;
            this.descriptionId = descriptionId;
            this.vedioURL = url;
        }
    }

    static {
        whatsNewList.add(new WhatsNew(R.drawable.item1_image, R.string.whatsnew_tittle1, R.string.whatsnew_body1, getVedioLinkUrl(R.string.whatsnew_link1, R.string.whatsnew_parameter2)));
        whatsNewList.add(new WhatsNew(R.drawable.item2_image, R.string.whatsnew_tittle2, R.string.whatsnew_body2, getVedioLinkUrl(R.string.whatsnew_link2, R.string.whatsnew_parameter2)));
        whatsNewList.add(new WhatsNew(R.drawable.item3_image, R.string.whatsnew_tittle3, R.string.whatsnew_body3, getVedioLinkUrl(R.string.whatsnew_link3, R.string.whatsnew_parameter2)));
    }

    public WhatsNewActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getWhatsNewAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getWhatsNewAdapter(this);
    }

    protected void onStartOverride() {
    }

    protected void onStopOverride() {
    }

    public boolean isBusy() {
        return false;
    }

    public ArrayList<WhatsNew> getWhatsNewList() {
        return whatsNewList;
    }

    public void load(boolean forceRefresh) {
    }

    public void navigateToWhatsNewDetails(WhatsNew item) {
        XLEGlobalData.getInstance().setSelectedDataSource(item.vedioURL);
        NavigateTo(VideoPlayerActivity.class);
    }

    public void navigateToMainPivot() {
        XLEGlobalData.getInstance().setActivePivotPane(MainPivotActivity.class, NowPlayingActivity.class);
        NavigateTo(MainPivotActivity.class, false);
        ApplicationSettingManager.getInstance().setShowWhatsNewLastVersionCode(XboxApplication.getVersionCode());
        AutoConnectAndLaunchViewModel.getInstance().autoConnectAndLaunch();
    }

    public void onBackButtonPressed() {
        if (getIsShowStartNowButton()) {
            super.onBackButtonPressed();
        } else {
            navigateToMainPivot();
        }
    }

    public boolean getIsShowStartNowButton() {
        return XboxApplication.getVersionCode() == ApplicationSettingManager.getInstance().getShowWhatsNewLastVersionCode();
    }

    public static String getVedioLinkUrl(int urlId, int paramId) {
        return JavaUtil.concatenateUrlWithLinkAndParam(XLEApplication.MainActivity.getString(urlId), XLEApplication.MainActivity.getString(paramId), "&");
    }
}
