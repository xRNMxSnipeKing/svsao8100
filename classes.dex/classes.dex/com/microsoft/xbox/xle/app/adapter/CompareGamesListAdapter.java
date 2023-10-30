package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.CompareGameInfo;
import com.microsoft.xbox.toolkit.ui.TextureManager;
import com.microsoft.xbox.xle.viewmodel.CompareGamesActivityViewModel;

public class CompareGamesListAdapter extends ArrayAdapter<CompareGameInfo> {
    protected CompareGamesActivityViewModel compareGamesViewModel;
    private int isLoadingIndex = -1;

    public CompareGamesListAdapter(Activity activity, int rowViewResourceId, CompareGamesActivityViewModel compareGamesViewModel) {
        super(activity, rowViewResourceId, compareGamesViewModel.getGames());
        this.compareGamesViewModel = compareGamesViewModel;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        if (this.compareGamesViewModel.getGames() != null) {
            for (int i = 0; i < this.compareGamesViewModel.getGames().size(); i++) {
                CompareGameInfo game = (CompareGameInfo) this.compareGamesViewModel.getGames().get(i);
                if (game.getGameTileUri() != null) {
                    TextureManager.Instance().preload(game.getGameTileUri());
                }
                if (game.getTitleId() == 0) {
                    this.isLoadingIndex = i;
                }
            }
        }
        super.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CompareGameItemViewHolder viewHolder;
        View v = convertView;
        LayoutInflater layoutInflater = null;
        if (v == null || v.getTag() == null) {
            layoutInflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
            v = layoutInflater.inflate(R.layout.compare_games_list_row, null);
            viewHolder = new CompareGameItemViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (CompareGameItemViewHolder) v.getTag();
        }
        CompareGameInfo game = (CompareGameInfo) getItem(position);
        if (game == null) {
            return v;
        }
        if (game.getTitleId() > 0) {
            viewHolder.setKey(game.getGameInfo());
            if (viewHolder.getGameTitleView() != null) {
                viewHolder.getGameTitleView().setText(game.getGameName());
            }
            if (viewHolder.getGameTileView() != null) {
                viewHolder.getGameTileView().setImageURI2(game.getGameTileUri());
            }
            if (viewHolder.getMeGamerscoreView() != null) {
                viewHolder.getMeGamerscoreView().setText(game.getMeGamerscore());
            }
            if (viewHolder.getYouGamerscoreView() == null) {
                return v;
            }
            viewHolder.getYouGamerscoreView().setText(game.getYouGamerscore());
            return v;
        }
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
        }
        return layoutInflater.inflate(R.layout.games_loading, null);
    }

    public boolean isEnabled(int position) {
        return position != this.isLoadingIndex;
    }
}
