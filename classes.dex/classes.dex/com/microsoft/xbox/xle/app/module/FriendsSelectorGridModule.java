package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.ui.XLEGridView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.FriendsSelectorListAdapter;
import com.microsoft.xbox.xle.viewmodel.FriendSelectorItem;
import com.microsoft.xbox.xle.viewmodel.FriendsSelectorActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;

public class FriendsSelectorGridModule extends ScreenModuleWithGrid {
    private ArrayList<FriendSelectorItem> friendsList;
    private FriendsSelectorListAdapter friendsSelectorListAdapter;
    private FriendsSelectorActivityViewModel friendsViewModel;
    private XLEGridView gridView;

    public FriendsSelectorGridModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.friends_picker_content_module);
    }

    protected void onFinishInflate() {
        this.gridView = (XLEGridView) findViewById(R.id.friends_picker_grid);
        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (view.getTag() == null || !(view.getTag() instanceof FriendSelectorItem)) {
                    XLELog.Error("FriendSelectorGridModule", "view.getTag type is unexpected");
                    return;
                }
                FriendSelectorItem friend = (FriendSelectorItem) view.getTag();
                if (friend != null) {
                    FriendsSelectorGridModule.this.friendsViewModel.toggleSelection(friend);
                }
            }
        });
    }

    public void updateView() {
        if (this.friendsViewModel.getFriends() == null) {
            return;
        }
        if (this.friendsList != this.friendsViewModel.getFriends()) {
            this.friendsList = this.friendsViewModel.getFriends();
            this.friendsSelectorListAdapter = new FriendsSelectorListAdapter(XLEApplication.getMainActivity(), R.layout.friends_selector_row, this.friendsViewModel.getFriends());
            this.gridView.setAdapter(this.friendsSelectorListAdapter);
            restorePosition();
            return;
        }
        this.friendsSelectorListAdapter.notifyDataSetChanged();
    }

    public void setViewModel(ViewModelBase vm) {
        this.friendsViewModel = (FriendsSelectorActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.friendsViewModel;
    }

    public XLEGridView getGridView() {
        return this.gridView;
    }
}
