package com.microsoft.xbox.xle.viewmodel;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.MAAS.MAASAnimationType;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.ui.appbar.AppBarMenuButton;
import com.microsoft.xbox.xle.anim.XLEMAASAnimationPackageNavigationManager;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.module.ScreenModuleWithGridLayout;
import com.microsoft.xbox.xle.ui.TitleBarView;
import java.util.ArrayList;
import java.util.List;

public abstract class AdapterBaseNormal extends AdapterBase {
    protected static final String AVATAR_VIEW_ANIMATION_NAME = "AvatarView";
    protected static final String CONTENT_ANIMATION_NAME = "Content";
    protected static final String SCREEN_BODY_ANIMATION_NAME = "Screen";
    protected static final String TITLE_BAR_ANIMATION_NAME = "TitleBar";
    protected View content;
    protected View screenBody;
    protected TitleBarView titleBar = ((TitleBarView) findViewById(R.id.title_bar));

    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ int val$state;

        AnonymousClass1(int i) {
            this.val$state = i;
        }

        public void onClick(View v) {
            DialogManager.getInstance().dismissAppBar();
            AdapterBaseNormal.this.getScreenModuleWithGridLayout().setState(this.val$state);
        }
    }

    public ArrayList<XLEAnimation> getAnimateIn(boolean goingBack) {
        ArrayList<XLEAnimation> animations = new ArrayList();
        XLEAnimation screenBodyAnimation = getScreenBodyAnimation(MAASAnimationType.ANIMATE_IN, goingBack);
        if (screenBodyAnimation != null) {
            animations.add(screenBodyAnimation);
        }
        XLEAnimation titleBarAnimation = getTitleBarAnimation(MAASAnimationType.ANIMATE_IN, goingBack);
        if (titleBarAnimation != null) {
            animations.add(titleBarAnimation);
        }
        XLEAnimation contentAnimation = getContentAnimation(MAASAnimationType.ANIMATE_IN, goingBack);
        if (contentAnimation != null) {
            animations.add(contentAnimation);
        }
        return animations;
    }

    public ArrayList<XLEAnimation> getAnimateOut(boolean goingBack) {
        ArrayList<XLEAnimation> animations = new ArrayList();
        XLEAnimation screenBodyAnimation = getScreenBodyAnimation(MAASAnimationType.ANIMATE_OUT, goingBack);
        if (screenBodyAnimation != null) {
            animations.add(screenBodyAnimation);
        }
        XLEAnimation titleBarAnimation = getTitleBarAnimation(MAASAnimationType.ANIMATE_OUT, goingBack);
        if (titleBarAnimation != null) {
            animations.add(titleBarAnimation);
        }
        XLEAnimation contentAnimation = getContentAnimation(MAASAnimationType.ANIMATE_OUT, goingBack);
        if (contentAnimation != null) {
            animations.add(contentAnimation);
        }
        return animations;
    }

    protected XLEAnimation getScreenBodyAnimation(MAASAnimationType animationType, boolean goingBack) {
        XLEAssert.assertNotNull(this.screenBody);
        XLEMAASAnimationPackageNavigationManager anim = (XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation(SCREEN_BODY_ANIMATION_NAME);
        return anim == null ? null : anim.compile(animationType, goingBack, this.screenBody);
    }

    protected XLEAnimation getContentAnimation(MAASAnimationType animationType, boolean goingBack) {
        if (this.content == null) {
            return null;
        }
        XLEMAASAnimationPackageNavigationManager anim = (XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation(CONTENT_ANIMATION_NAME);
        if (anim == null) {
            return null;
        }
        return anim.compile(animationType, goingBack, this.content);
    }

    protected XLEAnimation getTitleBarAnimation(MAASAnimationType animationType, boolean goingBack) {
        if (this.titleBar.getVisibility() != 0) {
            return null;
        }
        XLEMAASAnimationPackageNavigationManager anim = (XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation(TITLE_BAR_ANIMATION_NAME);
        if (anim == null) {
            return null;
        }
        return anim.compile(animationType, goingBack, this.titleBar);
    }

    protected void updateLoadingIndicator(boolean isLoading) {
        if (this.isActive) {
            this.titleBar.updateIsLoading(isLoading);
        }
    }

    public void onSetInactive() {
        super.onSetInactive();
        this.titleBar.updateIsLoading(false);
    }

    public void onStart() {
        this.titleBar = (TitleBarView) findViewById(R.id.title_bar);
        MAAS.getInstance().getAnimation(SCREEN_BODY_ANIMATION_NAME);
        MAAS.getInstance().getAnimation(TITLE_BAR_ANIMATION_NAME);
        MAAS.getInstance().getAnimation(CONTENT_ANIMATION_NAME);
        super.onStart();
    }

    protected final List<AppBarMenuButton> getTestMenuButtonsForGridLayout() {
        if (getScreenModuleWithGridLayout() == null || XLEApplication.Instance.getIsTablet()) {
        }
        return super.getTestMenuButtons();
    }

    protected ScreenModuleWithGridLayout getScreenModuleWithGridLayout() {
        return null;
    }

    protected final AppBarMenuButton createMenuButtonForGridLayout(String text, int state) {
        if (getScreenModuleWithGridLayout() != null) {
        }
        return null;
    }
}
