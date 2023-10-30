package com.microsoft.xbox.xle.app.adapter;

import com.microsoft.xbox.xle.test.automator.Automator;
import com.microsoft.xbox.xle.viewmodel.AboutActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.AchievementsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ActivityGalleryActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ActivityOverviewActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ActivitySummaryActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xbox.xle.viewmodel.AlbumDetailsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.AppDetailsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ArtistDetailsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.CollectionGalleryActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.CompareAchievementsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.CompareGamesActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ComposeMessageActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.DetailsPivotActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.DiscoverActivityViewModel2;
import com.microsoft.xbox.xle.viewmodel.FriendsListActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.GameContentDetailActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.GameDetailInfoActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.GameRelatedActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.MessagesActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.MovieDetailsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.MovieRelatedActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.NowPlayingActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.RecentGamesActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.SearchActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.SearchFilterActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.SearchGameHistoryActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.SearchGamerActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.SearchResultsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.SettingsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.SystemCheckActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.TVEpisodeRelatedActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.TabletProfileActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.TvEpisodeDetailsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.TvSeasonDetailsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.TvSeasonRelatedActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.TvSeriesDetailsViewModel;
import com.microsoft.xbox.xle.viewmodel.TvSeriesRelatedActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.WhatsNewActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.XboxConsoleHelpViewModel;
import com.microsoft.xbox.xle.viewmodel.YouBioActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.YouProfileActivityViewModel;

public class AdapterFactory {
    private static AdapterFactory instance = new AdapterFactory();

    private AdapterFactory() {
    }

    public static AdapterFactory getInstance() {
        return instance;
    }

    public AdapterBase getDiscoverAdapter2(DiscoverActivityViewModel2 viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new DiscoverActivityAdapter2(viewModel);
    }

    public AdapterBase getSystemCheckAdapter(SystemCheckActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new SystemCheckActivityAdapter(viewModel);
    }

    public AdapterBase getAchievementsAdapter(AchievementsActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new AchievementsActivityAdapter(viewModel);
    }

    public AdapterBase getCompareGamesAdapter(CompareGamesActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new CompareGamesActivityAdapter(viewModel);
    }

    public AdapterBase getCompareAchievementsAdapter(CompareAchievementsActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new CompareAchievementsActivityAdapter(viewModel);
    }

    public AdapterBase getMessagesAdapter(MessagesActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new MessagesActivityAdapter(viewModel);
    }

    public AdapterBase getFriendsAdapter(FriendsListActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new FriendsListActivityAdapter(viewModel);
    }

    public AdapterBase getSearchGamerAdapter(SearchGamerActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new SearchGamerActivityAdapter(viewModel);
    }

    public AdapterBase getSearchGameHistoryAdapter(SearchGameHistoryActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new SearchGameHistoryActivityAdapter(viewModel);
    }

    public AdapterBase getYouProfileAdapter(YouProfileActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new YouProfileActivityAdapter(viewModel);
    }

    public AdapterBase getTabletProfileAdapter(TabletProfileActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return null;
        }
        return new TabletProfileActivityAdapter(viewModel);
    }

    public AdapterBase getRecentGamesAdapter(RecentGamesActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new RecentGamesActivityAdapter(viewModel);
    }

    public AdapterBase getYouBioAdapter(YouBioActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new YouBioActivityAdapter(viewModel);
    }

    public AdapterBase getComposeMessageAdapter(ComposeMessageActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new ComposeMessageActivityAdapter(viewModel);
    }

    public AdapterBase getNowPlayingActivityAdapter(NowPlayingActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new NowPlayingActivityAdapter(viewModel);
    }

    public AdapterBase getWhatsNewAdapter(WhatsNewActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new WhatsNewActivityAdapter(viewModel);
    }

    public AdapterBase getTvSeriesDetailsAdapter(TvSeriesDetailsViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new TvSeriesDetailsAdapter(viewModel);
    }

    public AdapterBase getTvSeasonDetailsAdapter(TvSeasonDetailsActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new TvSeasonDetailsActivityAdapter(viewModel);
    }

    public AdapterBase getTvEpisodeDetailsAdapter(TvEpisodeDetailsActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new TvEpisodeDetailsAdapter(viewModel);
    }

    public AdapterBase getMovieDetailsAdapter(MovieDetailsActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new MovieDetailsActivityAdapter(viewModel);
    }

    public AdapterBase getGameDetailInfoAdapter(GameDetailInfoActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new GameDetailInfoActivityAdapter(viewModel);
    }

    public AdapterBase getArtistDetailAdapter(ArtistDetailsActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new ArtistDetailsActivityAdapter(viewModel);
    }

    public AdapterBase getSearchFilterAdapter(SearchFilterActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new SearchFilterActivityAdapter(viewModel);
    }

    public AdapterBase getSettingsAdapter(SettingsActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new SettingsActivityAdapter(viewModel);
    }

    public AdapterBase getAlbumDetailAdapter(AlbumDetailsActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new AlbumDetailsActivityAdapter(viewModel);
    }

    public AdapterBase getAppDetailsAdapter(AppDetailsActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new AppDetailsActivityAdapter(viewModel);
    }

    public AdapterBase getSearchDataAdapter(SearchActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new SearchActivityAdapter(viewModel);
    }

    public AdapterBase getSearchDataResultAdapter(SearchResultsActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new SearchResultsActivityAdapter(viewModel);
    }

    public AdapterBase getActivitySummaryAdapter(ActivitySummaryActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new ActivitySummaryActivityAdapter(viewModel);
    }

    public AdapterBase getXboxConsoleHelpAdapter(XboxConsoleHelpViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new XboxConsoleHelpAdapter(viewModel);
    }

    public AdapterBase getActivityOverviewAdapter(ActivityOverviewActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new ActivityOverviewActivityAdapter(viewModel);
    }

    public AdapterBase getActivityGalleryAdapter(ActivityGalleryActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new ActivityGalleryActivityAdapter(viewModel);
    }

    public AdapterBase getAboutActivityAdapter(AboutActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new AboutActivityAdapter(viewModel);
    }

    public AdapterBase getCollectionAdapter(CollectionActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new CollectionActivityAdapter(viewModel);
    }

    public AdapterBase getCollectionGalleryAdapter(CollectionGalleryActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new CollectionGalleryActivityAdapter(viewModel);
    }

    public AdapterBase getGameContentActivityAdapter(GameContentDetailActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new GameContentDetailActivityAdapter(viewModel);
    }

    public AdapterBase getMovieRelatedAdapter(MovieRelatedActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new MovieRelatedActivityAdapter(viewModel);
    }

    public AdapterBase getTVEpisodeRelatedAdapter(TVEpisodeRelatedActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new TVEpisodeRelatedActivityAdapter(viewModel);
    }

    public AdapterBase getTvSeasonRelatedAdapter(TvSeasonRelatedActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new TVEpisodeRelatedActivityAdapter(viewModel);
    }

    public AdapterBase getGameRelatedAdapter(GameRelatedActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new GameRelatedActivityAdapter(viewModel);
    }

    public AdapterBase getTvSeriesRelatedAdapter(TvSeriesRelatedActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new TVEpisodeRelatedActivityAdapter(viewModel);
    }

    public AdapterBase getDetailsPivotActivityAdapter(DetailsPivotActivityViewModel viewModel) {
        if (Automator.getInstance().getAdapter(viewModel.getClass()) != null) {
            return Automator.getInstance().getAdapter(viewModel.getClass());
        }
        return new DetailsPivotActivityAdapter(viewModel);
    }
}
