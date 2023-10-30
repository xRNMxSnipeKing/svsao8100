package org.simpleframework.xml.transform;

class EmptyMatcher implements Matcher {
    EmptyMatcher() {
    }

    public Transform match(Class type) throws Exception {
        return null;
    }
}
