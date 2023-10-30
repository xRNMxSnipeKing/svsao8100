package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.ui.BioView;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.YouBioActivityViewModel;

public class YouBioActivityAdapter extends AdapterBaseNormal {
    private BioView bioView;
    private YouBioActivityViewModel youViewModel;

    public YouBioActivityAdapter(YouBioActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.you_bio_activity_body);
        this.youViewModel = viewModel;
        this.bioView = (BioView) findViewById(R.id.you_bio_view);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                YouBioActivityAdapter.this.youViewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        if (this.youViewModel.getIsActive()) {
            updateLoadingIndicator(this.youViewModel.isBusy());
        }
        if (this.youViewModel.getGamertag() != null && this.youViewModel.getGamertag().length() > 0) {
            this.bioView.setName(this.youViewModel.getName());
            this.bioView.setMotto(this.youViewModel.getMotto());
            this.bioView.setLocation(this.youViewModel.getLocation());
            this.bioView.setBio(this.youViewModel.getBio());
        }
    }
}
