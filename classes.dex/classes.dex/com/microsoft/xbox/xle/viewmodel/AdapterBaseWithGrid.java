package com.microsoft.xbox.xle.viewmodel;

import android.view.View;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.MAAS.MAASAnimationType;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.ui.XLEGridView;
import com.microsoft.xbox.xle.anim.XLEMAASAnimationPackageNavigationManager;
import java.util.ArrayList;

public abstract class AdapterBaseWithGrid extends AdapterBaseNormal {
    protected static final String GRID_VIEW_ANIMATION_NAME = "GridView";
    protected XLEGridView gridView = null;

    protected abstract ViewModelBase getViewModel();

    protected void setListView(XLEGridView gridView) {
        XLEAssert.assertTrue(this.gridView != null);
        this.gridView = gridView;
    }

    public ArrayList<XLEAnimation> getAnimateIn(boolean goingBack) {
        XLEAssert.assertNotNull(this.gridView);
        ArrayList<XLEAnimation> animations = super.getAnimateIn(goingBack);
        if (animations != null) {
            XLEMAASAnimationPackageNavigationManager anim = (XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation(GRID_VIEW_ANIMATION_NAME);
            if (anim != null) {
                XLEAnimation gridViewAnimation = anim.compile(MAASAnimationType.ANIMATE_IN, goingBack, this.gridView);
                if (gridViewAnimation != null) {
                    animations.add(gridViewAnimation);
                }
            }
        }
        return animations;
    }

    public ArrayList<XLEAnimation> getAnimateOut(boolean goingBack) {
        XLEAssert.assertNotNull(this.gridView);
        ArrayList<XLEAnimation> animations = super.getAnimateOut(goingBack);
        if (animations != null) {
            XLEMAASAnimationPackageNavigationManager anim = (XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation(GRID_VIEW_ANIMATION_NAME);
            if (anim != null) {
                XLEAnimation gridViewAnimation = anim.compile(MAASAnimationType.ANIMATE_OUT, goingBack, this.gridView);
                if (gridViewAnimation != null) {
                    animations.add(gridViewAnimation);
                }
            }
        }
        return animations;
    }

    public void onStart() {
        MAAS.getInstance().getAnimation(GRID_VIEW_ANIMATION_NAME);
        super.onStart();
    }

    public void onStop() {
        super.onStop();
        saveListPosition();
    }

    private void saveListPosition() {
        int offset = 0;
        ViewModelBase vm = getViewModel();
        if (vm != null && this.gridView != null) {
            int index = this.gridView.getFirstVisiblePosition();
            View v = this.gridView.getChildAt(0);
            if (v != null) {
                offset = v.getTop();
            }
            vm.setListPosition(index, offset);
        }
    }

    protected void restorePosition() {
        ViewModelBase vm = getViewModel();
        if (vm != null && this.gridView != null) {
            this.gridView.setSelection(vm.getAndResetListPosition());
        }
    }
}
