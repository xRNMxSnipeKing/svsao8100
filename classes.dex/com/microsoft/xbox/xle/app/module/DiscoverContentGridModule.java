package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2DiscoverData;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.toolkit.ui.AbstractGridLayout;
import com.microsoft.xbox.toolkit.ui.HeroGridLayout;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.DiscoverContentGridAdapter;
import com.microsoft.xbox.xle.viewmodel.DiscoverActivityViewModel2;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;

public class DiscoverContentGridModule extends ScreenModuleWithGridLayout {
    private EDSV2DiscoverData discoverData;
    private SwitchPanel discoverSwitchPanel;
    private int featuresNum;
    private DiscoverContentGridAdapter gridAdapter;
    private HeroGridLayout heroGridLayout;
    private int picksNum;
    private DiscoverActivityViewModel2 viewModel;

    public DiscoverContentGridModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.discover_activity2_content);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.heroGridLayout = (HeroGridLayout) findViewById(R.id.discover_gridlayout);
        this.discoverData = null;
        calculateDiscoverItemsNum();
        this.discoverSwitchPanel = (SwitchPanel) findViewById(R.id.discover_switch_panel2);
        this.gridAdapter = new DiscoverContentGridAdapter(XLEApplication.getMainActivity(), R.layout.discover_content_hero_grid_row, R.layout.discover_content_grid_row, 0, null, this.viewModel, this.featuresNum);
        this.heroGridLayout.setGridAdapter(this.gridAdapter);
    }

    protected SwitchPanel getSwitchPanel() {
        return this.discoverSwitchPanel;
    }

    protected AbstractGridLayout getGridLayout() {
        return this.heroGridLayout;
    }

    public void updateView() {
        this.discoverSwitchPanel.setState(this.viewModel.getViewModelState().ordinal());
        getGridLayout().getGridAdapter().setGridLayoutModelState(this.viewModel.getViewModelState().ordinal());
        if (this.viewModel.getDiscoverList() == null) {
            return;
        }
        if (this.discoverData != this.viewModel.getDiscoverList()) {
            this.discoverData = this.viewModel.getDiscoverList();
            this.gridAdapter.setDataObjectAndViewModel(createDiscoverItemList(this.discoverData), this.viewModel);
            return;
        }
        this.heroGridLayout.notifyDataChanged();
    }

    public void setViewModel(ViewModelBase vm) {
        this.viewModel = (DiscoverActivityViewModel2) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }

    private void calculateDiscoverItemsNum() {
        if (this.heroGridLayout.getColumnCount() > 0 && this.heroGridLayout.getRowCount() > 0) {
            this.featuresNum = this.heroGridLayout.getRowCount() + 1;
            this.picksNum = (this.heroGridLayout.getColumnCount() - 2) * this.heroGridLayout.getRowCount();
        }
    }

    private ArrayList<EDSV2MediaItem> createDiscoverItemList(EDSV2DiscoverData discoverData) {
        ArrayList<EDSV2MediaItem> itemList = new ArrayList();
        if (discoverData != null) {
            int i;
            int browseItemSize = discoverData.getBrowseItems().size();
            int picksSize = discoverData.getPicksForYou().size();
            for (i = 0; i < this.featuresNum; i++) {
                if (browseItemSize >= this.featuresNum) {
                    itemList.add(discoverData.getBrowseItems().get(i));
                } else if (browseItemSize < this.featuresNum && browseItemSize > 0) {
                    itemList.add(null);
                }
            }
            for (i = 0; i < this.picksNum; i++) {
                if (picksSize >= this.picksNum) {
                    itemList.add(discoverData.getPicksForYou().get(i));
                } else {
                    itemList.add(null);
                }
            }
        }
        return itemList;
    }
}
