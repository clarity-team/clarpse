package com.clarity.java;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;

public class ComponentCodeTest {

    @Test
    public void testClassLevelCode() throws Exception {

        final String code = "/** lol */ package lol; import test; /** class comment */ public class Test { @Override Test(String str) { Object localVar;} @Deprecated interface Cakes { abstract void tester(); } }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("lol.Test").code().trim().replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase(code.trim().replaceAll("[\\n\\t ]", "")));
    }

    @Test
    public void testMethodLevelCode() throws Exception {

        final String code = "public class Test { /*lol*/@Override Test(String str) { Object localVar;} @Deprecated interface Cakes { abstract void tester(); } }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.Test(java.lang.String)").code().trim()
                .replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase(
                        "/*lol*/@Override Test(String str) { Object localVar;}".trim().replaceAll("[\\n\\t ]", "")));
    }

    @Test
    public void testMethodParamLevelCode() throws Exception {

        final String code = "public class Test { @Override Test(String str) { Object localVar;} @Deprecated interface Cakes { abstract void tester(); } }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.Test(java.lang.String).str").code().trim()
                .replaceAll("[\\n\\t\\r ]", "").equalsIgnoreCase("String str".trim().replaceAll("[\\n\\t ]", "")));
    }

    @Test
    public void testInterfaceLevelCode() throws Exception {

        final String code = "@Deprecated public interface Cakes { abstract void tester();  }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel
                .getComponent("Cakes")
                .code()
                .trim()
                .replaceAll("[\\n\\t\\r ]", "")
                .equalsIgnoreCase(
                        "@Deprecated public interface Cakes { abstract void tester(); }".trim().replaceAll("[\\n\\t ]",
                                "")));
    }

    @Test
    public void testInterfaceMethodLevelCode() throws Exception {

        final String code = "public class Test { @Override Test(String str) { Object localVar;} @Deprecated interface Cakes { abstract void tester(); } }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
        rawData.insertFile(new RawFile("file2.java", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Test.Cakes.tester()").code().trim().replaceAll("[\\n\\t ]", "")
                .equalsIgnoreCase("abstract void tester();".trim().replaceAll("[\\n\\t\\r ]", "")));
    }

}
