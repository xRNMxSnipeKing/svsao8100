package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.ArtistDetailsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public class ArtistDetailsActivityAdapter extends AdapterBaseWithList {
    private XLEUniformImageView artistTile;
    private CustomTypefaceTextView artistTittle;
    private SwitchPanel switchPanel;
    private ArtistDetailsActivityViewModel viewModel;

    public ArtistDetailsActivityAdapter(ArtistDetailsActivityViewModel viewModel) {
        this.viewModel = viewModel;
        this.screenBody = findViewById(R.id.artistdetails_activity_body);
        this.content = findViewById(R.id.artistdetail_switch_panel);
        this.switchPanel = (SwitchPanel) this.content;
        this.artistTittle = (CustomTypefaceTextView) findViewById(R.id.artist_detail_title);
        this.artistTile = (XLEUniformImageView) findViewById(R.id.artist_album_artist_tile_image);
        findAndInitializeModuleById(R.id.artist_detail_list_module, this.viewModel);
    }

    public void updateViewOverride() {
        this.switchPanel.setState(this.viewModel.getViewModelState().ordinal());
        XLEUtil.updateTextIfNotNull(this.artistTittle, JavaUtil.stringToUpper(this.viewModel.getTitle()));
        if (this.artistTile != null) {
            this.artistTile.setImageURI2(this.viewModel.getImageUrl(), XLEUtil.getMediaItemDefaultRid(EDSV2MediaType.MEDIATYPE_MUSICARTIST));
        }
        updateLoadingIndicator(this.viewModel.isBusy());
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                ArtistDetailsActivityAdapter.this.viewModel.load(true);
            }
        });
    }

    protected SwitchPanel getSwitchPanel() {
        return this.switchPanel;
    }

    protected ViewModelBase getViewModel() {
        return this.viewModel;
    }
}
