package com.microsoft.xbox.service.model.serialization;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;

@Root(name = "PrivacySettings")
public class PrivacySettings {
    public int FriendsList;
    public int GamerProfile;
    public int MemberContent;
    public int OnlineStatus;
    public int PlayedGames;
    public int VoiceAndText;
    @ElementList
    private ArrayList<PrivacySetting> items;
    public Hashtable<String, Integer> settings = new Hashtable();

    @Persist
    public void preProcess() {
        this.items = new ArrayList();
        this.items.add(new PrivacySetting("VoiceAndText", this.VoiceAndText));
        this.items.add(new PrivacySetting("GamerProfile", this.GamerProfile));
        this.items.add(new PrivacySetting("OnlineStatus", this.OnlineStatus));
        this.items.add(new PrivacySetting("MemberContent", this.MemberContent));
        this.items.add(new PrivacySetting("PlayedGames", this.PlayedGames));
        this.items.add(new PrivacySetting("FriendsList", this.FriendsList));
    }

    @Commit
    public void postprocess() {
        Iterator i$ = this.items.iterator();
        while (i$.hasNext()) {
            PrivacySetting item = (PrivacySetting) i$.next();
            if (item != null) {
                this.settings.put(item.PrivacySetting, Integer.valueOf(item.unsignedInt));
            }
        }
        this.VoiceAndText = ((Integer) this.settings.get("VoiceAndText")).intValue();
        this.GamerProfile = ((Integer) this.settings.get("GamerProfile")).intValue();
        this.OnlineStatus = ((Integer) this.settings.get("OnlineStatus")).intValue();
        this.MemberContent = ((Integer) this.settings.get("MemberContent")).intValue();
        this.PlayedGames = ((Integer) this.settings.get("PlayedGames")).intValue();
        this.FriendsList = ((Integer) this.settings.get("FriendsList")).intValue();
    }
}
