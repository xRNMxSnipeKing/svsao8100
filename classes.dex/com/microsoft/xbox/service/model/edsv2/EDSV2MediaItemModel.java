package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.ModelBase;
import com.microsoft.xbox.toolkit.FixedSizeHashtable;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEConstants;
import java.net.URI;
import java.util.Enumeration;
import java.util.Hashtable;

public abstract class EDSV2MediaItemModel<T> extends ModelBase<T> {
    private static final int MAX_DETAIL_MODELS = 10;
    private static FixedSizeHashtable<String, EDSV2MediaItemModel> identifierToModelCache = new FixedSizeHashtable(10);
    private static Hashtable<String, String> partnerMediaIdToIdentifierMap = new Hashtable();
    private static Hashtable<String, EDSV2NowPlayingDetailModel> tempPartnerMediaIdToNowPlayingModelMap = new Hashtable();
    private static Hashtable<Long, EDSV2MediaItemModel> tempTitleIdToModelMap = new Hashtable();
    private static Hashtable<Long, EDSV2NowPlayingDetailModel> tempTitleIdToNowPlayingModelMap = new Hashtable();
    private static Hashtable<Long, String> titleIdToIdentiferMap = new Hashtable();
    private static EDSV2MediaItemModel zuneDetailModel;
    private boolean shouldLoadFullDetail;

    public abstract URI getBackgroundImageUrl();

    public abstract String getCanonicalId();

    public abstract String getPartnerMediaId();

    public abstract String getTitle();

    public abstract long getTitleId();

    public abstract void load(boolean z);

    public boolean drainShouldLoadFullDetail() {
        boolean rv = this.shouldLoadFullDetail;
        this.shouldLoadFullDetail = false;
        return rv;
    }

    public static final <U extends EDSV2MediaItemModel> U getModel(EDSV2MediaItem mediaItem) {
        boolean z;
        boolean z2 = false;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        String str = "At least one parameter must be valid.";
        if (!(JavaUtil.isNullOrEmpty(mediaItem.getCanonicalId()) && JavaUtil.isNullOrEmpty(mediaItem.getPartnerMediaId()) && mediaItem.getTitleId() <= 0)) {
            z2 = true;
        }
        XLEAssert.assertTrue(str, z2);
        EDSV2MediaItemModel model = findModelInCache(mediaItem);
        if (model != null) {
            return model;
        }
        model = createModel(mediaItem);
        addModelToCache(model);
        return model;
    }

    public static EDSV2NowPlayingDetailModel getNowPlayingModel(long titleId, String partnerMediaId) {
        boolean z;
        boolean z2 = false;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        String str = "At least one parameter must be valid.";
        if (!JavaUtil.isNullOrEmpty(partnerMediaId) || titleId > 0) {
            z2 = true;
        }
        XLEAssert.assertTrue(str, z2);
        EDSV2MediaItem mediaItem = new EDSV2MediaItem();
        mediaItem.setTitleId(titleId);
        mediaItem.setPartnerMediaId(partnerMediaId);
        EDSV2NowPlayingDetailModel nowPlayingModel;
        if (!JavaUtil.isNullOrEmpty(partnerMediaId)) {
            nowPlayingModel = (EDSV2NowPlayingDetailModel) tempPartnerMediaIdToNowPlayingModelMap.get(partnerMediaId);
            if (nowPlayingModel != null) {
                return nowPlayingModel;
            }
            nowPlayingModel = new EDSV2NowPlayingDetailModel(mediaItem, (EDSV2MediaItemDetailModel) findModelInCache(mediaItem));
            tempPartnerMediaIdToNowPlayingModelMap.put(partnerMediaId, nowPlayingModel);
            return nowPlayingModel;
        } else if (titleId <= 0) {
            return null;
        } else {
            nowPlayingModel = (EDSV2NowPlayingDetailModel) tempTitleIdToNowPlayingModelMap.get(Long.valueOf(titleId));
            if (nowPlayingModel != null) {
                return nowPlayingModel;
            }
            nowPlayingModel = new EDSV2NowPlayingDetailModel(mediaItem, (EDSV2MediaItemDetailModel) findModelInCache(mediaItem));
            tempTitleIdToNowPlayingModelMap.put(Long.valueOf(titleId), nowPlayingModel);
            return nowPlayingModel;
        }
    }

    protected static final EDSV2MediaItemModel findModelInCache(EDSV2MediaItem mediaItem) {
        String str = "At least one parameter must be valid.";
        boolean z = (JavaUtil.isNullOrEmpty(mediaItem.getCanonicalId()) && JavaUtil.isNullOrEmpty(mediaItem.getPartnerMediaId()) && mediaItem.getTitleId() <= 0) ? false : true;
        XLEAssert.assertTrue(str, z);
        EDSV2MediaItemModel model = null;
        if (mediaItem.getTitleId() == XLEConstants.ZUNE_TITLE_ID && mediaItem.getMediaType() == 61) {
            model = zuneDetailModel;
        } else if (!JavaUtil.isNullOrEmpty(mediaItem.getCanonicalId())) {
            model = (EDSV2MediaItemModel) identifierToModelCache.get(mediaItem.getCanonicalId());
        } else if (!JavaUtil.isNullOrEmpty(mediaItem.getPartnerMediaId())) {
            String mediaCanonicalId = (String) partnerMediaIdToIdentifierMap.get(mediaItem.getPartnerMediaId());
            if (mediaCanonicalId != null) {
                model = (EDSV2MediaItemModel) identifierToModelCache.get(mediaCanonicalId);
            } else {
                nowPlayingModel = (EDSV2NowPlayingDetailModel) tempPartnerMediaIdToNowPlayingModelMap.get(mediaItem.getPartnerMediaId());
                if (nowPlayingModel != null) {
                    return nowPlayingModel.getInternalModel();
                }
            }
        } else if (mediaItem.getTitleId() > 0) {
            String titleCanonicalId = (String) titleIdToIdentiferMap.get(Long.valueOf(mediaItem.getTitleId()));
            if (titleCanonicalId != null) {
                model = (EDSV2MediaItemModel) identifierToModelCache.get(titleCanonicalId);
            } else if (tempTitleIdToModelMap.containsKey(Long.valueOf(mediaItem.getTitleId()))) {
                model = (EDSV2MediaItemModel) tempTitleIdToModelMap.get(Long.valueOf(mediaItem.getTitleId()));
            } else {
                nowPlayingModel = (EDSV2NowPlayingDetailModel) tempTitleIdToNowPlayingModelMap.get(Long.valueOf(mediaItem.getTitleId()));
                if (nowPlayingModel != null) {
                    return nowPlayingModel.getInternalModel();
                }
            }
        }
        return model;
    }

    protected static final EDSV2MediaItemModel createModel(EDSV2MediaItem mediaItem) {
        int mediaType = mediaItem.getMediaType();
        switch (mediaType) {
            case 1:
            case 5:
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMEDEMO /*19*/:
            case EDSV2MediaType.MEDIATYPE_XBOXORIGINALGAME /*21*/:
            case EDSV2MediaType.MEDIATYPE_XBOXARCADEGAME /*23*/:
            case EDSV2MediaType.MEDIATYPE_XBOXXNACOMMUNITYGAME /*37*/:
            case EDSV2MediaType.MEDIATYPE_WEBGAME /*57*/:
            case EDSV2MediaType.MEDIATYPE_MOBILEGAME /*58*/:
                return new EDSV2GameDetailModel(mediaItem);
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMECONTENT /*18*/:
            case 20:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMERTILE /*22*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMECONSUMABLE /*24*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMEVIDEO /*30*/:
            case EDSV2MediaType.MEDIATYPE_XBOXGAMETRAILER /*34*/:
                return new EDSV2GameContentDetailModel(mediaItem);
            case EDSV2MediaType.MEDIATYPE_XBOXAPP /*61*/:
                return new EDSV2AppDetailModel(mediaItem);
            case EDSV2MediaType.MEDIATYPE_MOVIE /*1000*/:
            case EDSV2MediaType.MEDIATYPE_MUSICVIDEO /*1008*/:
                return new EDSV2MovieDetailModel(mediaItem);
            case EDSV2MediaType.MEDIATYPE_TVSHOW /*1002*/:
            case EDSV2MediaType.MEDIATYPE_TVEPISODE /*1003*/:
                return new EDSV2TVEpisodeDetailModel(mediaItem);
            case EDSV2MediaType.MEDIATYPE_TVSERIES /*1004*/:
                return new EDSV2TVSeriesBrowseSeasonModel(mediaItem);
            case EDSV2MediaType.MEDIATYPE_TVSEASON /*1005*/:
                return new EDSV2TVSeasonBrowseEpisodeModel(mediaItem);
            case EDSV2MediaType.MEDIATYPE_ALBUM /*1006*/:
                return new EDSV2MusicAlbumDetailModel(mediaItem);
            case EDSV2MediaType.MEDIATYPE_TRACK /*1007*/:
                return new EDSV2MusicTrackDetailModel(mediaItem);
            case EDSV2MediaType.MEDIATYPE_MUSICARTIST /*1009*/:
                return new EDSV2MusicArtistBrowseAlbumModel(mediaItem);
            default:
                if (!JavaUtil.isNullOrEmpty(mediaItem.getPartnerMediaId())) {
                    return new EDSV2NowPlayingDetailModel(mediaItem, null);
                }
                if (mediaItem.getTitleId() > 0) {
                    return new EDSV2NowPlayingDetailModel(mediaItem, null);
                }
                throw new UnsupportedOperationException("Media type: " + mediaType);
        }
    }

    private static final void addModelToCache(EDSV2MediaItemModel model) {
        String str = "At least one parameter must be valid.";
        boolean z = (JavaUtil.isNullOrEmpty(model.getCanonicalId()) && JavaUtil.isNullOrEmpty(model.getPartnerMediaId()) && model.getTitleId() <= 0) ? false : true;
        XLEAssert.assertTrue(str, z);
        if (model.getTitleId() == XLEConstants.ZUNE_TITLE_ID) {
            zuneDetailModel = model;
        } else if (!JavaUtil.isNullOrEmpty(model.getCanonicalId())) {
            identifierToModelCache.put(model.getCanonicalId(), model);
        } else if (JavaUtil.isNullOrEmpty(model.getPartnerMediaId())) {
            if (model.getTitleId() <= 0) {
                return;
            }
            if ((model instanceof EDSV2NowPlayingDetailModel) && ((EDSV2NowPlayingDetailModel) model).getInternalModel() == null) {
                tempTitleIdToNowPlayingModelMap.put(Long.valueOf(model.getTitleId()), (EDSV2NowPlayingDetailModel) model);
            } else {
                tempTitleIdToModelMap.put(Long.valueOf(model.getTitleId()), model);
            }
        } else if ((model instanceof EDSV2NowPlayingDetailModel) && ((EDSV2NowPlayingDetailModel) model).getInternalModel() == null) {
            tempPartnerMediaIdToNowPlayingModelMap.put(model.getPartnerMediaId(), (EDSV2NowPlayingDetailModel) model);
        }
    }

    protected static final void updateModelInCache(EDSV2MediaItemModel model, String partnerMediaId) {
        XLEAssert.assertTrue(!JavaUtil.isNullOrEmpty(model.getCanonicalId()));
        if (!JavaUtil.isNullOrEmpty(partnerMediaId) && tempPartnerMediaIdToNowPlayingModelMap.containsKey(partnerMediaId)) {
            tempPartnerMediaIdToNowPlayingModelMap.remove(partnerMediaId);
            model.shouldLoadFullDetail = true;
        }
        if (tempTitleIdToNowPlayingModelMap.containsKey(Long.valueOf(model.getTitleId()))) {
            tempTitleIdToNowPlayingModelMap.remove(Long.valueOf(model.getTitleId()));
        }
        if (tempTitleIdToModelMap.containsValue(model)) {
            tempTitleIdToModelMap.remove(Long.valueOf(model.getTitleId()));
        }
        if (identifierToModelCache.get(model.getCanonicalId()) == null) {
            identifierToModelCache.put(model.getCanonicalId(), model);
            if (!JavaUtil.isNullOrEmpty(partnerMediaId)) {
                partnerMediaIdToIdentifierMap.put(partnerMediaId, model.getCanonicalId());
            } else if (model.getTitleId() > 0) {
                titleIdToIdentiferMap.put(Long.valueOf(model.getTitleId()), model.getCanonicalId());
            }
        }
    }

    public static final void reset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        Enumeration<EDSV2MediaItemModel> e = identifierToModelCache.elements();
        while (e.hasMoreElements()) {
            ((EDSV2MediaItemModel) e.nextElement()).clearObserver();
        }
        Enumeration<EDSV2NowPlayingDetailModel> e2 = tempPartnerMediaIdToNowPlayingModelMap.elements();
        while (e2.hasMoreElements()) {
            ((EDSV2NowPlayingDetailModel) e2.nextElement()).clearObserver();
        }
        e2 = tempTitleIdToNowPlayingModelMap.elements();
        while (e2.hasMoreElements()) {
            ((EDSV2NowPlayingDetailModel) e2.nextElement()).clearObserver();
        }
        e = tempTitleIdToModelMap.elements();
        while (e.hasMoreElements()) {
            ((EDSV2MediaItemModel) e.nextElement()).clearObserver();
        }
        identifierToModelCache = new FixedSizeHashtable(10);
        partnerMediaIdToIdentifierMap.clear();
        titleIdToIdentiferMap.clear();
        tempPartnerMediaIdToNowPlayingModelMap.clear();
        tempTitleIdToNowPlayingModelMap.clear();
        tempTitleIdToModelMap.clear();
        if (zuneDetailModel != null) {
            zuneDetailModel.clearObserver();
            zuneDetailModel = null;
        }
    }

    public static EDSV2MediaItemModel getZuneModel() {
        EDSV2AppMediaItem zuneItem = new EDSV2AppMediaItem();
        zuneItem.setTitleId(XLEConstants.ZUNE_TITLE_ID);
        zuneItem.setMediaType(61);
        return getModel(zuneItem);
    }

    public static String getZuneCanonicalId() {
        EDSV2MediaItemModel model = getZuneModel();
        if (model != null) {
            return model.getCanonicalId();
        }
        return null;
    }
}
