package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView.ScaleType;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XboxApplication;
import java.net.URI;

public class XLEUniformImageView extends XLEImageView {
    public static final int FIX_HEIGHT = 2;
    public static final int FIX_WIDTH = 1;
    private float aspectRatio;
    private int currentErrorResourceId = -1;
    private int currentLoadingResourceId = -1;
    private URI currentUri = null;
    private boolean fixHeight = false;
    private boolean fixWidth = false;
    private int maxHeight;
    private int maxWidth;
    private int measuredHeight;
    private int measuredWidth;

    public XLEUniformImageView(Context context) {
        super(context);
        setSoundEffectsEnabled(false);
    }

    public XLEUniformImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("XLEUniformImageView"));
        int fixDimension = a.getInt(XboxApplication.Instance.getStyleableRValue("XLEUniformImageView_fixDimension"), 0);
        int maxWidth = a.getDimensionPixelSize(XboxApplication.Instance.getStyleableRValue("XLEUniformImageView_maxWidth"), 0);
        int maxHeight = a.getDimensionPixelSize(XboxApplication.Instance.getStyleableRValue("XLEUniformImageView_maxHeight"), 0);
        this.aspectRatio = a.getFloat(XboxApplication.Instance.getStyleableRValue("XLEUniformImageView_aspectRatio"), 0.0f);
        a.recycle();
        setFixDimension(fixDimension, maxWidth, maxHeight);
        setScaleType(ScaleType.FIT_CENTER);
        setSoundEffectsEnabled(false);
    }

    public void setFixDimension(int fixFlag, int maxWidth, int maxHeight) {
        if (JavaUtil.containsFlag(fixFlag, 1)) {
            this.fixWidth = true;
        }
        if (JavaUtil.containsFlag(fixFlag, 2)) {
            this.fixHeight = true;
        }
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    public void setImageURI2(URI uri, int defaultResourceId) {
        setImageURI2(uri, defaultResourceId, defaultResourceId);
    }

    private void calculateAspectRatioFromResource(int errorResourceId) {
        if (errorResourceId > 0) {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(XboxApplication.Resources, errorResourceId, options);
            this.aspectRatio = ((float) options.outHeight) / ((float) options.outWidth);
        }
    }

    public void setImageURI2(URI uri, int loadingResourceId, int errorResourceId) {
        if (uri != this.currentUri || ((uri != null && !uri.equals(this.currentUri)) || loadingResourceId != this.currentLoadingResourceId || errorResourceId != this.currentErrorResourceId)) {
            calculateAspectRatioFromResource(errorResourceId);
            this.currentLoadingResourceId = loadingResourceId;
            this.currentErrorResourceId = errorResourceId;
            this.currentUri = uri;
            this.measuredWidth = 0;
            this.measuredHeight = 0;
            bindIfPossible();
            requestLayout();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if ((this.fixWidth && getLayoutParams() != null && getLayoutParams().width == -1) || (this.fixHeight && getLayoutParams() != null && getLayoutParams().height == -1)) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), AvatarEditorModel.AVATAREDIT_OPTION_COLOR_LIP), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), AvatarEditorModel.AVATAREDIT_OPTION_COLOR_LIP));
            this.maxWidth = getMeasuredWidth();
            this.maxHeight = getMeasuredHeight();
        }
        int[] size = sizeToAR((this.maxWidth - getLeftPaddingOffset()) - getRightPaddingOffset(), (this.maxHeight - getTopPaddingOffset()) - getBottomPaddingOffset());
        if (size != null) {
            this.measuredWidth = size == null ? getMeasuredWidth() : (size[0] + getLeftPaddingOffset()) + getRightPaddingOffset();
            this.measuredHeight = size == null ? getMeasuredHeight() : (size[1] + getTopPaddingOffset()) + getBottomPaddingOffset();
        }
        setMeasuredDimension(this.measuredWidth, this.measuredHeight);
        bindIfPossible();
    }

    private void bindIfPossible() {
        if (this.measuredWidth > 0 && this.measuredHeight > 0) {
            TextureManager.Instance().bindToView(this.currentUri, this, new TextureBindingOption(this.measuredWidth, this.measuredHeight, this.currentLoadingResourceId, this.currentErrorResourceId, false));
        }
    }

    private int[] sizeToAR(int w, int h) {
        if (this.aspectRatio <= 0.0f) {
            return null;
        }
        int newHeight = h;
        int newWidth = w;
        float bmpAspectRatio = this.aspectRatio;
        if (this.aspectRatio > 0.0f) {
            bmpAspectRatio = this.aspectRatio;
        }
        float destAspectRatio = ((float) h) / ((float) w);
        if (this.fixWidth && this.fixHeight) {
            if (destAspectRatio < bmpAspectRatio) {
                newHeight = h;
                newWidth = (int) (((float) h) / bmpAspectRatio);
            } else {
                newWidth = w;
                newHeight = (int) (((float) w) * bmpAspectRatio);
            }
        } else if (this.fixWidth) {
            newWidth = w;
            newHeight = (int) (((float) w) * bmpAspectRatio);
        } else if (this.fixHeight) {
            newHeight = h;
            newWidth = (int) (((float) h) / bmpAspectRatio);
        }
        return new int[]{newWidth, newHeight};
    }
}
