package org.simpleframework.xml.core;

import java.util.List;

interface Creator {
    List<Initializer> getInitializers();

    Object getInstance(Context context) throws Exception;

    Object getInstance(Context context, Criteria criteria) throws Exception;

    Parameter getParameter(String str);

    List<Parameter> getParameters();

    boolean isDefault();
}
