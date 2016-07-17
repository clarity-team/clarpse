import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.clarity.java.ComponentExistTest;
import com.clarity.java.ParseComponentPackageAttributeTest;
import com.clarity.java.ParseComponentTypeTest;
import com.clarity.java.ParseLocalMethodInvocationsTest;
import com.clarity.java.ParseLineNumberAttributeTest;
import com.clarity.java.ParseMethodInvocationsTest;

/**
 * Clarpse's main test suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ComponentExistTest.class,
    ParseComponentPackageAttributeTest.class,
    ParseComponentTypeTest.class,
    ParseLineNumberAttributeTest.class,
    ParseLocalMethodInvocationsTest.class,
    RawFileComparisonTest.class,
    ComponentSourceFilePathTest.class,
    ParseMethodInvocationsTest.class
})
public class ClarpseTest {

}
