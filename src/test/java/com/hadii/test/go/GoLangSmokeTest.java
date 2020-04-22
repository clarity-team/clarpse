package com.hadii.test.go;

import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants.TypeReferences;
import com.hadii.test.ClarpseTestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GoLangSmokeTest {

    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static void setup() throws Exception {
        generatedSourceModel = ClarpseTestUtil.sourceCodeModel("/istio-master.zip", Lang.GOLANG);
    }

    @Test
    public void spotCheckInteface() {
        assertTrue(generatedSourceModel.containsComponent("tests.integration.framework.Component"));
    }

    @Test
    public void spotCheckIntefacev2() {
        assertTrue(generatedSourceModel.containsComponent("pilot.model.ServiceAccounts"));
    }

    @Test
    public void spotCheckStructImplementsInterface() {
        assertTrue(generatedSourceModel.getComponent("pilot.platform.eureka.serviceAccounts")
                .get().references(TypeReferences.IMPLEMENTATION).get(0).invokedComponent()
                .equals("pilot.model.ServiceAccounts"));
    }

    /**
     * func (sa *serviceAccounts) GetIstioServiceAccounts(hostname value, ports []value) []value {
     * return nil
     * }
     */
    @Test
    public void spotCheckStructMethodv2() {
        assertTrue(generatedSourceModel
                .containsComponent("pilot.platform.eureka.serviceAccounts.GetIstioServiceAccounts(string, []string) : ([]string)"));
    }

    /**
     * Start() error
     */
    @Test
    public void spotCheckInterfaceMethod() {
        assertTrue(generatedSourceModel.containsComponent("tests.integration.framework.Component.Start() : (error)"));
    }

    @Test
    public void spotCheckStruct() {
        assertTrue(generatedSourceModel.containsComponent("tests.integration.framework.IstioTestFramework"));
    }

    @Test
    public void spotCheckStructField() {
        assertTrue(generatedSourceModel.containsComponent("tests.integration.framework.IstioTestFramework.Components"));
    }

    @Test
    public void spotCheckStructMethod() {
        assertTrue(generatedSourceModel.containsComponent("tests.integration.framework.IstioTestFramework.SetUp() : (error)"));
    }

    @Test
    public void spotCheckStructv2() {
        assertTrue(generatedSourceModel.containsComponent("tests.integration.environment.AppOnlyEnv"));
    }

    @Test
    public void spotCheckStructExtension() {
        assertTrue(generatedSourceModel.getComponent("tests.integration.environment.AppOnlyEnv")
                .get().references(TypeReferences.EXTENSION).get(0).invokedComponent()
                .equals("tests.integration.framework.TestEnv"));
    }

    @Test
    public void spotCheckStructcomponentReferences() {
        assertTrue(generatedSourceModel.getComponent("mixer.pkg.pool.GoroutinePool").get().references().toString().equals(
                "[SimpleTypeReference:mixer.pkg.pool.WorkFunc, SimpleTypeReference:sync.WaitGroup, SimpleTypeReference:bool, SimpleTypeReference:int]"));
    }

    @Test
    public void spotCheckStructcomponentReferencesv2() {
        assertTrue(generatedSourceModel.getComponent("mixer.pkg.aspect.quotasExecutor").get().references().toString().equals(
                "[SimpleTypeReference:mixer.pkg.aspect.quotasManager, SimpleTypeReference:mixer.pkg.adapter.QuotasAspect, SimpleTypeReference:string, SimpleTypeReference:mixer.pkg.aspect.quotaInfo, SimpleTypeReference:mixer.pkg.attribute.Bag, SimpleTypeReference:mixer.pkg.expr.Evaluator, SimpleTypeReference:mixer.pkg.aspect.QuotaMethodArgs, SimpleTypeReference:mixer.pkg.adapter.QuotaResultLegacy]"));
    }

    @Test
    public void spotCheckStructModifier() {
        assertTrue(generatedSourceModel.getComponent("mixer.pkg.aspect.quotasExecutor").get().modifiers().size() == 1
                && generatedSourceModel.getComponent("mixer.pkg.aspect.quotasExecutor").get().modifiers()
                        .contains("private"));
    }

    @Test
    public void spotCheckInterfaceImports() {
        assertTrue(generatedSourceModel.getComponent("broker.pkg.platform.kube.crd.IstioObject").get().imports().toString()
                .equals("[fmt, os, mixer.pkg.il.runtime, github.com.golang.glog, github.com.hashicorp.go-multierror, k8s.io.apiextensions-apiserver.pkg.apis.apiextensions.v1beta1, k8s.io.apiextensions-apiserver.pkg.client.clientset.clientset, k8s.io.apimachinery.pkg.api.errors, k8s.io.apimachinery.pkg.apis.meta.v1, k8s.io.apimachinery.pkg.runtime, k8s.io.apimachinery.pkg.runtime.schema, k8s.io.apimachinery.pkg.runtime.serializer, k8s.io.apimachinery.pkg.util.wait, k8s.io.client-go.plugin.pkg.client.auth.gcp, k8s.io.client-go.plugin.pkg.client.auth.oidc, k8s.io.client-go.rest, k8s.io.client-go.tools.clientcmd, broker.pkg.model.config]"));
    }

    @Test
    public void spotCheckStructDoc() {
        assertTrue(generatedSourceModel.getComponent("broker.pkg.platform.kube.crd.Client").get().comment().trim()
                .equals("Client is a basic REST client for CRDs implementing config store"));
    }

    @Test
    public void spotCheckStructFieldDoc() {
        assertTrue(generatedSourceModel.getComponent("broker.pkg.platform.kube.crd.Client.restconfig").get().comment().trim()
                .equals("restconfig for REST type descriptors"));
    }

    @Test
    public void spotCheckStructFuncDoc() {
        assertTrue(generatedSourceModel.getComponent("broker.pkg.platform.kube.crd.Client.RegisterResources() : (error)").get().comment()
                .trim().equals("RegisterResources sends a request to create CRDs and waits for them to initialize"));
    }
}