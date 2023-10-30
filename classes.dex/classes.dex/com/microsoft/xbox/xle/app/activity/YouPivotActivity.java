package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.pivot.Pivot;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;

public class YouPivotActivity extends PivotActivity {
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.you_pivot_activity);
        this.pivot = (Pivot) findViewById(R.id.you_pivot);
        this.pivot.onCreate();
        XboxMobileOmnitureTracking.TrackProfileViewOther();
    }

    public void onCreateContentView() {
    }

    protected String getActivityName() {
        return "YouPivot";
    }

    protected String getChannelName() {
        return ActivityBase.socialChannel;
    }
}
