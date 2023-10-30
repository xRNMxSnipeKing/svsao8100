package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.AchievementItem;
import com.microsoft.xbox.toolkit.ui.TextureManager;
import com.microsoft.xbox.xle.viewmodel.AchievementsActivityViewModel;
import java.util.Iterator;

public class AchievementsListAdapter extends ArrayAdapter<AchievementItem> {
    private AchievementsActivityViewModel viewModel;

    public AchievementsListAdapter(Context context, int rowViewResourceId, AchievementsActivityViewModel viewModel) {
        super(context, rowViewResourceId, viewModel.getAchievements());
        this.viewModel = viewModel;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        if (this.viewModel.getAchievements() != null) {
            Iterator i$ = this.viewModel.getAchievements().iterator();
            while (i$.hasNext()) {
                TextureManager.Instance().preload(((AchievementItem) i$.next()).getTileUri());
            }
        }
        super.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AchievementItemViewHolder viewHolder;
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.achievements_list_row, null);
            viewHolder = new AchievementItemViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (AchievementItemViewHolder) v.getTag();
        }
        AchievementItem achievement = (AchievementItem) getItem(position);
        if (achievement != null) {
            viewHolder.setKey(achievement.getKey());
            if (viewHolder.getAchievementTitleView() != null) {
                viewHolder.getAchievementTitleView().setText(achievement.getName());
            }
            if (viewHolder.getScoreView() != null) {
                viewHolder.getScoreView().setText(achievement.getGamerscore());
            }
            if (viewHolder.getAcquiredView() != null) {
                viewHolder.getAcquiredView().setText(achievement.getAcquired());
            }
            if (viewHolder.getTileView() != null) {
                viewHolder.getTileView().setImageURI2(achievement.getTileUri());
            }
            if (viewHolder.getCompleteDate() != null) {
                viewHolder.getCompleteDate().setText(achievement.getEarnedDateTime());
            }
            if (viewHolder.getDescription() != null) {
                viewHolder.getDescription().setText(achievement.getDescription());
            }
        }
        return v;
    }
}
