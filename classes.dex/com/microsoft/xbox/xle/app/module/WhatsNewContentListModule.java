package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.WhatsNewListAdapter;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xbox.xle.viewmodel.WhatsNewActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.WhatsNewActivityViewModel.WhatsNew;
import java.util.ArrayList;

public class WhatsNewContentListModule extends ScreenModuleWithList {
    private WhatsNewListAdapter listAdapter;
    private XLEListView listView;
    private WhatsNewActivityViewModel viewModel;
    private ArrayList<WhatsNew> whatsNewList;

    public WhatsNewContentListModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.whats_new_activity_content);
    }

    protected void onFinishInflate() {
        this.listView = (XLEListView) findViewById(R.id.whats_new_list_view);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long arg3) {
                WhatsNewContentListModule.this.viewModel.navigateToWhatsNewDetails((WhatsNew) view.getTag());
            }
        });
    }

    public void updateView() {
        if (this.viewModel.getWhatsNewList() == null) {
            return;
        }
        if (this.listAdapter == null || this.whatsNewList != this.viewModel.getWhatsNewList()) {
            this.whatsNewList = this.viewModel.getWhatsNewList();
            this.listAdapter = new WhatsNewListAdapter(XLEApplication.getMainActivity().getBaseContext(), R.layout.whats_new_list_row, this.whatsNewList);
            this.listView.setAdapter(this.listAdapter);
            restoreListPosition();
            this.listView.onDataUpdated();
            return;
        }
        this.listAdapter.notifyDataSetChanged();
    }

    public void setViewModel(ViewModelBase vm) {
        this.viewModel = (WhatsNewActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }

    public XLEListView getListView() {
        return this.listView;
    }
}
