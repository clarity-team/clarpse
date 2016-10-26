package com.clarity;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.clarity.sourcemodel.Component;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

/**
 * @author Muntazir Fadhel
 */
public final class ClarpseUtil {

    public static final int   BUFFER_SIZE = 4096;

    static final ObjectMapper JSON_MAPPER  = new ObjectMapper();;
    static {
        JSON_MAPPER.setSerializationInclusion(Include.NON_NULL);
        JSON_MAPPER.registerModule(new AfterburnerModule());
        JSON_MAPPER.setVisibilityChecker(JSON_MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }

    private ClarpseUtil() {

    }

    /**
     * Gets a general object key from a given map using the given value.
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

        final byte[] bytes = JSON_MAPPER.writeValueAsBytes(object);
        return new String(bytes, "UTF-8");
    }

    public static String fromJavaToJson(final Serializable object)
            throws JsonGenerationException, JsonMappingException, IOException {
        return fromJavaToJson(object, false);
    }

    @SuppressWarnings("unchecked")
    public static Object fromJsonToJava(final String json, @SuppressWarnings("rawtypes") final Class type)
            throws JsonParseException, JsonMappingException, IOException {
        return JSON_MAPPER.readValue(json.getBytes("UTF-8"), type);
    }

    public static Component getParentMethodComponent(Component cmp, final Map<String, Component> components) {

        String currParentClassName = cmp.uniqueName();
        final int numberOfParentCmps = StringUtils.countMatches(cmp.componentName(), ".");
        for (int i = numberOfParentCmps; i > 0; i--) {
            currParentClassName = cmp.parentUniqueName();
            if (components.containsKey(currParentClassName)
                    && components.get(currParentClassName).componentType().isMethodComponent()) {
                break;
            }
        }
        return components.get(currParentClassName);
    }

    public static Component getParentBaseComponent(Component cmp, final Map<String, Component> map) {

        String currParentClassName = cmp.parentUniqueName();
        Component parent = map.get(currParentClassName);
        while (parent != null && !parent.componentType().isBaseComponent()) {
            currParentClassName = parent.parentUniqueName();
            parent = map.get(currParentClassName);
        }
        return parent;
    }
}
