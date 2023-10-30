package com.microsoft.xbox.toolkit.ui;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import java.util.Iterator;
import java.util.Stack;

public class NavigationManager {
    private NavigationManagerAnimationState animationState;
    final Runnable callAfterAnimation;
    private boolean cannotNavigateTripwire;
    private XLEAnimationPackage currentAnimation;
    private boolean goingBack;
    private Stack<ScreenLayout> navigationStack;
    private boolean transitionAnimate;
    private Runnable transitionLambda;

    private enum NavigationManagerAnimationState {
        NONE,
        ANIMATING_IN,
        ANIMATING_OUT,
        COUNT
    }

    private static class NavigationManagerHolder {
        public static final NavigationManager instance = new NavigationManager();

        private NavigationManagerHolder() {
        }
    }

    private NavigationManager() {
        boolean z = true;
        this.navigationStack = new Stack();
        this.currentAnimation = null;
        this.animationState = NavigationManagerAnimationState.NONE;
        this.transitionLambda = null;
        this.goingBack = false;
        this.transitionAnimate = true;
        this.cannotNavigateTripwire = false;
        this.callAfterAnimation = new Runnable() {
            public void run() {
                NavigationManager.this.OnAnimationEnd();
            }
        };
        String str = "You must access navigation manager on UI thread.";
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = false;
        }
        XLEAssert.assertTrue(str, z);
        XLELog.Warning("NavigationManager", "Create a new instance of navigation manager");
    }

    public static NavigationManager getInstance() {
        return NavigationManagerHolder.instance;
    }

    public ScreenLayout getCurrentActivity() {
        if (this.navigationStack.empty()) {
            return null;
        }
        return (ScreenLayout) this.navigationStack.peek();
    }

    public ScreenLayout getPreviousActivity() {
        if (this.navigationStack.empty() || this.navigationStack.size() <= 1) {
            return null;
        }
        return (ScreenLayout) this.navigationStack.get(this.navigationStack.size() - 2);
    }

    public void NavigateTo(Class<? extends ScreenLayout> screenClass, boolean addToStack) {
        if (addToStack) {
            try {
                PushScreen(screenClass);
                return;
            } catch (XLEException e) {
                XLELog.Error("Failed to navigate", e.toString());
                return;
            }
        }
        PopScreensAndReplace(1, screenClass);
    }

    public void OnBackButtonPressed() {
        if (getCurrentActivity() != null) {
            getCurrentActivity().onBackButtonPressed();
        }
    }

    public boolean TEST_isAnimatingIn() {
        return false;
    }

    public boolean TEST_isAnimatingOut() {
        return false;
    }

    public boolean isAnimating() {
        return this.animationState != NavigationManagerAnimationState.NONE;
    }

    public void GoBack() throws XLEException {
        getInstance().PopScreen();
    }

    public boolean ShouldBackCloseApp() {
        return Size() == 1 && this.animationState == NavigationManagerAnimationState.NONE;
    }

    public boolean IsScreenOnStack(Class<? extends ScreenLayout> screenClass) {
        Iterator i$ = this.navigationStack.iterator();
        while (i$.hasNext()) {
            if (((ScreenLayout) i$.next()).getClass().equals(screenClass)) {
                return true;
            }
        }
        return false;
    }

    public int CountPopsToScreen(Class<? extends ScreenLayout> screenClass) {
        int TOP_ELEM = this.navigationStack.size() - 1;
        for (int i = TOP_ELEM; i >= 0; i--) {
            if (((ScreenLayout) this.navigationStack.get(i)).getClass().equals(screenClass)) {
                return TOP_ELEM - i;
            }
        }
        return -1;
    }

    private int Size() {
        return this.navigationStack.size();
    }

    public void RestartCurrentScreen(boolean animate) throws XLEException {
        XLELog.Diagnostic("NavigationManager", "Restart current activity");
        if (this.animationState == NavigationManagerAnimationState.ANIMATING_OUT) {
            OnAnimationEnd();
        } else if (this.animationState == NavigationManagerAnimationState.ANIMATING_IN) {
            OnAnimationEnd();
            PopScreensAndReplace(1, getCurrentActivity().getClass(), animate);
        } else {
            PopScreensAndReplace(1, getCurrentActivity().getClass(), animate);
        }
    }

    public void PopScreen() throws XLEException {
        PopScreens(1);
    }

    public void PopScreens(int popCount) throws XLEException {
        PopScreensAndReplace(popCount, null);
    }

    public void GotoScreenWithPop(Class<? extends ScreenLayout> screenClass) throws XLEException {
        int toPop = CountPopsToScreen(screenClass);
        if (toPop > 0) {
            PopScreensAndReplace(toPop, null, true);
        } else {
            PopScreensAndReplace(Size(), screenClass, true);
        }
    }

    public void PushScreen(Class<? extends ScreenLayout> screenClass) throws XLEException {
        PopScreensAndReplace(0, screenClass, true, false);
    }

    public void PopScreensAndReplace(int popCount, Class<? extends ScreenLayout> newScreenClass) throws XLEException {
        PopScreensAndReplace(popCount, newScreenClass, true);
    }

    public void PopScreensAndReplace(int popCount, Class<? extends ScreenLayout> newScreenClass, boolean animate) throws XLEException {
        PopScreensAndReplace(popCount, newScreenClass, animate, true);
    }

    public void PopScreensAndReplace(final int popCount, Class<? extends ScreenLayout> newScreenClass, boolean animate, boolean goingBack) throws XLEException {
        boolean z = false;
        String str = "You must access navigation manager on UI thread.";
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        }
        XLEAssert.assertTrue(str, z);
        if (this.cannotNavigateTripwire) {
            throw new UnsupportedOperationException("NavigationManager: attempted to execute a recursive navigation in the OnStop/OnStart method.  This is forbidden.");
        }
        ScreenLayout newScreen;
        if (newScreenClass == null) {
            newScreen = null;
        } else {
            try {
                newScreen = (ScreenLayout) newScreenClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception e) {
                throw new XLEException(XLEErrorCode.FAILED_DEV_ERROR, "FIXME: Failed to create a screen of type " + newScreenClass.getName(), e);
            }
        }
        Runnable popAndReplaceRunnable = new Runnable() {
            public void run() {
                NavigationManager.this.cannotNavigateTripwire = true;
                if (NavigationManager.this.getCurrentActivity() != null) {
                    NavigationManager.this.getCurrentActivity().onSetInactive();
                    NavigationManager.this.getCurrentActivity().onPause();
                    NavigationManager.this.getCurrentActivity().onStop();
                }
                for (int i = 0; i < popCount; i++) {
                    NavigationManager.this.getCurrentActivity().onDestroy();
                    NavigationManager.this.navigationStack.pop();
                }
                TextureManager.Instance().purgeResourceBitmapCache();
                if (newScreen != null) {
                    if (NavigationManager.this.getCurrentActivity() != null) {
                        NavigationManager.this.getCurrentActivity().onTombstone();
                    }
                    newScreen.setIsPivotPane(false);
                    NavigationManager.this.navigationStack.push(newScreen);
                    XboxApplication.MainActivity.setContentView(NavigationManager.this.getCurrentActivity());
                    NavigationManager.this.getCurrentActivity().onCreate();
                } else if (NavigationManager.this.getCurrentActivity() != null) {
                    XboxApplication.MainActivity.setContentView(NavigationManager.this.getCurrentActivity());
                    if (NavigationManager.this.getCurrentActivity().getIsTombstoned()) {
                        NavigationManager.this.getCurrentActivity().onRehydrate();
                    }
                }
                if (NavigationManager.this.getCurrentActivity() != null) {
                    NavigationManager.this.getCurrentActivity().onStart();
                    NavigationManager.this.getCurrentActivity().onResume();
                    NavigationManager.this.getCurrentActivity().onSetActive();
                    NavigationManager.this.getCurrentActivity().onAnimateInStarted();
                }
                NavigationManager.this.cannotNavigateTripwire = false;
            }
        };
        switch (this.animationState) {
            case NONE:
                Transition(goingBack, popAndReplaceRunnable, animate);
                return;
            default:
                ReplaceOnAnimationEnd(goingBack, popAndReplaceRunnable, animate);
                return;
        }
    }

    public void onApplicationPause() {
        for (int i = 0; i < this.navigationStack.size(); i++) {
            ((ScreenLayout) this.navigationStack.get(i)).onApplicationPause();
        }
    }

    public void onApplicationResume() {
        for (int i = 0; i < this.navigationStack.size(); i++) {
            ((ScreenLayout) this.navigationStack.get(i)).onApplicationResume();
        }
    }

    public void RemoveScreensFromBackstack(Class<? extends ScreenLayout> inclusiveScreenClass) {
        int i;
        XLEAssert.assertTrue("You must access navigation manager on UI thread.", Thread.currentThread() == ThreadManager.UIThread);
        int initialStackSize = this.navigationStack.size();
        int lastScreenIndex = initialStackSize - 1;
        for (i = 0; i < initialStackSize - 1; i++) {
            if (((ScreenLayout) this.navigationStack.get(i)).getClass().equals(inclusiveScreenClass)) {
                lastScreenIndex = i;
                break;
            }
        }
        if (lastScreenIndex < initialStackSize - 1) {
            for (i = initialStackSize - 2; i >= lastScreenIndex; i--) {
                ScreenLayout screenToRemove = (ScreenLayout) this.navigationStack.get(i);
                if (screenToRemove != null) {
                    screenToRemove.onDestroy();
                }
                this.navigationStack.removeElementAt(i);
            }
        }
    }

    private void Transition(boolean goingBack, Runnable lambda, boolean animate) {
        this.transitionLambda = lambda;
        this.transitionAnimate = animate;
        this.goingBack = goingBack;
        this.currentAnimation = getCurrentActivity() == null ? null : getCurrentActivity().getAnimateOut(goingBack);
        startAnimation(this.currentAnimation, NavigationManagerAnimationState.ANIMATING_OUT);
    }

    private void ReplaceOnAnimationEnd(boolean goingBack, Runnable lambda, boolean animate) {
        boolean z = this.animationState == NavigationManagerAnimationState.ANIMATING_OUT || this.animationState == NavigationManagerAnimationState.ANIMATING_IN;
        XLEAssert.assertTrue(z);
        this.animationState = NavigationManagerAnimationState.ANIMATING_OUT;
        this.transitionLambda = lambda;
        this.transitionAnimate = animate;
        this.goingBack = goingBack;
    }

    private void OnAnimationEnd() {
        switch (this.animationState) {
            case ANIMATING_IN:
                XboxApplication.MainActivity.setAnimationBlocking(false);
                this.animationState = NavigationManagerAnimationState.NONE;
                if (getCurrentActivity() != null) {
                    getCurrentActivity().onAnimateInCompleted();
                    return;
                }
                return;
            case ANIMATING_OUT:
                this.transitionLambda.run();
                XLEAnimationPackage anim = null;
                if (getCurrentActivity() != null) {
                    anim = getCurrentActivity().getAnimateIn(this.goingBack);
                }
                XboxApplication.MainActivity.onBeforeNavigatingIn();
                startAnimation(anim, NavigationManagerAnimationState.ANIMATING_IN);
                return;
            default:
                return;
        }
    }

    private void startAnimation(XLEAnimationPackage anim, NavigationManagerAnimationState state) {
        this.animationState = state;
        this.currentAnimation = anim;
        XboxApplication.MainActivity.setAnimationBlocking(true);
        if (!this.transitionAnimate || anim == null) {
            this.callAfterAnimation.run();
            return;
        }
        anim.setOnAnimationEndRunnable(this.callAfterAnimation);
        anim.startAnimation();
    }
}
