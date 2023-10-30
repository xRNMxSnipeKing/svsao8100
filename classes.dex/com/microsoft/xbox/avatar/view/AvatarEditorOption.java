package com.microsoft.xbox.avatar.view;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.view.AvatarAnimationCatalog.AvatarClosetSpinAnimationType;
import com.microsoft.xbox.toolkit.UrlUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.net.URI;

public abstract class AvatarEditorOption {
    protected static final int TILE_RESOLUTION = 128;
    private AvatarEditorOptionDisplayType displayType;
    private String filePath;
    protected int id;
    private URI uri;

    public enum AvatarEditorOptionDisplayType {
        COLOR,
        RESOURCE,
        URI,
        FILE,
        COUNT
    }

    public AvatarEditorOption(String url) {
        setDisplayTypeURL(url);
    }

    public AvatarEditorOption(int id, AvatarEditorOptionDisplayType type) {
        setDisplayTypeResource(id, type);
    }

    public final void setDisplayTypeURL(String url) {
        if (url.startsWith("http")) {
            this.displayType = AvatarEditorOptionDisplayType.URI;
            this.uri = UrlUtil.getEncodedUri(url);
            return;
        }
        this.filePath = url;
        this.displayType = AvatarEditorOptionDisplayType.FILE;
    }

    public final void setDisplayTypeResource(int id, AvatarEditorOptionDisplayType type) {
        this.displayType = type;
        this.id = id;
        boolean z = this.displayType == AvatarEditorOptionDisplayType.RESOURCE || this.displayType == AvatarEditorOptionDisplayType.COLOR;
        XLEAssert.assertTrue(z);
    }

    public int getOwnershipResourceId() {
        return R.drawable.empty;
    }

    public int getColorableStyleAssetResourceId() {
        return R.drawable.empty;
    }

    public int getSelectedResourceId() {
        if (isSelected()) {
            return R.drawable.editor_selected;
        }
        return R.drawable.empty;
    }

    public boolean isSelected() {
        return false;
    }

    public boolean isColorable() {
        return false;
    }

    public AvatarEditorOptionDisplayType getDisplayType() {
        return this.displayType;
    }

    public int getColor() {
        return this.id;
    }

    public int getResourceId() {
        XLEAssert.assertTrue(this.id >= 0);
        return this.id;
    }

    public URI getTileUri() {
        return this.uri;
    }

    public String getTilePath() {
        return this.filePath;
    }

    public String getButtonTitle() {
        return null;
    }

    public String getAssetTitle() {
        return null;
    }

    public AvatarClosetSpinAnimationType getClosetSpinAnimationType() {
        return AvatarClosetSpinAnimationType.None;
    }

    public boolean isEnabled() {
        return true;
    }
}
