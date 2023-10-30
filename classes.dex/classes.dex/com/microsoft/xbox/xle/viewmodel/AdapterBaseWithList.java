package com.microsoft.xbox.xle.viewmodel;

import android.view.View;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.MAAS.MAASAnimationType;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.toolkit.ui.appbar.AppBarMenuButton;
import com.microsoft.xbox.xle.anim.XLEMAASAnimationPackageNavigationManager;
import java.util.ArrayList;
import java.util.List;

public abstract class AdapterBaseWithList extends AdapterBaseNormal {
    protected static final String LIST_VIEW_ANIMATION_NAME = "ListView";
    protected XLEListView listView = null;

    protected abstract SwitchPanel getSwitchPanel();

    protected abstract ViewModelBase getViewModel();

    protected void setListView(XLEListView listView) {
        this.listView = listView;
        XLEAssert.assertTrue(this.listView != null);
    }

    public ArrayList<XLEAnimation> getAnimateIn(boolean goingBack) {
        ArrayList<XLEAnimation> animations = super.getAnimateIn(goingBack);
        if (!(this.listView == null || animations == null)) {
            XLEMAASAnimationPackageNavigationManager anim = (XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation(LIST_VIEW_ANIMATION_NAME);
            if (anim != null) {
                XLEAnimation listViewAnimation = anim.compile(MAASAnimationType.ANIMATE_IN, goingBack, this.listView);
                if (listViewAnimation != null) {
                    animations.add(listViewAnimation);
                }
            }
        }
        return animations;
    }

    public ArrayList<XLEAnimation> getAnimateOut(boolean goingBack) {
        ArrayList<XLEAnimation> animations = super.getAnimateOut(goingBack);
        if (!(this.listView == null || animations == null)) {
            XLEMAASAnimationPackageNavigationManager anim = (XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation(LIST_VIEW_ANIMATION_NAME);
            if (anim != null) {
                XLEAnimation listViewAnimation = anim.compile(MAASAnimationType.ANIMATE_OUT, goingBack, this.listView);
                if (listViewAnimation != null) {
                    animations.add(listViewAnimation);
                }
            }
        }
        return animations;
    }

    public void onStart() {
        MAAS.getInstance().getAnimation(LIST_VIEW_ANIMATION_NAME);
        super.onStart();
    }

    public void onStop() {
        super.onStop();
        saveListPosition();
    }

    protected List<AppBarMenuButton> getTestMenuButtons() {
        if (getSwitchPanel() != null) {
        }
        return super.getTestMenuButtons();
    }

    protected AppBarMenuButton createMenuButton(String text, int state) {
        return null;
    }

    private void saveListPosition() {
        int offset = 0;
        ViewModelBase vm = getViewModel();
        if (vm != null && this.listView != null) {
            int index = this.listView.getFirstVisiblePosition();
            View v = this.listView.getChildAt(0);
            if (v != null) {
                offset = v.getTop();
            }
            vm.setListPosition(index, offset);
        }
    }

    protected void restoreListPosition() {
        ViewModelBase vm = getViewModel();
        if (vm != null && this.listView != null) {
            this.listView.setSelectionFromTop(vm.getAndResetListPosition(), vm.getAndResetListOffset());
        }
    }
}
