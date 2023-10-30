package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.CompareGameInfo;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.CompareGamesActivityViewModel;

public class CompareGamesHorizontalListAdapter extends CompareGamesListAdapter {
    public CompareGamesHorizontalListAdapter(Activity activity, int rowViewResourceId, CompareGamesActivityViewModel compareGamesViewModel) {
        super(activity, rowViewResourceId, compareGamesViewModel);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater layoutInflater = null;
        if (v == null || v.getTag() == null) {
            layoutInflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
            v = layoutInflater.inflate(R.layout.compare_games_list_row, null);
        }
        v.setTag(null);
        View view;
        if (position == 0) {
            view = v.findViewById(R.id.compare_games_list_item_layout);
            if (view != null) {
                view.setVisibility(8);
            }
            view = v.findViewById(R.id.compare_games_header_layout);
            if (view == null) {
                return v;
            }
            view.setVisibility(0);
            TextView youGamerName = (TextView) view.findViewById(R.id.you_gamer_me);
            XLEImageViewFast meGamerPicHeader = (XLEImageViewFast) view.findViewById(R.id.me_gamer_gamerpic_header);
            TextView meGamerTotalAchievementsHeader = (TextView) view.findViewById(R.id.me_gamer_total_achievements_header);
            XLEImageViewFast youGamerPicHeader = (XLEImageViewFast) view.findViewById(R.id.you_gamer_gamerpic_header);
            TextView youGamerMeHeader = (TextView) view.findViewById(R.id.you_gamer_me_header);
            TextView youGamerTotalAchievementsHeader = (TextView) view.findViewById(R.id.you_gamer_total_achievements_header);
            TextView youGamerscoreHeader = (TextView) view.findViewById(R.id.you_gamer_gamerscore_header);
            ((TextView) view.findViewById(R.id.me_gamer_gamerscore_header)).setText(this.compareGamesViewModel.getMeGamerScore());
            youGamerscoreHeader.setText(this.compareGamesViewModel.getYouGamerScore());
            meGamerPicHeader.setImageURI2(this.compareGamesViewModel.getMeGamerpicUri());
            youGamerPicHeader.setImageURI2(this.compareGamesViewModel.getYouGamerpicUri());
            youGamerName.setText(this.compareGamesViewModel.getYouGamerTag());
            youGamerMeHeader.setText(this.compareGamesViewModel.getYouGamerTag());
            meGamerTotalAchievementsHeader.setText(this.compareGamesViewModel.getMeTotalGamesPlayed());
            youGamerTotalAchievementsHeader.setText(this.compareGamesViewModel.getYouTotalGamesPlayed());
            return v;
        }
        view = v.findViewById(R.id.compare_games_header_layout);
        if (view != null) {
            view.setVisibility(8);
        }
        view = v.findViewById(R.id.compare_games_list_item_layout);
        if (view == null) {
            return v;
        }
        view.setVisibility(0);
        CompareGameInfo game = getItem(position);
        if (game == null) {
            return v;
        }
        if (game.getTitleId() > 0) {
            v.setTag(game);
            XLEUniformImageView gameTileView = (XLEUniformImageView) view.findViewById(R.id.compare_games_listItem_tile_tablet);
            if (gameTileView != null) {
                gameTileView.setImageURI2(Title.getImageUrl(MeProfileModel.getModel().getLegalLocale(), game.getTitleId()), XLEUtil.getMediaItemDefaultRid(1));
            }
            TextView meGamerAchievementsPercent = (TextView) view.findViewById(R.id.me_gamer_achievements_percent);
            if (meGamerAchievementsPercent != null) {
                meGamerAchievementsPercent.setText(game.getMeAchievementsEarnedPercent());
            }
            TextView meGamerTotalAchievements = (TextView) view.findViewById(R.id.me_gamer_total_achievements);
            if (meGamerTotalAchievements != null) {
                meGamerTotalAchievements.setText(game.getMeAchievementsEarnedWithTotal());
            }
            TextView youGamerAchievementsPercent = (TextView) view.findViewById(R.id.you_gamer_achievements_percent);
            if (youGamerAchievementsPercent != null) {
                youGamerAchievementsPercent.setText(game.getYouAchievementsEarnedPercent());
            }
            TextView youGamerTotalAchievements = (TextView) view.findViewById(R.id.you_gamer_total_achievements);
            if (youGamerTotalAchievements != null) {
                youGamerTotalAchievements.setText(game.getYouAchievementsEarnedWithTotal());
            }
            TextView meGamerscoreView = (TextView) view.findViewById(R.id.compare_games_listItem_score_me);
            if (meGamerscoreView != null) {
                meGamerscoreView.setText(game.getMeGamerscoreWithTotal());
            }
            TextView youGamerscoreView = (TextView) view.findViewById(R.id.compare_games_listItem_score_you);
            if (youGamerscoreView == null) {
                return v;
            }
            youGamerscoreView.setText(game.getYouGamerscoreWithTotal());
            return v;
        }
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
        }
        return layoutInflater.inflate(R.layout.games_loading, null);
    }

    public int getCount() {
        return super.getCount() + 1;
    }

    public CompareGameInfo getItem(int position) {
        if (position == 0) {
            return null;
        }
        return (CompareGameInfo) super.getItem(position - 1);
    }

    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
