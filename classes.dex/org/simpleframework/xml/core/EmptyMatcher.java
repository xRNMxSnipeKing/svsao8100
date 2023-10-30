package org.simpleframework.xml.core;

import org.simpleframework.xml.transform.Matcher;
import org.simpleframework.xml.transform.Transform;

class EmptyMatcher implements Matcher {
    EmptyMatcher() {
    }

    public Transform match(Class type) throws Exception {
        return null;
    }
}
