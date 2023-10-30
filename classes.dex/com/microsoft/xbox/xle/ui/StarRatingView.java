package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XboxApplication;

public class StarRatingView extends View {
    private static final int DefaultPaddingLeft = 3;
    private static final int DefaultTotalStar = 5;
    private static final int MASK_COLOR_GRAY = 2;
    private float averageUserRating;
    private Bitmap emptyBitmap;
    private Bitmap fullStarBitmap;
    private Bitmap halfStarBitmap;
    private Paint paint;
    private Bitmap quarterBitmap;
    private Bitmap threeQuartersBitmap;

    public StarRatingView(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    public StarRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.paint = new Paint();
        this.fullStarBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.star_full);
        TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("StarRatingView"));
        if (a.getInt(XboxApplication.Instance.getStyleableRValue("StarRatingView_maskColorType"), 0) == 2) {
            this.threeQuartersBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.star_3qtr_gray);
            this.halfStarBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.star_half_gray);
            this.quarterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.star_qtr_gray);
            this.emptyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.star_empty_gray);
        } else {
            this.threeQuartersBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.star_3qtr);
            this.halfStarBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.star_half);
            this.quarterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.star_qtr);
            this.emptyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.star_empty);
        }
        a.recycle();
    }

    public void setAverageUserRating(float averageUserRating) {
        if (this.averageUserRating != averageUserRating) {
            this.averageUserRating = averageUserRating;
            postInvalidate();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((((this.fullStarBitmap.getWidth() * 5) + 12) + getPaddingLeft()) + getPaddingRight(), (this.fullStarBitmap.getHeight() + getPaddingTop()) + getPaddingBottom());
    }

    protected void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int leftNow = left;
        int fullStarNum = (int) this.averageUserRating;
        int emptyStarNum = (5 - fullStarNum) - 1;
        float quarterStar = this.averageUserRating - ((float) fullStarNum);
        for (i = 0; i < fullStarNum; i++) {
            canvas.drawBitmap(this.fullStarBitmap, (float) leftNow, (float) top, this.paint);
            leftNow += this.fullStarBitmap.getWidth() + 3;
        }
        if (quarterStar <= 0.0f) {
            canvas.drawBitmap(this.emptyBitmap, (float) leftNow, (float) top, this.paint);
            leftNow += this.emptyBitmap.getWidth() + 3;
        } else if (((double) quarterStar) <= 0.25d) {
            canvas.drawBitmap(this.quarterBitmap, (float) leftNow, (float) top, this.paint);
            leftNow += this.quarterBitmap.getWidth() + 3;
        } else if (((double) quarterStar) <= 0.5d) {
            canvas.drawBitmap(this.halfStarBitmap, (float) leftNow, (float) top, this.paint);
            leftNow += this.halfStarBitmap.getWidth() + 3;
        } else if (((double) quarterStar) <= 0.75d) {
            canvas.drawBitmap(this.threeQuartersBitmap, (float) leftNow, (float) top, this.paint);
            leftNow += this.threeQuartersBitmap.getWidth() + 3;
        } else {
            canvas.drawBitmap(this.fullStarBitmap, (float) leftNow, (float) top, this.paint);
            leftNow += this.fullStarBitmap.getWidth() + 3;
        }
        for (i = 0; i < emptyStarNum; i++) {
            canvas.drawBitmap(this.emptyBitmap, (float) leftNow, (float) top, this.paint);
            leftNow += this.emptyBitmap.getWidth() + 3;
        }
    }
}
