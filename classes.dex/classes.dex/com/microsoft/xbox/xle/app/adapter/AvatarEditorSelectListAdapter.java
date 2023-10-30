package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.view.AvatarEditorOption;
import com.microsoft.xbox.toolkit.ui.TextureManager;
import com.microsoft.xbox.xle.viewmodel.AvatarEditorSelectActivityViewModel;

public class AvatarEditorSelectListAdapter extends ArrayAdapter<AvatarEditorOption> {
    private AvatarEditorSelectActivityViewModel viewModel;

    public AvatarEditorSelectListAdapter(Context context, int rowViewResourceId, AvatarEditorSelectActivityViewModel viewModel) {
        super(context, rowViewResourceId, viewModel.getOptions());
        this.viewModel = viewModel;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        if (this.viewModel.getOptions() != null) {
            for (AvatarEditorOption option : this.viewModel.getOptions()) {
                switch (option.getDisplayType()) {
                    case URI:
                        TextureManager.Instance().preload(option.getTileUri());
                        break;
                    case FILE:
                        TextureManager.Instance().preloadFromFile(option.getTilePath());
                        break;
                    case RESOURCE:
                        TextureManager.Instance().preload(option.getResourceId());
                        break;
                    default:
                        break;
                }
            }
        }
        TextureManager.Instance().preload((int) R.drawable.editor_icon_awarded);
        TextureManager.Instance().preload((int) R.drawable.editor_icon_purchased);
        TextureManager.Instance().preload((int) R.drawable.editor_colors);
        TextureManager.Instance().preload((int) R.drawable.editor_selected);
        super.notifyDataSetChanged();
    }

    public boolean isEnabled(int position) {
        return ((AvatarEditorOption) getItem(position)).isEnabled();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AvatarEditorOptionViewHolder viewHolder;
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.avatar_editor_select_grid_item, null);
            viewHolder = new AvatarEditorOptionViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (AvatarEditorOptionViewHolder) v.getTag();
        }
        AvatarEditorOption option = (AvatarEditorOption) getItem(position);
        viewHolder.setOption(option);
        if (option != null) {
            if (viewHolder.getContentView() != null) {
                viewHolder.getContentView().setBackgroundColor(0);
                switch (option.getDisplayType()) {
                    case URI:
                        viewHolder.getContentView().setImageURI2(option.getTileUri());
                        break;
                    case FILE:
                        viewHolder.getContentView().setImageFilePath(option.getTilePath());
                        break;
                    case RESOURCE:
                        viewHolder.getContentView().setImageResource(option.getResourceId());
                        break;
                    case COLOR:
                        viewHolder.getContentView().setImageBitmap(null);
                        viewHolder.getContentView().setBackgroundColor(option.getColor());
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }
            if (viewHolder.getTitle() != null) {
                if (option.getButtonTitle() != null) {
                    viewHolder.getTitle().setText(option.getButtonTitle());
                    viewHolder.getTitle().setVisibility(0);
                } else {
                    viewHolder.getTitle().setVisibility(8);
                }
            }
            if (viewHolder.getOwnershipOverlayView() != null) {
                viewHolder.getOwnershipOverlayView().setImageResource(option.getOwnershipResourceId());
            }
            if (viewHolder.getColorOverlayView() != null) {
                viewHolder.getColorOverlayView().setImageResource(option.getColorableStyleAssetResourceId());
            }
            if (viewHolder.getSelectedOverlayView() != null) {
                viewHolder.getSelectedOverlayView().setImageResource(option.getSelectedResourceId());
            }
        }
        if (v != null) {
            v.setFocusable(false);
        }
        return v;
    }
}
