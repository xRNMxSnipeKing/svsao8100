package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import com.microsoft.smartglass.R;

public class SmartGlassFamilyPasscodeControl extends RelativeLayout {
    private View familyPasscode0 = findViewById(R.id.familyPasscode0);
    private View familyPasscode1 = findViewById(R.id.familyPasscode1);
    private View familyPasscode2 = findViewById(R.id.familyPasscode2);
    private View familyPasscode3 = findViewById(R.id.familyPasscode3);
    private View familyPasscode4 = findViewById(R.id.familyPasscode4);
    private View familyPasscode5 = findViewById(R.id.familyPasscode5);
    private View familyPasscode6 = findViewById(R.id.familyPasscode6);
    private View familyPasscode7 = findViewById(R.id.familyPasscode7);
    private View familyPasscode8 = findViewById(R.id.familyPasscode8);
    private View familyPasscode9 = findViewById(R.id.familyPasscode9);
    private View familyPasscodeBack = findViewById(R.id.familyPasscodeBack);
    private FamilyPasscodeRunnable runnable;

    public enum FamilyPasscodeButton {
        BUTTON1,
        BUTTON2,
        BUTTON3,
        BUTTON4,
        BUTTON5,
        BUTTON6,
        BUTTON7,
        BUTTON8,
        BUTTON9,
        BUTTON0,
        BUTTONBACK
    }

    public interface FamilyPasscodeRunnable {
        void run(FamilyPasscodeButton familyPasscodeButton);
    }

    public SmartGlassFamilyPasscodeControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.smart_glass_family_passcode_control, this, true);
        this.familyPasscode1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmartGlassFamilyPasscodeControl.this.buttonPress(FamilyPasscodeButton.BUTTON1);
            }
        });
        this.familyPasscode2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmartGlassFamilyPasscodeControl.this.buttonPress(FamilyPasscodeButton.BUTTON2);
            }
        });
        this.familyPasscode3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmartGlassFamilyPasscodeControl.this.buttonPress(FamilyPasscodeButton.BUTTON3);
            }
        });
        this.familyPasscode4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmartGlassFamilyPasscodeControl.this.buttonPress(FamilyPasscodeButton.BUTTON4);
            }
        });
        this.familyPasscode5.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmartGlassFamilyPasscodeControl.this.buttonPress(FamilyPasscodeButton.BUTTON5);
            }
        });
        this.familyPasscode6.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmartGlassFamilyPasscodeControl.this.buttonPress(FamilyPasscodeButton.BUTTON6);
            }
        });
        this.familyPasscode7.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmartGlassFamilyPasscodeControl.this.buttonPress(FamilyPasscodeButton.BUTTON7);
            }
        });
        this.familyPasscode8.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmartGlassFamilyPasscodeControl.this.buttonPress(FamilyPasscodeButton.BUTTON8);
            }
        });
        this.familyPasscode9.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmartGlassFamilyPasscodeControl.this.buttonPress(FamilyPasscodeButton.BUTTON9);
            }
        });
        this.familyPasscode0.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmartGlassFamilyPasscodeControl.this.buttonPress(FamilyPasscodeButton.BUTTON0);
            }
        });
        this.familyPasscodeBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmartGlassFamilyPasscodeControl.this.buttonPress(FamilyPasscodeButton.BUTTONBACK);
            }
        });
    }

    public void setFamilyPasscodeRunnable(FamilyPasscodeRunnable runnable) {
        this.runnable = runnable;
    }

    private void buttonPress(FamilyPasscodeButton button) {
        if (this.runnable != null) {
            this.runnable.run(button);
        }
    }
}
