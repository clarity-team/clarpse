package com.hadii.clarpse;

import com.hadii.clarpse.compiler.File;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.compiler.SourceFiles;
import com.hadii.clarpse.sourcemodel.Component;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ClarpseUtil {

    public static final int BUFFER_SIZE = 4096;

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

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
     * Gets a key from the given map using the given value corresponding to the key.
     * @param value String value whose key should be returned
     * @param map The Map to search for the value in
     * @return The key corresponding to the give value
     */
    public static Object getObjectFromStringObjectKeyValueMap(final String value, final Map<?, ?> map) {
        final Iterator<?> it = map.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry pair = (Map.Entry) it.next();
            if (pair.getValue().equals(value)) {
                return pair.getKey();
            }
        }
        return null;
    }

    private static String fromJavaToJson(final Serializable object, final boolean prettyPrint)
            throws JsonGenerationException, JsonMappingException, IOException {

        final byte[] bytes = JSON_MAPPER.writeValueAsBytes(object);
        return new String(bytes, "UTF-8");
    }

    public static String fromJavaToJson(final Serializable object)
            throws JsonGenerationException, JsonMappingException, IOException {
        return fromJavaToJson(object, false);
    }

    @SuppressWarnings("unchecked")
    public static Object fromJsonToJava(final String json, final Class type)
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

    private static List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        try (InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }

        return filenames;
    }

    private static InputStream getResourceAsStream(String resource) {
        final InputStream in = getContextClassLoader().getResourceAsStream(resource);

        if (in == null) {
            return ClarpseUtil.class.getResourceAsStream(resource);
        } else {
            return in;
        }
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static SourceFiles javaParseRequestContentObjFromResourceDir(String dir) throws IOException {

        SourceFiles req = new SourceFiles(Lang.JAVA);
        List<String> fileNames = getResourceFiles(dir);
        for (String s : fileNames) {
            req.insertFile(new File(s, IOUtils.toString(ClarpseUtil.class.getResourceAsStream(dir + s), "UTF-8")));
        }
        return req;
    }

    public static SourceFiles parseRequestContentObjFromResourceDir(String dir, Lang java) throws IOException {

        SourceFiles req = new SourceFiles(java);
        List<String> fileNames = getResourceFiles(dir);
        for (String s : fileNames) {
            req.insertFile(new File(s, IOUtils.toString(ClarpseUtil.class.getResourceAsStream(dir + s), "UTF-8")));
        }
        return req;
    }
}
