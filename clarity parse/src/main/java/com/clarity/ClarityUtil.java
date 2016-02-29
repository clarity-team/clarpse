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
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Muntazir Fadhel
 */
public final class ClarityUtil {

    /**
     *
     */
    public static final int BUFFER_SIZE = 4096;

    /**
     * Convert object to JSON String.
     *
     * @param object
     *            to serialize to string
     * @param prettyPrint
     *            true when output should be pretty printed.
     * @return String serialized object
     * @throws JsonGenerationException
     *             exception
     * @throws JsonMappingException
     *             exception
     * @throws IOException
     *             exception
     */
    public static String fromJavaToJson(final Serializable object, final boolean prettyPrint)
            throws JsonGenerationException, JsonMappingException, IOException {
        final ObjectMapper jsonMapper = new ObjectMapper();

        jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, prettyPrint);
        jsonMapper.setVisibilityChecker(jsonMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        final String str = jsonMapper.writeValueAsString(object);
        return str;
    }

    /**
     * Utility method to convert a java object to a JSON String.
     *
     * @param object
     *            the object to be converted
     * @return JSON String
     * @throws Exception
     *             when conversion encounters an error.
     */
    public static String fromJavaToJson(final Serializable object) throws Exception {
        return fromJavaToJson(object, false);
    }

    /**
     * Convert a JSON string to an object.
     *
     * @param json
     *            json to convert
     * @param type
     *            type
     * @return java object deserialized from json input
     * @throws JsonParseException
     *             exception
     * @throws JsonMappingException
     *             exception
     * @throws IOException
     *             exception
     */
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

    /**
     * @param componentName
     *            Component hierarchical name.
     * @return parent component name
     */
    public static String getParentComponentUniqueName(final String componentName) {

        final int lastPeriod = componentName.lastIndexOf(".");
        final String currParentClassName = componentName.substring(0, lastPeriod);
        return currParentClassName;
    }

    /**
     *
     */
    private ClarityUtil() {

    }

    /**
     * Gets a general object key from a given map using the given value.
     *
     * @param value
     *            value of the pair
     * @param hashMap
     *            Map to search in
     * @return object key of the pair
     */
    public static Object getObjectFromStringObjectKeyValueMap(final String value, final Map<?, ?> hashMap) {
        final Iterator<?> it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            final Map.Entry pair = (Map.Entry) it.next();
            if (pair.getValue().equals(value)) {
                return pair.getKey();
            }
        }
        return null;
    }
}
