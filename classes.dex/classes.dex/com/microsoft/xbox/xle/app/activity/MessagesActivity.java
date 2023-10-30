package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.viewmodel.MessagesActivityViewModel;

public class MessagesActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new MessagesActivityViewModel();
        XboxMobileOmnitureTracking.TrackMsgCheck();
    }

    public void onCreateContentView() {
        setContentView(R.layout.messages_activity);
        setAppBarLayout(R.layout.messages_appbar, false, false);
    }

    protected String getActivityName() {
        return "Messages";
    }

    protected String getChannelName() {
        return ActivityBase.messageChannel;
    }
}
