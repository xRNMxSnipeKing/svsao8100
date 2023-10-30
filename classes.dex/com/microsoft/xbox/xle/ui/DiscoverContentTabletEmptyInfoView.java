package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.app.XLEApplication;

public class DiscoverContentTabletEmptyInfoView extends LinearLayout {
    public DiscoverContentTabletEmptyInfoView(Context context) {
        super(context);
        init(context, null);
    }

    public DiscoverContentTabletEmptyInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DiscoverContentTabletEmptyInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService("layout_inflater");
        if (XLEApplication.Instance.isAspectRatioLong()) {
            vi.inflate(R.layout.discover_activity2_content_empty_info_long, this, true);
        } else {
            vi.inflate(R.layout.discover_activity2_content_empty_info_not_long, this, true);
        }
    }
}
