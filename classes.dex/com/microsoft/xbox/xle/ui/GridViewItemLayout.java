package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class GridViewItemLayout extends LinearLayout {
    private static int[] maxRowHeight;
    private static int numColumns;
    private int position;

    public GridViewItemLayout(Context context) {
        super(context);
    }

    public GridViewItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static void initItemLayout(int numColumns, int itemCount) {
        int numRows;
        numColumns = numColumns;
        if (itemCount % numColumns == 0) {
            numRows = itemCount / numColumns;
        } else {
            numRows = (itemCount / numColumns) + 1;
        }
        maxRowHeight = new int[numRows];
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (numColumns > 1 && maxRowHeight != null) {
            int rowIndex = this.position / numColumns;
            int measuredHeight = getMeasuredHeight();
            if (measuredHeight > maxRowHeight[rowIndex]) {
                maxRowHeight[rowIndex] = measuredHeight;
            }
            setMeasuredDimension(getMeasuredWidth(), maxRowHeight[rowIndex]);
        }
    }
}
