package org.simpleframework.xml.core;

interface Group {
    LabelMap getElements(Context context) throws Exception;

    Label getLabel(Class cls);

    String toString();
}
