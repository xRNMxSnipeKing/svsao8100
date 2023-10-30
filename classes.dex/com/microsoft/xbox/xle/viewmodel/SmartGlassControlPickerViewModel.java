package com.microsoft.xbox.xle.viewmodel;

public class SmartGlassControlPickerViewModel {
    private boolean canBrowserControlBeActive = false;
    private boolean canGestureControlBeActive = false;
    private boolean canTextControlBeActive = false;

    public void onControllerStateChanged(boolean canGestureControlBeActive, boolean canBrowserControlBeActive, boolean canTextControlBeActive, int preferredControl) {
        this.canBrowserControlBeActive = canBrowserControlBeActive;
        this.canGestureControlBeActive = canGestureControlBeActive;
        this.canTextControlBeActive = canTextControlBeActive;
    }

    public boolean getCanBrowserControlBeActive() {
        return this.canBrowserControlBeActive;
    }

    public boolean getCanGestureControlBeActive() {
        return this.canGestureControlBeActive;
    }

    public boolean getCanTextControlBeActive() {
        return this.canTextControlBeActive;
    }
}
