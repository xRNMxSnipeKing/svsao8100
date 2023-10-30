package com.microsoft.xbox.xle.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.PageIndicator;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.appbar.AppBarMenuButton;
import com.microsoft.xbox.toolkit.ui.appbar.ApplicationBarView;
import com.microsoft.xbox.xle.app.ApplicationBarManager.AppBarState;
import com.microsoft.xbox.xle.test.automator.Automator;
import java.util.ArrayList;

public class ApplicationBarManagerPhone extends ApplicationBarManager {
    private static ApplicationBarManagerPhone instance = new ApplicationBarManagerPhone();
    protected XLEButton[] collapsedMediaButtons;
    protected XLEButton[] expandedMediaButtons;
    protected PageIndicator pageIndicator;

    protected ApplicationBarManagerPhone() {
    }

    public static ApplicationBarManagerPhone getInstance() {
        return instance;
    }

    public void onCreate() {
        super.onCreate();
        this.pageIndicator = new PageIndicator(XboxApplication.MainActivity);
        LayoutInflater vi = (LayoutInflater) XLEApplication.MainActivity.getSystemService("layout_inflater");
        ApplicationBarView mediaAppBar = (ApplicationBarView) vi.inflate(R.layout.appbar_media, null);
        XLEAssert.assertNotNull(mediaAppBar);
        this.collapsedMediaButtons = mediaAppBar.getAppBarButtons();
        mediaAppBar.removeAllViews();
        mediaAppBar = (ApplicationBarView) vi.inflate(R.layout.appbar_media, null);
        XLEAssert.assertNotNull(mediaAppBar);
        this.expandedMediaButtons = mediaAppBar.getAppBarButtons();
        mediaAppBar.removeAllViews();
    }

    public void setTotalPageCount(int totalCount) {
        if (this.pageIndicator != null) {
            this.pageIndicator.setTotalPageCount(totalCount);
        }
    }

    public void setCurrentPage(int pageIndex) {
        if (this.pageIndicator != null) {
            this.pageIndicator.setCurrentPage(pageIndex);
        }
    }

    public void addNewExpandedButtons(XLEButton[] buttons) {
        this.expandedAppBarView.cleanup();
        Automator.getInstance().cleanupListenerHooks();
        if (this.shouldShowNowPlaying) {
            this.expandedAppBarView.getNowPlayingTile().setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManagerPhone.this.expandedAppBar.dismiss();
                    ApplicationBarManagerPhone.this.navigateToNowPlayingDetails();
                }
            });
        }
        if (this.shouldShowNowPlaying) {
            this.expandedAppBarView.addMenuButton(this.globalMenuButtons[0]);
            this.expandedAppBarView.addMenuButton(this.globalMenuButtons[1]);
        }
        if (this.enableMediaTransportControls) {
            for (XLEButton button : this.expandedMediaButtons) {
                this.expandedAppBarView.addIconButton(button);
                int visibility = this.showMediaButton ? 0 : 8;
                if (button.getId() == R.id.appbar_media_play && !NowPlayingGlobalModel.getInstance().isMediaPaused()) {
                    visibility = 8;
                } else if (button.getId() == R.id.appbar_media_pause && NowPlayingGlobalModel.getInstance().isMediaPaused()) {
                    visibility = 8;
                }
                button.setVisibility(visibility);
            }
        }
        if (buttons != null && buttons.length > 0) {
            for (XLEButton button2 : buttons) {
                if (button2 instanceof AppBarMenuButton) {
                    this.expandedAppBarView.addMenuButton(button2);
                } else {
                    this.expandedAppBarView.addIconButton(button2);
                }
            }
        }
        if (this.shouldShowNowPlaying) {
            for (int i = 2; i < this.globalMenuButtons.length; i++) {
                this.expandedAppBarView.addMenuButton(this.globalMenuButtons[i]);
            }
            setButtonClickListener(R.id.appbar_home, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManagerPhone.this.navigateToHome();
                }
            });
            setButtonClickListener(R.id.appbar_settings, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManagerPhone.this.navigateToSettings();
                }
            });
            setButtonClickListener(R.id.appbar_remote, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManagerPhone.this.navigateToRemote();
                }
            });
        }
    }

    public void addNewCollapsedButtons(XLEButton[] buttons) {
        boolean z = true;
        boolean z2 = false;
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
        if (this.enableMediaTransportControls) {
            for (XLEButton button2 : this.collapsedMediaButtons) {
                appBarButtons.add(button2);
                int visibility = this.showMediaButton ? 0 : 8;
                if (button2.getId() == R.id.appbar_media_play && !NowPlayingGlobalModel.getInstance().isMediaPaused()) {
                    visibility = 8;
                } else if (button2.getId() == R.id.appbar_media_pause && NowPlayingGlobalModel.getInstance().isMediaPaused()) {
                    visibility = 8;
                }
                button2.setVisibility(visibility);
            }
        }
        if (buttons != null && buttons.length > 0) {
            for (XLEButton button22 : buttons) {
                if (button22 instanceof AppBarMenuButton) {
                    newScreenHasMenuItems = true;
                } else {
                    appBarButtons.add(button22);
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
            z2 = true;
        } else if (this.currentAppBarState == AppBarState.FULL) {
            z2 = true;
        }
        XLEAssert.assertTrue(z2);
        if (this.shouldShowNowPlaying) {
            z = this.isExpandable;
        }
        XLEAssert.assertTrue(z);
    }

    protected void updateRemoteButton() {
        if (NowPlayingGlobalModel.getInstance().isConnectedToConsole()) {
            setButtonText(R.id.appbar_remote, ApplicationBarManager.TEXT_REMOTE);
        } else {
            setButtonText(R.id.appbar_remote, ApplicationBarManager.TEXT_CONNECT_TO_XBOX);
        }
    }

    public void hide() {
        super.hide();
        if (this.pageIndicator != null) {
            this.pageIndicator.setVisibility(8);
        }
    }

    public void show() {
        super.show();
        if (this.pageIndicator != null) {
            this.pageIndicator.setVisibility(0);
        }
    }

    protected void disableButtons() {
        super.disableButtons();
        this.pageIndicator.setEnabled(false);
    }

    protected void enableButtons() {
        super.enableButtons();
        this.pageIndicator.setEnabled(true);
    }

    public PageIndicator getPageIndicator() {
        return this.pageIndicator;
    }

    protected boolean shouldShowSwapButtonAnimation() {
        return true;
    }
}
