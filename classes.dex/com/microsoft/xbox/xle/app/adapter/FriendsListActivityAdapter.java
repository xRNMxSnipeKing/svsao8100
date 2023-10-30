package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.FriendsListActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public class FriendsListActivityAdapter extends AdapterBaseWithList {
    private SwitchPanel friendsSwitchPanel;
    private FriendsListActivityViewModel friendsViewModel;

    public FriendsListActivityAdapter(FriendsListActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.friends_list_activity_body);
        this.content = findViewById(R.id.friends_list_switch_panel);
        this.friendsViewModel = viewModel;
        this.friendsSwitchPanel = (SwitchPanel) this.content;
        findAndInitializeModuleById(R.id.friends_list_module, this.friendsViewModel);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_search, new OnClickListener() {
            public void onClick(View v) {
                FriendsListActivityAdapter.this.friendsViewModel.navigateToSearchGamer();
            }
        });
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View v) {
                FriendsListActivityAdapter.this.friendsViewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.friendsViewModel.isBusy());
        setAppBarButtonEnabled(R.id.appbar_search, !this.friendsViewModel.isBusy());
        this.friendsSwitchPanel.setState(this.friendsViewModel.getViewModelState().ordinal());
    }

    protected SwitchPanel getSwitchPanel() {
        return this.friendsSwitchPanel;
    }

    protected ViewModelBase getViewModel() {
        return this.friendsViewModel;
    }
}
