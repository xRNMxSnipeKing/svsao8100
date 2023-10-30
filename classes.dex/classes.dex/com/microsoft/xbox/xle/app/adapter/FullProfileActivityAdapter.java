package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.ui.BioView;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.FullProfileActivityViewModel;

public class FullProfileActivityAdapter extends AdapterBaseNormal {
    private BioView bioView;
    private TextView gamerTagView;
    private XLEImageViewFast gamerpic;
    private TextView gamerscoreView;
    private TextView memberLevelView;
    private FullProfileActivityViewModel profileViewModel;

    public FullProfileActivityAdapter(FullProfileActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.full_profile_activity_body);
        this.profileViewModel = viewModel;
        this.gamerTagView = (TextView) findViewById(R.id.full_profile_gamertag);
        this.gamerscoreView = (TextView) findViewById(R.id.full_profile_gamerscore);
        this.memberLevelView = (TextView) findViewById(R.id.full_profile_memberlevel);
        this.bioView = (BioView) findViewById(R.id.full_profile_bio_view);
        this.gamerpic = (XLEImageViewFast) findViewById(R.id.full_profile_gamerpic);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                FullProfileActivityAdapter.this.profileViewModel.load(true);
            }
        });
        setAppBarButtonClickListener(R.id.full_profile_edit, new OnClickListener() {
            public void onClick(View arg0) {
                FullProfileActivityAdapter.this.profileViewModel.navigateToEditProfile();
            }
        });
    }

    public void updateViewOverride() {
        boolean editEnabled;
        updateLoadingIndicator(this.profileViewModel.isBusy());
        this.gamerTagView.setText(this.profileViewModel.getGamertag());
        this.gamerscoreView.setText(this.profileViewModel.getGamerscore());
        this.memberLevelView.setVisibility(this.profileViewModel.getIsGold() ? 0 : 4);
        this.bioView.setName(this.profileViewModel.getName());
        this.bioView.setMotto(this.profileViewModel.getMotto());
        this.bioView.setLocation(this.profileViewModel.getLocation());
        this.bioView.setBio(this.profileViewModel.getBio());
        this.gamerpic.setImageURI2(this.profileViewModel.getGamerPicUri());
        if (this.profileViewModel.getGamertag() == null || this.profileViewModel.getGamertag().length() <= 0) {
            editEnabled = false;
        } else {
            editEnabled = true;
        }
        setAppBarButtonEnabled(R.id.full_profile_edit, editEnabled);
    }
}
