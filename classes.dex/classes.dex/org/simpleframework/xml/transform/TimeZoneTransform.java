package org.simpleframework.xml.transform;

import java.util.TimeZone;

class TimeZoneTransform implements Transform<TimeZone> {
    TimeZoneTransform() {
    }

    public TimeZone read(String zone) {
        return TimeZone.getTimeZone(zone);
    }

    public String write(TimeZone zone) {
        return zone.getID();
    }
}
