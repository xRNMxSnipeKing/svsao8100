package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEButton;

public class XLEAvatarEditorButton extends XLEButton {
    private Drawable disabledImageFemaleHandle;
    private Drawable disabledImageMaleHandle;
    private Drawable enabledImageFemaleHandle;
    private Drawable enabledImageMaleHandle;
    private boolean isMale = false;

    public XLEAvatarEditorButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XLEAvatarEditorButton);
        this.disabledImageMaleHandle = a.getDrawable(0);
        this.disabledImageFemaleHandle = a.getDrawable(1);
        this.enabledImageMaleHandle = a.getDrawable(2);
        this.enabledImageFemaleHandle = a.getDrawable(3);
        a.recycle();
    }

    public void setGender(boolean isMale) {
        if (this.isMale != isMale) {
            this.isMale = isMale;
            updateImage();
        }
    }

    protected void updateImage() {
        if (this.stateHandler.getDisabled()) {
            setBackgroundDrawable(getDisabledImage());
        } else {
            setBackgroundDrawable(getEnabledImage());
        }
    }

    private Drawable getEnabledImage() {
        if (this.isMale) {
            return this.enabledImageMaleHandle;
        }
        return this.enabledImageFemaleHandle;
    }

    private Drawable getDisabledImage() {
        if (this.isMale) {
            return this.disabledImageMaleHandle;
        }
        return this.disabledImageFemaleHandle;
    }
}
