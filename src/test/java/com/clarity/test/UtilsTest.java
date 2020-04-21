package com.clarity.test;

import com.clarity.ResolvedRelativePath;
import com.clarity.reference.SimpleTypeReference;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceModelConstants;
import org.junit.Test;

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
    public void cloneComponentInvocationCopyTest() throws Exception {
        Component aField = new Component();
        aField.setPackageName("com.test");
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
        aField.setPackageName("com.test");
        aField.setComponentName("classA.aField");
        aField.setComponentType(OOPSourceModelConstants.ComponentType.FIELD);
        aField.setName("aField");
        aField.insertComponentRef(new SimpleTypeReference("com.test.classB"));
        Component bField = new Component(aField);
        assert (bField.references().size() == 1);
    }
}
