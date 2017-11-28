package com.clarity.test.go;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.test.ClarpseTestUtil;

public class GoLangSmokeTest {

    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static void setup() throws Exception {
        generatedSourceModel = ClarpseTestUtil.sourceCodeModel("tenta-browser", "tenta-dns");
    }

    @Test
    public void emptyTest() {
        assertTrue(generatedSourceModel.containsComponent("auth.tokenJWT"));
    }

}