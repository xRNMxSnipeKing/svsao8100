package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.FriendsListActivityViewModel;

public class FriendsListActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new FriendsListActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.friends_list_activity);
        setAppBarLayout(R.layout.appbar_searchrefresh, false, false);
    }

    protected String getActivityName() {
        return "FriendsList";
    }

    protected String getChannelName() {
        return ActivityBase.socialChannel;
    }
}
