package com.microsoft.xbox.service.model.zest;

import com.microsoft.xbox.service.model.ModelBase;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.network.managers.CommerceServiceManager;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoaderTask;
import com.microsoft.xbox.toolkit.FileStorageManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.Date;

public class CommerceUserInfoModel extends ModelBase<SignInResponse> {
    private static String LOCAL_FILE_NAME = "UserInfo";
    private final String defaultLegalLocale;
    private boolean isLoadingFromFile;
    private SignInResponse userInfo;

    private static class CommerceUserInfoModelContainer {
        private static CommerceUserInfoModel instance = new CommerceUserInfoModel();

        private CommerceUserInfoModelContainer() {
        }

        private static void reset() {
            instance = new CommerceUserInfoModel();
        }
    }

    private class LoadUserFromFileRunner extends IDataLoaderRunnable<SignInResponse> {
        private CommerceUserInfoModel caller;

        public LoadUserFromFileRunner(CommerceUserInfoModel caller) {
            this.caller = caller;
        }

        public SignInResponse buildData() throws XLEException {
            return (SignInResponse) FileStorageManager.getInstance().readFromFile(XboxApplication.Instance, CommerceUserInfoModel.LOCAL_FILE_NAME, SignInResponse.class);
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<SignInResponse> result) {
            this.caller.onLoadUserCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_LOAD_USER_FROM_FILE;
        }
    }

    private class SaveUserToFileRunner extends IDataLoaderRunnable<Void> {
        private SignInResponse result;

        public SaveUserToFileRunner(SignInResponse result) {
            this.result = result;
        }

        public Void buildData() throws XLEException {
            FileStorageManager.getInstance().saveToFile(XboxApplication.Instance, CommerceUserInfoModel.LOCAL_FILE_NAME, this.result);
            return null;
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<Void> asyncResult) {
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_SAVE_USER_TO_FILE;
        }
    }

    private class ZestSignInRunner extends IDataLoaderRunnable<SignInResponse> {
        private CommerceUserInfoModel caller;

        public ZestSignInRunner(CommerceUserInfoModel caller) {
            this.caller = caller;
        }

        public SignInResponse buildData() throws XLEException {
            return new CommerceServiceManager().signIn();
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<SignInResponse> result) {
            this.caller.onSignInCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_SIGN_IN_ZEST;
        }
    }

    public static CommerceUserInfoModel getInstance() {
        return CommerceUserInfoModelContainer.instance;
    }

    private CommerceUserInfoModel() {
        this.defaultLegalLocale = "en-us";
        loadFromFile();
    }

    public void reset() {
        XLEAssert.assertIsUIThread();
        clearObserver();
        FileStorageManager.getInstance().deleteFile(XboxApplication.Instance, LOCAL_FILE_NAME);
        CommerceUserInfoModelContainer.reset();
    }

    public AccountInfo getAccountInfo() {
        if (this.userInfo != null) {
            return this.userInfo.AccountInfo;
        }
        return null;
    }

    public String getLegalLocale() {
        if (this.userInfo == null) {
            getClass();
            return "en-us";
        } else if (this.userInfo.AccountInfo != null && !this.userInfo.AccountInfo.Locale.isEmpty()) {
            return this.userInfo.AccountInfo.Locale;
        } else {
            getClass();
            return "en-us";
        }
    }

    public SubscriptionInfo getSubscriptionInfo() {
        if (this.userInfo != null) {
            return this.userInfo.SubscriptionInfo;
        }
        return null;
    }

    public void SignIn(boolean forceLoad) {
        if (shouldRefresh() || forceLoad) {
            XLELog.Diagnostic("CommerceUserInfoModel", "actual sign in to zest service ...");
            this.isLoading = true;
            new DataLoaderTask(0, new ZestSignInRunner(this)).execute();
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.ZestSignIn, this.isLoading), this, null));
    }

    private void loadFromFile() {
        if (this.isLoadingFromFile) {
            XLELog.Diagnostic("CommerceUserInfoModel", "already loading from file, ignore this request");
            return;
        }
        XLELog.Diagnostic("CommerceUserInfoModel", "kick off loading from file");
        this.userInfo = null;
        DataLoaderTask<SignInResponse> task = new DataLoaderTask(0, new LoadUserFromFileRunner(this));
        this.isLoadingFromFile = true;
        task.execute();
    }

    private void onSignInCompleted(AsyncResult<SignInResponse> result) {
        XLELog.Diagnostic("CommerceUserInfoModel", "onSignInCompleted called");
        if (result.getException() == null) {
            this.userInfo = (SignInResponse) result.getResult();
            this.lastRefreshTime = new Date();
            this.userInfo.retrievedTime = this.lastRefreshTime;
            XLELog.Diagnostic("CommerceUserInfoModel", "update refresh date to saved file " + this.userInfo.retrievedTime);
            new DataLoaderTask(0, new SaveUserToFileRunner(this.userInfo)).execute();
        } else {
            XLELog.Diagnostic("CommerceUserInfoModel", "sign in failed with exception " + result.getException().toString());
        }
        this.isLoading = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.ZestSignIn, true), this, result.getException()));
    }

    private void onLoadUserCompleted(AsyncResult<SignInResponse> result) {
        XLELog.Diagnostic("CommerceUserInfoModel", "onLoadUserCompleted called");
        XLEAssert.assertIsUIThread();
        if (result.getException() == null) {
            if (this.userInfo == null) {
                this.userInfo = (SignInResponse) result.getResult();
                this.lastRefreshTime = this.userInfo.retrievedTime;
                XLELog.Diagnostic("CommerceUserInfoModel", "use refresh date from saved file " + this.lastRefreshTime);
            } else {
                XLELog.Warning("CommerceUserInfoModel", "user info is already assigned, ignore this");
            }
        }
        this.isLoadingFromFile = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.ZestSignIn, true), this, result.getException()));
    }
}
