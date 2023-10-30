package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.util.List;

public class CollectionTitleListAdapter extends ArrayAdapter<Title> {
    private static final int TITLE_TYPE_APP = 0;
    private static final int TITLE_TYPE_GAME = 1;
    private static final int TITLE_TYPE_MAX = 2;
    private static final String XBOX_MUSIC_TITLE_STRING = XLEApplication.Resources.getString(R.string.xbox_music_title);
    private static final String XBOX_VIDEO_TITLE_STRING = XLEApplication.Resources.getString(R.string.xbox_video_title);

    public CollectionTitleListAdapter(Activity activity, int rowViewResourceId, List<Title> list) {
        super(activity, rowViewResourceId, list);
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService("layout_inflater");
            if (getItemViewType(position) == 0) {
                v = vi.inflate(R.layout.app_item_list_row, null);
                v.setTag(new AppItemViewHolder(v));
            } else {
                v = vi.inflate(R.layout.games_list_row, null);
                v.setTag(new GameItemViewHolder(v));
            }
        }
        if (getItemViewType(position) == 0) {
            return getViewForApp(position, v);
        }
        return getViewForGame(position, v);
    }

    private View getViewForApp(int position, View v) {
        AppItemViewHolder viewHolder = (AppItemViewHolder) v.getTag();
        Title title = (Title) getItem(position);
        if (title != null) {
            viewHolder.setKey(title);
            if (viewHolder.getTitleView() != null) {
                viewHolder.getTitleView().setText(title.getName());
            }
            if (viewHolder.getTileView() != null) {
                if (title.getIsXboxVideo()) {
                    viewHolder.getTileView().setImageURI2(null, R.drawable.xbox_video_boxart);
                } else if (title.getIsXboxMusic()) {
                    viewHolder.getTileView().setImageURI2(null, R.drawable.xbox_music_boxart);
                } else {
                    viewHolder.getTileView().setImageURI2(title.getImageUrl(MeProfileModel.getModel().getLegalLocale()), XLEUtil.getMediaItemDefaultRid(61));
                }
            }
            if (viewHolder.getDescriptionView() != null) {
                viewHolder.getDescriptionView().setText(JavaUtil.getLocalizedDateStringValidated(title.getLastPlayed()));
            } else {
                viewHolder.getDescriptionView().setText(null);
            }
        }
        return v;
    }

    private View getViewForGame(int position, View v) {
        GameItemViewHolder viewHolder = (GameItemViewHolder) v.getTag();
        Title game = (Title) getItem(position);
        if (game != null) {
            viewHolder.setGame(game);
            if (viewHolder.getGameTitleView() != null) {
                viewHolder.getGameTitleView().setText(game.getName());
            }
            if (viewHolder.getTileView() != null) {
                viewHolder.getTileView().setImageURI2(game.getImageUrl(MeProfileModel.getModel().getLegalLocale()), XLEUtil.getMediaItemDefaultRid(1));
            }
            if (viewHolder.getDetailsView() != null) {
                viewHolder.getDetailsView().setVisibility(game.getTotalAchievements() > 0 ? 0 : 4);
            }
            if (viewHolder.getAchievementsView() != null) {
                viewHolder.getAchievementsView().setText(Integer.toString(game.getCurrentAchievements()));
            }
            if (viewHolder.getScoreView() != null) {
                viewHolder.getScoreView().setText(String.format("%d/%d", new Object[]{Integer.valueOf(game.getCurrentGamerScore()), Integer.valueOf(game.getTotalGamerScore())}));
            }
        }
        return v;
    }

    public int getItemViewType(int position) {
        if (((Title) getItem(position)).IsGame()) {
            return 1;
        }
        return 0;
    }

    public int getViewTypeCount() {
        return 2;
    }
}
