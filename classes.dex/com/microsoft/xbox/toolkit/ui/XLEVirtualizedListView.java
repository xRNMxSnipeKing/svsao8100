package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import com.microsoft.xbox.toolkit.XLEAsyncTask;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import java.util.ArrayList;
import java.util.List;

public class XLEVirtualizedListView extends XLEListView implements OnScrollListener {
    private static final int BLOCKS_RESIDENT_IN_RAM = 2;
    private static final int ELEMENTS_PER_BLOCK = 10;
    private int basemultiple;
    private View footer;
    private View header;
    private boolean isLoadingMore;
    private VirtualListLoader loadMoreListener;
    private ArrayList stageddata;

    public interface VirtualListLoader<T> {
        int getSize();

        List<T> load(int i, int i2);
    }

    public XLEVirtualizedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        init(context);
    }

    private void init(Context context) {
        this.isLoadingMore = false;
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService("layout_inflater");
        View headerLayout = vi.inflate(XboxApplication.Instance.getLayoutRValue("load_more_listview_footer"), null);
        this.header = headerLayout.findViewById(XboxApplication.Instance.getIdRValue("load_more_listview_footer"));
        this.header.setVisibility(8);
        addHeaderView(headerLayout, null, false);
        View footerLayout = vi.inflate(XboxApplication.Instance.getLayoutRValue("load_more_listview_footer"), null);
        this.footer = footerLayout.findViewById(XboxApplication.Instance.getIdRValue("load_more_listview_footer"));
        this.footer.setVisibility(8);
        addFooterView(footerLayout, null, false);
        setOnScrollListener(this);
        this.basemultiple = 0;
        this.stageddata = new ArrayList();
        this.stageddata.clear();
    }

    public void setLoadMoreListener(VirtualListLoader loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
        this.basemultiple = 0;
        this.stageddata.clear();
        this.stageddata.addAll(0, load(getMin(), getMax()));
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (!this.isLoadingMore && scrollState == 0) {
            int prefirst = visibleIndexToRealIndex(getFirstVisiblePosition() - 1);
            int postlast = visibleIndexToRealIndex(getLastVisiblePosition() - 1);
            if (ensureMinLoaded(prefirst)) {
                this.isLoadingMore = true;
                this.header.setVisibility(0);
                if (this.basemultiple - 1 >= 0) {
                    new XLEAsyncTask<List<Object>>(XLEThreadPool.networkOperationsThreadPool) {
                        protected void onPreExecute() {
                        }

                        protected List<Object> doInBackground() {
                            return XLEVirtualizedListView.this.load((XLEVirtualizedListView.this.basemultiple - 1) * 10, 10);
                        }

                        protected void onPostExecute(List<Object> result) {
                            int desiredFirstIndex = XLEVirtualizedListView.this.visibleIndexToRealIndex(XLEVirtualizedListView.this.getFirstVisiblePosition());
                            int max = Math.min(20, XLEVirtualizedListView.this.stageddata.size());
                            XLELog.Error("BUGBUGBUG", String.format("loadPrev array size %d", new Object[]{Integer.valueOf(XLEVirtualizedListView.this.stageddata.size())}));
                            XLELog.Error("BUGBUGBUG", String.format("loadPrev clear %d %d", new Object[]{Integer.valueOf(10), Integer.valueOf(max)}));
                            XLEVirtualizedListView.this.stageddata.subList(10, max).clear();
                            XLEVirtualizedListView.this.basemultiple = XLEVirtualizedListView.this.basemultiple - 1;
                            XLEVirtualizedListView.this.basemultiple = Math.max(0, XLEVirtualizedListView.this.basemultiple);
                            XLELog.Error("BUGBUGBUG", String.format("loadPrev, now %d", new Object[]{Integer.valueOf(XLEVirtualizedListView.this.basemultiple)}));
                            XLEVirtualizedListView.this.stageddata.addAll(0, result);
                            XLEVirtualizedListView.this.onLoadPrevDone();
                            XLEVirtualizedListView.this.onDataUpdated();
                            XLEVirtualizedListView.this.setSelection(XLEVirtualizedListView.this.realIndexToVisibleIndex(desiredFirstIndex));
                        }
                    }.execute();
                }
            }
            if (ensureMaxLoaded(postlast)) {
                this.isLoadingMore = true;
                this.footer.setVisibility(0);
                if (this.basemultiple + 1 <= getSize() / 10) {
                    new XLEAsyncTask<List<Object>>(XLEThreadPool.networkOperationsThreadPool) {
                        protected void onPreExecute() {
                        }

                        protected List<Object> doInBackground() {
                            return XLEVirtualizedListView.this.load((XLEVirtualizedListView.this.basemultiple + 2) * 10, 10);
                        }

                        protected void onPostExecute(List<Object> result) {
                            int desiredFirstIndex = XLEVirtualizedListView.this.visibleIndexToRealIndex(XLEVirtualizedListView.this.getFirstVisiblePosition());
                            int desiredYOffset = (int) (XLEVirtualizedListView.this.getChildAt(0).getY() - XLEVirtualizedListView.this.getY());
                            XLEVirtualizedListView.this.stageddata.subList(0, Math.min(10, XLEVirtualizedListView.this.getSize())).clear();
                            XLEVirtualizedListView.this.basemultiple = XLEVirtualizedListView.this.basemultiple + 1;
                            XLEVirtualizedListView.this.stageddata.addAll(XLEVirtualizedListView.this.stageddata.size(), result);
                            XLEVirtualizedListView.this.onLoadNextDone();
                            XLEVirtualizedListView.this.onDataUpdated();
                            XLEVirtualizedListView.this.setSelectionFromTop(XLEVirtualizedListView.this.realIndexToVisibleIndex(desiredFirstIndex), desiredYOffset);
                        }
                    }.execute();
                }
            }
        }
    }

    public void onLoadPrevDone() {
        this.isLoadingMore = false;
        this.header.setVisibility(8);
    }

    public void onLoadNextDone() {
        this.isLoadingMore = false;
        this.footer.setVisibility(8);
    }

    public <T> List<T> getStagedData() {
        return this.stageddata;
    }

    public int visibleIndexToRealIndex(int visibleIndex) {
        return getMin() + visibleIndex;
    }

    public int realIndexToVisibleIndex(int realIndex) {
        return realIndex - getMin();
    }

    public List load(int base, int count) {
        if (this.loadMoreListener == null) {
            return new ArrayList();
        }
        return this.loadMoreListener.load(base, Math.min(getSize(), base + count));
    }

    public int getSize() {
        if (this.loadMoreListener == null) {
            return 0;
        }
        return this.loadMoreListener.getSize();
    }

    private boolean inRange(int pos) {
        return pos >= 0 && pos < getSize();
    }

    public int getMin() {
        return this.basemultiple * 10;
    }

    public int getMax() {
        return (this.basemultiple + 2) * 10;
    }

    public boolean ensureMinLoaded(int prefirst) {
        return prefirst < getMin() && inRange(prefirst);
    }

    public boolean ensureMaxLoaded(int postlast) {
        return postlast >= getMax() && inRange(postlast);
    }
}
