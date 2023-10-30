package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.pivot.Pivot;
import com.microsoft.xbox.xle.viewmodel.TitleBarViewModel;

public class MainPivotActivity extends PivotActivity {
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.main_pivot_activity);
        this.pivot = (Pivot) findViewById(R.id.main_pivot);
        this.pivot.onCreate();
    }

    public void onCreateContentView() {
    }

    public void onStart() {
        this.viewModel = new TitleBarViewModel();
        super.onStart();
    }

    protected String getActivityName() {
        return "MainPivot";
    }

    protected String getChannelName() {
        return ActivityBase.homeChannel;
    }
}
