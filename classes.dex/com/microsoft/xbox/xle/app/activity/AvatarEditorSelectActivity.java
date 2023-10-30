package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.AvatarEditorSelectActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;

public class AvatarEditorSelectActivity extends ActivityBase {
    public void onCreate() {
        onCreateContentView();
        this.viewModel = new AvatarEditorSelectActivityViewModel();
        super.onCreate();
    }

    public void onCreateContentView() {
        setContentView(R.layout.avatar_editor_select);
        setAppBarLayout(-1, true, false);
    }

    public AvatarEditorSelectActivityViewModel getViewModel() {
        return (AvatarEditorSelectActivityViewModel) this.viewModel;
    }

    protected String getActivityName() {
        AvatarEditorSelectActivityViewModel viewModel = getViewModel();
        if (viewModel != null) {
            return viewModel.getTag();
        }
        return "AvatarEditorSelect";
    }

    public void onStart() {
        super.onStart();
        if (XLEGlobalData.getInstance().getSelectedMenu() != null) {
            XLEApplication.getMainActivity().addPivotHeader(XLEGlobalData.getInstance().getSelectedMenu().getTitle(), 0, null);
        }
    }

    protected String getChannelName() {
        return ActivityBase.avatarChannel;
    }
}
