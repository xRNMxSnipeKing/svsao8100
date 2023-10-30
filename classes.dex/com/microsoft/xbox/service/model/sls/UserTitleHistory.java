package com.microsoft.xbox.service.model.sls;

import java.util.List;

public class UserTitleHistory {
    public UserTitlePaginationInfo paginationInfo;
    public List<Title> titles;
    public String version;

    public List<Title> getTitles() {
        return this.titles;
    }
}
