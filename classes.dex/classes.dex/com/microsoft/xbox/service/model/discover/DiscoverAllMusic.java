package com.microsoft.xbox.service.model.discover;

import com.microsoft.xbox.service.model.serialization.UTCDateConverter;
import java.util.ArrayList;
import java.util.Date;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;
import org.simpleframework.xml.core.Validate;

@Root(name = "feed")
public class DiscoverAllMusic {
    public MusicInfo MusicInfoEntry;
    @ElementList(inline = true, name = "entry")
    private ArrayList<MusicInfo> internalMusicInfo = new ArrayList();
    @Element(required = false)
    @Convert(UTCDateConverter.class)
    public Date retrievedTime;

    @Validate
    public void validate() {
        if (this.internalMusicInfo != null && !this.internalMusicInfo.isEmpty()) {
            this.MusicInfoEntry = (MusicInfo) this.internalMusicInfo.get(0);
        }
    }
}
