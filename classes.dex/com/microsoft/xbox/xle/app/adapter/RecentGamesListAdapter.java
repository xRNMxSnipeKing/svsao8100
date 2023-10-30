package com.microsoft.xbox.xle.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.TextureManager;
import com.microsoft.xbox.xle.viewmodel.RecentGamesActivityViewModel;
import java.util.Date;
import java.util.Iterator;

public class RecentGamesListAdapter extends ArrayAdapter<GameInfo> {
    private static final Date MIN_DATE = new Date(100, 1, 1);
    private RecentGamesActivityViewModel gamesViewModel;

    public RecentGamesListAdapter(Activity activity, int rowViewResourceId, RecentGamesActivityViewModel gamesViewModel) {
        super(activity, rowViewResourceId, gamesViewModel.getGames());
        this.gamesViewModel = gamesViewModel;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        if (this.gamesViewModel.getGames() != null) {
            Iterator i$ = this.gamesViewModel.getGames().iterator();
            while (i$.hasNext()) {
                TextureManager.Instance().preload(((GameInfo) i$.next()).ImageUri);
            }
        }
        super.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleListItemViewHolder viewHolder;
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.simple_list_row, null);
            viewHolder = new SimpleListItemViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (SimpleListItemViewHolder) v.getTag();
        }
        GameInfo game = (GameInfo) getItem(position);
        if (game != null) {
            viewHolder.setKey(game);
            if (viewHolder.getTitleView() != null) {
                viewHolder.getTitleView().setText(game.Name);
            }
            if (viewHolder.getTileView() != null) {
                viewHolder.getTileView().setImageURI2(game.ImageUri);
            }
            if (viewHolder.getDescriptionView() != null && game.LastPlayed.after(MIN_DATE) && game.LastPlayed.before(new Date())) {
                viewHolder.getDescriptionView().setText(JavaUtil.getLocalizedDateString(game.LastPlayed));
            } else {
                viewHolder.getDescriptionView().setText(null);
            }
        }
        return v;
    }
}
