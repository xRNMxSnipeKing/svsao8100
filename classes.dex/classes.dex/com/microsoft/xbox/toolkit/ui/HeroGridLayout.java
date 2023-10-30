package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.XLEApplication;

public class HeroGridLayout extends AbstractGridLayout {
    private static final int HERO_SPAN_COLUMN = 1;
    private static final int HERO_SPAN_ROW = 0;
    private int columnWidth;
    private int columnWidthMod;
    private int gridDividerSize;
    private int height;
    private int heroHeight;
    private int heroSpan;
    private float heroWeight;
    private int heroWidth;
    private int rowHeight;
    private int rowHeightMod;
    private int width;

    public HeroGridLayout(Context context, AttributeSet attrs) {
        boolean z;
        boolean z2 = true;
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("HeroGridLayout"));
        this.heroSpan = a.getInt(XboxApplication.Instance.getStyleableRValue("HeroGridLayout_heroSpan"), 0);
        this.heroWeight = a.getFloat(XboxApplication.Instance.getStyleableRValue("HeroGridLayout_heroWeight"), 0.5f);
        this.gridDividerSize = a.getDimensionPixelSize(XboxApplication.Instance.getStyleableRValue("HeroGridLayout_heroDividerSize"), 0);
        if (!XLEApplication.Instance.isAspectRatioLong()) {
            int notLongColumnNumber = a.getInt(XboxApplication.Instance.getStyleableRValue("HeroGridLayout_heroNotLongColumnNumber"), 0);
            if (notLongColumnNumber > 0) {
                setColumnCount(notLongColumnNumber);
            }
        }
        a.recycle();
        if (getColumnCount() > 0) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (getRowCount() > 0) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if ((this.heroSpan != 0 || getColumnCount() <= 1) && (this.heroSpan != 1 || getRowCount() <= 1)) {
            z = false;
        } else {
            z = true;
        }
        XLEAssert.assertTrue(z);
        if (this.heroWeight <= 0.0f || this.heroWeight >= 1.0f) {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && getGridAdapter() != null && !(this.width == w && this.height == h)) {
            this.heroWidth = w;
            this.heroHeight = h;
            w -= this.gridDividerSize * (getColumnCount() - 1);
            h -= this.gridDividerSize * (getRowCount() - 1);
            if (this.heroSpan == 1) {
                this.heroHeight = (int) (((float) h) * this.heroWeight);
                this.columnWidth = w / getColumnCount();
                this.rowHeight = (h - this.heroHeight) / (getRowCount() - 1);
                this.columnWidthMod = w % getColumnCount();
                this.rowHeightMod = (h - this.heroHeight) % (getRowCount() - 1);
            } else {
                this.heroWidth = (int) (((float) w) * this.heroWeight);
                this.columnWidth = (w - this.heroWidth) / (getColumnCount() - 1);
                this.rowHeight = h / getRowCount();
                this.columnWidthMod = (w - this.heroWidth) % (getColumnCount() - 1);
                this.rowHeightMod = h % getRowCount();
            }
            notifyDataChanged();
        }
        this.width = w;
        this.height = h;
    }

    public void notifyDataChanged() {
        if (getGridAdapter() != null) {
            LayoutParams params;
            int i;
            removeAllViews();
            int rStart = 0;
            int cStart = 0;
            View view = getGridAdapter().getGridView(0);
            if (this.heroSpan == 1) {
                rStart = 1;
                params = new LayoutParams(GridLayout.spec(0, 1, CENTER), GridLayout.spec(0, getColumnCount(), CENTER));
                params.width = this.heroWidth;
                params.height = this.heroHeight;
                params.setMargins(0, 0, 0, this.gridDividerSize);
                addView(view, params);
                i = 0 + 1;
            } else {
                cStart = 1;
                params = new LayoutParams(GridLayout.spec(0, getRowCount(), CENTER), GridLayout.spec(0, 1, CENTER));
                params.width = this.heroWidth;
                params.height = this.heroHeight;
                params.setMargins(0, 0, this.gridDividerSize, 0);
                addView(view, params);
                i = 0 + 1;
            }
            int c = cStart;
            while (c < getColumnCount()) {
                int r = rStart;
                while (r < getRowCount()) {
                    view = getGridAdapter().getGridView(i);
                    params = new LayoutParams(GridLayout.spec(r, 1, CENTER), GridLayout.spec(c, 1, CENTER));
                    int gridWidth = this.columnWidth;
                    if (this.columnWidthMod != 0 && c >= getColumnCount() - this.columnWidthMod) {
                        gridWidth++;
                    }
                    int gridHeight = this.rowHeight;
                    if (this.rowHeightMod != 0 && r >= getRowCount() - this.rowHeightMod) {
                        gridHeight++;
                    }
                    params.width = gridWidth;
                    params.height = gridHeight;
                    int rightMargin = this.gridDividerSize;
                    int bottomMargin = this.gridDividerSize;
                    if (c == getColumnCount() - 1) {
                        rightMargin = 0;
                    }
                    if (r == getRowCount() - 1) {
                        bottomMargin = 0;
                    }
                    params.setMargins(0, 0, rightMargin, bottomMargin);
                    addView(view, params);
                    i++;
                    r++;
                }
                c++;
            }
            post(new Runnable() {
                public void run() {
                    HeroGridLayout.this.requestLayout();
                }
            });
        }
    }
}
