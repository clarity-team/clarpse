package com.clarity.java;

import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.ClarpseUtil;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ParseService;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

public class SkipAnnotationTypesTest {

    private static String code1 = "@javax.xml.bind.annotation.XmlSchema(namespace=W3CEndpointReference.NS,"
            + "location=\"http://www.w3.org/2006/03/addressing/ws-addr.xsd\") package java.lang.invoke;"
            + "import java.lang.annotation.ElementType;"
            + "import java.lang.annotation.Retention;"
            + "import java.lang.annotation.Target;"
            + "@Target({ElementType})"
            + "@interface VMCONSTANTPOOL_METHOD {"
            + "}";

    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static void setup() throws Exception {

        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);

        rawData.insertFile(new RawFile("file2", code1));
        final ParseService parseService = new ParseService();
        generatedSourceModel = parseService.parseProject(rawData);
        System.out.println(ClarpseUtil.fromJavaToJson(generatedSourceModel));
    }

    @Test
    public void skipAnnotationTypes() {

    }

}
