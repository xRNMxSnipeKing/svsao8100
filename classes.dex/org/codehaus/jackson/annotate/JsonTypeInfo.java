package org.codehaus.jackson.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JacksonAnnotation
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonTypeInfo {

    public enum As {
        PROPERTY,
        WRAPPER_OBJECT,
        WRAPPER_ARRAY,
        EXTERNAL_PROPERTY
    }

    public enum Id {
        NONE(null),
        CLASS("@class"),
        MINIMAL_CLASS("@c"),
        NAME("@type"),
        CUSTOM(null);
        
        private final String _defaultPropertyName;

        private Id(String defProp) {
            this._defaultPropertyName = defProp;
        }

        public String getDefaultPropertyName() {
            return this._defaultPropertyName;
        }
    }

    public static abstract class None {
    }

    Class<?> defaultImpl() default None.class;

    As include() default As.PROPERTY;

    String property() default "";

    Id use();
}
