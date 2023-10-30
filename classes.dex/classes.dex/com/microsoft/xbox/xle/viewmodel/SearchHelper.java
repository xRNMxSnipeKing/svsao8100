package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchFilterType;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.XLEApplication;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class SearchHelper {
    public static boolean checkValidSearchTag(String searchTag) {
        if (XboxApplication.Resources == null || JavaUtil.isNullOrEmpty(searchTag) || searchTag.length() > XboxApplication.Resources.getInteger(R.integer.max_search_term_length) || JavaUtil.urlEncode(searchTag) == null) {
            return false;
        }
        return true;
    }

    public static String formatSearchFilterCountString(EDSV2SearchFilterType filter, int count, boolean isSubHeader) {
        String result = getSearchFilterString(filter);
        String countWithExceedSign = count > SearchResultsActivityViewModel.MAX_SEARCH_RESULT_ITEMS ? "500+" : String.valueOf(count);
        return String.format(result + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + XLEApplication.Resources.getString(R.string.search_data_result_filter_count), new Object[]{countWithExceedSign});
    }

    public static String getSearchFilterString(EDSV2SearchFilterType filter) {
        switch (filter) {
            case SEARCHFILTERTYPE_MUSIC:
                return XLEApplication.Resources.getString(R.string.search_filter_title_music);
            case SEARCHFILTERTYPE_APP:
                return XLEApplication.Resources.getString(R.string.search_filter_title_apps);
            case SEARCHFILTERTYPE_XBOXGAME:
                return XLEApplication.Resources.getString(R.string.search_filter_title_games);
            case SEARCHFILTERTYPE_TV:
                return XLEApplication.Resources.getString(R.string.search_filter_title_tv_shows);
            case SEARCHFILTERTYPE_MOVIE:
                return XLEApplication.Resources.getString(R.string.search_filter_title_movies);
            case SEARCHFILTERTYPE_ALL:
                return XLEApplication.Resources.getString(R.string.search_filter_title_all);
            default:
                XLEAssert.assertTrue("call this inappropriately", false);
                return null;
        }
    }
}
