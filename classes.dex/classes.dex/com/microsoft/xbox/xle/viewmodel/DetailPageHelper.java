package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.eds.DetailDisplayScreenType;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.xle.app.activity.AchievementsActivity;
import com.microsoft.xbox.xle.app.activity.ActivityGalleryActivity;
import com.microsoft.xbox.xle.app.activity.ActivityOverviewActivity;
import com.microsoft.xbox.xle.app.activity.ActivitySummaryActivity;
import com.microsoft.xbox.xle.app.activity.AlbumDetailActivity;
import com.microsoft.xbox.xle.app.activity.AppDetailsActivity;
import com.microsoft.xbox.xle.app.activity.ArtistDetailActivity;
import com.microsoft.xbox.xle.app.activity.GameContentDetailActivity;
import com.microsoft.xbox.xle.app.activity.GameDetailInfoActivity;
import com.microsoft.xbox.xle.app.activity.GameRelatedActivity;
import com.microsoft.xbox.xle.app.activity.MovieDetailsActivity;
import com.microsoft.xbox.xle.app.activity.MovieRelatedActivity;
import com.microsoft.xbox.xle.app.activity.TVEpisodeRelatedActivity;
import com.microsoft.xbox.xle.app.activity.TvEpisodeDetailsActivity;
import com.microsoft.xbox.xle.app.activity.TvSeasonDetailsActivity;
import com.microsoft.xbox.xle.app.activity.TvSeasonRelatedActivity;
import com.microsoft.xbox.xle.app.activity.TvSeriesDetailsActivity;
import com.microsoft.xbox.xle.app.activity.TvSeriesRelatedActivity;

public class DetailPageHelper {
    public static DetailPivotPaneData[] getDetailPivotPaneData(DetailDisplayScreenType screenType) {
        return getDetailPivotPaneData(screenType, false);
    }

    public static DetailPivotPaneData[] getDetailPivotPaneData(DetailDisplayScreenType screenType, boolean addActivityPaneByDefault) {
        switch (screenType) {
            case GameDetails:
                return new DetailPivotPaneData[]{new DetailPivotPaneData(GameDetailInfoActivity.class, true), new DetailPivotPaneData(ActivitySummaryActivity.class, addActivityPaneByDefault), new DetailPivotPaneData(AchievementsActivity.class, true), new DetailPivotPaneData(GameRelatedActivity.class, true)};
            case VideoDetails:
                return new DetailPivotPaneData[]{new DetailPivotPaneData(MovieDetailsActivity.class, true), new DetailPivotPaneData(ActivitySummaryActivity.class, true), new DetailPivotPaneData(MovieRelatedActivity.class, true)};
            case AlbumDetails:
                return new DetailPivotPaneData[]{new DetailPivotPaneData(AlbumDetailActivity.class, true), new DetailPivotPaneData(ActivitySummaryActivity.class, true)};
            case TvEpisodeDetails:
                return new DetailPivotPaneData[]{new DetailPivotPaneData(TvEpisodeDetailsActivity.class, true), new DetailPivotPaneData(ActivitySummaryActivity.class, true), new DetailPivotPaneData(TVEpisodeRelatedActivity.class, true)};
            case MusicArtistDetails:
                return new DetailPivotPaneData[]{new DetailPivotPaneData(ArtistDetailActivity.class, true)};
            case MusicVideoDetails:
                return new DetailPivotPaneData[]{new DetailPivotPaneData(MovieDetailsActivity.class, true)};
            case AppDetails:
                return new DetailPivotPaneData[]{new DetailPivotPaneData(AppDetailsActivity.class, true), new DetailPivotPaneData(ActivitySummaryActivity.class, addActivityPaneByDefault)};
            case TvSeriesDetails:
                return new DetailPivotPaneData[]{new DetailPivotPaneData(TvSeriesDetailsActivity.class, true), new DetailPivotPaneData(TvSeriesRelatedActivity.class, true)};
            case TvSeasonDetails:
                return new DetailPivotPaneData[]{new DetailPivotPaneData(TvSeasonDetailsActivity.class, true), new DetailPivotPaneData(TvSeasonRelatedActivity.class, true)};
            case ActivityDetails:
                return new DetailPivotPaneData[]{new DetailPivotPaneData(ActivityOverviewActivity.class, true), new DetailPivotPaneData(ActivityGalleryActivity.class, false)};
            case GameContentDetails:
                return new DetailPivotPaneData[]{new DetailPivotPaneData(GameContentDetailActivity.class, true)};
            default:
                return null;
        }
    }

    public static boolean isActivityDetailsPivotPaneData(DetailPivotPaneData[] pivotPaneData) {
        return pivotPaneData != null && pivotPaneData.length > 1 && pivotPaneData[0].getPivotPaneClass() == ActivityOverviewActivity.class;
    }

    public static DetailDisplayScreenType getDetailScreenTypeFromMediaType(int mediaType) {
        switch (mediaType) {
            case 1:
            case 5:
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMEDEMO /*19*/:
            case EDSV2MediaType.MEDIATYPE_XBOXORIGINALGAME /*21*/:
            case EDSV2MediaType.MEDIATYPE_XBOXARCADEGAME /*23*/:
            case EDSV2MediaType.MEDIATYPE_XBOXXNACOMMUNITYGAME /*37*/:
            case EDSV2MediaType.MEDIATYPE_WEBGAME /*57*/:
            case EDSV2MediaType.MEDIATYPE_MOBILEGAME /*58*/:
                return DetailDisplayScreenType.GameDetails;
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMECONTENT /*18*/:
            case 20:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMERTILE /*22*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMECONSUMABLE /*24*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMEVIDEO /*30*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMETRAILER /*34*/:
                return DetailDisplayScreenType.GameContentDetails;
            case EDSV2MediaType.MEDIATYPE_XBOXAPP /*61*/:
                return DetailDisplayScreenType.AppDetails;
            case EDSV2MediaType.MEDIATYPE_GAMELAYER /*65*/:
            case EDSV2MediaType.MEDIATYPE_GAMEACTIVITY /*66*/:
                return DetailDisplayScreenType.ActivityDetails;
            case EDSV2MediaType.MEDIATYPE_MOVIE /*1000*/:
                return DetailDisplayScreenType.VideoDetails;
            case EDSV2MediaType.MEDIATYPE_TVSHOW /*1002*/:
            case EDSV2MediaType.MEDIATYPE_TVEPISODE /*1003*/:
                return DetailDisplayScreenType.TvEpisodeDetails;
            case EDSV2MediaType.MEDIATYPE_TVSERIES /*1004*/:
                return DetailDisplayScreenType.TvSeriesDetails;
            case EDSV2MediaType.MEDIATYPE_TVSEASON /*1005*/:
                return DetailDisplayScreenType.TvSeasonDetails;
            case EDSV2MediaType.MEDIATYPE_ALBUM /*1006*/:
                return DetailDisplayScreenType.AlbumDetails;
            case EDSV2MediaType.MEDIATYPE_MUSICVIDEO /*1008*/:
                return DetailDisplayScreenType.MusicVideoDetails;
            case EDSV2MediaType.MEDIATYPE_MUSICARTIST /*1009*/:
                return DetailDisplayScreenType.MusicArtistDetails;
            default:
                return DetailDisplayScreenType.Unknown;
        }
    }
}
