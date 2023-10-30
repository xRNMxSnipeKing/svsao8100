package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.serialization.MessageSummary;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.TextureManager;
import com.microsoft.xbox.toolkit.ui.ToggleTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.app.XLEApplication;
import java.util.ArrayList;
import java.util.Iterator;

public class MessagesListAdapter extends ArrayAdapter<MessageSummary> {
    ArrayList<MessageSummary> items;
    int selectedPos = -1;

    public MessagesListAdapter(Activity activity, int rowViewResourceId, ArrayList<MessageSummary> items) {
        super(activity, rowViewResourceId, items);
        this.items = items;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        if (this.items != null) {
            Iterator i$ = this.items.iterator();
            while (i$.hasNext()) {
                MessageSummary summary = (MessageSummary) i$.next();
                if (summary.SenderGamerPicUri != null) {
                    TextureManager.Instance().preload(summary.SenderGamerPicUri);
                }
            }
        }
        super.notifyDataSetChanged();
    }

    public void setSelectedPos(int position) {
        this.selectedPos = position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.messages_list_row, null);
        }
        MessageSummary message = (MessageSummary) this.items.get(position);
        if (message != null) {
            v.setTag(message);
            ToggleTypefaceTextView titleView = (ToggleTypefaceTextView) v.findViewById(R.id.messages_listItem_title);
            ToggleTypefaceTextView senderView = (ToggleTypefaceTextView) v.findViewById(R.id.messages_listItem_sender);
            TextView sentDateView = (TextView) v.findViewById(R.id.messages_listItem_date);
            XLEImageViewFast tileView = (XLEImageViewFast) v.findViewById(R.id.messages_listItem_tile);
            ImageView photoIcon = (ImageView) v.findViewById(R.id.messages_listItem_photo);
            ImageView audioIcon = (ImageView) v.findViewById(R.id.messages_listItem_audio);
            if (titleView != null) {
                titleView.setIsPositive(message.HasBeenRead);
                titleView.setText(message.getDisplaySubject());
            }
            if (senderView != null) {
                senderView.setIsPositive(message.HasBeenRead);
                senderView.setText(message.SenderGamertag);
            }
            if (sentDateView != null) {
                sentDateView.setText(JavaUtil.getLocalizedDateString(message.SentTime));
            }
            if (tileView != null) {
                tileView.setImageURI2(message.SenderGamerPicUri);
            }
            if (photoIcon != null) {
                if (message.HasImage) {
                    photoIcon.setVisibility(0);
                } else {
                    photoIcon.setVisibility(8);
                }
            }
            if (audioIcon != null) {
                if (message.HasVoice) {
                    audioIcon.setVisibility(0);
                } else {
                    audioIcon.setVisibility(8);
                }
            }
            if (XLEApplication.Instance.getIsTablet()) {
                if (this.selectedPos == position) {
                    v.setBackgroundColor(XLEApplication.getMainActivity().getResources().getColor(R.color.palegray));
                } else {
                    v.setBackgroundColor(0);
                }
            }
        }
        return v;
    }
}
