package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.EditProfileActivityViewModel;

public class EditProfileActivity extends ActivityBase {
    public EditProfileActivity() {
        super(4);
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new EditProfileActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.edit_profile_activity);
        setAppBarLayout(R.layout.appbar_savecancel, true, false);
    }

    protected String getActivityName() {
        return "EditProfile";
    }

    protected String getChannelName() {
        return ActivityBase.profileChannel;
    }
}
