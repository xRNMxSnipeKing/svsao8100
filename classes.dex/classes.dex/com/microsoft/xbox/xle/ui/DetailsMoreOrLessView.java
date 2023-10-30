package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.anim.HeightAnimation;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.XLEApplication;

public class DetailsMoreOrLessView extends LinearLayout implements OnClickListener {
    private static int DEFAULT_ANIMATION_DURATION = 600;
    private static String LESS = XLEApplication.Resources.getString(R.string.details_less);
    private static String MORE = XLEApplication.Resources.getString(R.string.details_more);
    private boolean alwaysShowAllText;
    private int descriptionViewEllipsizeHeight;
    private int descriptionViewExpandHeight;
    private CustomTypefaceEllipsizeTextView detailsDescription;
    private HeightAnimation ellipaisAnimation;
    private HeightAnimation expandAnimation;
    private XLEButton moreOrLessButton;

    public DetailsMoreOrLessView(Context context) {
        this(context, null);
    }

    public void onClick(View v) {
        if (this.moreOrLessButton.getVisibility() == 0) {
            handleMoreAndLessClickEvent();
        }
    }

    public DetailsMoreOrLessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alwaysShowAllText = false;
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.details_more_or_less_layout, this, true);
        this.detailsDescription = (CustomTypefaceEllipsizeTextView) findViewById(R.id.details_description);
        this.moreOrLessButton = (XLEButton) findViewById(R.id.details_more_or_less);
        if (attrs != null) {
            this.alwaysShowAllText = context.obtainStyledAttributes(attrs, R.styleable.DetailsMoreOrLessView).getBoolean(0, false);
        }
        this.detailsDescription.setAlwaysShowText(this.alwaysShowAllText);
        if (this.alwaysShowAllText) {
            this.moreOrLessButton.setVisibility(8);
            return;
        }
        this.detailsDescription.setEllipsizeListener(new EllipsizeListener() {
            public void onEllipsizeChange(boolean isEllipsized) {
                if (isEllipsized) {
                    DetailsMoreOrLessView.this.moreOrLessButton.setText(DetailsMoreOrLessView.MORE);
                    DetailsMoreOrLessView.this.moreOrLessButton.setVisibility(0);
                    DetailsMoreOrLessView.this.setClickable(true);
                    return;
                }
                DetailsMoreOrLessView.this.detailsDescription.setLines(DetailsMoreOrLessView.this.detailsDescription.getLineCount());
                DetailsMoreOrLessView.this.moreOrLessButton.setVisibility(8);
                DetailsMoreOrLessView.this.setClickable(false);
            }
        });
        setOnClickListener(this);
        this.moreOrLessButton.setOnClickListener(this);
    }

    public void setText(String text) {
        if (this.alwaysShowAllText) {
            this.detailsDescription.setText(text);
            this.detailsDescription.setEllipsize(null);
        } else if (!this.detailsDescription.getText().equals(text)) {
            if (text != null || this.detailsDescription.getText().length() != 0) {
                this.detailsDescription.setLines(this.detailsDescription.getCollapsedLineCount());
                this.detailsDescription.setEllipsize(TruncateAt.END);
                this.detailsDescription.setText(text);
            }
        }
    }

    private void handleMoreAndLessClickEvent() {
        if (LESS.equals(this.moreOrLessButton.getText())) {
            this.moreOrLessButton.setText(MORE);
            if (this.ellipaisAnimation == null) {
                this.ellipaisAnimation = new HeightAnimation(this.detailsDescription.getHeight(), this.descriptionViewEllipsizeHeight);
                this.ellipaisAnimation.setTargetView(this.detailsDescription);
                this.ellipaisAnimation.setAnimationListener(new AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        DetailsMoreOrLessView.this.detailsDescription.setLines(DetailsMoreOrLessView.this.detailsDescription.getCollapsedLineCount());
                        DetailsMoreOrLessView.this.detailsDescription.setEllipsize(TruncateAt.END);
                    }
                });
                this.ellipaisAnimation.setDuration((long) DEFAULT_ANIMATION_DURATION);
            } else {
                this.ellipaisAnimation.reset();
            }
            this.detailsDescription.startAnimation(this.ellipaisAnimation);
            return;
        }
        this.descriptionViewEllipsizeHeight = this.detailsDescription.getHeight();
        this.descriptionViewExpandHeight = this.detailsDescription.getTextExpandedHeight();
        this.moreOrLessButton.setText(LESS);
        if (this.expandAnimation == null) {
            this.expandAnimation = new HeightAnimation(this.descriptionViewEllipsizeHeight, this.descriptionViewExpandHeight);
            this.expandAnimation.setTargetView(this.detailsDescription);
            this.expandAnimation.setDuration((long) DEFAULT_ANIMATION_DURATION);
            this.expandAnimation.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    DetailsMoreOrLessView.this.detailsDescription.setLines(DetailsMoreOrLessView.this.detailsDescription.getLineCount());
                    DetailsMoreOrLessView.this.detailsDescription.setEllipsize(null);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                }
            });
        } else {
            this.expandAnimation.reset();
        }
        this.detailsDescription.startAnimation(this.expandAnimation);
    }
}
