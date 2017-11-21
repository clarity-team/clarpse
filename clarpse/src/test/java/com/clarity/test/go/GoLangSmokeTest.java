package com.clarity.test.go;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.ClarpseUtil;
import com.clarity.parser.Lang;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.test.ClarpseTestUtil;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class GoLangSmokeTest {

    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static void setup() throws Exception {
        generatedSourceModel = ClarpseTestUtil.sourceCodeModel("/caddy-master.zip", Lang.GOLANG);
        PrintWriter writer = new PrintWriter("caddy-master-parse-summary.txt");
        generatedSourceModel.getComponents().entrySet().forEach(entry -> {
            try {
                writer.println(ClarpseUtil.fromJavaToJson(entry.getValue(), true));
            } catch (JsonGenerationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JsonMappingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        writer.close();
    }

    @Test
    public void emptyTest() {
        assertTrue(generatedSourceModel.containsComponent("auth.tokenJWT"));
    }

}