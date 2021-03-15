package com.hadii.test.go;

import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants.TypeReferences;
import com.hadii.test.ClarpseTestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GoLangModuleHandlingTest {

    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static void setup() throws Exception {
        generatedSourceModel = ClarpseTestUtil.sourceCodeModel("/magma-master.zip", Lang.GOLANG);
    }

    @Test
    public void spotCheckConfigTestStruct() {
        assertTrue(generatedSourceModel.containsComponent("service.config.ConfigTestStruct"));
    }


    @Test
    public void spotCheckTypeAndKeyStruct() {
        assertTrue(generatedSourceModel.containsComponent("storage.TypeAndKey"));
    }

    @Test
    public void spotCheckTypeAndKeyFieldVars() {
        assertTrue(generatedSourceModel.containsComponent("storage.TypeAndKey.Key"));
        assertTrue(generatedSourceModel.containsComponent("storage.TypeAndKey.Type"));
    }

    @Test
    public void spotCheckTypeAndKeyStructMethod() {
        assertTrue(generatedSourceModel.containsComponent("storage.TypeAndKey.String() : (string)"));
    }

    @Test
    public void spotCheckUUIDGeneratorStruct() {
        assertTrue(generatedSourceModel.containsComponent("storage.UUIDGenerator"));
    }

    @Test
    public void spotCheckUUIDGeneratorImplementsInterface() {
        assertTrue(generatedSourceModel.getComponent("storage.UUIDGenerator").get()
        .references(TypeReferences.IMPLEMENTATION).get(0).invokedComponent().equals("storage.IDGenerator"));
    }

}