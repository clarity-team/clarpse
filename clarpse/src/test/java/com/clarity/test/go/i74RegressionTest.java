package com.clarity.test.go;

import com.clarity.compiler.Lang;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.test.ClarpseTestUtil;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * https://github.com/Zir0-93/clarpse/issues/74
 */
public class i74RegressionTest {

    private static OOPSourceCodeModel generatedSourceModel;

    @Test
    public void shouldNotThrowStackOverflowException() throws Exception {
        generatedSourceModel = ClarpseTestUtil.sourceCodeModel("/go-master.zip", Lang.GOLANG);
        assertTrue(true);
    }
}