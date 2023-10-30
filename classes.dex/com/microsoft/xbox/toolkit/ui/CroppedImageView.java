package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.XLEAsyncTask;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;

public class CroppedImageView extends ImageView {
    private static final int CROP_WAIT_TIMEOUT_MS = 1500;
    private Rect destRect;
    private Runnable onBitmapBindComplete = null;
    private Bitmap pendingBitmap = null;
    private Rect sourceRect;

    private class CropBitmapTask extends XLEAsyncTask<Bitmap> {
        private Bitmap sourceBitmap;

        public CropBitmapTask(Bitmap sourceBitmap) {
            super(XLEThreadPool.textureThreadPool);
            this.sourceBitmap = sourceBitmap;
        }

        protected void onPreExecute() {
        }

        protected Bitmap doInBackground() {
            BackgroundThreadWaitor.getInstance().waitForReady(CroppedImageView.CROP_WAIT_TIMEOUT_MS);
            Bitmap cropped = null;
            try {
                cropped = Bitmap.createBitmap(CroppedImageView.this.destRect.width(), CroppedImageView.this.destRect.height(), Config.ARGB_8888);
                new Canvas(cropped).drawBitmap(this.sourceBitmap, CroppedImageView.this.sourceRect, CroppedImageView.this.destRect, null);
            } catch (Exception ex) {
                XLELog.Error("CroppedImageView", "Failed to crop bitmap: " + ex.toString());
            }
            BackgroundThreadWaitor.getInstance().waitForReady(CroppedImageView.CROP_WAIT_TIMEOUT_MS);
            return cropped;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                CroppedImageView.this.onBitmapCropCompleted(result);
            }
        }
    }

    public CroppedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("CroppedImageView"));
        this.sourceRect = new Rect(a.getInteger(XboxApplication.Instance.getStyleableRValue("CroppedImageView_startX"), 0), a.getInt(XboxApplication.Instance.getStyleableRValue("CroppedImageView_startY"), 0), a.getInteger(XboxApplication.Instance.getStyleableRValue("CroppedImageView_endX"), 0), a.getInt(XboxApplication.Instance.getStyleableRValue("CroppedImageView_endY"), 0));
        this.destRect = new Rect(0, 0, this.sourceRect.width(), this.sourceRect.height());
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            new CropBitmapTask(bitmap).execute();
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (hasSize() && this.pendingBitmap != null) {
            bindToBitmap(this.pendingBitmap);
        }
    }

    private void onBitmapCropCompleted(Bitmap cropped) {
        XLELog.Diagnostic("MVHFPS", "onBitmapCropComplete");
        if (hasSize()) {
            bindToBitmap(cropped);
        } else {
            this.pendingBitmap = cropped;
        }
    }

    private void bindToBitmap(Bitmap bitmap) {
        this.pendingBitmap = null;
        if (this.onBitmapBindComplete != null) {
            this.onBitmapBindComplete.run();
        }
        super.setImageBitmap(bitmap);
    }

    private boolean hasSize() {
        return getWidth() > 0 && getHeight() > 0;
    }

    public void setOnBitmapBindCompleteRunnable(Runnable runnable) {
        this.onBitmapBindComplete = runnable;
    }
}
