package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.MAAS.MAASAnimationType;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.ui.XLEGridView;
import com.microsoft.xbox.xle.anim.XLEMAASAnimationPackageNavigationManager;
import java.util.ArrayList;

public abstract class AdapterBaseWithGridAndFloatingAvatarView extends AdapterBaseWithFloatingAvatarView {
    protected static final String GRID_VIEW_ANIMATION_NAME = "GridView";
    protected XLEGridView gridView = null;

    protected void setListView(XLEGridView gridView) {
        XLEAssert.assertTrue(this.gridView != null);
        this.gridView = gridView;
    }

    public ArrayList<XLEAnimation> getAnimateIn(boolean goingBack) {
        XLEAssert.assertNotNull(this.gridView);
        ArrayList<XLEAnimation> animations = super.getAnimateIn(goingBack);
        XLEAnimation gridViewAnimation = ((XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation(GRID_VIEW_ANIMATION_NAME)).compile(MAASAnimationType.ANIMATE_IN, goingBack, this.gridView);
        if (gridViewAnimation != null) {
            animations.add(gridViewAnimation);
        }
        return animations;
    }

    public ArrayList<XLEAnimation> getAnimateOut(boolean goingBack) {
        XLEAssert.assertNotNull(this.gridView);
        ArrayList<XLEAnimation> animations = super.getAnimateOut(goingBack);
        XLEAnimation gridViewAnimation = ((XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation(GRID_VIEW_ANIMATION_NAME)).compile(MAASAnimationType.ANIMATE_OUT, goingBack, this.gridView);
        if (gridViewAnimation != null) {
            animations.add(gridViewAnimation);
        }
        return animations;
    }

    public void onStart() {
        MAAS.getInstance().getAnimation(GRID_VIEW_ANIMATION_NAME);
        super.onStart();
    }
}
