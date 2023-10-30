package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicAlbumMediaItem;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.text.SimpleDateFormat;
import java.util.List;

public class ArtistAlbumListAdapter extends ArrayAdapter<EDSV2MusicAlbumMediaItem> {
    private List<EDSV2MusicAlbumMediaItem> items;

    public ArtistAlbumListAdapter(Activity activity, int rowViewResourceId, List<EDSV2MusicAlbumMediaItem> items) {
        super(activity, rowViewResourceId, items);
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.artist_album_list_row, null);
        }
        EDSV2MusicAlbumMediaItem item = (EDSV2MusicAlbumMediaItem) this.items.get(position);
        if (item != null) {
            v.setTag(item);
            XLEUniformImageView tileView = (XLEUniformImageView) v.findViewById(R.id.artist_album_tile_image);
            CustomTypefaceTextView albumNameView = (CustomTypefaceTextView) v.findViewById(R.id.album_name);
            CustomTypefaceTextView artistNameView = (CustomTypefaceTextView) v.findViewById(R.id.artist_name);
            CustomTypefaceTextView artistReleaseData = (CustomTypefaceTextView) v.findViewById(R.id.artist_release_data);
            XLEImageViewFast smartglassIcon = (XLEImageViewFast) v.findViewById(R.id.smartglassicon);
            if (tileView != null) {
                tileView.setImageURI2(item.getImageUrl(), XLEUtil.getMediaItemDefaultRid(EDSV2MediaType.MEDIATYPE_ALBUM));
            }
            if (albumNameView != null) {
                albumNameView.setText(item.getTitle());
            }
            if (artistNameView != null) {
                artistNameView.setText(item.getArtistName());
            }
            if (artistReleaseData != null) {
                artistReleaseData.setText(item.getReleaseDate() != null ? new SimpleDateFormat("yyyy").format(item.getReleaseDate()) : "");
            }
            if (smartglassIcon != null) {
                smartglassIcon.setVisibility(item.getHasSmartGlassActivity() ? 0 : 8);
            }
        }
        return v;
    }
}
