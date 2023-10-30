package com.microsoft.xbox.xle.viewmodel;

import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.ActiveTitleInfo;
import com.microsoft.xbox.service.model.LRCControlKey;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.SessionModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.smartglass.ScrollPoint;
import com.microsoft.xbox.service.model.smartglass.SmartGlassXBLBrowserControllerViewModel;
import com.microsoft.xbox.service.model.smartglass.SmartGlassXBLSharedControllerViewModel;
import com.microsoft.xbox.service.model.smartglass.TouchFrame;
import com.microsoft.xbox.service.model.smartglass.XBLText;
import com.microsoft.xbox.service.model.smartglass.XBLTextInputState;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionActiveTitleInfoListener;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.SoundManager;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEConstants;
import com.microsoft.xbox.toolkit.XLEGestureOverlayProcessor.GestureType;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxAppMeasurement;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.activity.SmartGlassActivity;
import com.microsoft.xbox.xle.app.adapter.SmartGlassAdapter;
import com.microsoft.xbox.xle.ui.SmartGlassFamilyPasscodeControl.FamilyPasscodeButton;
import java.lang.ref.WeakReference;

public class SmartGlassViewModel extends ViewModelBase implements ICompanionSessionActiveTitleInfoListener {
    private static int launchTitleBrowserTimeoutVersion = 0;
    private final int FAST_INPUT_SAMPLING_MS;
    private final int MEDIUM_INPUT_SAMPLING_MS;
    private final int MEDIUM_THRESHOLD;
    private final int MS_PER_SECOND;
    private final int SLOW_INPUT_SAMPLING_MS;
    private final int SLOW_THRESHOLD;
    public SmartGlassControlPickerViewModel controlPickerViewModel;
    private XBLText replacementText;
    private XBLTextInputState replacementTextState;
    private String replacementUrl;
    private SmartGlassXBLSharedControllerViewModel sgControllerViewModel;
    private final ScreenStateSwitcher state;
    private long timeToRecalculateLatency;
    private long titleRequestStartMs;
    private long xboxLatency;

    public enum ScreenState {
        LOADING,
        CONTROL_PICKER,
        TEXT,
        GESTURE,
        BROWSER,
        TEXT_FAMILY_PASSCODE
    }

    private class ScreenStateSwitcher {
        private ScreenState stateCurrent;
        private ScreenState stateLevel1;

        private ScreenStateSwitcher() {
            this.stateLevel1 = ScreenState.LOADING;
            this.stateCurrent = ScreenState.LOADING;
        }

        public void changeState(ScreenState state) {
            if (state == ScreenState.BROWSER || state == ScreenState.GESTURE || state == ScreenState.LOADING) {
                this.stateLevel1 = state;
            } else if (this.stateCurrent == ScreenState.BROWSER || this.stateCurrent == ScreenState.GESTURE) {
                this.stateLevel1 = this.stateCurrent;
            }
            this.stateCurrent = state;
            int nativeState = SmartGlassViewModel.this.screenStateToNativeInt(this.stateCurrent);
            if (nativeState != 0) {
                SmartGlassViewModel.this.sgControllerViewModel.setCurrentControllerState(nativeState);
            }
        }

        public void initializeLevel1IfNecessary(boolean canBrowserControlBeActive) {
            if (this.stateLevel1 == ScreenState.LOADING) {
                this.stateLevel1 = canBrowserControlBeActive ? ScreenState.BROWSER : ScreenState.GESTURE;
            }
        }

        public ScreenState getStateLevel1() {
            return this.stateLevel1;
        }

        public ScreenState getState() {
            return this.stateCurrent;
        }
    }

    public SmartGlassViewModel() {
        this.state = new ScreenStateSwitcher();
        this.replacementText = null;
        this.replacementUrl = null;
        this.replacementTextState = null;
        this.SLOW_INPUT_SAMPLING_MS = 48;
        this.MEDIUM_INPUT_SAMPLING_MS = 32;
        this.FAST_INPUT_SAMPLING_MS = 16;
        this.SLOW_THRESHOLD = 625;
        this.MEDIUM_THRESHOLD = 325;
        this.MS_PER_SECOND = EDSV2MediaType.MEDIATYPE_MOVIE;
        this.sgControllerViewModel = SmartGlassXBLSharedControllerViewModel.getInstance();
        this.adapter = new SmartGlassAdapter(this);
        this.controlPickerViewModel = new SmartGlassControlPickerViewModel();
    }

    public void onResume() {
        if (this.adapter != null) {
            this.adapter.onResume();
        }
        this.state.changeState(ScreenState.LOADING);
        if (XLEGlobalData.getInstance().getLaunchTitleIsBrowser()) {
            final WeakReference<AdapterBase> adapterweakptr = new WeakReference(this.adapter);
            final int version = launchTitleBrowserTimeoutVersion + 1;
            launchTitleBrowserTimeoutVersion = version;
            ThreadManager.UIThreadPostDelayed(new Runnable() {
                public void run() {
                    if (version == SmartGlassViewModel.launchTitleBrowserTimeoutVersion) {
                        AdapterBase adapterptr = (AdapterBase) adapterweakptr.get();
                        XLEGlobalData.getInstance().setLaunchTitleIsBrowser(false);
                        if (adapterptr != null) {
                            adapterptr.updateView();
                        }
                    }
                }
            }, 15000);
        }
        if (SessionModel.getInstance().getDisplayedSessionState() == 2) {
            startNativeViewModel();
        }
        this.adapter.updateView();
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                SessionModel.getInstance().load(false);
            }
        });
    }

    public void onPause() {
        super.onPause();
        XLEGlobalData.getInstance().setLaunchTitleIsBrowser(false);
        launchTitleBrowserTimeoutVersion++;
        stopNativeViewModel();
    }

    public void onRehydrate() {
        this.adapter = new SmartGlassAdapter(this);
    }

    public boolean isBusy() {
        return false;
    }

    public void load(boolean forceRefresh) {
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onStartOverride() {
        SessionModel.getInstance().addObserver(this);
        CompanionSession.getInstance().addCompanionSessionActiveTitleInfoListener(this);
        this.xboxLatency = 0;
        this.timeToRecalculateLatency = 1000;
    }

    public void onStopOverride() {
        SessionModel.getInstance().removeObserver(this);
        CompanionSession.getInstance().removeCompanionSessionActiveTitleInfoListener(this);
    }

    public ScreenState getSwitchPanelState() {
        if (XLEGlobalData.getInstance().getLaunchTitleIsBrowser() && this.state.getState() == ScreenState.GESTURE) {
            return ScreenState.LOADING;
        }
        return this.state.getState();
    }

    public ScreenState getStateLevel1() {
        return this.state.getStateLevel1();
    }

    public void showControlPicker() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.state.changeState(ScreenState.CONTROL_PICKER);
        this.adapter.updateView();
    }

    public void navigateToBrowser() {
        if (this.controlPickerViewModel != null && this.controlPickerViewModel.getCanBrowserControlBeActive()) {
            XboxAppMeasurement.getInstance().trackVisit("SmartGlass", "SGBrowser");
            this.state.changeState(ScreenState.BROWSER);
            SmartGlassXBLBrowserControllerViewModel.getInstance().sendAppInfo();
            SmartGlassXBLBrowserControllerViewModel.getInstance().updateUrl();
        }
        XLEGlobalData.getInstance().setLaunchTitleIsBrowser(false);
        updateXboxLatency();
        this.adapter.updateView();
    }

    public void updateXboxLatency() {
        if (!SmartGlassXBLSharedControllerViewModel.getInstance().getCanBrowserControlBeActive()) {
            return;
        }
        if (CompanionSession.getInstance().getCurrentSessionState() == 2) {
            this.titleRequestStartMs = SystemClock.uptimeMillis();
            CompanionSession.getInstance().GetActiveTitleInfo();
            return;
        }
        XLELog.Diagnostic("SmartGlassViewModel", "session not connected, ignore this updatexbox latency call. ");
    }

    public void navigateToController() {
        if (this.controlPickerViewModel != null && this.controlPickerViewModel.getCanGestureControlBeActive()) {
            XboxAppMeasurement.getInstance().trackVisit("SmartGlass", "SGGesture");
            this.state.changeState(ScreenState.GESTURE);
        }
        this.adapter.updateView();
    }

    public void navigateToKeyboard() {
        if (this.controlPickerViewModel != null && this.controlPickerViewModel.getCanTextControlBeActive()) {
            XboxAppMeasurement.getInstance().trackVisit("SmartGlass", "SGKeyboard");
            this.sgControllerViewModel.getTextInputViewModel().updateKeyboardState();
            XBLTextInputState textstate = this.sgControllerViewModel.getTextInputViewModel().getTextInputState();
            if (textstate != null) {
                if (textstate.showPassCode) {
                    this.state.changeState(ScreenState.TEXT_FAMILY_PASSCODE);
                } else {
                    this.state.changeState(ScreenState.TEXT);
                }
            }
        }
        this.adapter.updateView();
    }

    public void navigateToGuide() {
        SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_PAD_XE);
        if (!this.controlPickerViewModel.getCanBrowserControlBeActive()) {
            if (this.controlPickerViewModel != null && this.controlPickerViewModel.getCanGestureControlBeActive()) {
                this.state.changeState(ScreenState.GESTURE);
            }
            dismissTextEntryOrControlPickerLayer();
        }
    }

    public SmartGlassControlPickerViewModel getControlPickerViewModel() {
        return this.controlPickerViewModel;
    }

    public void onGesture(GestureType type) {
        switch (type) {
            case TAP:
                SoundManager.getInstance().playSound(R.raw.sndbuttonselectandroid);
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_PAD_A);
                return;
            case SWIPE_LEFT:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_PAD_DPAD_LEFT);
                return;
            case SWIPE_RIGHT:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_PAD_DPAD_RIGHT);
                return;
            case SWIPE_UP:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_PAD_DPAD_UP);
                return;
            case SWIPE_DOWN:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_PAD_DPAD_DOWN);
                return;
            default:
                return;
        }
    }

    public void onDpadB() {
        SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_PAD_B);
    }

    public void onDpadX() {
        SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_PAD_X);
    }

    public void onDpadY() {
        SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_PAD_Y);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        boolean goBack = false;
        if (!(asyncResult == null || asyncResult.getResult() == null)) {
            switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
                case SessionState:
                    switch (SessionModel.getInstance().getDisplayedSessionState()) {
                        case 0:
                        case 3:
                            stopNativeViewModel();
                            break;
                        case 1:
                            this.state.changeState(ScreenState.LOADING);
                            this.adapter.updateView();
                            break;
                        case 2:
                            startNativeViewModel();
                            this.adapter.updateView();
                            break;
                        default:
                            break;
                    }
                case SessionRetryConnectFailed:
                    goBack = true;
                    break;
            }
        }
        XLELog.Diagnostic("SmartGlassViewModel", "Check drain slow connection warning");
        if (SessionModel.getInstance().drainShouldShowSlowConnectionDialog()) {
            showMustActDialog(XLEApplication.Resources.getString(R.string.SlowConnectionDetected_Header), XLEApplication.Resources.getString(R.string.SlowConnectionDescription1), XLEApplication.Resources.getString(R.string.ContinueWithoutWifi), new Runnable() {
                public void run() {
                }
            }, true);
        }
        if (goBack) {
            goBack();
        }
    }

    public XBLText getText() {
        return this.sgControllerViewModel.getTextInputViewModel().getText();
    }

    public XBLTextInputState getTextState() {
        return this.sgControllerViewModel.getTextInputViewModel().getTextInputState();
    }

    public void setText(XBLText newText) {
        onTextChanged(this.sgControllerViewModel.getTextInputViewModel().SetText(newText));
    }

    public void onTextOKButtonClick() {
        if (!getTextState().isKeyboardRunestrip || SessionModel.getInstance().getCurrentTitleId() == XLEConstants.BROWSER_TITLE_ID) {
            this.sgControllerViewModel.getTextInputViewModel().OnOKButtonClick();
            return;
        }
        SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_PAD_DPAD_DOWN);
        dismissTextEntryOrControlPickerLayer();
    }

    public void onTextCancelButtonClick() {
        this.sgControllerViewModel.getTextInputViewModel().OnCancelButtonClick();
    }

    public void onBackButtonPressed() {
        switch (getSwitchPanelState()) {
            case TEXT:
            case TEXT_FAMILY_PASSCODE:
            case CONTROL_PICKER:
                dismissTextEntryOrControlPickerLayer();
                return;
            default:
                super.onBackButtonPressed();
                return;
        }
    }

    public XBLText drainReplacementText() {
        XBLText rv = this.replacementText;
        this.replacementText = null;
        return rv;
    }

    public String drainReplacementUrl() {
        String rv = this.replacementUrl;
        this.replacementUrl = null;
        return rv;
    }

    public XBLTextInputState drainReplacementTextState() {
        XBLTextInputState rv = this.replacementTextState;
        this.replacementTextState = null;
        return rv;
    }

    public void dismissTextEntryOrControlPickerLayer() {
        if (this.state.getStateLevel1() == ScreenState.BROWSER && this.controlPickerViewModel.getCanBrowserControlBeActive()) {
            navigateToBrowser();
        } else if (this.state.getStateLevel1() == ScreenState.GESTURE && this.controlPickerViewModel.getCanGestureControlBeActive()) {
            navigateToController();
        } else if (this.state.getStateLevel1() == ScreenState.LOADING) {
            this.state.changeState(ScreenState.LOADING);
            this.adapter.updateView();
        } else {
            goBack();
        }
    }

    public void onFamilyPasscodeButton(FamilyPasscodeButton button) {
        switch (button) {
            case BUTTON1:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_1);
                return;
            case BUTTON2:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_2);
                return;
            case BUTTON3:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_3);
                return;
            case BUTTON4:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_4);
                return;
            case BUTTON5:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_5);
                return;
            case BUTTON6:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_6);
                return;
            case BUTTON7:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_7);
                return;
            case BUTTON8:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_8);
                return;
            case BUTTON9:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_9);
                return;
            case BUTTON0:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_0);
                return;
            case BUTTONBACK:
                SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_PAD_BACK);
                return;
            default:
                return;
        }
    }

    public void onBrowserBackButton() {
        SmartGlassXBLBrowserControllerViewModel.getInstance().back();
    }

    public void onBrowserStopRefreshButton() {
        SmartGlassXBLBrowserControllerViewModel.getInstance().refreshOrStop();
    }

    public boolean getUrlChanging() {
        return SmartGlassXBLBrowserControllerViewModel.getInstance().getIsNavigating();
    }

    public void browserDownloadLocally() {
        String url = SmartGlassXBLBrowserControllerViewModel.getInstance().getCurrentUrl();
        if (url != null) {
            Intent i = new Intent("android.intent.action.VIEW");
            i.setData(Uri.parse(url));
            XLEApplication.getMainActivity().startActivity(i);
        }
    }

    public void browserControls() {
        SmartGlassXBLBrowserControllerViewModel.getInstance().showBrowserControls();
    }

    public void setUrl(String url) {
        SmartGlassXBLBrowserControllerViewModel.getInstance().navigate(url);
    }

    public void onTouch(TouchFrame touch) {
        if (touch.points != null && touch.points.length > 0) {
            if (SystemClock.uptimeMillis() - this.titleRequestStartMs > this.timeToRecalculateLatency) {
                this.timeToRecalculateLatency = 60000;
                updateXboxLatency();
            }
            SmartGlassXBLBrowserControllerViewModel.getInstance().sendTouchFrame(touch);
        }
    }

    public void onScroll(ScrollPoint touch) {
        SmartGlassXBLBrowserControllerViewModel.getInstance().sendScrollFrame(touch);
    }

    public void cancelAndExit() {
        goBack();
    }

    public boolean shouldShowMediaTransport() {
        return NowPlayingGlobalModel.getInstance().isMediaInProgress();
    }

    public void OnGetActiveTitleInfoResponse(ActiveTitleInfo settings) {
        if (getIsActive()) {
            this.xboxLatency = SystemClock.uptimeMillis() - this.titleRequestStartMs;
            XLELog.Diagnostic("SmartGlassViewModel", "Updated xboxLatency is " + this.xboxLatency);
            this.adapter.updateView();
        }
    }

    public int getTouchMsPerFrame() {
        if (this.xboxLatency > 625) {
            return 48;
        }
        if (this.xboxLatency > 325) {
            return 32;
        }
        return 16;
    }

    private void startNativeViewModel() {
        XLELog.Diagnostic("SmartGlassViewModel", "startNativeViewModel");
        if (!this.sgControllerViewModel.isInitialized()) {
            XLELog.Diagnostic("SmartGlassViewModel", "startNativeViewModel running");
            this.sgControllerViewModel = SmartGlassXBLSharedControllerViewModel.getInstance();
            this.sgControllerViewModel.init();
            this.sgControllerViewModel.setOnControllerStateChangedRunnable(new Runnable() {
                public void run() {
                    SmartGlassViewModel.this.onControllerStateChanged(SmartGlassViewModel.this.sgControllerViewModel.getCanGestureControlBeActive(), SmartGlassViewModel.this.sgControllerViewModel.getCanBrowserControlBeActive(), SmartGlassViewModel.this.sgControllerViewModel.getCanTextControlBeActive(), SmartGlassViewModel.this.sgControllerViewModel.getIsRemoteOpenedByAnotherUser(), SmartGlassViewModel.this.sgControllerViewModel.getPreferredControl());
                }
            });
            this.sgControllerViewModel.getTextInputViewModel().setOnTextChangedRunnable(new Runnable() {
                public void run() {
                    SmartGlassViewModel.this.onTextChanged(SmartGlassViewModel.this.sgControllerViewModel.getTextInputViewModel().getText());
                }
            });
            this.sgControllerViewModel.getTextInputViewModel().setOnKeyboardStateChangedRunnable(new Runnable() {
                public void run() {
                    SmartGlassViewModel.this.onKeyboardStateChanged(SmartGlassViewModel.this.sgControllerViewModel.getTextInputViewModel().getTextInputState());
                }
            });
            SmartGlassXBLBrowserControllerViewModel.getInstance().setOnUrlChangedRunnable(new Runnable() {
                public void run() {
                    SmartGlassViewModel.this.onUrlChanged(SmartGlassXBLBrowserControllerViewModel.getInstance().getCurrentUrl());
                }
            });
            SmartGlassXBLBrowserControllerViewModel.getInstance().setOnUrlChangingRunnable(new Runnable() {
                public void run() {
                    SmartGlassViewModel.this.onUrlChanged(SmartGlassXBLBrowserControllerViewModel.getInstance().getCurrentUrl());
                }
            });
            this.sgControllerViewModel.start();
        }
    }

    private void stopNativeViewModel() {
        if (this.sgControllerViewModel.isInitialized()) {
            this.sgControllerViewModel.setOnControllerStateChangedRunnable(null);
            this.sgControllerViewModel.getTextInputViewModel().setOnTextChangedRunnable(null);
            this.sgControllerViewModel.getTextInputViewModel().setOnKeyboardStateChangedRunnable(null);
            SmartGlassXBLBrowserControllerViewModel.getInstance().setOnUrlChangedRunnable(null);
            SmartGlassXBLBrowserControllerViewModel.getInstance().setOnUrlChangingRunnable(null);
            this.sgControllerViewModel.stop();
            this.sgControllerViewModel.dealloc();
        }
    }

    private int screenStateToNativeInt(ScreenState v) {
        switch (v) {
            case TEXT:
            case TEXT_FAMILY_PASSCODE:
                return 3;
            case BROWSER:
                return 2;
            case GESTURE:
                return 1;
            default:
                return 0;
        }
    }

    private void onControllerStateChanged(boolean canGestureControlBeActive, boolean canBrowserControlBeActive, boolean canTextControlBeActive, boolean isRemoteOpenedByAnotherUser, int preferredControl) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (!isRemoteOpenedByAnotherUser) {
            this.controlPickerViewModel.onControllerStateChanged(canGestureControlBeActive, canBrowserControlBeActive, canTextControlBeActive, preferredControl);
            this.state.initializeLevel1IfNecessary(canBrowserControlBeActive);
            switch (preferredControl) {
                case 0:
                    this.state.changeState(ScreenState.LOADING);
                    break;
                case 1:
                    navigateToController();
                    break;
                case 2:
                    navigateToBrowser();
                    break;
                case 3:
                    navigateToKeyboard();
                    break;
                default:
                    break;
            }
        }
        this.state.changeState(ScreenState.GESTURE);
        showMustActDialog(XboxApplication.Resources.getString(R.string.smart_glass_controller), XboxApplication.Resources.getString(R.string.smart_glass_exclusive_mode_taken), XboxApplication.Resources.getString(R.string.OK), new Runnable() {
            public void run() {
                if (NavigationManager.getInstance().getCurrentActivity() instanceof SmartGlassActivity) {
                    SmartGlassViewModel.this.goBack();
                }
            }
        }, true);
        this.adapter.updateView();
    }

    private void onTextChanged(XBLText text) {
        if (text != null) {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            XLELog.Info("SmartGlassViewModel", String.format("onTextChanged(%s,%d,%d)", new Object[]{text.text, Long.valueOf(text.selectionIndex), Long.valueOf(text.selectionLength)}));
            this.replacementText = text;
        }
        this.adapter.updateView();
    }

    private void onKeyboardStateChanged(XBLTextInputState textstate) {
        boolean z = true;
        if (textstate != null) {
            XLELog.Info("SmartGlassViewModel", String.format("onKeyboardStateChanged(%d,%d,%d,%s)", new Object[]{Long.valueOf(textstate.maxLength), Integer.valueOf(textstate.keyboardType), Integer.valueOf(textstate.inputType), textstate.supportedCharacters}));
            if (Thread.currentThread() != ThreadManager.UIThread) {
                z = false;
            }
            XLEAssert.assertTrue(z);
            this.replacementTextState = textstate;
        }
    }

    private void onUrlChanged(String newurl) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (newurl != null) {
            XLELog.Info("SmartGlassViewModel", String.format("onUrlChanged(%s)", new Object[]{newurl}));
            this.replacementUrl = newurl;
        }
        this.adapter.updateView();
    }
}
