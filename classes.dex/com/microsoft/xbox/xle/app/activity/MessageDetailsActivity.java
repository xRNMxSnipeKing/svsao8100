package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.viewmodel.MessageDetailsActivityViewModel;

public class MessageDetailsActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new MessageDetailsActivityViewModel();
        XboxMobileOmnitureTracking.TrackMsgRead();
    }

    public void onCreateContentView() {
        setContentView(R.layout.messagedetails_activity);
        setAppBarLayout(R.layout.messagedetails_appbar, false, false);
    }

    protected String getActivityName() {
        return "MessageDetails";
    }

    protected String getChannelName() {
        return ActivityBase.messageChannel;
    }
}
