package com.clarity.test;

import com.clarity.CommonDir;
import com.clarity.compiler.Lang;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class RegressionTest {

    /**
     * https://github.com/Zir0-93/clarpse/issues/74
     */
    @Test
    public void shouldNotThrowStackOverflowException() throws Exception {
        ClarpseTestUtil.sourceCodeModel("/go-master.zip", Lang.GOLANG);
    }


    @Test
    public void shouldNotArrayOutOfBoundsException() throws Exception {
        assertTrue(new CommonDir("/test/lol/cakes", "/").value().equalsIgnoreCase("/"));
    }
}