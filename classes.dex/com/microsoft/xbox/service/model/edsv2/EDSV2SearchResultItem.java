package com.microsoft.xbox.service.model.edsv2;

public class EDSV2SearchResultItem extends EDSV2MediaItem {
    private String artistName;
    private float averageUserRating;
    private int userRatingCount;

    public float getAverageUserRating() {
        return this.averageUserRating;
    }

    public void setAverageUserRating(float averageUserRating) {
        this.averageUserRating = averageUserRating;
    }

    public int getUserRatingCount() {
        return this.userRatingCount;
    }

    public void setUserRatingCount(int userRatingCount) {
        this.userRatingCount = userRatingCount;
    }

    public String getArtistName() {
        return this.artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
