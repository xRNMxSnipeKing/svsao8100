package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class XLEImageView extends ImageView {
    public String TEST_loadingOrLoadedImageUrl;

    public XLEImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSoundEffectsEnabled(false);
    }

    public XLEImageView(Context context) {
        super(context);
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            super.setImageBitmap(bitmap);
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        super.setOnClickListener(TouchUtil.createOnClickListener(listener));
    }
}
