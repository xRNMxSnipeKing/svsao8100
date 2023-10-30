package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.XboxConsoleHelpViewModel;

public class XboxConsoleHelpActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new XboxConsoleHelpViewModel();
    }

    protected String getActivityName() {
        return "XboxConsoleHelp";
    }

    protected String getChannelName() {
        return null;
    }

    public void onCreateContentView() {
        setContentView(R.layout.xbox_console_help_activity);
        setAppBarLayout(-1, true, false);
    }
}
