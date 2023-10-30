package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.discover.DiscoverAllMusic;
import com.microsoft.xbox.toolkit.XLEException;

public interface IDiscoverServiceManager {
    DiscoverAllMusic getAllMusicData() throws XLEException;
}
