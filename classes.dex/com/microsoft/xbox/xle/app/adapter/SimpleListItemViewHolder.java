package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;

public class SimpleListItemViewHolder {
    private final TextView descriptionView;
    private Object key;
    private final XLEImageViewFast tileView;
    private final TextView titleView;

    public SimpleListItemViewHolder(View parentView) {
        this.titleView = (TextView) parentView.findViewById(R.id.listItem_title);
        this.descriptionView = (TextView) parentView.findViewById(R.id.listItem_description);
        this.tileView = (XLEImageViewFast) parentView.findViewById(R.id.listItem_tile);
    }

    public TextView getTitleView() {
        return this.titleView;
    }

    public TextView getDescriptionView() {
        return this.descriptionView;
    }

    public XLEImageViewFast getTileView() {
        return this.tileView;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getKey() {
        return this.key;
    }
}
