package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.TextureManager;
import com.microsoft.xbox.xle.viewmodel.FriendItem;
import java.util.ArrayList;

public class SearchGamerListAdapter extends ArrayAdapter<FriendItem> {
    public SearchGamerListAdapter(Activity activity, int rowViewResourceId, ArrayList<FriendItem> friends) {
        super(activity, rowViewResourceId, friends);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (int i = 0; i < getCount(); i++) {
            FriendItem friend = (FriendItem) getItem(i);
            if (friend.getGamerpicUri() != null) {
                TextureManager.Instance().preload(friend.getGamerpicUri());
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleListItemViewHolder viewHolder;
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.simple_list_row, null);
            viewHolder = new SimpleListItemViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (SimpleListItemViewHolder) v.getTag();
        }
        FriendItem friend = (FriendItem) getItem(position);
        if (friend != null) {
            viewHolder.setKey(friend.getGamertag());
            if (viewHolder.getTitleView() != null) {
                viewHolder.getTitleView().setText(friend.getGamertag());
            }
            if (viewHolder.getDescriptionView() != null) {
                viewHolder.getDescriptionView().setText(friend.getName());
            }
            if (viewHolder.getTileView() != null) {
                viewHolder.getTileView().setImageURI2(friend.getGamerpicUri(), false);
            }
        }
        return v;
    }
}
