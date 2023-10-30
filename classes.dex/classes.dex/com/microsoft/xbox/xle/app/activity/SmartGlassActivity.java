package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.SmartGlassViewModel;

public class SmartGlassActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new SmartGlassViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.smart_glass_activity);
        setAppBarLayout(-1, true, false);
    }

    protected boolean isManagingOwnAppBar() {
        return true;
    }

    protected String getActivityName() {
        return "SmartGlass";
    }

    protected String getChannelName() {
        return null;
    }

    public boolean getCanAutoLaunch() {
        return true;
    }

    protected void trackVisit() {
    }
}
