package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEGridView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.WhatsNewListAdapter;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xbox.xle.viewmodel.WhatsNewActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.WhatsNewActivityViewModel.WhatsNew;
import java.util.ArrayList;

public class WhatsNewContentGridModule extends ScreenModuleWithGrid {
    private XLEGridView gridView;
    private WhatsNewListAdapter listAdapter;
    private WhatsNewActivityViewModel viewModel;
    private ArrayList<WhatsNew> whatsNewList;

    public WhatsNewContentGridModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.whats_new_activity_content);
    }

    protected void onFinishInflate() {
        this.gridView = (XLEGridView) findViewById(R.id.whats_new_grid_view);
        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                WhatsNewContentGridModule.this.viewModel.navigateToWhatsNewDetails((WhatsNew) view.getTag());
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
            this.gridView.setAdapter(this.listAdapter);
            restorePosition();
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

    public XLEGridView getGridView() {
        return this.gridView;
    }
}
