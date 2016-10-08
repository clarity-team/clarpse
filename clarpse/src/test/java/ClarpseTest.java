import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.clarity.java.AccessModifiersTest;
import com.clarity.java.AnnotationInvocationTest;
import com.clarity.java.ChildComponentsTest;
import com.clarity.java.CommentsParsingTest;
import com.clarity.java.ComplexMethodInvocationsTest;
import com.clarity.java.ComponentExistTest;
import com.clarity.java.ComponentTypeTest;
import com.clarity.java.InvocationInheritanceTest;
import com.clarity.java.LineNumberAttributeTest;
import com.clarity.java.PackageAttributeTest;
import com.clarity.java.SimpleMethodInvocationsTest;
import com.clarity.java.TypeDeclarationTest;
import com.clarity.java.TypeExtensionTest;
import com.clarity.java.TypeImplementationTest;

/**
 * Clarpse's main test suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ ComponentExistTest.class, PackageAttributeTest.class, ComponentTypeTest.class,
        LineNumberAttributeTest.class, SimpleMethodInvocationsTest.class, RawFileComparisonTest.class,
        ComponentSourceFilePathTest.class, ComplexMethodInvocationsTest.class, AnnotationInvocationTest.class,
        TypeExtensionTest.class, TypeImplementationTest.class, TypeDeclarationTest.class, ChildComponentsTest.class,
        AccessModifiersTest.class, CommentsParsingTest.class, InvocationInheritanceTest.class })
public class ClarpseTest {

}
