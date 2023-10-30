package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;

public class MetacriticRatingView extends LinearLayout {
    private CustomTypefaceTextView ratingText;

    public MetacriticRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setRating(int value) {
        if (value == 0) {
            setVisibility(8);
            return;
        }
        setVisibility(0);
        this.ratingText.setText(String.valueOf(value));
    }

    private void init(Context context, AttributeSet attrs) {
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.metacritic_rating_view, this, true);
        this.ratingText = (CustomTypefaceTextView) findViewById(R.id.metacritic_rating_value);
    }
}
