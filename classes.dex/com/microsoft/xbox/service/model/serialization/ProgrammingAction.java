package com.microsoft.xbox.service.model.serialization;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Action")
public class ProgrammingAction {
    public static String Album = "MusicMarketplace/Album";
    public static String App = "AppMarketplace/Activity";
    public static String Game = "GameMarketplace/Xbox";
    public static String Movie = "VideoMarketplace/Movie";
    public static String TV = "VideoMarketplace/Series";
    @Element(name = "Parameters")
    public String Parameters;
    @Element(name = "Target")
    public String Target;
    @Attribute(name = "type")
    public String type;
}
