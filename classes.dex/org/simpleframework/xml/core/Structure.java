package org.simpleframework.xml.core;

import org.simpleframework.xml.Version;

class Structure {
    private final Model model;
    private final boolean primitive;
    private final Label text;
    private final Label version;

    public Structure(Model model, Label version, Label text, boolean primitive) {
        this.primitive = primitive;
        this.version = version;
        this.model = model;
        this.text = text;
    }

    public Section getSection(Context context) {
        return new ModelSection(context, this.model);
    }

    public boolean isPrimitive() {
        return this.primitive;
    }

    public Version getRevision() {
        if (this.version != null) {
            return (Version) this.version.getContact().getAnnotation(Version.class);
        }
        return null;
    }

    public Label getVersion() {
        return this.version;
    }

    public Label getText() {
        return this.text;
    }
}
