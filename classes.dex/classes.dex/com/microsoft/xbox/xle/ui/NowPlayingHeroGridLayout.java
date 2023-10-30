package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.AbstractGridLayout;
import com.microsoft.xbox.xle.app.XLEApplication;

public class NowPlayingHeroGridLayout extends AbstractGridLayout {
    private int columnWidth;
    private int columnWidthMod;
    private int gridDividerSize;
    private int height;
    private int heroColumnCount = 2;
    private int heroHeight;
    private int heroRowCount = 2;
    private int heroWidth;
    private boolean isLongAspectRatio = true;
    private int rowHeight;
    private int rowHeightMod;
    private int width;

    public NowPlayingHeroGridLayout(Context context, AttributeSet attrs) {
        boolean z;
        boolean z2 = true;
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("HeroGridLayout"));
        this.gridDividerSize = a.getDimensionPixelSize(XboxApplication.Instance.getStyleableRValue("HeroGridLayout_heroDividerSize"), 0);
        if (!XLEApplication.Instance.isAspectRatioLong()) {
            int notLongColumnNumber = a.getInt(XboxApplication.Instance.getStyleableRValue("HeroGridLayout_heroNotLongColumnNumber"), 0);
            if (notLongColumnNumber > 0) {
                setColumnCount(notLongColumnNumber);
                this.isLongAspectRatio = false;
            }
        }
        a.recycle();
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
            w -= this.gridDividerSize * (getColumnCount() - 1);
            h -= this.gridDividerSize * (getRowCount() - 1);
            this.columnWidth = w / getColumnCount();
            this.rowHeight = h / getRowCount();
            this.columnWidthMod = w % getColumnCount();
            this.rowHeightMod = h % getRowCount();
            this.heroWidth = (this.heroColumnCount * this.columnWidth) + (this.gridDividerSize * (this.heroColumnCount - 1));
            this.heroHeight = (this.heroRowCount * this.rowHeight) + (this.gridDividerSize * (this.heroRowCount - 1));
            if (this.columnWidthMod != 0 && this.heroColumnCount > getColumnCount() - this.columnWidthMod) {
                this.heroWidth += (this.heroColumnCount - getColumnCount()) + this.columnWidthMod;
            }
            if (this.rowHeightMod != 0 && this.heroRowCount > getRowCount() - this.rowHeightMod) {
                this.heroHeight += (this.heroRowCount - getRowCount()) + this.rowHeightMod;
            }
            notifyDataChanged();
        }
        this.width = w;
        this.height = h;
    }

    private void createView(int cellIndex, int itemWidth, int itemHeight, int rowPosition, int columnPosition, int rowSpan, int columnSpan, int rightMargin, int topMargin) {
        View view = getGridAdapter().getGridView(cellIndex);
        LayoutParams params = new LayoutParams(GridLayout.spec(rowPosition, rowSpan, CENTER), GridLayout.spec(columnPosition, columnSpan, CENTER));
        params.width = itemWidth;
        params.height = itemHeight;
        params.setMargins(0, topMargin, rightMargin, 0);
        addView(view, params);
        Log.d("NowPlayingHeroGridLayout", String.format("Adding view: row %d, column %d, width %d, height %d", new Object[]{Integer.valueOf(rowSpan), Integer.valueOf(columnSpan), Integer.valueOf(this.columnWidth), Integer.valueOf(this.rowHeight)}));
    }

    public void notifyDataChanged() {
        if (getGridAdapter() != null) {
            removeAllViews();
            Log.d("NowPlayingHeroGridLayout", "Removing all views");
            int cellIndex = 0;
            int r = 0;
            while (r < getRowCount()) {
                int c = 0;
                while (c < getColumnCount()) {
                    if (r == 0 && c == 0) {
                        createView(cellIndex, this.heroWidth, this.heroHeight, r, c, this.heroRowCount, this.heroColumnCount, getColumnCount() > this.heroColumnCount ? this.gridDividerSize : 0, 0);
                    } else if (r < this.heroRowCount && c < this.heroColumnCount) {
                        c++;
                    } else if (r == 2 && c == 0) {
                        rightMargin = getColumnCount() > this.heroColumnCount ? this.gridDividerSize : 0;
                        gridHeight = this.rowHeight + this.gridDividerSize;
                        if (this.rowHeightMod != 0 && r >= getRowCount() - this.rowHeightMod) {
                            gridHeight++;
                        }
                        createView(cellIndex, this.heroWidth, gridHeight, r, c, 1, 2, rightMargin, 0);
                    } else if (r == 2 && c == 1) {
                        c++;
                    } else {
                        rightMargin = c == getColumnCount() + -1 ? 0 : this.gridDividerSize;
                        int topMargin = r == 0 ? 0 : this.gridDividerSize;
                        int gridWidth = this.columnWidth;
                        if (this.columnWidthMod != 0 && c >= getColumnCount() - this.columnWidthMod) {
                            gridWidth++;
                        }
                        gridHeight = this.rowHeight;
                        if (this.rowHeightMod != 0 && r >= getRowCount() - this.rowHeightMod) {
                            gridHeight++;
                        }
                        createView(cellIndex, gridWidth, gridHeight, r, c, 1, 1, rightMargin, topMargin);
                        XLELog.Diagnostic("HeroGridLayout", "notifyDataChanged columnIndex==" + c + "; rowIndex ==" + r + "; gridWidth==" + gridWidth + "; gridHeight==" + gridHeight + "; rightMargin==" + rightMargin + "; topMargin==" + topMargin);
                    }
                    cellIndex++;
                    c++;
                }
                r++;
            }
            post(new Runnable() {
                public void run() {
                    NowPlayingHeroGridLayout.this.requestLayout();
                }
            });
        }
    }

    public boolean isLongAspectRatio() {
        return this.isLongAspectRatio;
    }
}
