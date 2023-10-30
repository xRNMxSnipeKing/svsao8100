package com.microsoft.xbox.xle.app.activity;

import android.webkit.CookieManager;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.test.automator.Automator;
import com.microsoft.xbox.xle.viewmodel.XboxAuthActivityViewModel;

public class XboxAuthActivity extends ActivityBase {
    public XboxAuthActivity() {
        super(4);
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new XboxAuthActivityViewModel();
        setOnLayoutChangedListener(new Runnable() {
            public void run() {
                XboxAuthActivity.this.actualSetActive();
            }
        });
    }

    public void onDestroy() {
        setOnLayoutChangedListener(null);
        super.onDestroy();
    }

    public void onCreateContentView() {
        setContentView(R.layout.login_activity);
        setAppBarLayout(-1, true, false);
    }

    public void onRehydrateOverride() {
        XLEAssert.assertTrue(false);
    }

    public void onStart() {
        Automator.getInstance().logOut();
        CookieManager.getInstance().removeExpiredCookie();
        CookieManager.getInstance().setAcceptCookie(true);
        super.onStart();
    }

    protected String getActivityName() {
        return "Login";
    }

    protected String getChannelName() {
        return ActivityBase.launchChannel;
    }

    public void onSetActive() {
    }

    private void actualSetActive() {
        if (!getIsActive()) {
            super.onSetActive();
        }
    }
}
