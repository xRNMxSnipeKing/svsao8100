package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.SearchTermData;
import java.util.List;

public class PopularNowListAdapter extends ArrayAdapter<SearchTermData> {
    private List<SearchTermData> items;

    public PopularNowListAdapter(Activity activity, int rowViewResourceId, List<SearchTermData> items) {
        super(activity, rowViewResourceId, items);
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.popular_now_list_row, null);
        }
        SearchTermData item = (SearchTermData) this.items.get(position);
        if (item != null) {
            v.setTag(item);
            TextView popularNowTagView = (TextView) v.findViewById(R.id.popular_now_listItem_title);
            if (popularNowTagView != null) {
                popularNowTagView.setText(item.getValue());
            }
        }
        return v;
    }
}
