package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.xbox.toolkit.ui.XLEGridView;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public abstract class ScreenModuleWithGrid extends ScreenModuleLayout {
    public abstract XLEGridView getGridView();

    public abstract ViewModelBase getViewModel();

    public abstract void setViewModel(ViewModelBase viewModelBase);

    public abstract void updateView();

    public ScreenModuleWithGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void restorePosition() {
        ViewModelBase vm = getViewModel();
        if (vm != null && getGridView() != null) {
            getGridView().setSelection(vm.getAndResetListPosition());
        }
    }
}
