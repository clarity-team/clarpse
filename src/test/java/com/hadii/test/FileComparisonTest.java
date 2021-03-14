package com.hadii.test;
import static org.junit.Assert.assertTrue;

import com.hadii.clarpse.compiler.ProjectFile;
import org.junit.BeforeClass;
import org.junit.Test;


public class FileComparisonTest {

    private static ProjectFile fileA;
    private static String rawFileAName = "fileA";
    private static String rawFileAContent = "Ain't nobody got time for that";

    private static ProjectFile fileB;
    private static String rawFileBName = "fileB";
    private static String rawFileBContent = "Ain't nobody got time for that";

    @BeforeClass
    public static void setup() {

        fileA = new ProjectFile(rawFileAName, rawFileAContent);
        fileB = new ProjectFile(rawFileBName, rawFileBContent);

    }

    @Test
    public void testRawFileAEqualsRawFileBIsFalse() {
        assertTrue(!fileA.equals(fileB));
    }

    @Test
    public void testRawFileBEqualsRawFileAIsFalse() {
        assertTrue(!fileB.equals(fileA));
    }

    @Test
    public void testRawFileAEqualsCopyIsTrue() {
        assertTrue(fileA.equals(fileA.copy()));
    }

    @Test
    public void testRawFileAHashCodeDoesNotEqualFileB() {
        assertTrue(fileA.hashCode() != fileB.hashCode());
    }

    @Test
    public void testRawFileAHashCodeEqualsCopy() {
        assertTrue(fileA.hashCode() == fileA.copy().hashCode());
    }

    @Test
    public void testRawFileBHashCodeDoesNotEqualFileA() {
        assertTrue(fileB.hashCode() != fileA.hashCode());
    }
}