package com.hadii.test.java;

import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.reference.SimpleTypeReference;
import com.hadii.clarpse.reference.TypeExtensionReference;
import com.hadii.clarpse.reference.TypeImplementationReference;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import com.hadii.test.ClarpseTestUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SmokeTest {

    private static OOPSourceCodeModel generatedSourceModel;

    @BeforeClass
    public static void setup() throws Exception {
        generatedSourceModel = ClarpseTestUtil.sourceCodeModel(
                "/junit5-main.zip", Lang.JAVA);
    }

    @Test
    public void spotCheckClass() {
        Assert.assertTrue(generatedSourceModel.containsComponent(
                "org.junit.jupiter.api.AssertNotNull"));
    }

    @Test
    public void spotCheckClassExtension() {
        Assert.assertTrue(generatedSourceModel.getComponent(
                "org.junit.platform.engine.discovery.ExcludeClassNameFilter")
                                              .get().references(OOPSourceModelConstants.TypeReferences.EXTENSION)
                                              .contains(new TypeExtensionReference(
                                                      "org.junit.platform.engine.discovery" +
                                                              ".AbstractClassNameFilter")));
    }

    @Test
    public void spotCheckClassDocs() {
        Assert.assertTrue(generatedSourceModel.getComponent(
                "org.junit.platform.launcher.LauncherDiscoveryListener")
                                              .get().comment().equals(
                                                      "/**\n" +
                                                              " * Register a concrete " +
                                                              "implementation of this interface " +
                                                              "with a\n" +
                                                              " * {@link org.junit.platform" +
                                                              ".launcher.core" +
                                                              ".LauncherDiscoveryRequestBuilder} " +
                                                              "or\n" +
                                                              " * {@link Launcher} to be notified" +
                                                              " of events that occur during test " +
                                                              "discovery.\n" +
                                                              " *\n" +
                                                              " * <p>All methods in this " +
                                                              "interface have empty " +
                                                              "<em>default</em> implementations" +
                                                              ".\n" +
                                                              " * Concrete implementations may " +
                                                              "therefore override one or more of " +
                                                              "these methods\n" +
                                                              " * to be notified of the selected " +
                                                              "events.\n" +
                                                              " *\n" +
                                                              " * <p>JUnit provides default " +
                                                              "implementations that are created " +
                                                              "via the factory\n" +
                                                              " * methods in\n" +
                                                              " * {@link org.junit.platform" +
                                                              ".launcher.listeners.discovery" +
                                                              ".LauncherDiscoveryListeners}.\n" +
                                                              " *\n" +
                                                              " * <p>The methods declared in this" +
                                                              " interface are called by the " +
                                                              "{@link Launcher}\n" +
                                                              " * created via the {@link org" +
                                                              ".junit.platform.launcher.core" +
                                                              ".LauncherFactory}\n" +
                                                              " * during test discovery.\n" +
                                                              " *\n" +
                                                              " * @see org.junit.platform" +
                                                              ".launcher.listeners.discovery" +
                                                              ".LauncherDiscoveryListeners\n" +
                                                              " * @see LauncherDiscoveryRequest" +
                                                              "#getDiscoveryListener()\n" +
                                                              " * @see org.junit.platform" +
                                                              ".launcher.core.LauncherConfig" +
                                                              ".Builder" +
                                                              "#addLauncherDiscoveryListeners\n" +
                                                              " * @since 1.6\n" +
                                                              " */\n"
                ));
    }

    @Test
    public void spotCheckClassImplementation() {
        Assert.assertTrue(generatedSourceModel.getComponent(
                "org.junit.platform.engine.discovery.ExcludePackageNameFilter")
                                              .get().references(OOPSourceModelConstants.TypeReferences.IMPLEMENTATION)
                                              .contains(new TypeImplementationReference(
                                                      "org.junit.platform.engine.discovery" +
                                                              ".PackageNameFilter")));
    }

    @Test
    public void spotCheckMethod() {
        Assert.assertTrue(generatedSourceModel.containsComponent(
                "org.junit.platform.engine.FilterResult.included(String)"));
    }

    @Test
    public void spotCheckSingletonListOriginalClassTypeReference() {
        Assert.assertTrue(generatedSourceModel.getComponent(
                "example.util.ListWriter").get()
                                              .references(OOPSourceModelConstants.TypeReferences.SIMPLE)
                                              .contains(new SimpleTypeReference("java.util.Collections"))
        );
    }
}
