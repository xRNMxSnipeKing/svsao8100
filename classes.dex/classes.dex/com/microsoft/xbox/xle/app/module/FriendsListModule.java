package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.FriendsListAdapter;
import com.microsoft.xbox.xle.viewmodel.FriendItem;
import com.microsoft.xbox.xle.viewmodel.IFriendsListViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;

public class FriendsListModule extends ScreenModuleLayout {
    private ArrayList<FriendItem> friendsList;
    private FriendsListAdapter friendsListAdapter;
    private IFriendsListViewModel friendsViewModel;
    private XLEListView listView;

    public FriendsListModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.friends_list_module);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.listView = (XLEListView) findViewById(R.id.friends_list);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (view.getTag() == null || !(view.getTag() instanceof FriendItem)) {
                    XLELog.Warning("FriendsListModule", "Friends list item tag is not a FriendItem.");
                } else {
                    FriendsListModule.this.friendsViewModel.navigateToYouProfile((FriendItem) view.getTag());
                }
            }
        });
    }

    public void updateView() {
        if (this.friendsViewModel.getFriends() != null) {
            this.listView.setVisibility(0);
            if (this.friendsList != this.friendsViewModel.getFriends()) {
                this.friendsList = this.friendsViewModel.getFriends();
                this.friendsListAdapter = new FriendsListAdapter(XLEApplication.getMainActivity(), R.layout.friends_list_row, this.friendsViewModel.getFriends());
                this.listView.setAdapter(this.friendsListAdapter);
                restoreListPosition();
                this.listView.onDataUpdated();
                return;
            }
            this.listView.notifyDataSetChanged();
            return;
        }
        this.listView.setVisibility(8);
    }

    public void setViewModel(ViewModelBase vm) {
        this.friendsViewModel = (IFriendsListViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return (ViewModelBase) this.friendsViewModel;
    }

    void restoreListPosition() {
        ViewModelBase vm = getViewModel();
        if (vm != null && this.listView != null) {
            this.listView.setSelectionFromTop(vm.getAndResetListPosition(), vm.getAndResetListOffset());
        }
    }
}
