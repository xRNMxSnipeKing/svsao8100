package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import com.microsoft.xbox.toolkit.network.ListState;
import java.util.ArrayList;
import java.util.List;

public class SimpleGridAdapter<T> {
    private Context context;
    private List<T> dataObjects;
    private int dataObjectsPreLength;
    private int emptyResourceId;
    private int gridLayoutListStateOrdinal = ListState.ValidContentState.ordinal();
    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    private ArrayList<View> viewCache = new ArrayList();
    private int viewResourceId;

    public SimpleGridAdapter(Context context, int resourceId, int emptyResourceId, List<T> objects) {
        this.context = context;
        this.viewResourceId = resourceId;
        this.emptyResourceId = emptyResourceId;
        this.dataObjects = objects;
        this.dataObjectsPreLength = this.dataObjects == null ? 0 : this.dataObjects.size();
    }

    public Context getContext() {
        return this.context;
    }

    public int getResourceId() {
        return this.viewResourceId;
    }

    public int getEmptyResourceId() {
        return this.emptyResourceId;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        this.mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.mDataSetObservable.unregisterObserver(observer);
        onDestory();
    }

    public void updateDataObjects(List<T> objects) {
        this.dataObjects = objects;
        this.dataObjectsPreLength = this.dataObjects == null ? 0 : this.dataObjects.size();
        notifyDataChanged();
    }

    public void notifyDataChanged() {
        int dataObjectsLength = this.dataObjects == null ? 0 : this.dataObjects.size();
        if (this.dataObjectsPreLength != dataObjectsLength) {
            onDestory();
            this.dataObjectsPreLength = dataObjectsLength;
        }
        this.mDataSetObservable.notifyChanged();
    }

    public void notifyDataInvalidated() {
        this.mDataSetObservable.notifyInvalidated();
    }

    public View getGridView(int index) {
        if (this.viewCache.size() > index && this.viewCache.get(index) != null) {
            return (View) this.viewCache.get(index);
        }
        this.viewCache.add(createGridView(index));
        return (View) this.viewCache.get(index);
    }

    protected View inflateEmptyView() {
        if (getEmptyResourceId() > 0) {
            return ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(getEmptyResourceId(), null);
        }
        return null;
    }

    protected View inflateView() {
        if (getResourceId() > 0) {
            return ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(getResourceId(), null);
        }
        return null;
    }

    protected View createGridView(int index) {
        T dataItem = getItem(index);
        if (dataItem == null) {
            return inflateEmptyView();
        }
        View v = inflateView();
        v.setTag(dataItem);
        return v;
    }

    public void setGridLayoutModelState(int listStateOrdinal) {
        this.gridLayoutListStateOrdinal = listStateOrdinal;
    }

    public T getItem(int index) {
        if (this.gridLayoutListStateOrdinal == ListState.ValidContentState.ordinal() && this.dataObjects != null && this.dataObjects.size() > index) {
            return this.dataObjects.get(index);
        }
        return null;
    }

    public void onDestory() {
        int num = this.viewCache.size();
        for (int i = 0; i < num; i++) {
            onItemDestory((View) this.viewCache.get(i));
        }
        this.viewCache.clear();
    }

    public void onItemDestory(View view) {
    }
}
