package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;

public class AppItemViewHolder {
    private final TextView descriptionView;
    private Object key;
    private final XLEUniformImageView tileView;
    private final TextView titleView;

    public AppItemViewHolder(View parentView) {
        this.titleView = (TextView) parentView.findViewById(R.id.listItem_title);
        this.descriptionView = (TextView) parentView.findViewById(R.id.listItem_description);
        this.tileView = (XLEUniformImageView) parentView.findViewById(R.id.listItem_tile);
    }

    public TextView getTitleView() {
        return this.titleView;
    }

    public TextView getDescriptionView() {
        return this.descriptionView;
    }

    public XLEUniformImageView getTileView() {
        return this.tileView;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getKey() {
        return this.key;
    }
}
