package com.microsoft.xbox.service.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@NamespaceList({@Namespace(reference = "http://schemas.datacontract.org/2004/07/Avatar.Services.ManifestWrite.Library")})
@Root(name = "UpdateManifestRequest")
public class AvatarManifestUpload {
    @Element
    public String Manifest;
}
