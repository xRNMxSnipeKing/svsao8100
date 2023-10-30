package com.microsoft.xbox.service.model.edsv2;

import java.util.ArrayList;
import java.util.Iterator;

public class EDSV2SearchResult {
    private String continuationToken;
    private ArrayList<EDSV2SearchFilterCount> filterResultCount;
    private String impressionGuid;
    private ArrayList<EDSV2SearchResultItem> items;

    public int getFilterTypeCount(EDSV2SearchFilterType type) {
        int resultTotal = 0;
        if (this.filterResultCount == null || this.filterResultCount.size() <= 0) {
            return 0;
        }
        Iterator i$;
        EDSV2SearchFilterCount data;
        if (type == EDSV2SearchFilterType.SEARCHFILTERTYPE_ALL) {
            i$ = this.filterResultCount.iterator();
            while (i$.hasNext()) {
                data = (EDSV2SearchFilterCount) i$.next();
                if (data.getFilterType() != EDSV2SearchFilterType.SEARCHFILTERTYPE_ALL) {
                    resultTotal += data.getResultCount();
                }
            }
            return resultTotal;
        }
        i$ = this.filterResultCount.iterator();
        while (i$.hasNext()) {
            data = (EDSV2SearchFilterCount) i$.next();
            if (data.getFilterType() == type) {
                return data.getResultCount();
            }
        }
        return 0;
    }

    public ArrayList<EDSV2SearchFilterCount> getFilterResultCount() {
        ArrayList<EDSV2SearchFilterCount> fixedfilterResultCount = new ArrayList();
        fixedfilterResultCount.add(new EDSV2SearchFilterCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_ALL, getFilterTypeCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_ALL)));
        fixedfilterResultCount.add(new EDSV2SearchFilterCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_MOVIE, getFilterTypeCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_MOVIE)));
        fixedfilterResultCount.add(new EDSV2SearchFilterCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_TV, getFilterTypeCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_TV)));
        fixedfilterResultCount.add(new EDSV2SearchFilterCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_XBOXGAME, getFilterTypeCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_XBOXGAME)));
        fixedfilterResultCount.add(new EDSV2SearchFilterCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_APP, getFilterTypeCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_APP)));
        fixedfilterResultCount.add(new EDSV2SearchFilterCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_MUSIC, getFilterTypeCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_MUSIC) + getFilterTypeCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_MUSICARTIST)));
        return fixedfilterResultCount;
    }

    public void setFilterResultCount(ArrayList<EDSV2SearchFilterCount> filterResultCount) {
        this.filterResultCount = filterResultCount;
    }

    public ArrayList<EDSV2SearchResultItem> getItems() {
        return this.items;
    }

    public void setItems(ArrayList<EDSV2SearchResultItem> items) {
        this.items = items;
    }

    public String getContinuationToken() {
        return this.continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    public String getImpressionGuid() {
        return this.impressionGuid;
    }

    public void setImpressionGuid(String impressionGuid) {
        this.impressionGuid = impressionGuid;
    }
}
