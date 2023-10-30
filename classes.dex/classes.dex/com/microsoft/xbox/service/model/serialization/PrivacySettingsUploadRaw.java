package com.microsoft.xbox.service.model.serialization;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@NamespaceList({@Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance"), @Namespace(prefix = "xsd", reference = "http://www.w3.org/2001/XMLSchema")})
@Root(name = "ProfileEx")
public class PrivacySettingsUploadRaw {
    public static final int PROFILE_SECTION_FLAGS_PRIVACY_SETTINGS = 64;
    @Element
    public PrivacySettings PrivacySettings;
    @Element
    private int SectionFlags = 64;

    public PrivacySettingsUploadRaw(PrivacySettings privacySettings) {
        this.PrivacySettings = privacySettings;
    }
}
