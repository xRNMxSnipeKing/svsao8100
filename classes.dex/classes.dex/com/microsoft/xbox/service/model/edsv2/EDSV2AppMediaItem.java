package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.util.ArrayList;

public class EDSV2AppMediaItem extends EDSV2MediaItem {
    public EDSV2AppMediaItem(EDSV2MediaItem source) {
        super(source);
        setMediaType(61);
    }

    public EDSV2AppMediaItem(Title title) {
        XLEAssert.assertNotNull(title);
        XLEAssert.assertTrue(title.IsApplication());
        setTitleId(title.getTitleId());
        setTitle(title.getName());
        setImageUrl(title.getImageUrl(MeProfileModel.getModel().getLegalLocale()));
        setMediaType(61);
        setProviders(new ArrayList());
    }

    public EDSV2AppMediaItem(EDSV2Provider provider) {
        XLEAssert.assertNotNull(provider);
        setTitleId(provider.getTitleId());
        setCanonicalId(provider.getCanonicalId());
        setTitle(provider.getName());
        setMediaType(61);
        setProviders(new ArrayList());
    }

    public void setProviders(ArrayList<EDSV2Provider> arrayList) {
        arrayList = new ArrayList();
        EDSV2Provider appProvider = new EDSV2Provider();
        appProvider.setTitleId(getTitleId());
        appProvider.setName(getTitle());
        appProvider.setCanonicalId(getCanonicalId());
        EDSV2PartnerApplicationLaunchInfo appProviderLaunchInfo = new EDSV2PartnerApplicationLaunchInfo();
        appProviderLaunchInfo.setTitleId(getTitleId());
        appProviderLaunchInfo.setLaunchType(LaunchType.AppLaunchType);
        appProviderLaunchInfo.setTitleType(JTitleType.Application);
        ArrayList<EDSV2PartnerApplicationLaunchInfo> launchInfoList = new ArrayList();
        launchInfoList.add(appProviderLaunchInfo);
        appProvider.setLaunchInfos(launchInfoList);
        arrayList.add(appProvider);
        super.setProviders(arrayList);
    }
}
