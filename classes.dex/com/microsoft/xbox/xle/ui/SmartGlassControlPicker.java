package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.ui.SmartGlassControlPickerButton.ButtonState;
import com.microsoft.xbox.xle.viewmodel.SmartGlassViewModel.ScreenState;

public class SmartGlassControlPicker extends RelativeLayout {
    private Runnable backgroundRunnable = null;
    private Runnable browserButtonRunnable = null;
    private SmartGlassControlPickerButton browserbutton = null;
    private Runnable controllerButtonRunnable = null;
    private SmartGlassControlPickerButton controllerbutton = null;
    private Runnable keyboardButtonRunnable = null;
    private SmartGlassControlPickerButton keyboardbutton = null;

    public SmartGlassControlPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.smart_glass_control_picker, this, true);
        this.browserbutton = (SmartGlassControlPickerButton) findViewById(R.id.smart_glass_control_picker_browser_button);
        this.browserbutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SmartGlassControlPicker.this.browserButtonRunnable != null) {
                    SmartGlassControlPicker.this.browserButtonRunnable.run();
                }
            }
        });
        this.controllerbutton = (SmartGlassControlPickerButton) findViewById(R.id.smart_glass_control_picker_navigation_button);
        this.controllerbutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SmartGlassControlPicker.this.controllerButtonRunnable != null) {
                    SmartGlassControlPicker.this.controllerButtonRunnable.run();
                }
            }
        });
        this.keyboardbutton = (SmartGlassControlPickerButton) findViewById(R.id.smart_glass_control_picker_keyboard_button);
        this.keyboardbutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SmartGlassControlPicker.this.keyboardButtonRunnable != null) {
                    SmartGlassControlPicker.this.keyboardButtonRunnable.run();
                }
            }
        });
        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SmartGlassControlPicker.this.backgroundRunnable != null) {
                    SmartGlassControlPicker.this.backgroundRunnable.run();
                }
            }
        });
    }

    public void setBrowserButtonRunnable(Runnable r) {
        this.browserButtonRunnable = r;
    }

    public void setControllerButtonRunnable(Runnable r) {
        this.controllerButtonRunnable = r;
    }

    public void setKeyboardButtonRunnable(Runnable r) {
        this.keyboardButtonRunnable = r;
    }

    public void setBackgroundRunnable(Runnable r) {
        this.backgroundRunnable = r;
    }

    public void configureButtons(boolean browserEnabled, boolean dpadEnabled, boolean keyboardEnabled, ScreenState screenState) {
        boolean z;
        boolean z2 = false;
        SmartGlassControlPickerButton smartGlassControlPickerButton = this.browserbutton;
        if (screenState == ScreenState.BROWSER) {
            z = true;
        } else {
            z = false;
        }
        configureButton(smartGlassControlPickerButton, browserEnabled, z);
        smartGlassControlPickerButton = this.controllerbutton;
        if (screenState == ScreenState.GESTURE) {
            z = true;
        } else {
            z = false;
        }
        configureButton(smartGlassControlPickerButton, dpadEnabled, z);
        SmartGlassControlPickerButton smartGlassControlPickerButton2 = this.keyboardbutton;
        if (screenState == ScreenState.TEXT || screenState == ScreenState.TEXT_FAMILY_PASSCODE) {
            z2 = true;
        }
        configureButton(smartGlassControlPickerButton2, keyboardEnabled, z2);
    }

    private void configureButton(SmartGlassControlPickerButton button, boolean enabled, boolean active) {
        if (active) {
            button.setState(ButtonState.ACTIVE);
        } else if (enabled) {
            button.setState(ButtonState.ENABLED);
        } else {
            button.setState(ButtonState.DISABLED);
        }
    }
}
