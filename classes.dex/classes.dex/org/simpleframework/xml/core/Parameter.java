package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;

interface Parameter {
    Annotation getAnnotation();

    int getIndex();

    String getName() throws Exception;

    String getName(Context context) throws Exception;

    Class getType();

    boolean isPrimitive();

    boolean isRequired();

    String toString();
}
