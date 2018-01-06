package com.clarity.test.go;

import com.clarity.compiler.Lang;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import com.clarity.test.ClarpseTestUtil;
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
                .componentInvocations(ComponentInvocations.IMPLEMENTATION).get(0).invokedComponent()
                .equals("pilot.model.ServiceAccounts"));
    }

    /**
     * func (sa *serviceAccounts) GetIstioServiceAccounts(hostname string, ports []string) []string {
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
                .componentInvocations(ComponentInvocations.EXTENSION).get(0).invokedComponent()
                .equals("tests.integration.framework.TestEnv"));
    }

    @Test
    public void spotCheckStructComponentInvocations() {
        assertTrue(generatedSourceModel.getComponent("mixer.pkg.pool.GoroutinePool").invocations().toString().equals(
                "[TypeDeclaration:mixer.pkg.pool.WorkFunc, TypeDeclaration:sync.WaitGroup, TypeDeclaration:bool]"));
    }

    @Test
    public void spotCheckStructComponentInvocationsv2() {
        assertTrue(generatedSourceModel.getComponent("mixer.pkg.aspect.quotasExecutor").invocations().toString().equals(
                "[TypeDeclaration:mixer.pkg.aspect.quotasManager, TypeDeclaration:mixer.pkg.adapter.QuotasAspect, TypeDeclaration:string, TypeDeclaration:mixer.pkg.aspect.quotaInfo, TypeImplementation:mixer.pkg.aspect.QuotaExecutor]"));
    }

    @Test
    public void spotCheckStructModifier() {
        assertTrue(generatedSourceModel.getComponent("mixer.pkg.aspect.quotasExecutor").modifiers().size() == 1
                && generatedSourceModel.getComponent("mixer.pkg.aspect.quotasExecutor").modifiers()
                        .contains("private"));
    }

    @Test
    public void spotCheckInterfaceImports() {
        assertTrue(generatedSourceModel.getComponent("broker.pkg.platform.kube.crd.IstioObject").imports().toString()
                .equals("[fmt, os, mixer.pkg.il.runtime, github.com.golang.glog, github.com.hashicorp.go-multierror, k8s.io.apiextensions-apiserver.pkg.apis.apiextensions.v1beta1, k8s.io.apiextensions-apiserver.pkg.client.clientset.clientset, k8s.io.apimachinery.pkg.api.errors, k8s.io.apimachinery.pkg.apis.meta.v1, k8s.io.apimachinery.pkg.runtime, k8s.io.apimachinery.pkg.runtime.schema, k8s.io.apimachinery.pkg.runtime.serializer, k8s.io.apimachinery.pkg.util.wait, k8s.io.client-go.plugin.pkg.client.auth.gcp, k8s.io.client-go.plugin.pkg.client.auth.oidc, k8s.io.client-go.rest, k8s.io.client-go.tools.clientcmd, broker.pkg.model.config]"));
    }

    @Test
    public void spotCheckStructDoc() {
        assertTrue(generatedSourceModel.getComponent("broker.pkg.platform.kube.crd.Client").comment().trim()
                .equals("Client is a basic REST client for CRDs implementing config store"));
    }

    @Test
    public void spotCheckStructFieldDoc() {
        assertTrue(generatedSourceModel.getComponent("broker.pkg.platform.kube.crd.Client.restconfig").comment().trim()
                .equals("restconfig for REST type descriptors"));
    }

    @Test
    public void spotCheckStructFuncDoc() {
        assertTrue(generatedSourceModel.getComponent("broker.pkg.platform.kube.crd.Client.RegisterResources() : (error)").comment()
                .trim().equals("RegisterResources sends a request to create CRDs and waits for them to initialize"));
    }
}