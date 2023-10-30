package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.network.managers.xblshared.ActivitiesServiceManager;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.service.network.managers.xblshared.EDSServiceManager;
import com.microsoft.xbox.service.network.managers.xblshared.IActivitiesServiceManager;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSession;
import com.microsoft.xbox.service.network.managers.xblshared.IEDSServiceManager;
import com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager;
import com.microsoft.xbox.service.network.managers.xblshared.SLSServiceManager;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;

public class ServiceManagerFactory {
    private static ServiceManagerFactory instance = new ServiceManagerFactory();
    private IAchievementServiceManager achievementServiceManager;
    private IActivitiesServiceManager activitiesServiceManager;
    private IAvatarClosetServiceManager avatarClosetServiceManager;
    private IAvatarManifestServiceManager avatarManifestServiceManager;
    private ICompanionSession companionSession;
    private IDiscoverServiceManager discoverServiceManager;
    private IEDSServiceManager edsServiceManager;
    private IFriendServiceManager friendServiceManager;
    private IGameServiceManager gameServiceManager;
    private IMessageServiceManager messageServiceManager;
    private IProfileServiceManager profileServiceManager;
    private ISLSServiceManager slsServiceManager;
    private IVersionCheckServiceManager versionCheckServiceManager;
    private IWhiteListServiceManager whitelistServiceManager;

    private ServiceManagerFactory() {
    }

    public static ServiceManagerFactory getInstance() {
        return instance;
    }

    public void setGameServiceManager(IGameServiceManager serviceManager) {
        this.gameServiceManager = serviceManager;
    }

    public void setMessageServiceManager(IMessageServiceManager serviceManager) {
        this.messageServiceManager = serviceManager;
    }

    public void setAchievementServiceManager(IAchievementServiceManager serviceManager) {
        this.achievementServiceManager = serviceManager;
    }

    public void setProfileServiceManager(IProfileServiceManager serviceManager) {
        this.profileServiceManager = serviceManager;
    }

    public void setFriendServiceManager(IFriendServiceManager serviceManager) {
        this.friendServiceManager = serviceManager;
    }

    public void setAvatarManifestServiceManager(IAvatarManifestServiceManager serviceManager) {
        this.avatarManifestServiceManager = serviceManager;
    }

    public void setAvatarClosetServiceManager(IAvatarClosetServiceManager serviceManager) {
        this.avatarClosetServiceManager = serviceManager;
    }

    public void setVersionCheckServiceManager(IVersionCheckServiceManager serviceManager) {
        this.versionCheckServiceManager = serviceManager;
    }

    public void setWhiteListServiceManager(IWhiteListServiceManager serviceManager) {
        this.whitelistServiceManager = serviceManager;
    }

    public void setEDSServiceManager(IEDSServiceManager serviceManager) {
        this.edsServiceManager = serviceManager;
    }

    public void setCompanionSession(ICompanionSession companionSession) {
        this.companionSession = companionSession;
    }

    public void setSLSServiceManager(ISLSServiceManager serviceManager) {
        this.slsServiceManager = serviceManager;
    }

    public void setDiscoverServiceManager(IDiscoverServiceManager serviceManager) {
        this.discoverServiceManager = serviceManager;
    }

    public IGameServiceManager getGameServiceManager() {
        if (this.gameServiceManager != null) {
            return this.gameServiceManager;
        }
        if (XboxLiveEnvironment.Instance().isUsingStub()) {
            return new GameServiceManager();
        }
        return new GameServiceManager();
    }

    public IProfileServiceManager getProfileServiceManager() {
        if (this.profileServiceManager != null) {
            return this.profileServiceManager;
        }
        if (XboxLiveEnvironment.Instance().isUsingStub()) {
            return new ProfileServiceManager();
        }
        return new ProfileServiceManager();
    }

    public IAvatarManifestServiceManager getAvatarManifestServiceManager() {
        if (this.avatarManifestServiceManager != null) {
            return this.avatarManifestServiceManager;
        }
        if (XboxLiveEnvironment.Instance().isUsingStub()) {
            return new AvatarManifestServiceManager();
        }
        return new AvatarManifestServiceManager();
    }

    public IAvatarClosetServiceManager getAvatarClosetServiceManager() {
        if (this.avatarClosetServiceManager != null) {
            return this.avatarClosetServiceManager;
        }
        if (XboxLiveEnvironment.Instance().isUsingStub()) {
            return new AvatarClosetServiceManager();
        }
        return new AvatarClosetServiceManager();
    }

    public IAchievementServiceManager getAchievementServiceManager() {
        if (this.achievementServiceManager != null) {
            return this.achievementServiceManager;
        }
        if (XboxLiveEnvironment.Instance().isUsingStub()) {
            return new AchievementServiceManager();
        }
        return new AchievementServiceManager();
    }

    public IMessageServiceManager getMessageServiceManager() {
        if (this.messageServiceManager != null) {
            return this.messageServiceManager;
        }
        if (XboxLiveEnvironment.Instance().isUsingStub()) {
            return new MessageServiceManager();
        }
        return new MessageServiceManager();
    }

    public IFriendServiceManager getFriendServiceManager() {
        if (this.friendServiceManager != null) {
            return this.friendServiceManager;
        }
        if (XboxLiveEnvironment.Instance().isUsingStub()) {
            return new FriendServiceManager();
        }
        return new FriendServiceManager();
    }

    public IDiscoverServiceManager getDiscoverServiceManager() {
        if (this.discoverServiceManager != null) {
            return this.discoverServiceManager;
        }
        if (XboxLiveEnvironment.Instance().isUsingStub()) {
            return new DiscoverServiceManager();
        }
        return new DiscoverServiceManager();
    }

    public ISGOverrideServiceManager getProgrammingServiceManager() {
        if (XboxLiveEnvironment.Instance().isUsingStub()) {
            return new SGOverrideServiceManager();
        }
        return new SGOverrideServiceManager();
    }

    public IVersionCheckServiceManager getVersionCheckServiceManager() {
        if (this.versionCheckServiceManager != null) {
            return this.versionCheckServiceManager;
        }
        if (XboxLiveEnvironment.Instance().isUsingStub()) {
            return new VersionCheckServiceManager();
        }
        return new VersionCheckServiceManager();
    }

    public IWhiteListServiceManager getWhiteListServiceManager() {
        if (this.whitelistServiceManager != null) {
            return this.whitelistServiceManager;
        }
        if (XboxLiveEnvironment.Instance().isUsingStub()) {
            return new WhiteListServiceManager();
        }
        return new WhiteListServiceManager();
    }

    public IEDSServiceManager getEDSServiceManager() {
        if (this.edsServiceManager != null) {
            return this.edsServiceManager;
        }
        return new EDSServiceManager();
    }

    public ISLSServiceManager getSLSServiceManager() {
        if (this.slsServiceManager != null) {
            return this.slsServiceManager;
        }
        return new SLSServiceManager();
    }

    public ICompanionSession getCompanionSession() {
        if (this.companionSession != null) {
            return this.companionSession;
        }
        return CompanionSession.getInstance();
    }

    public IActivitiesServiceManager getActivitiesServiceManager() {
        if (this.activitiesServiceManager != null) {
            return this.activitiesServiceManager;
        }
        return new ActivitiesServiceManager();
    }
}
