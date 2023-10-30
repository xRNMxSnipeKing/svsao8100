package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVSeasonMediaItem;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.text.SimpleDateFormat;
import java.util.List;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class TvSeasonListAdapter extends ArrayAdapter<EDSV2TVSeasonMediaItem> {
    private List<EDSV2TVSeasonMediaItem> items;

    public TvSeasonListAdapter(Activity activity, int rowViewResourceId, List<EDSV2TVSeasonMediaItem> items) {
        super(activity, rowViewResourceId, items);
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.tv_series_details_select_list_item, null);
        }
        EDSV2TVSeasonMediaItem item = (EDSV2TVSeasonMediaItem) this.items.get(position);
        if (item != null) {
            v.setTag(item);
            XLEUniformImageView tileView = (XLEUniformImageView) v.findViewById(R.id.season_list_item_tile);
            TextView title = (TextView) v.findViewById(R.id.season_list_item_content);
            TextView date = (TextView) v.findViewById(R.id.season_select_list_item_date);
            XLEImageViewFast smartglassIcon = (XLEImageViewFast) v.findViewById(R.id.smartglassicon);
            if (tileView != null) {
                tileView.setImageURI2(item.getImageUrl(), XLEUtil.getMediaItemDefaultRid(item.getMediaType()));
            }
            if (title != null) {
                title.setText(getContext().getString(R.string.tv_series_details_season) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + item.getSeasonNumber());
            }
            if (date != null) {
                date.setText(item.getReleaseDate() == null ? "" : new SimpleDateFormat("yyyy").format(item.getReleaseDate()));
            }
            if (smartglassIcon != null) {
                smartglassIcon.setVisibility(item.getHasSmartGlassActivity() ? 0 : 8);
            }
        }
        return v;
    }
}
