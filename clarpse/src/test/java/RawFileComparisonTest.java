import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.parser.RawFile;


public class RawFileComparisonTest {

    private static RawFile rawFileA;
    private static String rawFileAName = "rawFileA";
    private static String rawFileAContent = "Ain't nobody got time for that";

    private static RawFile rawFileB;
    private static String rawFileBName = "rawFileB";
    private static String rawFileBContent = "Ain't nobody got time for that";

    @BeforeClass
    public static void setup() {

        rawFileA = new RawFile(rawFileAName, rawFileAContent);
        rawFileB = new RawFile(rawFileBName, rawFileBContent);

    }

    @Test
    public void testRawFileAEqualsRawFileBIsFalse() {
        assertTrue(!rawFileA.equals(rawFileB));
    }

    @Test
    public void testRawFileBEqualsRawFileAIsFalse() {
        assertTrue(!rawFileB.equals(rawFileA));
    }

    @Test
    public void testRawFileAEqualsCopyIsTrue() {
        assertTrue(rawFileA.equals(rawFileA.copy()));
    }

    @Test
    public void testRawFileAHashCodeDoesNotEqualFileB() {
        assertTrue(rawFileA.hashCode() != rawFileB.hashCode());
    }

    @Test
    public void testRawFileAHashCodeEqualsCopy() {
        assertTrue(rawFileA.hashCode() == rawFileA.copy().hashCode());
    }

    @Test
    public void testRawFileBHashCodeDoesNotEqualFileA() {
        assertTrue(rawFileB.hashCode() != rawFileA.hashCode());
    }
}