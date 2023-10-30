package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchFilterCount;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.SearchHelper;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;
import java.util.List;

public class SearchFilterListAdapter extends ArrayAdapter<EDSV2SearchFilterCount> {
    private List<EDSV2SearchFilterCount> items;
    private int resource;

    public SearchFilterListAdapter(Activity activity, int resource, List<EDSV2SearchFilterCount> objects) {
        super(activity, resource, objects);
        this.items = objects;
        this.resource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(this.resource, null);
        }
        EDSV2SearchFilterCount searchFilterCountData = (EDSV2SearchFilterCount) this.items.get(position);
        if (searchFilterCountData != null) {
            TextView typeName = (TextView) view.findViewById(R.id.jfilter_search_type_name);
            if (searchFilterCountData.getFilterType() == XLEGlobalData.getInstance().getSelectedFilter()) {
                typeName.setTextColor(XLEApplication.Resources.getColor(R.color.textfieldgreen));
            } else if (XboxApplication.Instance.getIsTablet()) {
                typeName.setTextColor(XLEApplication.Resources.getColor(R.color.darkgray));
            } else {
                typeName.setTextColor(XLEApplication.Resources.getColor(R.color.textfieldwhite));
            }
            typeName.setText(SearchHelper.formatSearchFilterCountString(searchFilterCountData.getFilterType(), searchFilterCountData.getResultCount(), false));
            view.setTag(searchFilterCountData);
        }
        return view;
    }
}
