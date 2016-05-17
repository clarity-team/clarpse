import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.clarity.java.ComponentExistTest;
import com.clarity.java.ParseComponentPackageAttributeTest;
import com.clarity.java.ParseComponentTypeTest;
import com.clarity.java.ParseExtendedAndImplementedAttributesTest;
import com.clarity.java.ParseExternalTypeReferencesAttributesTest;
import com.clarity.java.ParseLineNumberAttributeTest;

/**
 * Clarpse's main test suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ComponentExistTest.class,
    ParseComponentPackageAttributeTest.class,
    ParseExternalTypeReferencesAttributesTest.class,
    ParseComponentTypeTest.class,
    ParseLineNumberAttributeTest.class,
 ParseExtendedAndImplementedAttributesTest.class,
 RawFileComparisonTest.class,
 ComponentSourceFilePathTest.class
})
public class ClarpseTest {

}
