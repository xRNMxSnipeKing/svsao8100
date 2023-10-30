package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.CompareAchievementInfo;
import com.microsoft.xbox.toolkit.ui.TextureManager;
import com.microsoft.xbox.xle.viewmodel.CompareAchievementsActivityViewModel;
import java.util.Iterator;

public class CompareAchievementsListAdapter extends ArrayAdapter<CompareAchievementInfo> {
    protected CompareAchievementsActivityViewModel compareAchievementsViewModel;

    public CompareAchievementsListAdapter(Activity activity, int rowViewResourceId, CompareAchievementsActivityViewModel compareAchievementsViewModel) {
        super(activity, rowViewResourceId, compareAchievementsViewModel.getAchievements());
        this.compareAchievementsViewModel = compareAchievementsViewModel;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        if (this.compareAchievementsViewModel.getAchievements() != null) {
            Iterator i$ = this.compareAchievementsViewModel.getAchievements().iterator();
            while (i$.hasNext()) {
                TextureManager.Instance().preload(((CompareAchievementInfo) i$.next()).getAchievementTileUri());
            }
        }
        super.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CompareGameItemViewHolder viewHolder;
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.compare_games_list_row, null);
            viewHolder = new CompareGameItemViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (CompareGameItemViewHolder) v.getTag();
        }
        CompareAchievementInfo achievement = (CompareAchievementInfo) getItem(position);
        if (achievement != null) {
            viewHolder.setKey(achievement.getKey());
            if (viewHolder.getGameTitleView() != null) {
                viewHolder.getGameTitleView().setText(achievement.getAchievementName());
            }
            if (viewHolder.getGameTileView() != null) {
                viewHolder.getGameTileView().setImageURI2(achievement.getAchievementTileUri());
            }
            if (viewHolder.getMeGamerscoreView() != null) {
                viewHolder.getMeGamerscoreView().setText(achievement.getMeGamerscore());
            }
            if (viewHolder.getYouGamerscoreView() != null) {
                viewHolder.getYouGamerscoreView().setText(achievement.getYouGamerscore());
            }
        }
        return v;
    }
}
