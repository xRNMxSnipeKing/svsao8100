package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;

public class Environment {
    private EnvironmentType environmentType;

    public Environment(EnvironmentType envType) {
        this.environmentType = envType;
    }

    public String getTmfUri() {
        switch (this.environmentType) {
            case Production:
                return "https://xlink.xboxlive.com";
            case PartnerNet:
                return "https://xlink.part.xboxlive.com";
            default:
                return "https://xlink.vint.xboxlive.com";
        }
    }

    public String getWindowsLiveEndpointUri() {
        switch (this.environmentType) {
            case Production:
                return "https://login.live.com/ppsecure/clientpost.srf?id=1&ru=https://kdc.xboxlive.com&wp=NFS_24HR_10_COMPACT";
            case PartnerNet:
                return "https://login.live-int.com/ppsecure/clientpost.srf?id=1&ru=https://kdc.part.xboxlive.com&wp=NFS_24HR_10_COMPACT";
            default:
                return "https://login.live-int.com/ppsecure/clientpost.srf?id=1&ru=https://kdc.vint.xboxlive.com&wp=NFS_24HR_10_COMPACT";
        }
    }

    public String getXstsEndpointUri() {
        switch (this.environmentType) {
            case Production:
                return "https://auth.xboxlive.com/XSts/xsts.svc/IWSTrust13";
            case PartnerNet:
                return "https://auth.part.xboxlive.com/XSts/xsts.svc/IWSTrust13";
            default:
                return "https://activeauth.vint.xboxlive.com/XSts/xsts.svc/IWSTrust13";
        }
    }

    public String getServicesEndpointUri(String subDomain, String path) {
        String hostDomain;
        switch (this.environmentType) {
            case Production:
                hostDomain = "xboxlive.com";
                break;
            case PartnerNet:
                hostDomain = "part.xboxlive.com";
                break;
            default:
                hostDomain = "vint.xboxlive.com";
                break;
        }
        if (!(path == null || path.isEmpty() || !path.startsWith("/"))) {
            path = path.substring(1);
        }
        return String.format("https://%s.%s/%s", new Object[]{subDomain, hostDomain, path});
    }

    public String getXlinkXstsAudienceUri() {
        return XboxLiveEnvironment.XLINK_AUDIENCE_URI;
    }

    public String getXliveXstsAudienceUri() {
        return XboxLiveEnvironment.SLS_AUDIENCE_URI;
    }
}
