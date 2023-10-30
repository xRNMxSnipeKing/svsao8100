package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import java.net.URI;
import java.util.ArrayList;

public class ActivityGalleryListAdapter extends ArrayAdapter<URI> {
    public ActivityGalleryListAdapter(Context context, int rowViewResourceId, ArrayList<URI> screenShotUrls) {
        super(context, rowViewResourceId, screenShotUrls);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.activity_gallery_list_item, null);
        }
        URI screenShotUrl = (URI) getItem(position);
        if (screenShotUrl != null) {
            v.setTag(screenShotUrl);
            XLEUniformImageView imageView = (XLEUniformImageView) v.findViewById(R.id.activity_gallery_image);
            if (imageView != null) {
                imageView.setImageURI2(screenShotUrl, R.drawable.activity_gallery_missing);
            }
        }
        if (v != null) {
            v.setFocusable(false);
        }
        return v;
    }
}
