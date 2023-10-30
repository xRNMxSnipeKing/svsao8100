package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.xle.app.adapter.AdapterFactory;

public class AboutActivityViewModel extends ViewModelBase {
    public AboutActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getAboutActivityAdapter(this);
    }

    protected void onStartOverride() {
    }

    protected void onStopOverride() {
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getAboutActivityAdapter(this);
    }

    public boolean isBusy() {
        return false;
    }

    public void load(boolean forceRefresh) {
    }
}
