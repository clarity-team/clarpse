package com.clarity.test;

import com.clarity.TrimmedString;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TrimmedStringTest {

    @Test
    public void SimpleTrimmedStringTest() throws Exception {
        assertTrue(new TrimmedString("//test//", "/").value().equalsIgnoreCase("test"));
    }

    @Test
    public void SpaceTrimmedStringTest() throws Exception {
        assertTrue(new TrimmedString(" test  ", " ").value().equalsIgnoreCase("test"));
    }
}
