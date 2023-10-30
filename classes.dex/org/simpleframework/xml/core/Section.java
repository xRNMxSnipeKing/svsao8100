package org.simpleframework.xml.core;

interface Section extends Iterable<String> {
    LabelMap getAttributes() throws Exception;

    Label getElement(String str) throws Exception;

    LabelMap getElements() throws Exception;

    String getName();

    String getPrefix();

    Section getSection(String str) throws Exception;

    boolean isSection(String str) throws Exception;
}
