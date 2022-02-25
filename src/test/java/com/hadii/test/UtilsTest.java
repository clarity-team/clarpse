package com.hadii.test;

import com.hadii.clarpse.CommonDir;
import com.hadii.clarpse.ResolvedRelativePath;
import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.reference.SimpleTypeReference;
import com.hadii.clarpse.sourcemodel.Component;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import com.hadii.clarpse.sourcemodel.Package;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class UtilsTest {

    @Test
    public void testResolvedDir() throws Exception {
        ResolvedRelativePath resolvedPath = new ResolvedRelativePath("/github/test/js/units/lol.js",
                "./../../http/./forks.js");
        assertTrue(resolvedPath.value().equals("/github/test/http"));
    }

    @Test
    public void testResolvedDirv2() throws Exception {
        ResolvedRelativePath resolvedPath = new ResolvedRelativePath("/github/test/js/units/lol.js", "./forks.js");
        assertTrue(resolvedPath.value().equals("/github/test/js/units"));
    }

    @Test(expected = Exception.class)
    public void testResolvedDirv3() throws Exception {
        new ResolvedRelativePath("/github/test/js/units/lol.js", "/forks.js");
    }

    @Test
    public void testResolvedDirv4() throws Exception {
        ResolvedRelativePath resolvedPath = new ResolvedRelativePath("/github/test/js/units/lol.js", "http/forks.js");
        assertTrue(resolvedPath.value().equals("/github/test/js/units/http"));
    }

    @Test
    public void testResolvedDirv5() throws Exception {
        ResolvedRelativePath resolvedPath = new ResolvedRelativePath("/lol.js", "./test/forks.js");
        assertTrue(resolvedPath.value().equals("/test"));
    }

    @Test
    public void testResolvedDirv6() throws Exception {
        ResolvedRelativePath resolvedPath = new ResolvedRelativePath("/lol.js", "forks.js");
        assertTrue(resolvedPath.value().equals("/"));
    }

    @Test
    public void testResolvedDirv7() throws Exception {
        ResolvedRelativePath resolvedPath = new ResolvedRelativePath("/src/test/foo/lol.js", "../../");
        assertTrue(resolvedPath.value().equals("/src"));
    }

    @Test
    public void testResolvedDirv8() throws Exception {
        ResolvedRelativePath resolvedPath = new ResolvedRelativePath("/src/test", "./../../");
        assertTrue(resolvedPath.value().equals("/"));
    }

    @Test
    public void cloneComponentInvocationCopyTest() throws Exception {
        Component aField = new Component();
        aField.setPkg(new Package("test", "com.test"));
        aField.setComponentName("classA.aField");
        aField.setComponentType(OOPSourceModelConstants.ComponentType.FIELD);
        aField.setName("aField");
        Component bField = new Component(aField);
        bField.insertComponentRef(new SimpleTypeReference("com.test.classB"));
        assert (aField.references().size() == 0);
    }

    @Test
    public void cloneComponentInvocationTest() throws Exception {
        Component aField = new Component();
        aField.setPkg(new Package("test", "com.test"));
        aField.setComponentName("classA.aField");
        aField.setComponentType(OOPSourceModelConstants.ComponentType.FIELD);
        aField.setName("aField");
        aField.insertComponentRef(new SimpleTypeReference("com.test.classB"));
        Component bField = new Component(aField);
        assert (bField.references().size() == 1);
    }

    @Test
    public void testSimpleCommonDirTest() throws Exception {
        String dirA = "/test/src/pkgs";
        String dirB = "/test/src/cuppy";
        assertEquals(new CommonDir(dirA, dirB).value(), "/test/src");
    }

    @Test
    public void testCommonDirWithInvalidDirProvided() throws Exception {
        String dirA = "/test/src/pkgs.go";
        String dirB = "/test/src/cuppy";
        assertEquals(new CommonDir(dirA, dirB).value(), "/test/src");
    }

    @Test
    public void testCommonDirWithRootLevelFile() throws Exception {
        String dirA = "/cakes.go";
        String dirB = "/test/src/cuppy/peeps.java";
        assertEquals(new CommonDir(dirA, dirB).value(), "/");
    }

    @Test
    public void testCommonDirWithRootDirProvided() throws Exception {
        String dirA = "/";
        String dirB = "/test/src/cuppy";
        assertEquals(new CommonDir(dirA, dirB).value(), "/");
    }

    @Test
    public void testCommonDirWithEmptyDirProvided() throws Exception {
        String dirA = "";
        String dirB = "/test/src/cuppy";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CommonDir(dirA, dirB).value();
        });
    }

    @Test
    public void testCommonDirWithSingleFilePath() throws Exception {
        String dirB = "/test/src/cuppy.js";
        assertEquals(new CommonDir(dirB).value(), "/test/src");
    }

    @Test
    public void testProjectFileThrowsOnAbsolutePath() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ProjectFile("../src/test", "");
        });
    }

    @Test
    public void testProjectFileAutoInsertsLeadingSlash() throws FileNotFoundException {
        assertEquals("/src/test", new ProjectFile("src/test", "").path());
    }
}
