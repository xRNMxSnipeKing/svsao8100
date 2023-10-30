package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.service.model.SearchTermData;
import com.microsoft.xbox.service.model.edsv2.EDSV2DiscoverData;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchResult;
import com.microsoft.xbox.toolkit.XLEException;
import java.util.ArrayList;
import java.util.List;

public interface IEDSServiceManager {
    List<SearchTermData> GetPopularSearchTerms() throws XLEException;

    <T extends EDSV2MediaItem> ArrayList<T> browseMediaItemList(String str, int i, int i2, String str2) throws XLEException;

    String getCombinedContentRating() throws XLEException;

    ArrayList<String> getGenreList(String str) throws XLEException;

    <T extends EDSV2MediaItem> T getMediaItemDetail(String str, String str2, long j, int i, String str3) throws XLEException;

    EDSV2DiscoverData getProgrammingItems2() throws XLEException;

    <T extends EDSV2MediaItem> ArrayList<T> getRelated(String str, int i, int i2) throws XLEException;

    <T extends EDSV2MediaItem> ArrayList<T> getSmartDJ(String str) throws XLEException;

    void initializeServiceManager();

    EDSV2SearchResult searchMediaItems(String str, int i, String str2, int i2) throws XLEException;
}
