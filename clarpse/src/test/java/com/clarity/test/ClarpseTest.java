package com.clarity.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.clarity.test.java.AccessModifiersTest;
import com.clarity.test.java.AnnotationInvocationTest;
import com.clarity.test.java.ChildComponentsTest;
import com.clarity.test.java.CommentsParsingTest;
import com.clarity.test.java.ComponentExistTest;
import com.clarity.test.java.ComponentTypeTest;
import com.clarity.test.java.InvocationInheritanceTest;
import com.clarity.test.java.PackageAttributeTest;
import com.clarity.test.java.TypeDeclarationTest;
import com.clarity.test.java.TypeExtensionTest;
import com.clarity.test.java.TypeImplementationTest;

/**
 * Clarpse's main test suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ ComponentExistTest.class, PackageAttributeTest.class, ComponentTypeTest.class,
        RawFileComparisonTest.class, ComponentSourceFilePathTest.class, AnnotationInvocationTest.class,
        TypeExtensionTest.class, TypeImplementationTest.class, TypeDeclarationTest.class, ChildComponentsTest.class,
        AccessModifiersTest.class, CommentsParsingTest.class, InvocationInheritanceTest.class })
public class ClarpseTest {

}
