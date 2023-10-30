package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.WhatsNewActivityViewModel.WhatsNew;
import java.util.ArrayList;
import java.util.List;

public class WhatsNewListAdapter extends ArrayAdapter<WhatsNew> {
    private List<WhatsNew> items;

    public WhatsNewListAdapter(Context context, int rowViewResourceId, ArrayList<WhatsNew> items) {
        super(context, rowViewResourceId, items);
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.whats_new_list_row, null);
        }
        WhatsNew whatsNew = (WhatsNew) this.items.get(position);
        if (whatsNew != null) {
            XLEImageViewFast tileView = (XLEImageViewFast) view.findViewById(R.id.whatsnew_listItem_tile);
            TextView tittleView = (TextView) view.findViewById(R.id.whatsnew_listItem_title);
            TextView descView = (TextView) view.findViewById(R.id.whatsnew_listItem_description);
            if (whatsNew != null) {
                String tittle = getStrAsRes(whatsNew.tittleId);
                String desc = getStrAsRes(whatsNew.descriptionId);
                tileView.setImageResource(whatsNew.imageId);
                tittleView.setText(tittle);
                descView.setText(desc);
                view.setTag(whatsNew);
            }
        }
        return view;
    }

    private String getStrAsRes(int id) {
        return XLEApplication.getMainActivity().getApplicationContext().getResources().getString(id);
    }
}
