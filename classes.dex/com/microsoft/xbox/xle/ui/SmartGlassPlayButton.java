package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.ButtonStateHandler;
import com.microsoft.xbox.toolkit.ui.TouchUtil;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;

public class SmartGlassPlayButton extends FrameLayout {
    private XLEImageViewFast playIcon;
    private TextView playTextView;
    private ButtonStateHandler stateHandler = new ButtonStateHandler();

    public SmartGlassPlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.smartglass_play, this, true);
        this.playIcon = (XLEImageViewFast) findViewById(R.id.smartglass_play_icon);
        this.playTextView = (TextView) findViewById(R.id.smartglass_play_text);
        setSoundEffectsEnabled(false);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XLEButton);
        this.stateHandler.setDisabled(a.getBoolean(0, false));
        this.stateHandler.setDisabledImageHandle(a.getResourceId(2, -1));
        this.stateHandler.setEnabledImageHandle(a.getResourceId(3, -1));
        this.stateHandler.setPressedImageHandle(a.getResourceId(4, -1));
        this.playTextView.setTextColor(a.getColor(6, this.playTextView.getCurrentTextColor()));
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.stateHandler.setEnabled(enabled);
        updateImage();
    }

    protected void onFinishInflate() {
        this.stateHandler.setEnabled(!this.stateHandler.getDisabled());
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                boolean handled = SmartGlassPlayButton.this.stateHandler.onTouch(event);
                SmartGlassPlayButton.this.updateImage();
                return handled;
            }
        });
    }

    public void setOnClickListener(OnClickListener listener) {
        super.setOnClickListener(TouchUtil.createOnClickListener(listener));
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        super.setOnLongClickListener(TouchUtil.createOnLongClickListener(listener));
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        boolean loadedNewImage = false;
        if (hasSize()) {
            loadedNewImage = this.stateHandler.onSizeChanged(getWidth(), getHeight());
        }
        if (loadedNewImage) {
            updateImage();
        }
    }

    private boolean hasSize() {
        return getWidth() > 0 && getHeight() > 0;
    }

    protected void updateImage() {
        this.playIcon.setBackgroundDrawable(this.stateHandler.getImageDrawable());
    }
}
