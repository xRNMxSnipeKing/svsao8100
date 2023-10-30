package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel.CollectionFilter;

public class CollectionFilterListAdapter extends ArrayAdapter<CollectionFilter> {
    private int resourceId;
    private CollectionFilter selectedFilter;

    public CollectionFilterListAdapter(Context context, int resourceId, CollectionFilter selectedFilter) {
        super(context, resourceId, CollectionFilter.values());
        this.resourceId = resourceId;
        this.selectedFilter = selectedFilter;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(this.resourceId, null);
        }
        CollectionFilter filter = (CollectionFilter) getItem(position);
        if (filter != null) {
            TextView filterTextView = (TextView) view.findViewById(R.id.jfilter_search_type_name);
            filterTextView.setText(filter.getText());
            view.setTag(filter);
            if (filter == this.selectedFilter) {
                filterTextView.setTextColor(XLEApplication.Resources.getColor(R.color.textfieldgreen));
            } else if (XboxApplication.Instance.getIsTablet()) {
                filterTextView.setTextColor(XLEApplication.Resources.getColor(R.color.darkgray));
            } else {
                filterTextView.setTextColor(XLEApplication.Resources.getColor(R.color.textfieldwhite));
            }
        }
        return view;
    }
}
