package com.microsoft.xbox.service.model.serialization;

import java.util.ArrayList;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "Games")
public class GameDataRaw {
    @Element(name = "TotalUniqueGames")
    public int TotalUniqueGames;
    @ElementList
    public ArrayList<UserGames> UserGamesCollection;
}
