package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Set;
import org.simpleframework.xml.strategy.Type;

interface Label {
    Annotation getAnnotation();

    Contact getContact();

    Converter getConverter(Context context) throws Exception;

    Decorator getDecorator() throws Exception;

    Type getDependent() throws Exception;

    Object getEmpty(Context context) throws Exception;

    String getEntry() throws Exception;

    Label getLabel(Class cls) throws Exception;

    String getName() throws Exception;

    String getName(Context context) throws Exception;

    String getOverride();

    String getPath();

    Class getType();

    Type getType(Class cls) throws Exception;

    Set<String> getUnion() throws Exception;

    Set<String> getUnion(Context context) throws Exception;

    boolean isAttribute();

    boolean isCollection();

    boolean isData();

    boolean isInline();

    boolean isRequired();

    String toString();
}
