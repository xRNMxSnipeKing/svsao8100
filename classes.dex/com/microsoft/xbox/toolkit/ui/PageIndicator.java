package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xle.test.interop.TestInterop;

public class PageIndicator extends RelativeLayout {
    private int currentPageIndex = -1;
    private XLEImageViewFast[] images;
    private boolean initialized = false;
    private LinearLayout linearLayout;
    private int totalPageCount = 0;

    public PageIndicator(Context context) {
        super(context);
        initialize();
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        this.linearLayout = new LinearLayout(getContext());
        this.linearLayout.setOrientation(0);
        removeAllViews();
        LayoutParams bodyParams = new LayoutParams(-2, -2);
        bodyParams.addRule(13);
        addView(this.linearLayout, bodyParams);
        this.initialized = true;
    }

    public void setTotalPageCount(int pageCount) {
        if (!this.initialized) {
            initialize();
        }
        if (pageCount == 0) {
            this.linearLayout.removeAllViews();
            this.currentPageIndex = -1;
            this.totalPageCount = 0;
            this.images = null;
            setVisibility(8);
            return;
        }
        if (this.totalPageCount != pageCount) {
            this.totalPageCount = pageCount;
            TestInterop.setTotalPageCount(this.totalPageCount);
            this.linearLayout.removeAllViews();
            if (this.totalPageCount > 1) {
                this.images = new XLEImageViewFast[this.totalPageCount];
                for (int i = 0; i < this.images.length; i++) {
                    XLEImageViewFast image = new XLEImageViewFast(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("pageIndicatorSize")), XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("pageIndicatorSize")));
                    params.setMargins(XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("pageIndicatorMargin")), XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("pageIndicatorMargin")), XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("pageIndicatorMargin")), XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("pageIndicatorMargin")));
                    image.setImageResource(XboxApplication.Instance.getDrawableRValue("gray_circle"));
                    image.setScaleType(ScaleType.FIT_XY);
                    TestInterop.setPageState(i, XboxApplication.Instance.getDrawableRValue("gray_circle"));
                    this.linearLayout.addView(image, params);
                    this.images[i] = image;
                }
            }
            this.currentPageIndex = -1;
        }
        setVisibility(0);
    }

    public void setCurrentPage(int currentPageIndex) {
        XLEAssert.assertTrue("Page index should be positive.", currentPageIndex >= 0);
        if (this.totalPageCount > 1 && this.currentPageIndex != currentPageIndex && currentPageIndex < this.images.length) {
            if (this.currentPageIndex >= 0) {
                this.images[this.currentPageIndex].setImageResource(XboxApplication.Instance.getDrawableRValue("gray_circle"));
                TestInterop.setPageState(this.currentPageIndex, XboxApplication.Instance.getDrawableRValue("gray_circle"));
            }
            this.currentPageIndex = currentPageIndex;
            this.images[this.currentPageIndex].setImageResource(XboxApplication.Instance.getDrawableRValue("black_circle"));
            TestInterop.setPageState(this.currentPageIndex, XboxApplication.Instance.getDrawableRValue("black_circle"));
        }
    }

    public int getId() {
        return XboxApplication.Instance.getIdRValue("pivot_page_indicator");
    }

    public boolean isLayoutRequested() {
        boolean override = super.isLayoutRequested();
        if (isEnabled()) {
            return override;
        }
        XLELog.Diagnostic("PageIndicator", "App bar animating. Overriding isLayoutRequested to false");
        return false;
    }
}
