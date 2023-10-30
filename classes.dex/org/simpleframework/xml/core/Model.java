package org.simpleframework.xml.core;

interface Model extends Iterable<String> {
    LabelMap buildAttributes(Context context) throws Exception;

    LabelMap buildElements(Context context) throws Exception;

    ModelMap buildModels(Context context) throws Exception;

    int getIndex();

    String getName();

    String getPrefix();

    boolean isAttribute(String str);

    boolean isComposite();

    boolean isElement(String str);

    boolean isEmpty();

    boolean isModel(String str);

    Model lookup(String str, int i);

    Model lookup(Expression expression);

    Model register(String str, String str2, int i) throws Exception;

    void register(Label label) throws Exception;

    void registerAttribute(String str) throws Exception;

    void registerAttribute(Label label) throws Exception;

    void registerElement(String str) throws Exception;

    void registerElement(Label label) throws Exception;

    void validate(Class cls) throws Exception;
}
