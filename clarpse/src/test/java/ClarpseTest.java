import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.clarity.java.ComponentExistTest;
import com.clarity.java.ComponentPackageNameTest;
import com.clarity.java.ExternalTypeReferencesTest;
import com.clarity.java.JavaComponentTypeTest;
import com.clarity.java.LineNumberTest;

/**
 * Clarpse's main test suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ComponentExistTest.class,
    ComponentPackageNameTest.class,
    ExternalTypeReferencesTest.class,
        JavaComponentTypeTest.class,
        LineNumberTest.class
})
public class ClarpseTest {

}
