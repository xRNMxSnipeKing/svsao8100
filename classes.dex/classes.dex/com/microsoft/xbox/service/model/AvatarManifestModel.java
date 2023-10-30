package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.AvatarManifestDataRaw;
import com.microsoft.xbox.service.model.serialization.XLEAvatarManifest;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoaderTask;
import com.microsoft.xbox.toolkit.FixedSizeHashtable;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.Enumeration;

public class AvatarManifestModel extends ModelBase<AvatarManifestDataRaw> {
    private static final int MAX_PLAYER_PROFILES = 10;
    private static boolean isUserAwareFiltered = false;
    private static FixedSizeHashtable<String, AvatarManifestModel> manifestModelCache = new FixedSizeHashtable(10);
    private static AvatarManifestModel playerModel = null;
    private boolean isSaving = false;
    private XLEAvatarManifest manifest = null;

    private class AvatarManifestLoaderRunnable extends IDataLoaderRunnable<AvatarManifestDataRaw> {
        private AvatarManifestModel caller;
        private String gamertag;

        public AvatarManifestLoaderRunnable(AvatarManifestModel caller, String gamertag) {
            this.caller = caller;
            this.gamertag = gamertag;
        }

        public void onPreExecute() {
        }

        public AvatarManifestDataRaw buildData() throws XLEException {
            if (this.gamertag == null) {
                return ServiceManagerFactory.getInstance().getAvatarManifestServiceManager().getPlayerData();
            }
            return ServiceManagerFactory.getInstance().getAvatarManifestServiceManager().getGamerData(this.gamertag);
        }

        public void onPostExcute(AsyncResult<AvatarManifestDataRaw> result) {
            this.caller.updateWithNewData(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_AVATAR_MANIFEST;
        }
    }

    private class AvatarManifestSaverRunnable extends IDataLoaderRunnable<XLEAvatarManifest> {
        private AvatarManifestModel caller;
        private XLEAvatarManifest uploadData = null;

        public AvatarManifestSaverRunnable(AvatarManifestModel caller, String newManifest) {
            this.caller = caller;
            this.uploadData = new XLEAvatarManifest(AvatarManifestModel.this.manifest);
            this.uploadData.Filtered = false;
            this.uploadData.Manifest = newManifest;
        }

        public void onPreExecute() {
        }

        public XLEAvatarManifest buildData() throws XLEException {
            ServiceManagerFactory.getInstance().getAvatarManifestServiceManager().savePlayerData(this.uploadData.Manifest);
            return this.uploadData;
        }

        public void onPostExcute(AsyncResult<XLEAvatarManifest> result) {
            this.caller.onSaveComplete(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_SAVE_AVATAR;
        }
    }

    private AvatarManifestModel(String gamerTag) {
        this.loaderRunnable = new AvatarManifestLoaderRunnable(this, gamerTag);
    }

    public XLEAvatarManifest getManifest() {
        return this.manifest;
    }

    public boolean getIsSaving() {
        return this.isSaving;
    }

    public static void setIsUserAwareFiltered(boolean isWarned) {
        isUserAwareFiltered = isWarned;
    }

    public static boolean getIsPlayerManifestFiltered() {
        AvatarManifestModel model = getPlayerModel();
        if (model.manifest == null || !model.manifest.Filtered || isUserAwareFiltered) {
            return false;
        }
        return true;
    }

    public void load(boolean forceRefresh) {
        loadInternal(forceRefresh, UpdateType.AvatarManifestLoad, this.loaderRunnable);
    }

    public void save(String newManifest) {
        boolean z;
        if (this.isSaving) {
            z = false;
        } else {
            z = true;
        }
        XLEAssert.assertTrue(z);
        this.isSaving = true;
        new DataLoaderTask(this.lastInvalidatedTick, new AvatarManifestSaverRunnable(this, newManifest)).execute();
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.AvatarManifestSave, false), this, null));
    }

    public void updateWithNewData(AsyncResult<AvatarManifestDataRaw> asyncResult) {
        super.updateWithNewData(asyncResult);
        if (!(asyncResult.getException() != null || asyncResult.getResult() == null || ((AvatarManifestDataRaw) asyncResult.getResult()).Manifests == null)) {
            this.manifest = new XLEAvatarManifest();
            if (((AvatarManifestDataRaw) asyncResult.getResult()).Manifests.size() > 0) {
                this.manifest = (XLEAvatarManifest) ((AvatarManifestDataRaw) asyncResult.getResult()).Manifests.get(0);
            }
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.AvatarManifestLoad, true), this, asyncResult.getException()));
    }

    private void onSaveComplete(AsyncResult<XLEAvatarManifest> asyncResult) {
        if (asyncResult.getException() == null) {
            XLEAssert.assertNotNull(asyncResult.getResult());
            this.manifest = (XLEAvatarManifest) asyncResult.getResult();
        }
        this.isSaving = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.AvatarManifestSave, true), this, asyncResult.getException()));
    }

    public static AvatarManifestModel getGamerModel(String gamerTag) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (gamerTag == null || gamerTag.length() == 0) {
            throw new IllegalArgumentException();
        }
        AvatarManifestModel model = (AvatarManifestModel) manifestModelCache.get(gamerTag);
        if (model != null) {
            return model;
        }
        model = new AvatarManifestModel(gamerTag);
        manifestModelCache.put(gamerTag, model);
        return model;
    }

    public static AvatarManifestModel getPlayerModel() {
        if (playerModel == null) {
            playerModel = new AvatarManifestModel(null);
        }
        return playerModel;
    }

    public static void reset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        Enumeration<AvatarManifestModel> e = manifestModelCache.elements();
        while (e.hasMoreElements()) {
            ((AvatarManifestModel) e.nextElement()).clearObserver();
        }
        manifestModelCache = new FixedSizeHashtable(10);
        playerModel = null;
    }
}
