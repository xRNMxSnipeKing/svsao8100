package org.simpleframework.xml.transform;

import java.io.File;

class FileTransform implements Transform<File> {
    FileTransform() {
    }

    public File read(String path) {
        return new File(path);
    }

    public String write(File path) {
        return path.getPath();
    }
}
