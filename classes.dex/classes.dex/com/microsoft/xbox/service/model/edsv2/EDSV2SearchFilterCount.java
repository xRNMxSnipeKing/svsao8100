package com.microsoft.xbox.service.model.edsv2;

public class EDSV2SearchFilterCount {
    private EDSV2SearchFilterType filterType;
    private int resultCount;

    public EDSV2SearchFilterCount(EDSV2SearchFilterType filterType, int resultCount) {
        this.filterType = filterType;
        this.resultCount = resultCount;
    }

    public EDSV2SearchFilterType getFilterType() {
        return this.filterType;
    }

    public void setFilterType(EDSV2SearchFilterType filterType) {
        this.filterType = filterType;
    }

    public int getResultCount() {
        return this.resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
