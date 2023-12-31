package org.codehaus.jackson.map.ser;

import java.util.HashSet;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.type.JavaType;

@JacksonStdImpl
@Deprecated
public class MapSerializer extends org.codehaus.jackson.map.ser.std.MapSerializer {
    protected MapSerializer() {
        this((HashSet) null, null, null, false, null, null, null, null);
    }

    @Deprecated
    protected MapSerializer(HashSet<String> ignoredEntries, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts) {
        super(ignoredEntries, UNSPECIFIED_TYPE, valueType, valueTypeIsStatic, vts, null, null, null);
    }

    @Deprecated
    protected MapSerializer(HashSet<String> ignoredEntries, JavaType keyType, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts, JsonSerializer<Object> keySerializer, BeanProperty property) {
        super(ignoredEntries, keyType, valueType, valueTypeIsStatic, vts, keySerializer, null, property);
    }

    protected MapSerializer(HashSet<String> ignoredEntries, JavaType keyType, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, BeanProperty property) {
        super(ignoredEntries, keyType, valueType, valueTypeIsStatic, vts, keySerializer, valueSerializer, property);
    }
}
