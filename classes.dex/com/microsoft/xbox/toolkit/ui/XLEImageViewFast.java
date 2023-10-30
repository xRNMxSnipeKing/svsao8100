package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.microsoft.xbox.toolkit.XboxApplication;
import java.net.URI;

public class XLEImageViewFast extends XLEImageView {
    protected int pendingBitmapResourceId = -1;
    private String pendingFilePath = null;
    protected URI pendingUri = null;
    private boolean useFileCache = true;

    public XLEImageViewFast(Context context) {
        super(context);
        setSoundEffectsEnabled(false);
    }

    public XLEImageViewFast(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("XLEImageViewFast"));
        setImageResource(a.getResourceId(XboxApplication.Instance.getStyleableRValue("XLEImageViewFast_src"), -1));
        a.recycle();
        setSoundEffectsEnabled(false);
    }

    public void setImageResource(int resourceId) {
        if (hasSize()) {
            bindToResourceId(resourceId);
        } else {
            this.pendingBitmapResourceId = resourceId;
        }
    }

    public void setImageURI2(URI uri) {
        if (hasSize()) {
            bindToUri(uri);
        } else {
            this.pendingUri = uri;
        }
    }

    public void setImageURI2(URI uri, boolean useFilaCache) {
        this.useFileCache = useFilaCache;
        if (hasSize()) {
            bindToUri(uri, new TextureBindingOption(getWidth(), getHeight(), false));
        } else {
            this.pendingUri = uri;
        }
    }

    public void setImageURI2(URI uri, int loadingResourceId, int errorResourceId) {
        TextureBindingOption option = new TextureBindingOption(getWidth(), getHeight(), loadingResourceId, errorResourceId, false);
        if (hasSize()) {
            bindToUri(uri, option);
        } else {
            this.pendingUri = uri;
        }
    }

    public void setImageFilePath(String filePath) {
        if (hasSize()) {
            bindToFilePath(filePath);
        } else {
            this.pendingFilePath = filePath;
        }
    }

    public void setImageURI(Uri uri) {
        throw new UnsupportedOperationException();
    }

    protected boolean hasSize() {
        return getWidth() > 0 && getHeight() > 0;
    }

    private void bindToResourceId(int resourceId) {
        this.pendingBitmapResourceId = -1;
        TextureManager.Instance().bindToView(resourceId, (ImageView) this, getWidth(), getHeight());
    }

    protected void bindToUri(URI uri) {
        this.pendingUri = null;
        bindToUri(uri, new TextureBindingOption(getWidth(), getHeight(), this.useFileCache));
    }

    private void bindToUri(URI uri, TextureBindingOption option) {
        this.pendingUri = null;
        TextureManager.Instance().bindToView(uri, this, option);
    }

    private void bindToFilePath(String filePath) {
        this.pendingFilePath = null;
        TextureManager.Instance().bindToViewFromFile(filePath, this, getWidth(), getHeight());
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSize(0, widthMeasureSpec), resolveSize(0, heightMeasureSpec));
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (hasSize()) {
            if (this.pendingBitmapResourceId >= 0) {
                bindToResourceId(this.pendingBitmapResourceId);
            }
            if (this.pendingUri != null) {
                bindToUri(this.pendingUri);
            }
            if (this.pendingFilePath != null) {
                bindToFilePath(this.pendingFilePath);
            }
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        super.setOnClickListener(TouchUtil.createOnClickListener(listener));
    }
}
