package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;

@JacksonStdImpl
public class ClassDeserializer extends StdScalarDeserializer<Class<?>> {
    public ClassDeserializer() {
        super(Class.class);
    }

    public Class<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken curr = jp.getCurrentToken();
        if (curr == JsonToken.VALUE_STRING) {
            String className = jp.getText();
            if (className.indexOf(46) < 0) {
                if ("int".equals(className)) {
                    return Integer.TYPE;
                }
                if ("long".equals(className)) {
                    return Long.TYPE;
                }
                if ("float".equals(className)) {
                    return Float.TYPE;
                }
                if ("double".equals(className)) {
                    return Double.TYPE;
                }
                if ("boolean".equals(className)) {
                    return Boolean.TYPE;
                }
                if ("byte".equals(className)) {
                    return Byte.TYPE;
                }
                if ("char".equals(className)) {
                    return Character.TYPE;
                }
                if ("short".equals(className)) {
                    return Short.TYPE;
                }
                if ("void".equals(className)) {
                    return Void.TYPE;
                }
            }
            try {
                return Class.forName(jp.getText());
            } catch (Throwable e) {
                throw ctxt.instantiationException(this._valueClass, e);
            }
        }
        throw ctxt.mappingException(this._valueClass, curr);
    }
}
