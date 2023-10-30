package com.microsoft.xbox.xle.app.adapter;

import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.service.model.smartglass.ScrollPoint;
import com.microsoft.xbox.service.model.smartglass.TouchFrame;
import com.microsoft.xbox.service.model.smartglass.XBLText;
import com.microsoft.xbox.service.model.smartglass.XBLTextInputState;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.SoundManager;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEGestureOverlayProcessor.GestureType;
import com.microsoft.xbox.toolkit.XLEGestureOverlayProcessor.OnGestureRunnable;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.MAAS.MAASAnimationType;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationView;
import com.microsoft.xbox.toolkit.ui.CancellableBlockingScreen;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceEditText;
import com.microsoft.xbox.toolkit.ui.EditTextContainer;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.appbar.ApplicationBarView;
import com.microsoft.xbox.xle.anim.XLEAdapterAnimation;
import com.microsoft.xbox.xle.anim.XLEAnimationQueue;
import com.microsoft.xbox.xle.anim.XLEAnimationQueue.XLEAnimationQueueItem;
import com.microsoft.xbox.xle.anim.XLEMAASAnimation;
import com.microsoft.xbox.xle.anim.XLEMAASAnimationPackageNavigationManager;
import com.microsoft.xbox.xle.app.ApplicationBarManager;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.ui.SmartGlassBrowser;
import com.microsoft.xbox.xle.ui.SmartGlassBrowser.TouchEventListener;
import com.microsoft.xbox.xle.ui.SmartGlassControlPicker;
import com.microsoft.xbox.xle.ui.SmartGlassController;
import com.microsoft.xbox.xle.ui.SmartGlassFamilyPasscodeControl;
import com.microsoft.xbox.xle.ui.SmartGlassFamilyPasscodeControl.FamilyPasscodeButton;
import com.microsoft.xbox.xle.ui.SmartGlassFamilyPasscodeControl.FamilyPasscodeRunnable;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.SmartGlassViewModel;
import com.microsoft.xbox.xle.viewmodel.SmartGlassViewModel.ScreenState;
import java.util.Timer;
import java.util.TimerTask;

public class SmartGlassAdapter extends AdapterBaseNormal {
    private static final String SMART_GLASS_BROWSER_IN = "SmartGlassBrowserIn";
    private static final String SMART_GLASS_BROWSER_OUT = "SmartGlassBrowserOut";
    private static final String SMART_GLASS_CONTROL_PICKER_IN = "SmartGlassControlPickerIn";
    private static final String SMART_GLASS_CONTROL_PICKER_IN_TABLET = "SmartGlassControlPickerInTablet";
    private static final String SMART_GLASS_CONTROL_PICKER_OUT = "SmartGlassControlPickerOut";
    private static final String SMART_GLASS_CONTROL_PICKER_OUT_TABLET = "SmartGlassControlPickerOutTablet";
    private static final String SMART_GLASS_DPAD_IN = "SmartGlassDpadIn";
    private static final String SMART_GLASS_DPAD_OUT = "SmartGlassDpadOut";
    private static final String SMART_GLASS_FAMILY_PASSCODE_IN = "SmartGlassFamilyPasscodeIn";
    private static final String SMART_GLASS_FAMILY_PASSCODE_OUT = "SmartGlassFamilyPasscodeOut";
    private static final String SMART_GLASS_TEXT_IN = "SmartGlassTextIn";
    private static final String SMART_GLASS_TEXT_OUT = "SmartGlassTextOut";
    private XLEAnimationQueue animqueue;
    private CancellableBlockingScreen blockingdialog;
    private SmartGlassBrowser browser;
    private SmartGlassController controller;
    private SmartGlassControlPicker controlpicker;
    private CustomTypefaceEditText editView;
    private EditTextContainer editViewContainer;
    private int editViewMaxLength;
    private View editViewReset;
    private SmartGlassFamilyPasscodeControl familyPasscode;
    private boolean inValidateAndSendTextIfChanged;
    private Timer instructionanimtimer;
    private boolean instructionsVisible;
    private ScreenState prevScreenState;
    private boolean replacingTextWithNetworkValue;
    private SwitchPanel switchpanel;
    private XLEButton textInputCancelButton;
    private XLEButton textInputOkButton;
    private SmartGlassViewModel viewmodel;

    abstract class SmartGlassAdapterRunnable implements Runnable {
        private ScreenState state;

        public abstract void runInner();

        public SmartGlassAdapterRunnable(ScreenState state) {
            this.state = state;
        }

        public final void run() {
            if (SmartGlassAdapter.this.viewmodel.getSwitchPanelState() == this.state) {
                runInner();
            }
        }
    }

    public SmartGlassAdapter(SmartGlassViewModel viewModel) {
        this.viewmodel = null;
        this.switchpanel = null;
        this.controlpicker = null;
        this.editView = null;
        this.editViewContainer = null;
        this.familyPasscode = null;
        this.editViewMaxLength = 0;
        this.inValidateAndSendTextIfChanged = false;
        this.replacingTextWithNetworkValue = false;
        this.animqueue = new XLEAnimationQueue();
        this.instructionanimtimer = new Timer();
        this.instructionsVisible = false;
        this.screenBody = findViewById(R.id.smart_glass_body);
        this.blockingdialog = new CancellableBlockingScreen(XboxApplication.MainActivity);
        this.viewmodel = viewModel;
        this.switchpanel = (SwitchPanel) findViewById(R.id.smart_glass_switch_panel);
        this.controlpicker = (SmartGlassControlPicker) findViewById(R.id.smart_glass_control_picker);
        this.editView = (CustomTypefaceEditText) findViewById(R.id.smart_glass_edit_view);
        this.editViewContainer = (EditTextContainer) findViewById(R.id.smart_glass_edit_view_container);
        this.editView.setContainer(this.editViewContainer);
        this.editViewReset = findViewById(R.id.smart_glass_edit_view_reset_button);
        this.editViewContainer.addChild(this.editViewReset);
        this.editViewContainer.addChild(findViewById(R.id.smart_glass_edit_view_wrapper));
        this.familyPasscode = (SmartGlassFamilyPasscodeControl) findViewById(R.id.smart_glass_family_passcode_control);
        this.browser = (SmartGlassBrowser) findViewById(R.id.smart_glass_browser_control);
        this.controller = (SmartGlassController) findViewById(R.id.smart_glass_controller);
        this.textInputOkButton = (XLEButton) findViewById(R.id.smart_glass_text_input_ok);
        this.textInputCancelButton = (XLEButton) findViewById(R.id.smart_glass_text_input_cancel);
        if (this.textInputOkButton != null) {
            this.textInputOkButton.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    SmartGlassAdapter.this.viewmodel.onTextOKButtonClick();
                }
            });
        }
        if (this.textInputCancelButton != null) {
            this.textInputCancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    SmartGlassAdapter.this.viewmodel.onTextCancelButtonClick();
                }
            });
        }
    }

    private void validateAndSendTextIfChanged() {
        if (!this.inValidateAndSendTextIfChanged) {
            this.inValidateAndSendTextIfChanged = true;
            this.viewmodel.setText(new XBLText(this.editView.getText().toString(), (long) this.editView.getSelectionStart(), (long) (this.editView.getSelectionEnd() - this.editView.getSelectionStart())));
            this.inValidateAndSendTextIfChanged = false;
        }
    }

    private void setSelection(long selectionIndex, long selectionLength, String text) {
        int selectStart = (int) selectionIndex;
        int selectEnd = (int) (selectionIndex + selectionLength);
        int maxTextLength = Math.min(text == null ? 0 : text.length(), this.editViewMaxLength);
        selectStart = Math.max(Math.min(selectStart, maxTextLength), 0);
        selectEnd = Math.max(Math.min(selectEnd, maxTextLength), 0);
        if (this.editView.getSelectionStart() != selectStart || this.editView.getSelectionEnd() != selectEnd) {
            this.editView.setSelection(selectStart, selectEnd);
        }
    }

    public void onStart() {
        super.onStart();
        this.prevScreenState = ScreenState.LOADING;
        MAAS.getInstance().getAnimation(SMART_GLASS_CONTROL_PICKER_IN);
        MAAS.getInstance().getAnimation(SMART_GLASS_CONTROL_PICKER_OUT);
        MAAS.getInstance().getAnimation(SMART_GLASS_TEXT_IN);
        MAAS.getInstance().getAnimation(SMART_GLASS_TEXT_OUT);
        MAAS.getInstance().getAnimation(SMART_GLASS_DPAD_IN);
        MAAS.getInstance().getAnimation(SMART_GLASS_DPAD_OUT);
        MAAS.getInstance().getAnimation(SMART_GLASS_BROWSER_IN);
        MAAS.getInstance().getAnimation(SMART_GLASS_BROWSER_OUT);
    }

    public void updateViewOverride() {
        boolean z = true;
        if (this.viewmodel.getSwitchPanelState() == ScreenState.LOADING) {
            this.blockingdialog.dismiss();
            this.blockingdialog = new CancellableBlockingScreen(XboxApplication.MainActivity);
            this.blockingdialog.setCancelButtonAction(new OnClickListener() {
                public void onClick(View v) {
                    SmartGlassAdapter.this.blockingdialog.dismiss();
                    SmartGlassAdapter.this.viewmodel.cancelAndExit();
                }
            });
            this.blockingdialog.show(XboxApplication.MainActivity, XboxApplication.Resources.getString(R.string.loading));
        } else {
            this.blockingdialog.dismiss();
        }
        if (this.prevScreenState != this.viewmodel.getSwitchPanelState()) {
            this.animqueue.push(getAnimationPackage(this.prevScreenState, false));
            this.animqueue.push(getAnimationPackage(this.viewmodel.getSwitchPanelState(), true));
            this.animqueue.startNext();
            showInstructionViewAndScheduleFadeOut(this.viewmodel.getSwitchPanelState());
            this.prevScreenState = this.viewmodel.getSwitchPanelState();
        }
        if (this.viewmodel.getControlPickerViewModel() != null) {
            this.controlpicker.configureButtons(this.viewmodel.getControlPickerViewModel().getCanBrowserControlBeActive(), this.viewmodel.getControlPickerViewModel().getCanGestureControlBeActive(), this.viewmodel.getControlPickerViewModel().getCanTextControlBeActive(), this.viewmodel.getStateLevel1());
        }
        switch (this.viewmodel.getSwitchPanelState()) {
            case TEXT:
                XBLTextInputState keyboardState = this.viewmodel.drainReplacementTextState();
                if (keyboardState != null) {
                    this.replacingTextWithNetworkValue = true;
                    this.editViewMaxLength = (int) keyboardState.maxLength;
                    this.editView.setFilters(new InputFilter[]{new LengthFilter(this.editViewMaxLength)});
                    int inputFilter = 0;
                    switch (keyboardState.getKeyboardType()) {
                        case KeyboardTypeSingleLine:
                            inputFilter = 0 & -131073;
                            break;
                        case KeyboardTypeMultiLine:
                            inputFilter = 0 | AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_FEATURES;
                            break;
                    }
                    switch (keyboardState.getInputType()) {
                        case KeyboardInputTypePassword:
                            inputFilter = (inputFilter | 1) | 128;
                            break;
                        case KeyboardInputTypeEmail:
                            inputFilter = (inputFilter | 1) | 32;
                            break;
                        case KeyboardInputTypePhone:
                            inputFilter |= 3;
                            break;
                        case KeyboardInputTypeNumber:
                            inputFilter |= 2;
                            break;
                        default:
                            inputFilter = (inputFilter | 1) | 0;
                            break;
                    }
                    this.editView.setInputType(inputFilter);
                    this.replacingTextWithNetworkValue = false;
                }
                XBLText replacementText = this.viewmodel.drainReplacementText();
                if (replacementText != null) {
                    this.replacingTextWithNetworkValue = true;
                    if (!JavaUtil.stringsEqual(replacementText.text, this.editView.getText().toString())) {
                        this.editView.setText(replacementText.text);
                    }
                    setSelection(replacementText.selectionIndex, replacementText.selectionLength, replacementText.text);
                    this.replacingTextWithNetworkValue = false;
                    break;
                }
                break;
            case BROWSER:
                boolean urlChanging = this.viewmodel.getUrlChanging();
                this.browser.updateStopRefreshVisibility(urlChanging);
                if (urlChanging) {
                    z = false;
                }
                setAppBarButtonEnabled(R.id.appbar_download, z);
                String replacementUrl = this.viewmodel.drainReplacementUrl();
                if (replacementUrl != null) {
                    this.browser.setUrl(replacementUrl);
                }
                this.browser.setTouchMsPerFrame(this.viewmodel.getTouchMsPerFrame());
                break;
        }
        setAppBarMediaButtonVisibility(this.viewmodel.shouldShowMediaTransport());
    }

    private void trueUpdateView(ScreenState state, boolean in) {
        boolean hideKeyboard;
        boolean transportVisible;
        boolean nextPrevVisible;
        boolean z = true;
        this.switchpanel.setState(state.ordinal());
        if (in && state == ScreenState.TEXT) {
            hideKeyboard = false;
        } else {
            hideKeyboard = true;
        }
        if (state == ScreenState.GESTURE || state == ScreenState.BROWSER) {
            transportVisible = true;
        } else {
            transportVisible = false;
        }
        if (state != ScreenState.BROWSER) {
            nextPrevVisible = true;
        } else {
            nextPrevVisible = false;
        }
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                if (hideKeyboard) {
                    SmartGlassAdapter.this.dismissKeyboard();
                } else {
                    SmartGlassAdapter.this.showKeyboard(SmartGlassAdapter.this.editView);
                }
            }
        });
        if (in) {
            LayoutInflater vi = (LayoutInflater) XboxApplication.Instance.getApplicationContext().getSystemService("layout_inflater");
            ApplicationBarView buttonholder = null;
            switch (state) {
                case TEXT:
                    buttonholder = (ApplicationBarView) vi.inflate(R.layout.smart_glass_check_x_appbar, null);
                    break;
                case BROWSER:
                    buttonholder = (ApplicationBarView) vi.inflate(R.layout.smart_glass_browser_appbar, null);
                    break;
                case CONTROL_PICKER:
                    buttonholder = (ApplicationBarView) vi.inflate(R.layout.smart_glass_control_picker_appbar, null);
                    break;
                case GESTURE:
                    buttonholder = (ApplicationBarView) vi.inflate(R.layout.smart_glass_controller_appbar, null);
                    break;
                case TEXT_FAMILY_PASSCODE:
                    buttonholder = (ApplicationBarView) vi.inflate(R.layout.smart_glass_x_appbar, null);
                    break;
            }
            XLEAssert.assertNotNull(buttonholder);
            XLEButton[] buttons = buttonholder == null ? new XLEButton[0] : buttonholder.getAppBarButtons();
            ApplicationBarManager.getInstance().setEnableMediaTransportControls(transportVisible, nextPrevVisible);
            ApplicationBarManager instance = ApplicationBarManager.getInstance();
            if (!XLEApplication.Instance.getIsTablet()) {
                z = false;
            }
            instance.setShouldShowNowPlaying(z);
            ApplicationBarManager.getInstance().addNewCollapsedButtons(buttons);
            ApplicationBarManager.getInstance().addNewExpandedButtons(new XLEButton[0]);
            setAppBarMediaButtonVisibility(this.viewmodel.shouldShowMediaTransport());
            ApplicationBarManager.getInstance().beginAnimation();
        }
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_switch_mode, new OnClickListener() {
            public void onClick(View arg0) {
                SmartGlassAdapter.this.viewmodel.showControlPicker();
            }
        });
        setAppBarButtonClickListener(R.id.appbar_x, new OnClickListener() {
            public void onClick(View arg0) {
                SmartGlassAdapter.this.viewmodel.onTextCancelButtonClick();
            }
        });
        setAppBarButtonClickListener(R.id.appbar_check, new OnClickListener() {
            public void onClick(View arg0) {
                SmartGlassAdapter.this.viewmodel.onTextOKButtonClick();
            }
        });
        setAppBarButtonClickListener(R.id.appbar_download, new OnClickListener() {
            public void onClick(View arg0) {
                SmartGlassAdapter.this.viewmodel.browserDownloadLocally();
            }
        });
        if (XLEApplication.Instance.getIsTablet()) {
            setAppBarButtonVisibility(R.id.appbar_remote, 8);
        }
    }

    public void onPause() {
        super.onPause();
        this.controlpicker.setBrowserButtonRunnable(null);
        this.controlpicker.setControllerButtonRunnable(null);
        this.controlpicker.setKeyboardButtonRunnable(null);
        this.controlpicker.setBackgroundRunnable(null);
        this.editView.setTextOrSelectionChangedRunnable(null);
        this.editView.setOnEditorActionListener(null);
        this.editViewContainer.setKeyboardDismissedRunnable(null);
        this.editViewReset.setOnClickListener(null);
        this.familyPasscode.setFamilyPasscodeRunnable(null);
        this.browser.setBackButtonRunnable(null);
        this.browser.setWebhubButtonRunnable(null);
        this.browser.setBrowserStopRefreshRunnable(null);
        this.browser.setUrlDoneRunnable(null);
        this.browser.setPanelTouchListener(null);
        this.browser.setOnCloseButton(null);
        this.controller.setOnDpadB(null);
        this.controller.setOnDpadX(null);
        this.controller.setOnDpadY(null);
        this.controller.setOnCloseButton(null);
        this.controller.setOnSendGestureEvent(null);
        this.controller.setOnTouchDown(null);
        this.controller.onPause();
        this.blockingdialog.dismiss();
        dismissKeyboard();
    }

    public void onResume() {
        super.onResume();
        this.controlpicker.setBrowserButtonRunnable(new SmartGlassAdapterRunnable(ScreenState.CONTROL_PICKER) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.navigateToBrowser();
            }
        });
        this.controlpicker.setControllerButtonRunnable(new SmartGlassAdapterRunnable(ScreenState.CONTROL_PICKER) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.navigateToController();
            }
        });
        this.controlpicker.setKeyboardButtonRunnable(new SmartGlassAdapterRunnable(ScreenState.CONTROL_PICKER) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.navigateToKeyboard();
            }
        });
        this.controlpicker.setBackgroundRunnable(new SmartGlassAdapterRunnable(ScreenState.CONTROL_PICKER) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.dismissTextEntryOrControlPickerLayer();
            }
        });
        this.editView.setTextOrSelectionChangedRunnable(new Runnable() {
            public void run() {
                if (!SmartGlassAdapter.this.replacingTextWithNetworkValue) {
                    SmartGlassAdapter.this.validateAndSendTextIfChanged();
                }
            }
        });
        this.editView.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (SmartGlassAdapter.this.viewmodel.getSwitchPanelState() != ScreenState.TEXT || actionId != 6) {
                    return false;
                }
                SmartGlassAdapter.this.viewmodel.onTextOKButtonClick();
                return true;
            }
        });
        this.editViewContainer.setKeyboardDismissedRunnable(new Runnable() {
            public void run() {
                SmartGlassAdapter.this.viewmodel.dismissTextEntryOrControlPickerLayer();
            }
        });
        this.editViewReset.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmartGlassAdapter.this.editView.setText("");
            }
        });
        this.familyPasscode.setFamilyPasscodeRunnable(new FamilyPasscodeRunnable() {
            public void run(FamilyPasscodeButton button) {
                if (SmartGlassAdapter.this.viewmodel.getSwitchPanelState() == ScreenState.TEXT_FAMILY_PASSCODE) {
                    SmartGlassAdapter.this.viewmodel.onFamilyPasscodeButton(button);
                }
            }
        });
        this.browser.setBackButtonRunnable(new SmartGlassAdapterRunnable(ScreenState.BROWSER) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.onBrowserBackButton();
            }
        });
        this.browser.setWebhubButtonRunnable(new SmartGlassAdapterRunnable(ScreenState.BROWSER) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.browserControls();
            }
        });
        this.browser.setBrowserStopRefreshRunnable(new SmartGlassAdapterRunnable(ScreenState.BROWSER) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.onBrowserStopRefreshButton();
            }
        });
        this.browser.setUrlDoneRunnable(new SmartGlassAdapterRunnable(ScreenState.BROWSER) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.setUrl(SmartGlassAdapter.this.browser.getUrl());
            }
        });
        this.browser.setPanelTouchListener(new TouchEventListener() {
            public void onTouchEvent(TouchFrame pt) {
                SmartGlassAdapter.this.instructionViewImmediateFadeOut();
                SmartGlassAdapter.this.viewmodel.onTouch(pt);
            }

            public void onScrollEvent(ScrollPoint pt) {
                SmartGlassAdapter.this.instructionViewImmediateFadeOut();
                SmartGlassAdapter.this.viewmodel.onScroll(pt);
            }
        });
        this.browser.setOnCloseButton(new SmartGlassAdapterRunnable(ScreenState.BROWSER) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.cancelAndExit();
            }
        });
        this.controller.setOnDpadGuide(new SmartGlassAdapterRunnable(ScreenState.GESTURE) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.navigateToGuide();
            }
        });
        this.controller.setOnDpadB(new SmartGlassAdapterRunnable(ScreenState.GESTURE) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.onDpadB();
            }
        });
        this.controller.setOnDpadX(new SmartGlassAdapterRunnable(ScreenState.GESTURE) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.onDpadX();
            }
        });
        this.controller.setOnDpadY(new SmartGlassAdapterRunnable(ScreenState.GESTURE) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.onDpadY();
            }
        });
        this.controller.setOnCloseButton(new SmartGlassAdapterRunnable(ScreenState.GESTURE) {
            public void runInner() {
                SmartGlassAdapter.this.viewmodel.cancelAndExit();
            }
        });
        this.controller.setOnSendGestureEvent(new OnGestureRunnable() {
            public void onGesture(GestureType type) {
                if (SmartGlassAdapter.this.viewmodel.getSwitchPanelState() == ScreenState.GESTURE) {
                    SmartGlassAdapter.this.viewmodel.onGesture(type);
                }
            }
        });
        this.controller.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SoundManager.getInstance().playSound(XboxApplication.Instance.getRawRValue("sndbuttonselectandroid"));
            }
        });
    }

    protected void showKeyboard(View view) {
        this.editViewContainer.setGrabBackButton(true);
        super.showKeyboard(view);
        this.editView.requestFocus();
    }

    protected void dismissKeyboard() {
        this.editViewContainer.setGrabBackButton(false);
        super.dismissKeyboard();
        this.editViewContainer.unfocusText();
    }

    private void showInstructionViewAndScheduleFadeOut(ScreenState newstate) {
        final View instructions = getInstructionViewForFadeOut(newstate);
        if (instructions != null) {
            XLEMAASAnimation animation = (XLEMAASAnimation) MAAS.getInstance().getAnimation("SmartGlassInstructionIn");
            if (animation != null) {
                ((XLEAnimationView) animation.compile(instructions)).start();
            }
            instructions.setVisibility(0);
            this.instructionsVisible = true;
            if (newstate != ScreenState.TEXT) {
                TimerTask task = new TimerTask() {
                    public void run() {
                        ThreadManager.UIThreadPost(new Runnable() {
                            public void run() {
                                SmartGlassAdapter.this.instructionViewImmediateFadeOut(instructions);
                            }
                        });
                    }
                };
                this.instructionanimtimer.cancel();
                this.instructionanimtimer = new Timer();
                this.instructionanimtimer.schedule(task, 5000);
            }
        }
    }

    private void instructionViewImmediateFadeOut() {
        View instructions = getInstructionViewForFadeOut(this.viewmodel.getSwitchPanelState());
        if (instructions != null) {
            instructionViewImmediateFadeOut(instructions);
        }
    }

    private void instructionViewImmediateFadeOut(final View instructions) {
        XLEAssert.assertNotNull(instructions);
        this.instructionanimtimer.cancel();
        if (this.instructionsVisible && instructions.getVisibility() == 0) {
            XLEMAASAnimation animation = (XLEMAASAnimation) MAAS.getInstance().getAnimation("SmartGlassInstructionOut");
            if (animation != null) {
                XLEAnimationView anim = (XLEAnimationView) animation.compile(instructions);
                anim.setOnAnimationEnd(new Runnable() {
                    public void run() {
                        instructions.setVisibility(4);
                    }
                });
                anim.start();
            }
        }
        this.instructionsVisible = false;
    }

    private View getInstructionViewForFadeOut(ScreenState newstate) {
        switch (newstate) {
            case TEXT:
                return findViewById(R.id.smart_glass_text_instruction);
            case BROWSER:
                return findViewById(R.id.smart_glass_browser_instructions);
            default:
                return null;
        }
    }

    private XLEAnimationQueueItem getAnimationPackage(final ScreenState newstate, final boolean in) {
        XLEAnimationQueueItem item = new XLEAnimationQueueItem();
        String animfile = null;
        switch (newstate) {
            case TEXT:
                animfile = in ? SMART_GLASS_TEXT_IN : SMART_GLASS_TEXT_OUT;
                break;
            case BROWSER:
                animfile = in ? SMART_GLASS_BROWSER_IN : SMART_GLASS_BROWSER_OUT;
                break;
            case CONTROL_PICKER:
                if (!XLEApplication.Instance.getIsTablet()) {
                    animfile = in ? SMART_GLASS_CONTROL_PICKER_IN : SMART_GLASS_CONTROL_PICKER_OUT;
                    break;
                }
                animfile = in ? SMART_GLASS_CONTROL_PICKER_IN_TABLET : SMART_GLASS_CONTROL_PICKER_OUT_TABLET;
                break;
            case GESTURE:
                animfile = in ? SMART_GLASS_DPAD_IN : SMART_GLASS_DPAD_OUT;
                break;
            case TEXT_FAMILY_PASSCODE:
                animfile = in ? SMART_GLASS_FAMILY_PASSCODE_IN : SMART_GLASS_FAMILY_PASSCODE_OUT;
                break;
        }
        if (animfile != null) {
            XLEAdapterAnimation anim = (XLEAdapterAnimation) MAAS.getInstance().getAnimation(animfile);
            if (anim != null) {
                item.pack = anim.compile();
            }
        }
        item.beforeRunnable = new Runnable() {
            public void run() {
                SmartGlassAdapter.this.trueUpdateView(newstate, in);
            }
        };
        return item;
    }

    protected XLEAnimation getScreenBodyAnimation(MAASAnimationType animationType, boolean goingBack) {
        XLEAssert.assertNotNull(this.screenBody);
        XLEMAASAnimationPackageNavigationManager anim = (XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation("SmartGlassScreen");
        if (anim != null) {
            return anim.compile(animationType, goingBack, this.screenBody);
        }
        return null;
    }
}
