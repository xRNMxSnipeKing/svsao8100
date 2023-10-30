package com.microsoft.xbox.service.model.serialization;

import java.util.ArrayList;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "AvatarManifests")
public class AvatarManifestDataRaw {
    @ElementList
    public ArrayList<XLEAvatarManifest> Manifests;
}
