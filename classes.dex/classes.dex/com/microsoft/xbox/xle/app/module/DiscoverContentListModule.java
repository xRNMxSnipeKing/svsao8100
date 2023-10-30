package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2DiscoverData;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.ui.DiscoverGridItem2;
import com.microsoft.xbox.xle.viewmodel.DiscoverActivityViewModel2;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.List;

public class DiscoverContentListModule extends ScreenModuleLayout {
    private EDSV2DiscoverData discoverList;
    private ScrollView discoverScrollView;
    private SwitchPanel featuredSwitchPanel;
    private TableLayout featuredTable;
    private SwitchPanel picksForYouSwitchPanel;
    private TableLayout picksForYouTable;
    private DiscoverActivityViewModel2 viewModel;

    public DiscoverContentListModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.discover_activity2_content);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.featuredTable = (TableLayout) findViewById(R.id.discover_featured_table);
        this.picksForYouTable = (TableLayout) findViewById(R.id.discover_picks_for_you_table);
        this.featuredSwitchPanel = (SwitchPanel) findViewById(R.id.discover_featured_switch_panel);
        this.picksForYouSwitchPanel = (SwitchPanel) findViewById(R.id.discover_picks_for_you_switch_panel);
        this.discoverScrollView = (ScrollView) findViewById(R.id.discover_scroll_view);
        this.discoverList = null;
    }

    public void updateView() {
        if (this.viewModel.getDiscoverList() != null && this.discoverList != this.viewModel.getDiscoverList()) {
            this.discoverList = this.viewModel.getDiscoverList();
            if (this.discoverList != null) {
                buildDiscoverButtons(this.featuredSwitchPanel, this.featuredTable, this.discoverList.getBrowseItems());
                buildDiscoverButtons(this.picksForYouSwitchPanel, this.picksForYouTable, this.discoverList.getPicksForYou());
            }
            restoreScrollPosition();
        }
    }

    private void buildDiscoverButtons(SwitchPanel panel, TableLayout table, List<EDSV2MediaItem> items) {
        int i = 1;
        table.removeAllViews();
        table.setStretchAllColumns(true);
        if (items == null || items.size() <= 0) {
            panel.setState(1);
            return;
        }
        panel.setState(0);
        int size = items.size() / 2;
        if (items.size() % 2 == 0) {
            i = 0;
        }
        int numRows = size + i;
        for (int row = 0; row < numRows; row++) {
            TableRow currentTableRow = new TableRow(XLEApplication.Instance.getApplicationContext());
            for (int col = 0; col < 2; col++) {
                View button;
                int providerIndex = (row * 2) + col;
                if (providerIndex < items.size()) {
                    button = buildDiscoverButton((EDSV2MediaItem) items.get(providerIndex));
                } else {
                    button = new View(XLEApplication.Instance.getApplicationContext());
                }
                currentTableRow.addView(button, new LayoutParams(0, -2, 1.0f));
            }
            table.addView(currentTableRow, new TableLayout.LayoutParams(-1, -2));
        }
    }

    private View buildDiscoverButton(final EDSV2MediaItem item) {
        DiscoverGridItem2 rv = new DiscoverGridItem2(XLEApplication.Instance.getApplicationContext());
        rv.image.setImageURI2(item.getImageUrl(), XLEUtil.getMediaItemDefaultRid(item.getMediaType()));
        rv.textview.setText(item.getTitle());
        rv.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DiscoverContentListModule.this.viewModel.navigateToItemDetails(item);
            }
        });
        return rv;
    }

    public void setViewModel(ViewModelBase vm) {
        this.viewModel = (DiscoverActivityViewModel2) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }

    private void restoreScrollPosition() {
        if (this.viewModel != null && this.discoverScrollView != null) {
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    DiscoverContentListModule.this.discoverScrollView.scrollTo(0, DiscoverContentListModule.this.viewModel.getAndResetListOffset());
                }
            });
        }
    }

    private void saveScrollPosition() {
        if (this.viewModel != null && this.discoverScrollView != null) {
            this.viewModel.setListPosition(0, this.discoverScrollView.getScrollY());
        }
    }

    public void onStop() {
        saveScrollPosition();
    }
}
