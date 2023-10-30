package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.view.AvatarEditorOption;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;

public class AvatarEditorOptionViewHolder {
    private final XLEImageViewFast colorOverlayView;
    private final XLEImageViewFast contentView;
    private AvatarEditorOption option;
    private final XLEImageViewFast ownershipOverlayView;
    private final XLEImageViewFast selectedOverlayView;
    private final TextView title;

    public AvatarEditorOptionViewHolder(View v) {
        this.contentView = (XLEImageViewFast) v.findViewById(R.id.avatar_editor_select_grid_item_content);
        this.title = (TextView) v.findViewById(R.id.avatar_editor_select_grid_item_title);
        this.ownershipOverlayView = (XLEImageViewFast) v.findViewById(R.id.avatar_edit_select_grid_item_overlay_ownership);
        this.colorOverlayView = (XLEImageViewFast) v.findViewById(R.id.avatar_edit_select_grid_item_overlay_color);
        this.selectedOverlayView = (XLEImageViewFast) v.findViewById(R.id.avatar_edit_select_grid_item_overlay_selected);
    }

    public XLEImageViewFast getContentView() {
        return this.contentView;
    }

    public TextView getTitle() {
        return this.title;
    }

    public XLEImageViewFast getOwnershipOverlayView() {
        return this.ownershipOverlayView;
    }

    public XLEImageViewFast getColorOverlayView() {
        return this.colorOverlayView;
    }

    public XLEImageViewFast getSelectedOverlayView() {
        return this.selectedOverlayView;
    }

    public AvatarEditorOption getOption() {
        return this.option;
    }

    public void setOption(AvatarEditorOption option) {
        this.option = option;
    }
}
