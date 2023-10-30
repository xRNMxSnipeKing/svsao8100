package com.microsoft.xbox.service.model.eds;

import java.net.URI;
import java.util.Date;

public abstract class EDSSearchResponseBase {
    public abstract float getAverageUserRating();

    public abstract String getDuration();

    public abstract JFilters getFilter();

    public abstract String getIdentifier();

    public abstract URI getImageUrl();

    public abstract String getName();

    public abstract String getProductionCompany();

    public abstract Date getReleaseDate();

    public abstract int getUserRatingCount();

    public abstract boolean isSmartGlassEnabled();
}
