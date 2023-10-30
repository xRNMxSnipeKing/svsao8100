package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.eds.JFilters;
import org.codehaus.jackson.annotate.JsonProperty;

public class SearchFilterCountData {
    private JFilters filterType;
    private int resultCount;

    public SearchFilterCountData(JFilters filterType, int resultCount) {
        this.filterType = filterType;
        this.resultCount = resultCount;
    }

    public JFilters getFilter() {
        return this.filterType;
    }

    @JsonProperty("filterType")
    public void setFilter(JFilters filterType) {
        this.filterType = filterType;
    }

    public int getCount() {
        return this.resultCount;
    }

    @JsonProperty("resultCount")
    public void setCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
