package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.viewmodel.ComposeMessageActivityViewModel;

public class ComposeMessageActivity extends ActivityBase {
    public ComposeMessageActivity() {
        super(4);
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new ComposeMessageActivityViewModel();
        XboxMobileOmnitureTracking.TrackMsgCompose();
    }

    public void onCreateContentView() {
        setContentView(R.layout.composemessage_activity);
        setAppBarLayout(R.layout.composemessage_appbar, true, false);
    }

    protected String getActivityName() {
        return "ComposeMessage";
    }

    protected String getChannelName() {
        return ActivityBase.messageChannel;
    }
}
