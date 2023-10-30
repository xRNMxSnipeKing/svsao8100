package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2GameMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MovieMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchResultItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVSeriesMediaItem;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEImageView;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.ui.MetacriticRatingView;
import com.microsoft.xbox.xle.ui.StarRatingWithUserCountView;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

public class MediaItemListAdapter<T extends EDSV2MediaItem> extends ArrayAdapter<T> {
    private Hashtable<Integer, Integer> defaultResourceIdToIndexTable = new Hashtable();
    private List<T> items;
    private int resourceId;

    public MediaItemListAdapter(Activity activity, int resourceId, List<T> items) {
        super(activity, resourceId, items);
        this.items = items;
        this.resourceId = resourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(this.resourceId, null);
        }
        EDSV2MediaItem item = (EDSV2MediaItem) this.items.get(position);
        if (item != null) {
            v.setTag(item);
            XLEUniformImageView imageView = (XLEUniformImageView) v.findViewById(R.id.search_data_result_tile_image);
            CustomTypefaceTextView nameView = (CustomTypefaceTextView) v.findViewById(R.id.search_data_result_name);
            CustomTypefaceTextView artistView = (CustomTypefaceTextView) v.findViewById(R.id.search_data_result_artist);
            XLEImageView typeImage = (XLEImageView) v.findViewById(R.id.search_data_result_type_image);
            CustomTypefaceTextView releaseDateView = (CustomTypefaceTextView) v.findViewById(R.id.tv_episode_details_release_date);
            StarRatingWithUserCountView starView = (StarRatingWithUserCountView) v.findViewById(R.id.search_data_result_rating_with_count);
            XLEImageViewFast smartGlassIcon = (XLEImageViewFast) v.findViewById(R.id.smartglassicon);
            MetacriticRatingView MetacriticRatingView = (MetacriticRatingView) v.findViewById(R.id.movie_details_metacriticrating);
            if (imageView != null) {
                imageView.setImageURI2(item.getImageUrl(), XLEUtil.getMediaItemDefaultRid(item.getMediaType()));
            }
            if (nameView != null) {
                nameView.setText(item.getTitle());
            }
            if (releaseDateView != null) {
                releaseDateView.setText(item.getReleaseDate() != null ? new SimpleDateFormat("yyyy").format(item.getReleaseDate()) : "");
            }
            if (item instanceof EDSV2SearchResultItem) {
                XLEUtil.setMediaItemViewValue(typeImage, item.getMediaType(), starView, ((EDSV2SearchResultItem) item).getAverageUserRating(), ((EDSV2SearchResultItem) item).getUserRatingCount(), artistView, ((EDSV2SearchResultItem) item).getArtistName());
            }
            if (item instanceof EDSV2GameMediaItem) {
                XLEUtil.setMediaItemViewValue(typeImage, item.getMediaType(), starView, ((EDSV2GameMediaItem) item).getAverageUserRating(), ((EDSV2GameMediaItem) item).getUserRatingCount(), artistView, null);
            }
            if (item instanceof EDSV2MovieMediaItem) {
                XLEUtil.setMediaItemViewValue(typeImage, item.getMediaType(), starView, 0.0f, 0, artistView, null);
                if (MetacriticRatingView != null) {
                    MetacriticRatingView.setRating((int) ((EDSV2MovieMediaItem) item).getMetaCriticReviewScore());
                }
            }
            if (item instanceof EDSV2TVSeriesMediaItem) {
                XLEUtil.setMediaItemViewValue(typeImage, item.getMediaType(), starView, 0.0f, 0, artistView, null);
            }
            if (smartGlassIcon != null) {
                smartGlassIcon.setVisibility(item.getHasSmartGlassActivity() ? 0 : 8);
            }
        }
        return v;
    }

    public int getItemViewType(int position) {
        int resourceId = XLEUtil.getMediaItemDefaultRid(((EDSV2MediaItem) getItem(position)).getMediaType());
        if (!this.defaultResourceIdToIndexTable.containsKey(Integer.valueOf(resourceId))) {
            this.defaultResourceIdToIndexTable.put(Integer.valueOf(resourceId), Integer.valueOf(this.defaultResourceIdToIndexTable.size()));
        }
        return ((Integer) this.defaultResourceIdToIndexTable.get(Integer.valueOf(resourceId))).intValue();
    }

    public int getViewTypeCount() {
        return 6;
    }
}
