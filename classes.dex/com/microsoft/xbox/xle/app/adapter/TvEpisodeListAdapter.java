package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVEpisodeMediaItem;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.app.XLEApplication;
import java.util.List;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class TvEpisodeListAdapter extends ArrayAdapter<EDSV2TVEpisodeMediaItem> {
    private List<EDSV2TVEpisodeMediaItem> items;

    public TvEpisodeListAdapter(Activity activity, int rowViewResourceId, List<EDSV2TVEpisodeMediaItem> items) {
        super(activity, rowViewResourceId, items);
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.tv_season_details_list_row, null);
        }
        EDSV2TVEpisodeMediaItem item = (EDSV2TVEpisodeMediaItem) this.items.get(position);
        if (item != null) {
            v.setTag(item);
            TextView tvEpisodeTitleTextView = (TextView) v.findViewById(R.id.tv_season_listItem_title);
            TextView tvEpisodeIndexAndDateTextView = (TextView) v.findViewById(R.id.tv_season_list_episode_number_and_release_date);
            TextView tvEpisodeIndexTextView = (TextView) v.findViewById(R.id.tv_season_list_episode_number);
            TextView tvEpisodeReleaseDateTextView = (TextView) v.findViewById(R.id.tv_season_list_episode_release_date);
            XLEImageViewFast smartglassIcon = (XLEImageViewFast) v.findViewById(R.id.smartglassicon);
            if (tvEpisodeTitleTextView != null) {
                tvEpisodeTitleTextView.setText(item.getTitle());
            }
            if (tvEpisodeIndexAndDateTextView != null) {
                tvEpisodeIndexAndDateTextView.setText(XLEApplication.MainActivity.getString(R.string.tv_season_details_episode) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + item.getEpisodeNumber() + ", " + JavaUtil.getDateStringAsMonthDateYear(item.getReleaseDate()));
            }
            if (tvEpisodeReleaseDateTextView != null) {
                tvEpisodeReleaseDateTextView.setText(JavaUtil.getLocalizedDateStringValidated(item.getReleaseDate()));
            }
            if (tvEpisodeIndexTextView != null) {
                tvEpisodeIndexTextView.setText(String.valueOf(item.getEpisodeNumber()));
            }
            if (smartglassIcon != null) {
                smartglassIcon.setVisibility(item.getHasSmartGlassActivity() ? 0 : 8);
            }
        }
        return v;
    }
}
