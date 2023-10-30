package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.FriendsSelectorActivityViewModel;

public class FriendsSelectorActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new FriendsSelectorActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.friends_picker_activity);
        setAppBarLayout(R.layout.friends_picker_appbar, true, false);
    }

    protected String getActivityName() {
        return "FriendsSelector";
    }

    protected String getChannelName() {
        return ActivityBase.messageChannel;
    }
}
