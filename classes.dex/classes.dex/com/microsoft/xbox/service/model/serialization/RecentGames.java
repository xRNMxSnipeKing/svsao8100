package com.microsoft.xbox.service.model.serialization;

import java.util.ArrayList;
import org.simpleframework.xml.ElementList;

public class RecentGames {
    @ElementList(required = false)
    public ArrayList<GameInfo> items;
}
