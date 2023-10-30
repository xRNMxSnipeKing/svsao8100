package com.microsoft.xbox.service.model.serialization;

import java.util.ArrayList;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class UserGames {
    @ElementList
    public ArrayList<GameInfo> GameList;
    @Element
    public String GamerTag;
    @Element
    public int Gamerscore;
    @Element
    public int TotalGamesPlayed;
    @Element
    public int TotalPossibleGamerscore;
}
