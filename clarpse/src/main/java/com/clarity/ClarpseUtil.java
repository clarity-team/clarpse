package com.clarity;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Muntazir Fadhel
 */
public final class ClarpseUtil {

    public static final int BUFFER_SIZE = 4096;

    private ClarpseUtil() {

    }

    /**
     * Gets a general object key from a given map using the given value.
     *
     */
    public static Object getObjectFromStringObjectKeyValueMap(final String value, final Map<?, ?> map) {
        final Iterator<?> it = map.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            final Map.Entry pair = (Map.Entry) it.next();
            if (pair.getValue().equals(value)) {
                return pair.getKey();
            }
        }
        return null;
    }

    public static String fromJavaToJson(final Serializable object, final boolean prettyPrint)
            throws JsonGenerationException, JsonMappingException, IOException {
        final ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.setVisibilityChecker(jsonMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        final String str = jsonMapper.writeValueAsString(object);
        return str;
    }

    public static String fromJavaToJson(final Serializable object) throws JsonGenerationException,
    JsonMappingException, IOException {
        return fromJavaToJson(object, false);
    }

    @SuppressWarnings("unchecked")
    public static Object fromJsonToJava(final String json, @SuppressWarnings("rawtypes") final Class type)
            throws JsonParseException, JsonMappingException, IOException {
        final ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.setVisibilityChecker(jsonMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return jsonMapper.readValue(json, type);
    }
}
