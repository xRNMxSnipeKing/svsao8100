package com.microsoft.xbox.xle.app.activity;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.CompareGamesActivityPivotViewModel;
import com.microsoft.xbox.xle.viewmodel.CompareGamesActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;

public class CompareGamesActivity extends ActivityBase {
    public CompareGamesActivity(Context context, AttributeSet attrs) {
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        if (XLEApplication.Instance.getIsTablet()) {
            this.viewModel = new CompareGamesActivityViewModel(XLEGlobalData.getInstance().getSelectedGamertag());
        } else {
            this.viewModel = new CompareGamesActivityPivotViewModel(XLEGlobalData.getInstance().getSelectedGamertag());
        }
    }

    public void onCreateContentView() {
        setContentView(R.layout.compare_games_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected boolean shouldTrackPageVisit() {
        return false;
    }

    protected String getActivityName() {
        return "CompareGames";
    }

    protected String getChannelName() {
        return ActivityBase.socialChannel;
    }
}
