package com.clarity.java;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ParseService;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

public class ParseFileWithEmptyPkgDeclaration_i5_Test {

    private static File sampleSourceFile;

    @BeforeClass
    public static void setup() {
        final URL url = ParseFileWithEmptyPkgDeclaration_i5_Test.class.getResource("resources/i5_test.txt");
        sampleSourceFile = new File(url.getFile());
    }

    @Test
    public void parseTest() throws Exception {

        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile(sampleSourceFile));
        final ParseService parseService = new ParseService();
        final OOPSourceCodeModel generatedSourceModel = parseService.parseProject(rawData);
        assertTrue(generatedSourceModel.containsComponent("CaptureOutputTest"));
    }
}
