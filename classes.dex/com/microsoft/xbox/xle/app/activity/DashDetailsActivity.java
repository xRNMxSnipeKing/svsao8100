package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.DashDetailsActivityViewModel;

public class DashDetailsActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new DashDetailsActivityViewModel();
    }

    public void onStart() {
        super.onStart();
        XLEApplication.getMainActivity().setPivotTitle(XLEApplication.Resources.getString(R.string.xbox_360_dashboard_title));
    }

    public void onStop() {
        super.onStop();
        XLEApplication.getMainActivity().setPivotTitle(null);
    }

    public void onCreateContentView() {
        setContentView(R.layout.dash_detail_activty);
        setAppBarLayout(-1, false, true);
    }

    protected String getActivityName() {
        return "DashDetails";
    }

    protected String getChannelName() {
        return ActivityBase.detailsChannel;
    }
}
