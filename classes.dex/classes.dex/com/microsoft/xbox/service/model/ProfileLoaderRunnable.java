package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.ProfileDataRaw;
import com.microsoft.xbox.service.network.managers.IProfileServiceManager;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;

public class ProfileLoaderRunnable extends IDataLoaderRunnable<ProfileDataRaw> {
    private ModelBase<ProfileDataRaw> caller;
    private String gamertag;
    private int profileSections;
    private IProfileServiceManager serviceManager;

    public ProfileLoaderRunnable(IProfileServiceManager serviceManager, ModelBase<ProfileDataRaw> caller, String gamertag, int profileSections) {
        this.serviceManager = serviceManager;
        this.caller = caller;
        this.gamertag = gamertag;
        this.profileSections = profileSections;
    }

    public void onPreExecute() {
    }

    public ProfileDataRaw buildData() throws XLEException {
        return this.serviceManager.getData(this.gamertag, this.profileSections);
    }

    public void onPostExcute(AsyncResult<ProfileDataRaw> result) {
        this.caller.updateWithNewData(result);
    }

    public long getDefaultErrorCode() {
        if (this.profileSections == 97) {
            return XLEErrorCode.FAILED_TO_GET_ME_PROFILE;
        }
        if (this.profileSections == 41) {
            return XLEErrorCode.FAILED_TO_GET_YOU_PROFILE;
        }
        if (this.profileSections == 128) {
            return XLEErrorCode.FAILED_TO_GET_FRIENDS;
        }
        XLEAssert.assertTrue("Unsupported profile serction: " + Integer.toString(this.profileSections), false);
        return XLEErrorCode.FAILED_DEV_ERROR;
    }
}
