package com.microsoft.xbox.xle.viewmodel;

public abstract class PivotViewModelBase extends ViewModelBase {
    public abstract boolean isBusy();

    public abstract void load(boolean z);

    protected abstract void onStartOverride();

    protected abstract void onStopOverride();

    public PivotViewModelBase() {
        super(true, true);
    }
}
