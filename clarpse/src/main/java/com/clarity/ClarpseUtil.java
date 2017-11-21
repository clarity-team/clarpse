package com.clarity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.Component;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

@SuppressWarnings("deprecation")
public final class ClarpseUtil {

    public static final int BUFFER_SIZE = 4096;

    static final ObjectMapper JSON_MAPPER = new ObjectMapper();;
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

    public static ParseRequestContent javaParseRequestContentObjFromResourceDir(String dir) throws IOException {

        ParseRequestContent req = new ParseRequestContent(Lang.JAVA);
        List<String> fileNames = getResourceFiles(dir);
        for (String s : fileNames) {
            req.insertFile(new RawFile(s, IOUtils.toString(ClarpseUtil.class.getResourceAsStream(dir + s), "UTF-8")));
        }
        return req;
    }

    public static ParseRequestContent parseRequestContentObjFromResourceDir(String dir, Lang java) throws IOException {

        ParseRequestContent req = new ParseRequestContent(java);
        List<String> fileNames = getResourceFiles(dir);
        for (String s : fileNames) {
            req.insertFile(new RawFile(s, IOUtils.toString(ClarpseUtil.class.getResourceAsStream(dir + s), "UTF-8")));
        }
        return req;
    }

    public static List<String> extractDocTypeMentions(String docComment) {
        List<String> docTypeMentions = new ArrayList<String>();
        Pattern linkPattern = Pattern.compile("\\{\\@link (.*?)\\}");
        Pattern linkPlainPattern = Pattern.compile("\\{\\@linkplain (.*?)\\}");

        Matcher matchPattern = linkPattern.matcher(docComment);
        while (matchPattern.find()) {
            docTypeMentions.add(matchPattern.group(1));
        }
        matchPattern = linkPlainPattern.matcher(docComment);
        while (matchPattern.find()) {
            docTypeMentions.add(matchPattern.group(1));
        }
        // we only consider doc links to other classes, interfaces, etc... (no methods
        // or variables)
        List<String> invalidMentions = new ArrayList<String>();
        docTypeMentions.forEach(mention -> {
            if (mention.matches(".*[#/:\\[\\]\\(\\)].*")) {
                invalidMentions.add(mention);
            }
        });
        docTypeMentions.removeAll(invalidMentions);
        return docTypeMentions;
    }
}
