package org.simpleframework.xml.transform;

import java.net.URL;

class URLTransform implements Transform<URL> {
    URLTransform() {
    }

    public URL read(String target) throws Exception {
        return new URL(target);
    }

    public String write(URL target) throws Exception {
        return target.toString();
    }
}
