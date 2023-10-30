package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEImageView;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.util.List;

public class CollectionGalleryListAdapter extends ArrayAdapter<Title> {
    private List<Title> items;
    private int resourceId;

    public CollectionGalleryListAdapter(Activity activity, int resourceId, List<Title> items) {
        super(activity, resourceId, items);
        this.items = items;
        this.resourceId = resourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(this.resourceId, null);
        }
        Title title = (Title) this.items.get(position);
        if (title != null) {
            v.setTag(title);
            XLEUniformImageView imageView = (XLEUniformImageView) v.findViewById(R.id.collection_gallery_listItem_tile);
            CustomTypefaceTextView nameView = (CustomTypefaceTextView) v.findViewById(R.id.collection_gallery_listItem_title);
            CustomTypefaceTextView achievementsView = (CustomTypefaceTextView) v.findViewById(R.id.collection_gallery_listItem_achievements);
            XLEImageView achievementsImage = (XLEImageView) v.findViewById(R.id.collection_gallery_listItem_achievements_icon);
            CustomTypefaceTextView scoreView = (CustomTypefaceTextView) v.findViewById(R.id.collection_gallery_listItem_score);
            XLEImageView scoreImage = (XLEImageView) v.findViewById(R.id.collection_gallery_listItem_gicon);
            CustomTypefaceTextView releaseDateView = (CustomTypefaceTextView) v.findViewById(R.id.listItem_release_date);
            XLEImageView typeImage = (XLEImageView) v.findViewById(R.id.listItem_type_image);
            if (nameView != null) {
                nameView.setText(title.getName());
            }
            if (((Title) getItem(position)).IsGame()) {
                if (imageView != null) {
                    imageView.setImageURI2(title.getImageUrl(MeProfileModel.getModel().getLegalLocale()), XLEUtil.getMediaItemDefaultRid(1));
                }
                achievementsView.setVisibility(0);
                achievementsImage.setVisibility(0);
                scoreView.setVisibility(0);
                scoreImage.setVisibility(0);
                achievementsImage.setVisibility(title.getTotalAchievements() > 0 ? 0 : 4);
                achievementsView.setText(String.format("%d/%d", new Object[]{Integer.valueOf(title.getCurrentAchievements()), Integer.valueOf(title.getTotalAchievements())}));
                scoreView.setText(String.format("%d/%d", new Object[]{Integer.valueOf(title.getCurrentGamerScore()), Integer.valueOf(title.getTotalGamerScore())}));
                typeImage.setImageResource(R.drawable.xboxgame);
            } else {
                if (imageView != null) {
                    if (title.getIsXboxVideo()) {
                        imageView.setImageURI2(null, R.drawable.xbox_video_boxart);
                    } else if (title.getIsXboxMusic()) {
                        imageView.setImageURI2(null, R.drawable.xbox_music_boxart);
                    } else {
                        imageView.setImageURI2(title.getImageUrl(MeProfileModel.getModel().getLegalLocale()), XLEUtil.getMediaItemDefaultRid(61));
                    }
                }
                achievementsView.setVisibility(8);
                achievementsImage.setVisibility(8);
                scoreView.setVisibility(8);
                scoreImage.setVisibility(8);
                typeImage.setImageResource(R.drawable.xboxapp);
            }
            if (releaseDateView != null) {
                if (title.getLastPlayed() != null) {
                    releaseDateView.setText(XLEUtil.dateToDurationSinceNowValidate(title.getLastPlayed()));
                } else {
                    releaseDateView.setText("");
                }
            }
        }
        return v;
    }
}
