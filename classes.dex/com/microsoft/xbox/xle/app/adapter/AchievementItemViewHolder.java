package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;

public class AchievementItemViewHolder {
    private final TextView achievementTitleView;
    private final TextView acquiredView;
    private final TextView description;
    private final TextView earnedDate;
    private String key;
    private final TextView scoreView;
    private final XLEImageViewFast tileView;

    public AchievementItemViewHolder(View parentView) {
        this.achievementTitleView = (TextView) parentView.findViewById(R.id.achievements_listItem_title);
        this.scoreView = (TextView) parentView.findViewById(R.id.achievements_listItem_score);
        this.acquiredView = (TextView) parentView.findViewById(R.id.achievements_listItem_acquired);
        this.tileView = (XLEImageViewFast) parentView.findViewById(R.id.achievements_listItem_tile);
        this.description = (TextView) parentView.findViewById(R.id.achievements_listItem_description);
        this.earnedDate = (TextView) parentView.findViewById(R.id.achievements_listItem_date);
    }

    public TextView getAchievementTitleView() {
        return this.achievementTitleView;
    }

    public TextView getAcquiredView() {
        return this.acquiredView;
    }

    public TextView getScoreView() {
        return this.scoreView;
    }

    public XLEImageViewFast getTileView() {
        return this.tileView;
    }

    public TextView getDescription() {
        return this.description;
    }

    public TextView getCompleteDate() {
        return this.earnedDate;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
