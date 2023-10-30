package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.ui.ToggleTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import java.util.List;

public class QuickplayListAdapter extends ArrayAdapter<Title> {
    List<Title> items;

    public QuickplayListAdapter(Activity activity, int rowViewResourceId, List<Title> items) {
        super(activity, rowViewResourceId, items);
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.quickplay_list_row, null);
        }
        Title quickplayItem = (Title) this.items.get(position);
        if (quickplayItem != null) {
            v.setTag(quickplayItem);
            ((XLEImageViewFast) v.findViewById(R.id.quickplay_listItem_tile)).setImageURI2(((Title) this.items.get(position)).getImageUrl("en-us"));
            ((ToggleTypefaceTextView) v.findViewById(R.id.quickplay_listItem_sender)).setText(((Title) this.items.get(position)).getName());
        }
        return v;
    }
}
