package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.toolkit.ThreadManager;
import java.util.Timer;
import java.util.TimerTask;

public class MottoBubbleTask {
    private static final int MOTTO_APPEAR_DELAY_MS = 1000;
    private static final int MOTTO_DISAPPEAR_DELAY_MS = 4000;
    private boolean animationReady = false;
    private boolean dataReady = false;
    private boolean mottoShown = false;
    private Timer mottoTimer;
    private Runnable onShowMottoChangedRunnable;
    private boolean showMotto;

    private class HideMottoTask extends TimerTask {
        private HideMottoTask() {
        }

        public void run() {
            MottoBubbleTask.this.setShowMotto(false);
        }
    }

    private class ShowMottoTask extends TimerTask {
        private ShowMottoTask() {
        }

        public void run() {
            if (MottoBubbleTask.this.mottoTimer != null) {
                MottoBubbleTask.this.setShowMotto(true);
                MottoBubbleTask.this.mottoTimer.schedule(new HideMottoTask(), 4000);
                return;
            }
            MottoBubbleTask.this.showMotto = false;
        }
    }

    public MottoBubbleTask(Runnable onShowMottoChangedRunnable) {
        this.onShowMottoChangedRunnable = onShowMottoChangedRunnable;
    }

    public boolean getShowMotto() {
        return this.showMotto;
    }

    private void setShowMotto(boolean value) {
        if (this.showMotto != value) {
            this.showMotto = value;
            if (this.onShowMottoChangedRunnable != null) {
                ThreadManager.UIThreadPost(this.onShowMottoChangedRunnable);
            }
        }
    }

    private void showMotto() {
        if (!this.showMotto) {
            this.mottoTimer = new Timer();
            this.mottoTimer.schedule(new ShowMottoTask(), 1000);
        }
    }

    public void cancelMotto() {
        if (this.mottoTimer != null) {
            this.showMotto = false;
            this.mottoTimer.cancel();
            this.mottoTimer = null;
        }
    }

    public void resetReadyFlags() {
        this.showMotto = false;
        this.animationReady = false;
        this.dataReady = false;
        this.mottoShown = false;
    }

    public void setAnimationReady() {
        this.animationReady = true;
        showIfReady();
    }

    public void setDataReady() {
        this.dataReady = true;
        showIfReady();
    }

    private void showIfReady() {
        if (!this.mottoShown && this.animationReady && this.dataReady) {
            this.mottoShown = true;
            showMotto();
        }
    }
}
