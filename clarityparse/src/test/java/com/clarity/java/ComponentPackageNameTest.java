package com.clarity.java;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ParseService;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Tests to ensure package name attribute of parsed components are correct.
 *
 * @author Muntazir Fadhel
 */
public class ComponentPackageNameTest {

    private static String codeString = "package com.clarity.test;   class SampleJavaClass {  private String sampleClassField;  }";
    private static String pkgName = "com.clarity.test";
    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static final void parseJavaSourceFile() throws Exception {
        final ParseRequestContent rawData = new ParseRequestContent();
        rawData.setLanguage(Lang.JAVA);
        rawData.insertFile(new RawFile("file1", codeString));
        final ParseService parseService = new ParseService();
        generatedSourceModel = parseService.parseProject(rawData);
    }

    @Test
    public final void testAccuratePackageNames() throws Exception {
        boolean isCorrectPckgName = true;
        final LinkedHashMap<?, ?> tempComponentList = new LinkedHashMap<Object, Object>(
                generatedSourceModel.getComponents());
        final Iterator<?> it = tempComponentList.entrySet().iterator();
        while (it.hasNext()) {
            final Entry<?, ?> pair = (Entry<?, ?>) it.next();
            final Component tempComponent = (Component) pair.getValue();
            if (!tempComponent.getPackageName().contains(pkgName)) {
                isCorrectPckgName = false;
            }
            it.remove();
        }
        Assert.assertTrue(isCorrectPckgName);
    }
}
