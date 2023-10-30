package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.XLEApplication;

public class PieChartView extends View {
    private float earnedPercentage;
    private RectF externalRectf = new RectF();
    private Paint paint = new Paint(1);
    private float strokeWidth;
    private float unearnedPercentage;

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.strokeWidth = (float) context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("PieChartView")).getDimensionPixelSize(XboxApplication.Instance.getStyleableRValue("PieChartView_strokeWidth"), 13);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth(this.strokeWidth);
        setBackgroundColor(0);
    }

    public void setPercentage(int percentage) {
        if (percentage < 0 || percentage > 100) {
            this.earnedPercentage = 0.0f;
            this.unearnedPercentage = 360.0f;
        } else {
            this.earnedPercentage = (float) (((double) percentage) * 3.6d);
            this.unearnedPercentage = (float) (((double) (100 - percentage)) * 3.6d);
        }
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.externalRectf.left = (float) (((double) ((float) canvas.getWidth())) * 0.2d);
        this.externalRectf.top = (float) (((double) ((float) canvas.getHeight())) * 0.2d);
        this.externalRectf.right = (float) (((double) ((float) canvas.getWidth())) * 0.8d);
        this.externalRectf.bottom = (float) (((double) ((float) canvas.getHeight())) * 0.8d);
        this.paint.setColor(XLEApplication.Resources.getColor(R.color.ltgray));
        canvas.drawArc(this.externalRectf, 0.0f, 360.0f, false, this.paint);
        this.paint.setColor(XLEApplication.Resources.getColor(R.color.XboxGreen));
        canvas.drawArc(this.externalRectf, 0.0f, this.earnedPercentage, false, this.paint);
    }
}
