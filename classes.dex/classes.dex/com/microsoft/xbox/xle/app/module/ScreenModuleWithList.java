package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public abstract class ScreenModuleWithList extends ScreenModuleLayout {
    public abstract XLEListView getListView();

    public abstract ViewModelBase getViewModel();

    public abstract void setViewModel(ViewModelBase viewModelBase);

    public abstract void updateView();

    public ScreenModuleWithList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void restoreListPosition() {
        ViewModelBase vm = getViewModel();
        if (vm != null && getListView() != null) {
            getListView().setSelectionFromTop(vm.getAndResetListPosition(), vm.getAndResetListOffset());
        }
    }

    private void saveListPosition() {
        int offset = 0;
        ViewModelBase vm = getViewModel();
        if (vm != null && getListView() != null) {
            int index = getListView().getFirstVisiblePosition();
            View v = getListView().getChildAt(0);
            if (v != null) {
                offset = v.getTop();
            }
            vm.setListPosition(index, offset);
        }
    }

    public void onStop() {
        super.onStop();
        saveListPosition();
    }
}
