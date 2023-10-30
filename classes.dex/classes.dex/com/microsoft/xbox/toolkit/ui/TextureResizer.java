package com.microsoft.xbox.toolkit.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.microsoft.xbox.toolkit.XLELog;

public class TextureResizer {
    public static Bitmap createScaledBitmap8888(Bitmap source, int dstwidth, int dstheight, boolean filter) {
        Bitmap bitmap;
        int width = source.getWidth();
        int height = source.getHeight();
        float sx = ((float) dstwidth) / ((float) width);
        float sy = ((float) dstheight) / ((float) height);
        Matrix m = new Matrix();
        m.setScale(sx, sy);
        if (0 + width > source.getWidth()) {
            throw new IllegalArgumentException("x + width must be <= bitmap.width()");
        }
        if (0 + height > source.getHeight()) {
            throw new IllegalArgumentException("y + height must be <= bitmap.height()");
        } else if (!source.isMutable() && null == null && null == null && width == source.getWidth() && height == source.getHeight() && (m == null || m.isIdentity())) {
            return source;
        } else {
            int neww = width;
            int newh = height;
            Canvas canvas = new Canvas();
            Rect rect = new Rect(0, 0, 0 + width, 0 + height);
            RectF dstR = new RectF(0.0f, 0.0f, (float) width, (float) height);
            Paint paint = null;
            if (m != null) {
                try {
                    if (!m.isIdentity()) {
                        boolean hasAlpha = source.hasAlpha() || !m.rectStaysRect();
                        RectF deviceR = new RectF();
                        m.mapRect(deviceR, dstR);
                        bitmap = Bitmap.createBitmap(Math.round(deviceR.width()), Math.round(deviceR.height()), Config.ARGB_8888);
                        if (hasAlpha) {
                            bitmap.eraseColor(0);
                        }
                        canvas.translate(-deviceR.left, -deviceR.top);
                        canvas.concat(m);
                        Paint paint2 = new Paint();
                        try {
                            paint2.setFilterBitmap(filter);
                            if (!m.rectStaysRect()) {
                                paint2.setAntiAlias(true);
                            }
                            paint = paint2;
                        } catch (OutOfMemoryError e) {
                            paint = paint2;
                            XLELog.Error("createScaledBitmap8888", "failed to create bitmap");
                            bitmap = null;
                            if (bitmap != null) {
                                bitmap.setDensity(source.getDensity());
                                canvas.setBitmap(bitmap);
                                canvas.drawBitmap(source, rect, dstR, paint);
                            }
                            return bitmap;
                        }
                        if (bitmap != null) {
                            bitmap.setDensity(source.getDensity());
                            canvas.setBitmap(bitmap);
                            canvas.drawBitmap(source, rect, dstR, paint);
                        }
                        return bitmap;
                    }
                } catch (OutOfMemoryError e2) {
                    XLELog.Error("createScaledBitmap8888", "failed to create bitmap");
                    bitmap = null;
                    if (bitmap != null) {
                        bitmap.setDensity(source.getDensity());
                        canvas.setBitmap(bitmap);
                        canvas.drawBitmap(source, rect, dstR, paint);
                    }
                    return bitmap;
                }
            }
            bitmap = Bitmap.createBitmap(neww, newh, Config.ARGB_8888);
            paint = null;
            if (bitmap != null) {
                bitmap.setDensity(source.getDensity());
                canvas.setBitmap(bitmap);
                canvas.drawBitmap(source, rect, dstR, paint);
            }
            return bitmap;
        }
    }
}
