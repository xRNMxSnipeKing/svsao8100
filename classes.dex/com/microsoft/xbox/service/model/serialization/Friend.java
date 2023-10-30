package com.microsoft.xbox.service.model.serialization;

import org.simpleframework.xml.Element;

public class Friend {
    @Element(required = false)
    public int FriendState;
    @Element(name = "ProfileEx", required = false)
    public ProfileDataRaw ProfileEx;

    public String getGamertag() {
        if (this.ProfileEx != null) {
            return this.ProfileEx.getGamertag();
        }
        return null;
    }

    public int getGameScore() {
        if (this.ProfileEx != null) {
            return this.ProfileEx.getGameScore();
        }
        return 0;
    }
}
