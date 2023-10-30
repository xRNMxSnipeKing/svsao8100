package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.TextureManager;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.viewmodel.FriendSelectorItem;
import java.util.ArrayList;
import java.util.Iterator;

public class FriendsSelectorListAdapter extends ArrayAdapter<FriendSelectorItem> {
    public FriendsSelectorListAdapter(Activity activity, int rowViewResourceId, ArrayList<FriendSelectorItem> friends) {
        super(activity, rowViewResourceId, friends);
        notifyDataSetChanged();
        Iterator i$ = friends.iterator();
        while (i$.hasNext()) {
            FriendSelectorItem friend = (FriendSelectorItem) i$.next();
            if (friend instanceof FriendSelectorItem) {
                TextureManager.Instance().preload(friend.getGamerpicUri());
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (getItem(position) instanceof FriendSelectorItem) {
            if (v == null || !(v instanceof LinearLayout)) {
                v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.friends_selector_row, null);
            }
            FriendSelectorItem friend = (FriendSelectorItem) getItem(position);
            if (friend != null) {
                v.setTag(friend);
                TextView gamertagView = (TextView) v.findViewById(R.id.friends_listItem_gamertag);
                XLEImageViewFast tileView = (XLEImageViewFast) v.findViewById(R.id.friends_listItem_tile);
                TextView nameView = (TextView) v.findViewById(R.id.friends_listItem_name);
                ImageView selectorIcon = (ImageView) v.findViewById(R.id.friends_listItem_selector_icon);
                if (gamertagView != null) {
                    gamertagView.setText(friend.getGamertag());
                }
                if (tileView != null) {
                    tileView.setImageURI2(friend.getGamerpicUri(), false);
                }
                if (nameView != null) {
                    nameView.setText(friend.getName());
                }
                if (friend.getIsSelected()) {
                    selectorIcon.setVisibility(0);
                } else {
                    selectorIcon.setVisibility(4);
                }
            }
        } else {
            if (v == null || !(v instanceof TextView)) {
                v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.friends_list_header, null);
            }
            String headerText = ((FriendSelectorItem) getItem(position)).toString();
            if (headerText != null && (v instanceof TextView)) {
                v.setTag(null);
                ((TextView) v).setText(headerText);
            }
        }
        return v;
    }
}
