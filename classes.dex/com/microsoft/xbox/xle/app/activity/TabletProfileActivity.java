package com.microsoft.xbox.xle.app.activity;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.TabletProfileActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;

public class TabletProfileActivity extends ActivityBase {
    public TabletProfileActivity(Context context, AttributeSet attrs) {
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new TabletProfileActivityViewModel(XLEGlobalData.getInstance().getSelectedGamertag());
    }

    public void onCreateContentView() {
        setContentView(R.layout.tablet_profile_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    public void onStart() {
        super.onStart();
        if (!XLEApplication.Instance.getIsTablet()) {
            return;
        }
        if (MeProfileModel.getModel().getGamertag().equalsIgnoreCase(XLEGlobalData.getInstance().getSelectedGamertag())) {
            XLEApplication.getMainActivity().addPivotHeader(XLEApplication.Resources.getString(R.string.full_profile_title), 0, null);
        } else {
            XLEApplication.getMainActivity().addPivotHeader(XLEApplication.Resources.getString(R.string.profile_title), 0, null);
        }
    }

    protected boolean shouldTrackPageVisit() {
        return true;
    }

    protected String getActivityName() {
        return "TabletProfile";
    }

    protected String getChannelName() {
        return ActivityBase.profileChannel;
    }
}
