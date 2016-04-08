import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.clarity.java.ComponentExistTest;
import com.clarity.java.ComponentPackageNameTest;
import com.clarity.java.ExternalTypeReferencesTest;
import com.clarity.java.JavaComponentTypeTest;

/**
 * Clarpse's main test suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ComponentExistTest.class,
    ComponentPackageNameTest.class,
    ExternalTypeReferencesTest.class,
        JavaComponentTypeTest.class
})
public class ClarpseTest {

}
