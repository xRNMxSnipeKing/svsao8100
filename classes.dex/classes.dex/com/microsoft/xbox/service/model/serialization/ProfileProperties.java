package com.microsoft.xbox.service.model.serialization;

import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.UrlUtil;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;

@Root(name = "ProfileProperties")
public class ProfileProperties {
    public URI AvatarImageUri;
    public String Bio;
    public URI GamerPicUri;
    public String Gamerscore;
    public String Gamertag;
    public boolean HasAvatar;
    public boolean IsParentallyControlled;
    public String Location;
    public String MembershipLevel;
    public String Motto;
    public String Name;
    public String ParentalControlGroup;
    public boolean ShowGamerProfile;
    public URI SmallGamerPicUri;
    @ElementList
    private ArrayList<ProfileProperty> items;
    public Hashtable<String, String> properties = new Hashtable();

    @Persist
    public void preProcess() {
        this.items = new ArrayList();
        this.items.add(new ProfileProperty("Motto", this.Motto != null ? this.Motto : "", "xsd:string"));
        this.items.add(new ProfileProperty("Bio", this.Bio != null ? this.Bio : "", "xsd:string"));
        this.items.add(new ProfileProperty("Location", this.Location != null ? this.Location : "", "xsd:string"));
        this.items.add(new ProfileProperty("Name", this.Name != null ? this.Name : "", "xsd:string"));
    }

    @Commit
    public void postprocess() {
        Iterator i$ = this.items.iterator();
        while (i$.hasNext()) {
            ProfileProperty item = (ProfileProperty) i$.next();
            if (item != null) {
                String key = item.ProfileProperty;
                String value = null;
                if (item.anyType != null) {
                    value = item.anyType.value;
                }
                if (value == null) {
                    value = "";
                }
                this.properties.put(key, value);
            }
        }
        this.Gamertag = (String) this.properties.get("GamerTag");
        this.Gamerscore = (String) this.properties.get("GamerScore");
        this.Motto = (String) this.properties.get("Motto");
        this.AvatarImageUri = UrlUtil.getEncodedUri((String) this.properties.get("AvatarImageUrl"));
        this.Bio = (String) this.properties.get("Bio");
        this.GamerPicUri = UrlUtil.getEncodedUri((String) this.properties.get("GamerPicUrl"));
        this.SmallGamerPicUri = UrlUtil.getEncodedUri((String) this.properties.get("SmallGamerPicUrl"));
        this.Location = (String) this.properties.get("Location");
        this.Name = (String) this.properties.get("Name");
        this.MembershipLevel = (String) this.properties.get("MembershipLevel");
        this.ParentalControlGroup = (String) this.properties.get("ParentalControlGroup");
        this.ShowGamerProfile = JavaUtil.tryParseBoolean((String) this.properties.get("ShowGamerProfile"), false);
        this.IsParentallyControlled = JavaUtil.tryParseBoolean((String) this.properties.get("IsParentallyControlled"), true);
        this.HasAvatar = JavaUtil.tryParseBoolean((String) this.properties.get("HasAvatar"), false);
    }
}
