package com.microsoft.xbox.service.model.serialization;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@NamespaceList({@Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance"), @Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema")})
@Root(name = "ProfileEx")
public class ProfileDataUploadRaw {
    public static final int PROFILE_SECTION_FLAGS_XBOX_LIVE_PROPERTIES = 1;
    @Element
    public ProfileProperties ProfileProperties;
    @Element(name = "SectionFlags")
    private int SectionFlags = 1;

    public ProfileDataUploadRaw(ProfileProperties profileProperties) {
        this.ProfileProperties = profileProperties;
    }
}
