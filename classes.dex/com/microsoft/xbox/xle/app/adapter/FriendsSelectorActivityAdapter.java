package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.FriendsSelectorActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public class FriendsSelectorActivityAdapter extends AdapterBaseWithList {
    private SwitchPanel friendsSwitchPanel;
    private FriendsSelectorActivityViewModel friendsViewModel;

    public FriendsSelectorActivityAdapter(FriendsSelectorActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.friends_picker_activity_body);
        this.friendsViewModel = viewModel;
        this.friendsSwitchPanel = (SwitchPanel) findViewById(R.id.friends_picker_switch_panel);
        findAndInitializeModuleById(R.id.friends_selector_list_module, this.friendsViewModel);
        findAndInitializeModuleById(R.id.friends_selector_grid_module, this.friendsViewModel);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.friends_picker_confirm, new OnClickListener() {
            public void onClick(View v) {
                FriendsSelectorActivityAdapter.this.friendsViewModel.confirm();
            }
        });
        setAppBarButtonClickListener(R.id.friends_picker_cancel, new OnClickListener() {
            public void onClick(View v) {
                FriendsSelectorActivityAdapter.this.friendsViewModel.cancel();
            }
        });
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View v) {
                FriendsSelectorActivityAdapter.this.friendsViewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.friendsViewModel.isBusy());
        this.friendsSwitchPanel.setState(this.friendsViewModel.getViewModelState().ordinal());
    }

    protected SwitchPanel getSwitchPanel() {
        return this.friendsSwitchPanel;
    }

    protected ViewModelBase getViewModel() {
        return this.friendsViewModel;
    }
}
