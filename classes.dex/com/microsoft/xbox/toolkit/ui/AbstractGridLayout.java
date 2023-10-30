package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.widget.GridLayout;

public abstract class AbstractGridLayout extends GridLayout {
    private SimpleGridAdapter mAdapter;
    private AdapterDataSetObserver mAdapterDataSetObserver;

    class AdapterDataSetObserver extends DataSetObserver {
        AdapterDataSetObserver() {
        }

        public void onChanged() {
            AbstractGridLayout.this.notifyDataChanged();
        }

        public void onInvalidated() {
            AbstractGridLayout.this.notifyDataChanged();
        }
    }

    public abstract void notifyDataChanged();

    public AbstractGridLayout(Context context) {
        super(context);
    }

    public AbstractGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SimpleGridAdapter getGridAdapter() {
        return this.mAdapter;
    }

    public void setGridAdapter(SimpleGridAdapter adapter) {
        if (!(this.mAdapter == null || this.mAdapterDataSetObserver == null)) {
            this.mAdapter.unregisterDataSetObserver(this.mAdapterDataSetObserver);
            this.mAdapter.onDestory();
        }
        this.mAdapter = adapter;
        if (this.mAdapter != null) {
            this.mAdapterDataSetObserver = new AdapterDataSetObserver();
            this.mAdapter.registerDataSetObserver(this.mAdapterDataSetObserver);
            notifyDataChanged();
        }
    }
}
