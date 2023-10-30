package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.xle.app.XLEApplication;

public class StarRatingWithUserCountView extends LinearLayout {
    private static final int MASK_COLOR_GRAY = 2;
    private StarRatingView starRatingView;
    private CustomTypefaceTextView userRateCount;

    public StarRatingWithUserCountView(Context context) {
        super(context);
        init(context);
    }

    public StarRatingWithUserCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context) {
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService("layout_inflater");
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("StarRatingView"));
            if (a.getInt(XboxApplication.Instance.getStyleableRValue("StarRatingView_maskColorType"), 0) == 2) {
                vi.inflate(R.layout.star_rating_gray_with_user_count, this, true);
            } else {
                vi.inflate(R.layout.star_rating_with_user_count, this, true);
            }
            a.recycle();
        } else {
            vi.inflate(R.layout.star_rating_with_user_count, this, true);
        }
        this.starRatingView = (StarRatingView) findViewById(R.id.average_user_rating);
        this.userRateCount = (CustomTypefaceTextView) findViewById(R.id.user_rate_count);
    }

    public void setAverageUserRatingAndUserCount(float averageUserRating, long userRateCount) {
        this.starRatingView.setAverageUserRating(averageUserRating);
        if (userRateCount > 0) {
            this.userRateCount.setText(String.format(XLEApplication.Resources.getString(R.string.details_user_rate_count), new Object[]{Long.valueOf(userRateCount)}));
        }
    }
}
