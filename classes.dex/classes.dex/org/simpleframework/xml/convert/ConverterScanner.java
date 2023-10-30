package org.simpleframework.xml.convert;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;

class ConverterScanner {
    private final ScannerBuilder builder = new ScannerBuilder();
    private final ConverterFactory factory = new ConverterFactory();

    public Converter getConverter(Type type, Value value) throws Exception {
        Convert convert = getConvert(type, getType(type, value));
        if (convert != null) {
            return this.factory.getInstance(convert);
        }
        return null;
    }

    public Converter getConverter(Type type, Object value) throws Exception {
        Convert convert = getConvert(type, getType(type, value));
        if (convert != null) {
            return this.factory.getInstance(convert);
        }
        return null;
    }

    private Convert getConvert(Type type, Class real) throws Exception {
        Convert convert = getConvert(type);
        if (convert == null) {
            return getConvert(real);
        }
        return convert;
    }

    private Convert getConvert(Type type) throws Exception {
        Convert convert = (Convert) type.getAnnotation(Convert.class);
        if (convert == null || ((Element) type.getAnnotation(Element.class)) != null) {
            return convert;
        }
        throw new ConvertException("Element annotation required for %s", type);
    }

    private Convert getConvert(Class real) throws Exception {
        Convert convert = (Convert) getAnnotation(real, Convert.class);
        if (convert == null || ((Root) getAnnotation(real, Root.class)) != null) {
            return convert;
        }
        throw new ConvertException("Root annotation required for %s", real);
    }

    private <T extends Annotation> T getAnnotation(Class<?> type, Class<T> label) {
        return this.builder.build(type).scan(label);
    }

    private Class getType(Type type, Value value) {
        Class real = type.getType();
        if (value != null) {
            return value.getType();
        }
        return real;
    }

    private Class getType(Type type, Object value) {
        Class real = type.getType();
        if (value != null) {
            return value.getClass();
        }
        return real;
    }
}
