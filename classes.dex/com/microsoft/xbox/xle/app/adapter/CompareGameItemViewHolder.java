package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;

public final class CompareGameItemViewHolder {
    private final XLEImageViewFast gameTileView;
    private final TextView gameTitleView;
    private Object key;
    private final TextView meGamerscoreView;
    private final TextView youGamerscoreView;

    public CompareGameItemViewHolder(View parentView) {
        this.gameTitleView = (TextView) parentView.findViewById(R.id.compare_games_listItem_title);
        this.gameTileView = (XLEImageViewFast) parentView.findViewById(R.id.compare_games_listItem_tile);
        this.meGamerscoreView = (TextView) parentView.findViewById(R.id.compare_games_listItem_score_me);
        this.youGamerscoreView = (TextView) parentView.findViewById(R.id.compare_games_listItem_score_you);
    }

    public TextView getGameTitleView() {
        return this.gameTitleView;
    }

    public XLEImageViewFast getGameTileView() {
        return this.gameTileView;
    }

    public TextView getMeGamerscoreView() {
        return this.meGamerscoreView;
    }

    public TextView getYouGamerscoreView() {
        return this.youGamerscoreView;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getKey() {
        return this.key;
    }
}
