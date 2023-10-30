package com.microsoft.xbox.xle.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEImageView;
import com.microsoft.xbox.xle.ui.StarRatingWithUserCountView;
import java.util.Date;
import java.util.Locale;

public class XLEUtil {
    private static final long DAY_IN_MILLISECONDS = 86400000;
    public static final int DEFAULT_RESOURCE_MAX_COUNT = 6;
    private static final long HOUR_IN_MILLISECONDS = 3600000;
    private static final Date MIN_DATE = new Date(100, 1, 1);
    private static final long MIN_IN_MILLISECONDS = 60000;

    public static int getMediaItemDefaultRid(int mediaType) {
        switch (mediaType) {
            case 1:
            case 5:
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMECONTENT /*18*/:
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMEDEMO /*19*/:
            case 20:
            case EDSV2MediaType.MEDIATYPE_XBOXORIGINALGAME /*21*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMERTILE /*22*/:
            case EDSV2MediaType.MEDIATYPE_XBOXARCADEGAME /*23*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMECONSUMABLE /*24*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMEVIDEO /*30*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMETRAILER /*34*/:
            case EDSV2MediaType.MEDIATYPE_XBOXBUNDLE /*36*/:
            case EDSV2MediaType.MEDIATYPE_XBOXXNACOMMUNITYGAME /*37*/:
            case EDSV2MediaType.MEDIATYPE_XBOXMARKETPLACE /*46*/:
            case EDSV2MediaType.MEDIATYPE_AVATARITEM /*47*/:
            case EDSV2MediaType.MEDIATYPE_WEBGAME /*57*/:
            case EDSV2MediaType.MEDIATYPE_MOBILEGAME /*58*/:
            case EDSV2MediaType.MEDIATYPE_XBOXMOBILEPDLC /*59*/:
            case EDSV2MediaType.MEDIATYPE_XBOXMOBILECONSUMABLE /*60*/:
            case EDSV2MediaType.MEDIATYPE_METROGAME /*62*/:
            case EDSV2MediaType.MEDIATYPE_METROGAMECONTENT /*63*/:
            case 64:
            case EDSV2MediaType.MEDIATYPE_GAMELAYER /*65*/:
            case EDSV2MediaType.MEDIATYPE_GAMEACTIVITY /*66*/:
                return R.drawable.game_missing;
            case EDSV2MediaType.MEDIATYPE_XBOXAPP /*61*/:
                return R.drawable.app_missing;
            case EDSV2MediaType.MEDIATYPE_MOVIE /*1000*/:
                return R.drawable.movie_missing;
            case EDSV2MediaType.MEDIATYPE_TVSHOW /*1002*/:
            case EDSV2MediaType.MEDIATYPE_TVEPISODE /*1003*/:
            case EDSV2MediaType.MEDIATYPE_TVSERIES /*1004*/:
            case EDSV2MediaType.MEDIATYPE_TVSEASON /*1005*/:
                return R.drawable.tv_missing;
            case EDSV2MediaType.MEDIATYPE_ALBUM /*1006*/:
            case EDSV2MediaType.MEDIATYPE_TRACK /*1007*/:
            case EDSV2MediaType.MEDIATYPE_MUSICVIDEO /*1008*/:
            case EDSV2MediaType.MEDIATYPE_MUSICARTIST /*1009*/:
                return R.drawable.music_missing;
            default:
                return R.drawable.unknown_missing;
        }
    }

    public static void setMediaItemViewValue(XLEImageView typeImage, int mediaType, StarRatingWithUserCountView starView, float averageUserRating, int userRatingCount, CustomTypefaceTextView artistView, String artistName) {
        artistView.setText("");
        starView.setVisibility(8);
        switch (mediaType) {
            case 1:
            case 5:
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMECONTENT /*18*/:
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMEDEMO /*19*/:
            case 20:
            case EDSV2MediaType.MEDIATYPE_XBOXORIGINALGAME /*21*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMERTILE /*22*/:
            case EDSV2MediaType.MEDIATYPE_XBOXARCADEGAME /*23*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMECONSUMABLE /*24*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMEVIDEO /*30*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMETRAILER /*34*/:
            case EDSV2MediaType.MEDIATYPE_XBOXBUNDLE /*36*/:
            case EDSV2MediaType.MEDIATYPE_XBOXXNACOMMUNITYGAME /*37*/:
            case EDSV2MediaType.MEDIATYPE_AVATARITEM /*47*/:
            case EDSV2MediaType.MEDIATYPE_WEBGAME /*57*/:
            case EDSV2MediaType.MEDIATYPE_MOBILEGAME /*58*/:
            case EDSV2MediaType.MEDIATYPE_XBOXMOBILEPDLC /*59*/:
            case EDSV2MediaType.MEDIATYPE_XBOXMOBILECONSUMABLE /*60*/:
            case EDSV2MediaType.MEDIATYPE_METROGAME /*62*/:
            case EDSV2MediaType.MEDIATYPE_METROGAMECONTENT /*63*/:
            case 64:
                typeImage.setImageResource(R.drawable.xboxgame);
                starView.setAverageUserRatingAndUserCount(averageUserRating, (long) userRatingCount);
                starView.setVisibility(0);
                break;
            case EDSV2MediaType.MEDIATYPE_XBOXAPP /*61*/:
                typeImage.setImageResource(R.drawable.xboxapp);
                break;
            case EDSV2MediaType.MEDIATYPE_MOVIE /*1000*/:
                typeImage.setImageResource(R.drawable.movie);
                break;
            case EDSV2MediaType.MEDIATYPE_TVSHOW /*1002*/:
            case EDSV2MediaType.MEDIATYPE_TVEPISODE /*1003*/:
            case EDSV2MediaType.MEDIATYPE_TVSERIES /*1004*/:
            case EDSV2MediaType.MEDIATYPE_TVSEASON /*1005*/:
                typeImage.setImageResource(R.drawable.tv);
                break;
            case EDSV2MediaType.MEDIATYPE_ALBUM /*1006*/:
            case EDSV2MediaType.MEDIATYPE_MUSICARTIST /*1009*/:
                typeImage.setImageResource(R.drawable.musictrack);
                artistView.setText(artistName);
                artistView.setVisibility(0);
                break;
            case EDSV2MediaType.MEDIATYPE_TRACK /*1007*/:
            case EDSV2MediaType.MEDIATYPE_MUSICVIDEO /*1008*/:
                typeImage.setImageResource(R.drawable.musicvideo);
                break;
            default:
                typeImage.setImageResource(0);
                break;
        }
        if (JavaUtil.isNullOrEmpty(artistView.getText().toString())) {
            artistView.setVisibility(8);
        }
    }

    public static String getStringForLocale(int resId, String locale) {
        Configuration conf = XLEApplication.MainActivity.getResources().getConfiguration();
        Locale backupLocale = conf.locale;
        conf.locale = new Locale(locale);
        DisplayMetrics metrics = new DisplayMetrics();
        XLEApplication.MainActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        String localeString = new Resources(XLEApplication.MainActivity.getAssets(), metrics, conf).getString(resId);
        conf.locale = backupLocale;
        XLEApplication.MainActivity.getResources().updateConfiguration(conf, metrics);
        return localeString;
    }

    public static String getMediaItemDefaultTypeName(int mediaType) {
        switch (mediaType) {
            case 1:
            case 5:
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMECONTENT /*18*/:
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMEDEMO /*19*/:
            case 20:
            case EDSV2MediaType.MEDIATYPE_XBOXORIGINALGAME /*21*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMERTILE /*22*/:
            case EDSV2MediaType.MEDIATYPE_XBOXARCADEGAME /*23*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMECONSUMABLE /*24*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMEVIDEO /*30*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMETRAILER /*34*/:
            case EDSV2MediaType.MEDIATYPE_XBOXBUNDLE /*36*/:
            case EDSV2MediaType.MEDIATYPE_XBOXXNACOMMUNITYGAME /*37*/:
            case EDSV2MediaType.MEDIATYPE_XBOXMARKETPLACE /*46*/:
            case EDSV2MediaType.MEDIATYPE_AVATARITEM /*47*/:
            case EDSV2MediaType.MEDIATYPE_WEBGAME /*57*/:
            case EDSV2MediaType.MEDIATYPE_MOBILEGAME /*58*/:
            case EDSV2MediaType.MEDIATYPE_XBOXMOBILEPDLC /*59*/:
            case EDSV2MediaType.MEDIATYPE_XBOXMOBILECONSUMABLE /*60*/:
            case EDSV2MediaType.MEDIATYPE_METROGAME /*62*/:
            case EDSV2MediaType.MEDIATYPE_METROGAMECONTENT /*63*/:
            case 64:
            case EDSV2MediaType.MEDIATYPE_GAMELAYER /*65*/:
            case EDSV2MediaType.MEDIATYPE_GAMEACTIVITY /*66*/:
                return XLEApplication.Resources.getString(R.string.discover_type_game);
            case EDSV2MediaType.MEDIATYPE_XBOXAPP /*61*/:
                return XLEApplication.Resources.getString(R.string.discover_type_app);
            case EDSV2MediaType.MEDIATYPE_MOVIE /*1000*/:
                return XLEApplication.Resources.getString(R.string.discover_type_movie);
            case EDSV2MediaType.MEDIATYPE_TVSHOW /*1002*/:
            case EDSV2MediaType.MEDIATYPE_TVEPISODE /*1003*/:
            case EDSV2MediaType.MEDIATYPE_TVSERIES /*1004*/:
            case EDSV2MediaType.MEDIATYPE_TVSEASON /*1005*/:
                return XLEApplication.Resources.getString(R.string.discover_type_tv);
            case EDSV2MediaType.MEDIATYPE_ALBUM /*1006*/:
            case EDSV2MediaType.MEDIATYPE_TRACK /*1007*/:
            case EDSV2MediaType.MEDIATYPE_MUSICVIDEO /*1008*/:
            case EDSV2MediaType.MEDIATYPE_MUSICARTIST /*1009*/:
                return XLEApplication.Resources.getString(R.string.discover_type_music);
            default:
                return null;
        }
    }

    public static String getNowPlayingRelatedMediaItemTypeName(int mediaType) {
        switch (mediaType) {
            case 1:
            case 5:
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMECONTENT /*18*/:
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMEDEMO /*19*/:
            case 20:
            case EDSV2MediaType.MEDIATYPE_XBOXORIGINALGAME /*21*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMERTILE /*22*/:
            case EDSV2MediaType.MEDIATYPE_XBOXARCADEGAME /*23*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMECONSUMABLE /*24*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMEVIDEO /*30*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMETRAILER /*34*/:
            case EDSV2MediaType.MEDIATYPE_XBOXBUNDLE /*36*/:
            case EDSV2MediaType.MEDIATYPE_XBOXXNACOMMUNITYGAME /*37*/:
            case EDSV2MediaType.MEDIATYPE_XBOXMARKETPLACE /*46*/:
            case EDSV2MediaType.MEDIATYPE_AVATARITEM /*47*/:
            case EDSV2MediaType.MEDIATYPE_WEBGAME /*57*/:
            case EDSV2MediaType.MEDIATYPE_MOBILEGAME /*58*/:
            case EDSV2MediaType.MEDIATYPE_XBOXMOBILEPDLC /*59*/:
            case EDSV2MediaType.MEDIATYPE_XBOXMOBILECONSUMABLE /*60*/:
            case EDSV2MediaType.MEDIATYPE_METROGAME /*62*/:
            case EDSV2MediaType.MEDIATYPE_METROGAMECONTENT /*63*/:
            case 64:
            case EDSV2MediaType.MEDIATYPE_GAMELAYER /*65*/:
            case EDSV2MediaType.MEDIATYPE_GAMEACTIVITY /*66*/:
                return XLEApplication.Resources.getString(R.string.now_playing_related_game);
            case EDSV2MediaType.MEDIATYPE_MOVIE /*1000*/:
                return XLEApplication.Resources.getString(R.string.now_playing_related_movie);
            case EDSV2MediaType.MEDIATYPE_TVSHOW /*1002*/:
            case EDSV2MediaType.MEDIATYPE_TVEPISODE /*1003*/:
            case EDSV2MediaType.MEDIATYPE_TVSERIES /*1004*/:
            case EDSV2MediaType.MEDIATYPE_TVSEASON /*1005*/:
                return XLEApplication.Resources.getString(R.string.now_playing_related_tv_series);
            case EDSV2MediaType.MEDIATYPE_ALBUM /*1006*/:
            case EDSV2MediaType.MEDIATYPE_TRACK /*1007*/:
            case EDSV2MediaType.MEDIATYPE_MUSICVIDEO /*1008*/:
            case EDSV2MediaType.MEDIATYPE_MUSICARTIST /*1009*/:
                return XLEApplication.Resources.getString(R.string.now_playing_related_album);
            default:
                return "";
        }
    }

    public static String dateToDurationSinceNow(Date date) {
        long timeDiff = new Date().getTime() - date.getTime();
        if (timeDiff < 0) {
            return "";
        }
        if (timeDiff < HOUR_IN_MILLISECONDS) {
            if (timeDiff / MIN_IN_MILLISECONDS <= 1) {
                return XboxApplication.Resources.getString(R.string.friend_status_minute_ago);
            }
            return String.format(XboxApplication.Resources.getString(R.string.friend_status_minutes_ago), new Object[]{Long.valueOf(minDiff)});
        } else if (timeDiff >= DAY_IN_MILLISECONDS) {
            return JavaUtil.getLocalizedDateString(date);
        } else {
            if (timeDiff / HOUR_IN_MILLISECONDS <= 1) {
                return XboxApplication.Resources.getString(R.string.friend_status_hour_ago);
            }
            return String.format(XboxApplication.Resources.getString(R.string.friend_status_hours_ago), new Object[]{Long.valueOf(hourDiff)});
        }
    }

    public static String dateToDurationSinceNowValidate(Date date) {
        if (!date.after(MIN_DATE) || date.compareTo(new Date()) > 0) {
            return null;
        }
        return dateToDurationSinceNow(date);
    }

    public static void updateTextIfNotNull(TextView textView, String text) {
        if (textView != null) {
            textView.setText(text);
        }
    }

    public static void updateVisibilityIfNotNull(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    public static void updateTextViewFromParentView(View parentView, int textViewID, String text) {
        updateTextIfNotNull((TextView) parentView.findViewById(textViewID), text);
    }

    public static void setKeepScreenOn(boolean keepScreenOn) {
        if (keepScreenOn) {
            XLEApplication.getMainActivity().getWindow().addFlags(128);
        } else {
            XLEApplication.getMainActivity().getWindow().clearFlags(128);
        }
    }

    public static void gotoXboxOneUpSell() {
        try {
            XboxApplication.MainActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.microsoft.xboxone.smartglass")));
        } catch (ActivityNotFoundException e) {
            XLELog.Error("XLEUtil", "failed to launch Google Play for Xbox One upsell.");
        }
    }
}
