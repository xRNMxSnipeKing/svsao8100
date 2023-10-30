package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.List;

interface Extractor<T extends Annotation> {
    List<T> getAnnotations() throws Exception;

    Label getLabel(T t) throws Exception;

    Class getType(T t) throws Exception;
}
