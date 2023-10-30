package com.microsoft.xbox.xle.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel.NowPlayingState;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.MediaProgressTimer.OnMediaProgressUpdatedListener;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.ui.PageIndicator;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.appbar.AppBarMenuButton;
import com.microsoft.xbox.toolkit.ui.appbar.ApplicationBarView;
import com.microsoft.xbox.xle.app.ApplicationBarManager.AppBarState;
import com.microsoft.xbox.xle.test.automator.Automator;
import java.util.ArrayList;

public class ApplicationBarManagerTablet extends ApplicationBarManager {
    private static final int MAX_PROGRESS = 100;
    private static ApplicationBarManagerTablet instance = new ApplicationBarManagerTablet();
    private boolean isProgressTimerSetup;
    private EDSV2MediaItem mediaItem;

    private ApplicationBarManagerTablet() {
    }

    public static ApplicationBarManagerTablet getInstance() {
        return instance;
    }

    public void onCreate() {
        super.onCreate();
        ApplicationBarView globalAppBar = (ApplicationBarView) ((LayoutInflater) XLEApplication.MainActivity.getSystemService("layout_inflater")).inflate(R.layout.appbar_global, null);
        XLEAssert.assertNotNull(globalAppBar);
        this.globalIconButtons = globalAppBar.getAppBarButtons();
        globalAppBar.removeAllViews();
    }

    public void addNewExpandedButtons(XLEButton[] buttons) {
        this.expandedAppBarView.cleanup();
        Automator.getInstance().cleanupListenerHooks();
        if (this.shouldShowNowPlaying) {
            this.expandedAppBarView.getNowPlayingTile().setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManagerTablet.this.expandedAppBar.dismiss();
                    ApplicationBarManagerTablet.this.navigateToNowPlayingDetails();
                }
            });
        }
        if (this.shouldShowNowPlaying) {
            this.expandedAppBarView.addIconButton(this.globalMenuButtons[0]);
            this.expandedAppBarView.addIconButton(this.globalMenuButtons[1]);
        }
        if (buttons != null && buttons.length > 0) {
            for (XLEButton button : buttons) {
                if (button instanceof AppBarMenuButton) {
                    this.expandedAppBarView.addMenuButton(button);
                } else {
                    this.expandedAppBarView.addIconButton(button);
                }
            }
        }
        if (this.shouldShowNowPlaying) {
            for (int i = 2; i < this.globalMenuButtons.length; i++) {
                this.expandedAppBarView.addMenuButton(this.globalMenuButtons[i]);
            }
            setButtonClickListener(R.id.appbar_home, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManagerTablet.this.navigateToHome();
                }
            });
            setButtonClickListener(R.id.appbar_settings, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManagerTablet.this.navigateToSettings();
                }
            });
            setButtonClickListener(R.id.appbar_remote, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManagerTablet.this.navigateToRemote();
                }
            });
            setButtonEnabled(R.id.appbar_remote, true);
            setButtonVisibility(R.id.appbar_remote, 0);
            setButtonVisibility(R.id.appbar_home, 0);
        }
        updateNowplayingInfo();
    }

    public void addNewCollapsedButtons(XLEButton[] buttons) {
        boolean z = false;
        boolean z2 = true;
        boolean newScreenHasMenuItems = false;
        if (this.previousButtons != null) {
            for (XLEButton button : this.previousButtons) {
                this.collapsedAppBarView.getIconButtonContainer().removeView(button);
            }
            this.previousButtons = null;
        }
        XLELog.Diagnostic("ApplicationBar", "Adding new buttons");
        this.previousButtons = this.newButtons;
        ArrayList<XLEButton> appBarButtons = new ArrayList();
        if (this.shouldShowNowPlaying) {
            appBarButtons.add(this.globalIconButtons[0]);
            appBarButtons.add(this.globalIconButtons[1]);
            setButtonClickListener(R.id.appbar_home, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManagerTablet.this.navigateToHome();
                }
            });
            setButtonClickListener(R.id.appbar_settings, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManagerTablet.this.navigateToSettings();
                }
            });
            setButtonClickListener(R.id.appbar_remote, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManagerTablet.this.navigateToRemote();
                }
            });
        }
        if (buttons != null && buttons.length > 0) {
            for (XLEButton button2 : buttons) {
                if (button2 instanceof AppBarMenuButton) {
                    newScreenHasMenuItems = true;
                } else {
                    appBarButtons.add(button2);
                }
            }
        }
        this.newButtons = (XLEButton[]) appBarButtons.toArray(new XLEButton[0]);
        if (this.previousButtons != null) {
            for (XLEButton onClickListener : this.previousButtons) {
                onClickListener.setOnClickListener(null);
            }
        }
        boolean newScreenHasButtons = (this.newButtons != null && this.newButtons.length > 0) || this.shouldShowNowPlaying;
        if (newScreenHasMenuItems || this.shouldShowNowPlaying) {
            newScreenHasMenuItems = true;
        } else {
            newScreenHasMenuItems = false;
        }
        this.lastAppBarState = this.currentAppBarState;
        XLELog.Diagnostic("ApplicationBar", "Last app bar state: " + this.lastAppBarState);
        if (newScreenHasButtons) {
            this.currentAppBarState = AppBarState.FULL;
        } else {
            this.currentAppBarState = AppBarState.HIDE;
        }
        XLELog.Diagnostic("ApplicationBar", "New app bar state: " + this.currentAppBarState);
        updateIsExpandable(newScreenHasMenuItems);
        if (!this.shouldShowNowPlaying) {
            z = true;
        } else if (this.currentAppBarState == AppBarState.FULL) {
            z = true;
        }
        XLEAssert.assertTrue(z);
        if (this.shouldShowNowPlaying) {
            z2 = this.isExpandable;
        }
        XLEAssert.assertTrue(z2);
        updateNowplayingInfo();
    }

    protected void addNewCollapsedButtonsToView() {
        this.collapsedAppBarView.getIconButtonContainer().removeAllViews();
        if (this.newButtons != null && this.newButtons.length > 0) {
            for (XLEButton button : this.newButtons) {
                this.collapsedAppBarView.addIconButton(button);
            }
        }
        updateMediaButtonVisibility();
        if (this.shouldShowNowPlaying) {
            setButtonClickListener(R.id.appbar_home, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManagerTablet.this.navigateToHome();
                }
            });
            setButtonClickListener(R.id.appbar_remote, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManagerTablet.this.navigateToRemote();
                }
            });
            setButtonEnabled(R.id.appbar_remote, true);
            setButtonVisibility(R.id.appbar_remote, 0);
            setButtonVisibility(R.id.appbar_home, 0);
        }
    }

    public void update(AsyncResult<UpdateData> asyncResult) {
        super.update(asyncResult);
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        boolean isFinal = ((UpdateData) asyncResult.getResult()).getIsFinal();
        XLELog.Diagnostic("ApplicationBarManagerTablet", "Received update: " + type.toString());
        if (isFinal) {
            switch (type) {
                case NowPlayingState:
                case NowPlayingDetail:
                    updateNowplayingInfo();
                    updateMediaButton();
                    return;
                default:
                    return;
            }
        } else if (type == UpdateType.NowPlayingState) {
            updateNowplayingInfo();
            updateMediaButton();
        }
    }

    protected void updateRemoteButton() {
    }

    public void setShouldShowNowPlaying(boolean shouldShowNowPlaying) {
        if (this.shouldShowNowPlaying != shouldShowNowPlaying) {
            super.setShouldShowNowPlaying(shouldShowNowPlaying);
            updateNowplayingInfo();
        }
    }

    private void updateMediaButton() {
        boolean visible = NowPlayingGlobalModel.getInstance().isMediaInProgress();
        if (this.showMediaButton != visible) {
            this.showMediaButton = visible;
            updateMediaButtonVisibility();
        }
    }

    private void updateNowplayingInfo() {
        int i;
        int i2 = 0;
        boolean visible = false;
        boolean shouldShowMediaProgress = false;
        if (this.shouldShowNowPlaying) {
            if (!(NowPlayingGlobalModel.getInstance().getNowPlayingState() == NowPlayingState.Connecting || NowPlayingGlobalModel.getInstance().getNowPlayingState() == NowPlayingState.Disconnected)) {
                visible = true;
                if (this.mediaItem != NowPlayingGlobalModel.getInstance().getNowPlayingMediaItem()) {
                    TextView nowPlayingTitle;
                    this.mediaItem = NowPlayingGlobalModel.getInstance().getNowPlayingMediaItem();
                    View nowplayingView = this.collapsedAppBarView.findViewById(R.id.expanded_appbar_now_playing_title);
                    if (nowplayingView != null) {
                        nowPlayingTitle = (TextView) nowplayingView;
                        if (this.mediaItem != null) {
                            nowPlayingTitle.setText(this.mediaItem.getTitle());
                        } else {
                            nowPlayingTitle.setText(null);
                        }
                    }
                    nowplayingView = this.expandedAppBarView.findViewById(R.id.expanded_appbar_now_playing_title);
                    if (nowplayingView != null) {
                        nowPlayingTitle = (TextView) nowplayingView;
                        if (this.mediaItem != null) {
                            nowPlayingTitle.setText(this.mediaItem.getTitle());
                        } else {
                            nowPlayingTitle.setText(null);
                        }
                    }
                }
            }
            if (visible && NowPlayingGlobalModel.getInstance().isMediaInProgress() && NowPlayingGlobalModel.getInstance().getMediaDurationInSeconds() > 0) {
                shouldShowMediaProgress = true;
            } else {
                shouldShowMediaProgress = false;
            }
            if (shouldShowMediaProgress) {
                setupMediaUpdateListener();
            } else {
                clearMediaUpdateListner();
            }
        }
        View view = this.collapsedAppBarView.findViewById(R.id.expanded_appbar_now_playing_container);
        if (view != null) {
            if (visible) {
                i = 0;
            } else {
                i = 8;
            }
            view.setVisibility(i);
        }
        view = this.expandedAppBarView.findViewById(R.id.expanded_appbar_now_playing_container);
        if (view != null) {
            if (visible) {
                i = 0;
            } else {
                i = 8;
            }
            view.setVisibility(i);
        }
        view = this.collapsedAppBarView.findViewById(R.id.expanded_appbar_progress_bar);
        if (view != null) {
            if (shouldShowMediaProgress) {
                i = 0;
            } else {
                i = 8;
            }
            view.setVisibility(i);
        }
        view = this.expandedAppBarView.findViewById(R.id.expanded_appbar_progress_bar);
        if (view != null) {
            if (shouldShowMediaProgress) {
                i = 0;
            } else {
                i = 8;
            }
            view.setVisibility(i);
        }
        view = this.collapsedAppBarView.findViewById(R.id.expanded_appbar_now_playing_position);
        if (view != null) {
            if (shouldShowMediaProgress) {
                i = 0;
            } else {
                i = 8;
            }
            view.setVisibility(i);
        }
        view = this.expandedAppBarView.findViewById(R.id.expanded_appbar_now_playing_position);
        if (view != null) {
            if (!shouldShowMediaProgress) {
                i2 = 8;
            }
            view.setVisibility(i2);
        }
    }

    public PageIndicator getPageIndicator() {
        return null;
    }

    public void setTotalPageCount(int totalCount) {
    }

    public void setCurrentPage(int pageIndex) {
    }

    protected boolean shouldShowSwapButtonAnimation() {
        return false;
    }

    private void setupMediaUpdateListener() {
        XLELog.Diagnostic("ApplicationBarManagerTablet", "setupMediaUpdateListener isProgressTimerSetup===" + this.isProgressTimerSetup);
        if (!this.isProgressTimerSetup) {
            this.isProgressTimerSetup = true;
            NowPlayingGlobalModel.getInstance().setOnMediaProgressUpdatedRunnable(new OnMediaProgressUpdatedListener() {
                public void onUpdate(long positionInSeconds, long durationInSeconds) {
                    String positionString = JavaUtil.getTimeStringMMSS(positionInSeconds);
                    String durationString = JavaUtil.getTimeStringMMSS(durationInSeconds);
                    String positionStr = String.format("%s / %s", new Object[]{positionString, durationString});
                    View nowplayingView = ApplicationBarManagerTablet.this.collapsedAppBarView.findViewById(R.id.expanded_appbar_now_playing_position);
                    if (nowplayingView != null) {
                        ((TextView) nowplayingView).setText(positionStr);
                    }
                    nowplayingView = ApplicationBarManagerTablet.this.expandedAppBarView.findViewById(R.id.expanded_appbar_now_playing_position);
                    if (nowplayingView != null) {
                        ((TextView) nowplayingView).setText(positionStr);
                    }
                    int progress = 0;
                    if (durationInSeconds != 0) {
                        progress = (int) ((double) ((100 * positionInSeconds) / durationInSeconds));
                    }
                    nowplayingView = ApplicationBarManagerTablet.this.collapsedAppBarView.findViewById(R.id.expanded_appbar_progress_bar);
                    if (nowplayingView != null) {
                        ((ProgressBar) nowplayingView).setProgress(progress);
                    }
                    nowplayingView = ApplicationBarManagerTablet.this.expandedAppBarView.findViewById(R.id.expanded_appbar_progress_bar);
                    if (nowplayingView != null) {
                        ((ProgressBar) nowplayingView).setProgress(progress);
                    }
                }
            });
        }
    }

    private void clearMediaUpdateListner() {
        XLELog.Diagnostic("ApplicationBarManagerTablet", "clearMediaUpdateListner isProgressTimerSetup===" + this.isProgressTimerSetup);
        NowPlayingGlobalModel.getInstance().setOnMediaProgressUpdatedRunnable(null);
        this.isProgressTimerSetup = false;
    }
}
