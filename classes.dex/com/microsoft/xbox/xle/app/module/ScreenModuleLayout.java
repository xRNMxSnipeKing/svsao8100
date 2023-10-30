package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public abstract class ScreenModuleLayout extends FrameLayout {
    public abstract ViewModelBase getViewModel();

    public abstract void setViewModel(ViewModelBase viewModelBase);

    public abstract void updateView();

    public ScreenModuleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void setContentView(int screenLayoutId) {
        ((LayoutInflater) XboxApplication.Instance.getSystemService("layout_inflater")).inflate(screenLayoutId, this, true);
    }

    public void onPause() {
    }

    public void onApplicationPause() {
    }

    public void onApplicationResume() {
    }

    public void onResume() {
    }

    public void onDestroy() {
    }

    public void onStart() {
    }

    public void onStop() {
    }
}
