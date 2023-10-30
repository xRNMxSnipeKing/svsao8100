package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.TouchUtil;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;

public class DiscoverGridItem2 extends FrameLayout {
    public XLEUniformImageView image = ((XLEUniformImageView) findViewById(R.id.discover_select_grid_item_content));
    public CustomTypefaceTextView textview = ((CustomTypefaceTextView) findViewById(R.id.discover_select_grid_item_title));

    public DiscoverGridItem2(Context context) {
        super(context);
        setSoundEffectsEnabled(false);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.discover_grid_item2, this, true);
    }

    public void setOnClickListener(OnClickListener handler) {
        super.setOnClickListener(TouchUtil.createOnClickListener(handler));
    }
}
