package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.TextureManager;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.viewmodel.FriendItem;
import java.util.ArrayList;
import java.util.HashSet;

public class FriendsListAdapter extends ArrayAdapter<FriendItem> {
    private HashSet<Integer> headerItems;
    private HashSet<Integer> nobodyItems;

    public FriendsListAdapter(Activity activity, int rowViewResourceId, ArrayList<FriendItem> friends) {
        super(activity, rowViewResourceId, friends);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        this.headerItems = new HashSet();
        this.nobodyItems = new HashSet();
        for (int i = 0; i < getCount(); i++) {
            FriendItem friend = (FriendItem) getItem(i);
            if (friend.getGamerpicUri() != null) {
                TextureManager.Instance().preload(friend.getGamerpicUri());
            }
            if (friend.getIsHeader()) {
                this.headerItems.add(Integer.valueOf(i));
            } else if (friend.getIsEmptyListText()) {
                this.nobodyItems.add(Integer.valueOf(i));
            }
        }
        super.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        FriendItem friend = (FriendItem) getItem(position);
        if (friend != null) {
            TextView textView;
            if (friend.getIsHeader()) {
                if (v == null || !(v instanceof TextView)) {
                    v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.friends_list_header, null);
                    textView = (TextView) v.findViewById(R.id.friends_list_header_text);
                    if (textView != null) {
                        textView.setText(friend.getStatusText());
                    } else {
                        ((TextView) v).setText(friend.getStatusText());
                    }
                }
                v.setTag(null);
            } else if (friend.getIsEmptyListText()) {
                if (v == null || !(v instanceof TextView)) {
                    v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.friends_list_nobody, null);
                    textView = (TextView) v.findViewById(R.id.friends_list_nobody_text);
                    if (textView != null) {
                        textView.setText(friend.getStatusText());
                    } else {
                        ((TextView) v).setText(friend.getStatusText());
                    }
                }
                v.setTag(null);
            } else {
                if (v == null || !(v instanceof LinearLayout)) {
                    v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.friends_list_row, null);
                }
                v.setTag(friend);
                TextView gamertagView = (TextView) v.findViewById(R.id.friends_listItem_gamertag);
                TextView statusView = (TextView) v.findViewById(R.id.friends_listItem_status);
                TextView lastOnlineView = (TextView) v.findViewById(R.id.friends_listItem_lastOnline);
                XLEImageViewFast tileView = (XLEImageViewFast) v.findViewById(R.id.friends_listItem_tile);
                CustomTypefaceTextView gameScoreView = (CustomTypefaceTextView) v.findViewById(R.id.friends_games_score);
                XLEImageViewFast offlineView = (XLEImageViewFast) v.findViewById(R.id.friends_offline);
                if (gamertagView != null) {
                    gamertagView.setText(friend.getGamertag());
                }
                if (statusView != null) {
                    statusView.setText(friend.getStatusText());
                }
                if (lastOnlineView != null) {
                    lastOnlineView.setText(friend.getLastOnlineText());
                }
                if (tileView != null) {
                    tileView.setImageURI2(friend.getGamerpicUri(), false);
                }
                if (gameScoreView != null) {
                    gameScoreView.setText(String.valueOf(friend.getGameScore()));
                }
                if (offlineView != null) {
                    if (friend.getIsOnline()) {
                        offlineView.setImageResource(R.drawable.online_icon);
                    } else {
                        offlineView.setImageResource(R.drawable.offline_icon);
                    }
                }
            }
        }
        return v;
    }

    public boolean isEnabled(int position) {
        return (this.headerItems.contains(Integer.valueOf(position)) || this.nobodyItems.contains(Integer.valueOf(position))) ? false : true;
    }

    public int getViewTypeCount() {
        return 3;
    }

    public int getItemViewType(int position) {
        if (this.headerItems.contains(Integer.valueOf(position))) {
            return 0;
        }
        if (this.nobodyItems.contains(Integer.valueOf(position))) {
            return 1;
        }
        return 2;
    }
}
