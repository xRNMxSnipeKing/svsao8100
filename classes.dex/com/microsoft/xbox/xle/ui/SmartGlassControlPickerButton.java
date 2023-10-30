package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.TouchUtil;
import com.microsoft.xbox.toolkit.ui.XLEImageView;

public class SmartGlassControlPickerButton extends RelativeLayout {
    private int ACTIVE_BACKGROUND_COLOR = -1;
    private int ACTIVE_TEXT_COLOR = XboxApplication.Resources.getColor(R.color.darkgray);
    private int DISABLED_BACKGROUND_COLOR = XboxApplication.Resources.getColor(R.color.darkgray);
    private int DISABLED_TEXT_COLOR = XboxApplication.Resources.getColor(R.color.ltgray);
    private int ENABLED_BACKGROUND_COLOR = XboxApplication.Resources.getColor(R.color.XboxGreen);
    private int ENABLED_TEXT_COLOR = -1;
    private Drawable activeimageid;
    private Drawable disabledimageid;
    private Drawable enabledimageid;
    private XLEImageView image;
    private CustomTypefaceTextView text;

    public enum ButtonState {
        ACTIVE,
        ENABLED,
        DISABLED
    }

    public SmartGlassControlPickerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSoundEffectsEnabled(false);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.smart_glass_control_picker_button, this, true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SmartGlassControlPickerButton);
        this.activeimageid = a.getDrawable(0);
        this.disabledimageid = a.getDrawable(1);
        this.enabledimageid = a.getDrawable(2);
        String textstr = a.getString(3);
        this.image = (XLEImageView) findViewById(R.id.smart_glass_control_picker_button_image);
        this.text = (CustomTypefaceTextView) findViewById(R.id.smart_glass_control_picker_button_text);
        this.text.setText(textstr);
        setState(ButtonState.DISABLED);
    }

    public void setState(ButtonState state) {
        switch (state) {
            case ACTIVE:
                setBackgroundColor(this.ACTIVE_BACKGROUND_COLOR);
                this.image.setImageDrawable(this.activeimageid);
                this.text.setTextColor(this.ACTIVE_TEXT_COLOR);
                return;
            case ENABLED:
                setBackgroundColor(this.ENABLED_BACKGROUND_COLOR);
                this.image.setImageDrawable(this.enabledimageid);
                this.text.setTextColor(this.ENABLED_TEXT_COLOR);
                return;
            case DISABLED:
                setBackgroundColor(this.DISABLED_BACKGROUND_COLOR);
                this.image.setImageDrawable(this.disabledimageid);
                this.text.setTextColor(this.DISABLED_TEXT_COLOR);
                return;
            default:
                return;
        }
    }

    public void setOnClickListener(OnClickListener handler) {
        super.setOnClickListener(TouchUtil.createOnClickListener(handler));
    }
}
