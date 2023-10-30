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

public class SimpleGridLayout extends AbstractGridLayout {
    private int columnCount;
    private int columnWidth;
    private int columnWidthMod;
    private int gridDividerSize;
    private int height;
    private int rowCount;
    private int rowHeight;
    private int rowHeightMod;
    private int width;

    public SimpleGridLayout(Context context, AttributeSet attrs) {
        boolean z;
        boolean z2 = true;
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("SimpleGridLayout"));
        this.gridDividerSize = a.getDimensionPixelSize(XboxApplication.Instance.getStyleableRValue("SimpleGridLayout_gridDividerSize"), 0);
        if (!XLEApplication.Instance.isAspectRatioLong()) {
            int notLongColumnNumber = a.getInt(XboxApplication.Instance.getStyleableRValue("SimpleGridLayout_notLongColumnNumber"), 0);
            if (notLongColumnNumber > 0) {
                setColumnCount(notLongColumnNumber);
            }
        }
        a.recycle();
        this.columnCount = getColumnCount();
        this.rowCount = getRowCount();
        if (getRowCount() > 0) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (getColumnCount() <= 0) {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && getGridAdapter() != null && !(this.width == w && this.height == h)) {
            w -= this.gridDividerSize * (this.columnCount - 1);
            h -= this.gridDividerSize * (this.rowCount - 1);
            this.columnWidth = w / this.columnCount;
            this.rowHeight = h / this.rowCount;
            this.columnWidthMod = w % this.columnCount;
            this.rowHeightMod = h % this.rowCount;
            notifyDataChanged();
        }
        this.width = w;
        this.height = h;
    }

    public void notifyDataChanged() {
        if (getGridAdapter() != null) {
            removeAllViews();
            int c = 0;
            while (c < this.columnCount) {
                int r = 0;
                while (r < this.rowCount) {
                    View view = getGridAdapter().getGridView((this.rowCount * c) + r);
                    LayoutParams params = new LayoutParams(GridLayout.spec(r, 1, CENTER), GridLayout.spec(c, 1, CENTER));
                    int gridWidth = this.columnWidth;
                    if (this.columnWidthMod != 0 && c >= this.columnCount - this.columnWidthMod) {
                        gridWidth++;
                    }
                    int gridHeight = this.rowHeight;
                    if (this.rowHeightMod != 0 && r >= this.rowCount - this.rowHeightMod) {
                        gridHeight++;
                    }
                    params.width = gridWidth;
                    params.height = gridHeight;
                    int rightMargin = this.gridDividerSize;
                    int bottomMargin = this.gridDividerSize;
                    if (c == this.columnCount - 1) {
                        rightMargin = 0;
                    }
                    if (r == this.rowCount - 1) {
                        bottomMargin = 0;
                    }
                    params.setMargins(0, 0, rightMargin, bottomMargin);
                    addView(view, params);
                    r++;
                }
                c++;
            }
            post(new Runnable() {
                public void run() {
                    SimpleGridLayout.this.requestLayout();
                }
            });
        }
    }
}
