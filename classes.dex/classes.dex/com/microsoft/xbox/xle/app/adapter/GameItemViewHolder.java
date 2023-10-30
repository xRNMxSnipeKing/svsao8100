package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;

public final class GameItemViewHolder {
    private final TextView achievementsView;
    private final View detailsView;
    private Title game;
    private final TextView gameTitleView;
    private final TextView scoreView;
    private final XLEUniformImageView tileView;

    public GameItemViewHolder(View parentView) {
        this.gameTitleView = (TextView) parentView.findViewById(R.id.games_listItem_title);
        this.achievementsView = (TextView) parentView.findViewById(R.id.games_listItem_achievements);
        this.scoreView = (TextView) parentView.findViewById(R.id.games_listItem_score);
        this.tileView = (XLEUniformImageView) parentView.findViewById(R.id.games_listItem_tile);
        this.detailsView = parentView.findViewById(R.id.games_listItem_details);
    }

    public TextView getGameTitleView() {
        return this.gameTitleView;
    }

    public TextView getAchievementsView() {
        return this.achievementsView;
    }

    public TextView getScoreView() {
        return this.scoreView;
    }

    public XLEUniformImageView getTileView() {
        return this.tileView;
    }

    public View getDetailsView() {
        return this.detailsView;
    }

    public void setGame(Title game) {
        this.game = game;
    }

    public Title getGame() {
        return this.game;
    }
}
