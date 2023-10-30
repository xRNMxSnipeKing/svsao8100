package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.StreamUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.io.IOException;

public class AvatarClosetModel extends ModelBase<byte[]> {
    private static AvatarClosetModel playerModel = null;
    private static AvatarClosetModel stockModel = null;
    private byte[] closetData = null;
    private UpdateType updateType;

    private class AvatarClosetLoaderRunnable extends IDataLoaderRunnable<byte[]> {
        public void onPreExecute() {
        }

        public byte[] buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getAvatarClosetServiceManager().getData();
        }

        public void onPostExcute(AsyncResult<byte[]> result) {
            AvatarClosetModel.this.updateWithNewData(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_AVATAR_CLOSET;
        }
    }

    private class AvatarClosetStockDataLoaderRunnable extends IDataLoaderRunnable<byte[]> {
        public void onPreExecute() {
        }

        public byte[] buildData() throws XLEException {
            try {
                return StreamUtil.CreateByteArray(XboxApplication.AssetManager.open(XboxLiveEnvironment.Instance().getStockAssetsPath()));
            } catch (IOException e) {
                XLELog.Error("AvatarClostModel", "Could not open file " + XboxLiveEnvironment.Instance().getStockAssetsPath());
                throw new XLEException(XLEErrorCode.FAILED_TO_LOAD_STOCK_ASSETS);
            }
        }

        public void onPostExcute(AsyncResult<byte[]> result) {
            AvatarClosetModel.this.updateWithNewData(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_LOAD_STOCK_ASSETS;
        }
    }

    private AvatarClosetModel(UpdateType type) {
        this.updateType = type;
    }

    public void setLoaderRunnable(IDataLoaderRunnable<byte[]> runnable) {
        this.loaderRunnable = runnable;
    }

    public byte[] getClosetData() {
        return this.closetData;
    }

    public void load(boolean forceRefresh) {
        loadInternal(forceRefresh, this.updateType, this.loaderRunnable);
    }

    public void updateWithNewData(AsyncResult<byte[]> asyncResult) {
        super.updateWithNewData(asyncResult);
        if (asyncResult.getException() == null && asyncResult.getResult() != null) {
            this.closetData = (byte[]) asyncResult.getResult();
        }
        notifyObservers(new AsyncResult(new UpdateData(this.updateType, true), this, asyncResult.getException()));
    }

    public static AvatarClosetModel getPlayerModel() {
        if (playerModel == null) {
            playerModel = new AvatarClosetModel(UpdateType.AvatarClosetData);
            AvatarClosetModel avatarClosetModel = playerModel;
            AvatarClosetModel avatarClosetModel2 = playerModel;
            avatarClosetModel2.getClass();
            avatarClosetModel.setLoaderRunnable(new AvatarClosetLoaderRunnable());
        }
        return playerModel;
    }

    public static AvatarClosetModel getStockModel() {
        if (stockModel == null) {
            stockModel = new AvatarClosetModel(UpdateType.AvatarStockClosetData);
            AvatarClosetModel avatarClosetModel = stockModel;
            AvatarClosetModel avatarClosetModel2 = stockModel;
            avatarClosetModel2.getClass();
            avatarClosetModel.setLoaderRunnable(new AvatarClosetStockDataLoaderRunnable());
        }
        return stockModel;
    }

    public static void reset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (playerModel != null) {
            playerModel.clearObserver();
            playerModel = null;
        }
        if (stockModel != null) {
            stockModel.clearObserver();
            stockModel = null;
        }
    }
}
