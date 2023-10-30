package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import java.util.List;

public class HeroGridAdapter<T> extends SimpleGridAdapter<T> {
    private int heroResourceId;

    public HeroGridAdapter(Context context, int heroResourceId, int resourceId, int emptyResourceId, List<T> objects) {
        super(context, resourceId, emptyResourceId, objects);
        this.heroResourceId = heroResourceId;
    }

    protected View createGridView(int index) {
        T dataItem = getItem(index);
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService("layout_inflater");
        if (dataItem == null && getEmptyResourceId() > 0) {
            return vi.inflate(getEmptyResourceId(), null);
        }
        if (index == 0) {
            View v = vi.inflate(this.heroResourceId, null);
            v.setTag(dataItem);
            return v;
        }
        v = vi.inflate(getResourceId(), null);
        v.setTag(dataItem);
        return v;
    }
}
