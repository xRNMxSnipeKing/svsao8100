package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.CompareAchievementInfo;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.CompareAchievementsActivityViewModel;

public class CompareAchievementsTabletListAdapter extends CompareAchievementsListAdapter {
    public CompareAchievementsTabletListAdapter(Activity activity, int rowViewResourceId, CompareAchievementsActivityViewModel compareAchievementsViewModel) {
        super(activity, rowViewResourceId, compareAchievementsViewModel);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.compare_achievements_list_row, null);
        }
        CompareAchievementInfo achievement = (CompareAchievementInfo) getItem(position);
        if (achievement != null) {
            v.setTag(achievement);
            XLEUtil.updateTextViewFromParentView(v, R.id.compare_achievements_listItem_title, achievement.getAchievementName());
            XLEImageViewFast gameTileView = (XLEImageViewFast) v.findViewById(R.id.compare_achievements_listItem_tile);
            if (gameTileView != null) {
                gameTileView.setImageURI2(achievement.getAchievementTileUri());
            }
            XLEUtil.updateTextViewFromParentView(v, R.id.compare_achievements_listItem_score_me, achievement.getMeGamerscore());
            XLEUtil.updateTextViewFromParentView(v, R.id.compare_achievements_listItem_score_you, achievement.getYouGamerscore());
            XLEUtil.updateTextViewFromParentView(v, R.id.compare_achievements_listItem_date_you, achievement.getYouGamerEarnedDateTime());
            XLEUtil.updateTextViewFromParentView(v, R.id.compare_achievements_listItem_date_me, achievement.getMeGamerEarnedDateTime());
            XLEUtil.updateTextViewFromParentView(v, R.id.compare_achievements_listItem_description, achievement.getMeGamerDescription());
        }
        return v;
    }
}
