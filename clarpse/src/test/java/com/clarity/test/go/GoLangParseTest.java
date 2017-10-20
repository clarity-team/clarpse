package com.clarity.test.go;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;

public class GoLangParseTest {

    @Test
    public void testPackageGroup() throws Exception {

        final String code = "package main\ntype person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").packageName().equals("main"));
    }

    @Test
    public void testShortImportType() throws Exception {

        final String code = "package main\n import\"fmt\"\n type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").imports().get(0).equals("fmt"));
    }

    @Test
    public void testLongImportType() throws Exception {

        final String code = "package main\n import m \"fmt\"\n type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").imports().get(0).equals("fmt"));
    }

    @Test
    public void testDotImportType() throws Exception {

        final String code = "package main\n import . \"fmt\"\n type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").imports().get(0).equals("fmt"));
    }

    @Test
    public void testParseGoStruct() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person"));
    }

    @Test
    public void testParseGoStructs() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {} type teacher struct{}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("main.person"));
        assertTrue(generatedSourceModel.containsComponent("main.teacher"));
    }

    @Test
    public void testParseGoStructPrivateVisibility() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {} type Teacher struct{}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").modifiers().contains("private"));
    }

    @Test
    public void testParseGoStructPublicVisibility() throws Exception {

        final String code = "package main\n import \"fmt\"\n /*test*/ type person struct {} type Teacher struct{}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.Teacher").modifiers().contains("public"));
    }

    @Test
    public void testParsedSingleLineStructDoc() throws Exception {

        final String code = "package main\n //test struct doc\n type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testParsMultiLineStructDoc() throws Exception {

        final String code = "package main\n //test struct\n// doc\n type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testParseMultiLineStructDocAfterAnotherStruct() throws Exception {

        final String code = "package main\n type animal struct {}\n//test struct\n// doc\n type person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testParseMultiLineStructDocSeparatedByEmptyLines() throws Exception {

        final String code = "package main\n//test struct\n// doc\n\n\ntype person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testParseSingleLineStructDocSeparatedByEmptyLines() throws Exception {

        final String code = "package main\n//test struct doc\n\n\n\ntype person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").comment().equals("test struct doc"));
    }

    @Test
    public void testGoStructHasCorrectSourceFileAttr() throws Exception {
        final String code = "package main\ntype person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").sourceFile().equals("person.go"));
    }

    @Test
    public void testGoStructHasCorrectComponentType() throws Exception {
        final String code = "package main\ntype person struct {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.GOLANG);
        rawData.insertFile(new RawFile("person.go", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("main.person").componentType() == ComponentType.CLASS);
    }

    @Test
    public void testIfParsedES6InstanceMethodExists() throws Exception {

        final String code = "class Polygon { say() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.say"));
    }

    @Test
    public void testIfParsedES6InstanceMethodDoc() throws Exception {

        final String code = "class Polygon { /** say doc \n comment */ say() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").comment().equals("/** say doc \n comment */"));
    }

    @Test
    public void testIfParsedES6InstanceMethodIsChildOfParentClass() throws Exception {

        final String code = "class Polygon { say() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").children().get(0).equals("Polygon.say"));
    }

    @Test
    public void testIfParsedES6InstanceMethodNameIsCorrect() throws Exception {

        final String code = "class Polygon { say() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").name().equals("say"));
    }

    @Test
    public void testIfParsedES6InstanceMethodComponentTypeIsCorrect() throws Exception {

        final String code = "class Polygon { say() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").componentType() == ComponentType.METHOD);
    }

    @Test
    public void testIfParsedES6StaticInstanceMethodExists() throws Exception {

        final String code = "class Polygon { static say() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.say"));
    }

    @Test
    public void testIfParsedES6StaticInstanceMethodAccessModifier() throws Exception {

        final String code = "class Polygon { static say() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").modifiers().contains("static"));
    }

    @Test
    public void testIfParsedES6StaticConstantModifier() throws Exception {

        final String code = "class Polygon { static constant1 = 33; }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constant1").modifiers().contains("static"));
    }

    @Test
    public void testIfParsedES6AsyncInstanceMethodExists() throws Exception {

        final String code = "class Polygon { static say() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.say"));
    }

    @Test
    public void testIfParsedES6InstanceMethodParamComponentExists() throws Exception {

        final String code = "class Polygon { constructor() {} call(height) {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.call.height"));
    }

    @Test
    public void testIfParsedES6InstanceMethodParamComponentsExists() throws Exception {

        final String code = "class Polygon { constructor() {} call(height, length, width) {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.call.height"));
        assertTrue(generatedSourceModel.containsComponent("Polygon.call.length"));
        assertTrue(generatedSourceModel.containsComponent("Polygon.call.width"));
    }

    @Test
    public void testIfParsedES6InstanceMethodParamComponentIsChildOfInstanceMethod() throws Exception {

        final String code = "class Polygon { constructor() {} say(height, length) {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").children().get(0).equals("Polygon.say.height"));
        assertTrue(generatedSourceModel.getComponent("Polygon.say").children().get(1).equals("Polygon.say.length"));
    }

    @Test
    public void testParsedES6InstanceMethodParamComponentType() throws Exception {

        final String code = "class Polygon { constructor() {} say(height) {}}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.height")
                .componentType() == ComponentType.METHOD_PARAMETER_COMPONENT);
    }

    @Test
    public void testIfParsedES6GetterMethodExists() throws Exception {

        final String code = "class Polygon { get height() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.get_height"));
    }

    @Test
    public void testIfParsedES6GetterIsChildOfParentClass() throws Exception {

        final String code = "class Polygon { get height() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").children().get(0).equals("Polygon.get_height"));
    }

    @Test
    public void testIfParsedES6SetterMethodComponentTypeIsCorrect() throws Exception {

        final String code = "class Polygon { set height(value) {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.set_height").componentType() == ComponentType.METHOD);
    }

    @Test
    public void testIfParsedES6SetterMethodExists() throws Exception {

        final String code = "class Polygon { set height(str) {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.set_height"));
    }

    @Test
    public void testIfParsedES6FieldVariableComponentExists() throws Exception {

        final String code = "class Polygon { constructor() {this.height = 4;} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.height"));
    }

    @Test
    public void testIfParsedES6FieldVariableComponentName() throws Exception {

        final String code = "class Polygon { constructor() {this.height = 4;} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").name().equals("height"));
    }

    @Test
    public void testIfParsedES6FieldVariableComponentType() throws Exception {

        final String code = "class Polygon { constructor() {this.height = 4;} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").componentType() == ComponentType.FIELD);
    }

    @Test
    public void testIfParsedES6FieldVariableMultipleComponentTypes() throws Exception {

        final String code = "class Polygon { constructor() {this.height = 4; this.width = false;} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.height")
                && generatedSourceModel.containsComponent("Polygon.width"));

        assertTrue(generatedSourceModel.getComponent("Polygon.width").declarationTypeSnippet().equals("Boolean"));
    }

    @Test
    public void testIfParsedES6FieldVariableIsChildOfClass() throws Exception {

        final String code = "class Polygon { constructor() {this.height = 4;} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").children().get(1).equals("Polygon.height"));
    }

    @Test
    public void testIfParsedES6FieldVariableBooleanValue() throws Exception {

        final String code = "class Polygon { constructor(height) {this.height = true;} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").value().equals("true"));
    }

    @Test
    public void testIfParsedES6FieldVariableStringValue() throws Exception {

        final String code = "class Polygon { constructor(height) {this.height = \"test\";} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").value().equals("test"));
    }

    @Test
    public void testIfParsedES6FieldVariableNumberValue() throws Exception {

        final String code = "class Polygon { constructor(height) {this.height = 56;} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").value().equals("56"));
    }

    @Test
    public void testIfParsedES6FieldVariableNumberDeclarationTypeSnippet() throws Exception {

        final String code = "class Polygon { constructor(height) {this.height = 56;} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").declarationTypeSnippet().equals("Number"));
    }

    @Test
    public void testIfParsedES6FieldVariableStringDeclarationTypeSnippet() throws Exception {

        final String code = "class Polygon { constructor(height) {this.height = \"test\";} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").declarationTypeSnippet().equals("String"));
    }

    @Test
    public void testIfParsedES6FieldVariableBooleanDeclarationTypeSnippet() throws Exception {

        final String code = "class Polygon { constructor(height) {this.height = false;} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").declarationTypeSnippet().equals("Boolean"));
    }

    @Test
    public void testIfParsedES6FieldVariableTypeInstantiation() throws Exception {

        final String code = "class React() {} class Polygon { constructor(height) {this.height = new React();} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").declarationTypeSnippet().equals("React"));
    }

    @Test
    public void testIfParsedES6FieldVariableTypeInstantiationWithValues() throws Exception {

        final String code = "class React() {} class Polygon { constructor(height) {this.height = new React(2,4,\"fe\");} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").declarationTypeSnippet().equals("React"));
    }

    @Test
    public void testIfParsedES6LocalVariableExists() throws Exception {

        final String code = "class React() {} class Polygon { say() { var test = new React(); }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.say.test"));
    }

    @Test
    public void testIfParsedES6LocalVariableComponentType() throws Exception {

        final String code = "class Polygon { say() { var test = new React(); }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test").componentType() == ComponentType.LOCAL);
    }

    @Test
    public void testIfParsedES6LocalLetVariableComponentType() throws Exception {

        final String code = "class Polygon { say() { let test = new React(); }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test").componentType() == ComponentType.LOCAL);
    }

    @Test
    public void testIfParsedES6LocalLetVariableComponentExists() throws Exception {

        final String code = "class Polygon { say() { let test = new React(); }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.say.test"));
    }

    @Test
    public void testIfParsedES6LocalLetVariableTypeDeclaration() throws Exception {

        final String code = "class Polygon { say() { let test = new React(); }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test")
                .componentInvocations(ComponentInvocations.INSTANTIATION).get(0).invokedComponent().equals("React"));
    }

    @Test
    public void testIfParsedES6LocalVariableIsChildOfParentMethod() throws Exception {

        final String code = "class Polygon { say() { var test = new React(); var lol = 4; }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").children().size() == 2);
        assertTrue(generatedSourceModel.getComponent("Polygon.say").children().get(1).equals("Polygon.say.lol"));
    }

    @Test
    public void testIfParsedES6LocalVariableTypeInstantiation() throws Exception {

        final String code = "class Polygon { say() { var test = new React(); var lol = 4; }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test")
                .componentInvocations(ComponentInvocations.INSTANTIATION).get(0).invokedComponent().equals("React"));
    }

    @Test
    public void testIfParsedMultipleES6LocalVariables() throws Exception {

        final String code = "class Polygon { say() { var test = new React(); var lol = 4; }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.say.test")
                && generatedSourceModel.containsComponent("Polygon.say.lol"));
    }

    @Test
    public void testIfParsedES6LocalVariableQualifiedTypeInstantiation() throws Exception {

        final String code = "class Polygon { say() { var test = new React.test(); var lol = 4; }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("React"));
    }

    @Test
    public void testIfParsedES6ConstructorLocalVar() throws Exception {

        final String code = "class Polygon { constructor() {  this.width = 4;  var test = new React(); } }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.constructor.test"));
    }

    @Test
    public void testIfParsedES6MethodTypeDeclaration() throws Exception {

        final String code = "class Polygon { constructor() {  new React().test(); } }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor")
                .componentInvocations(ComponentInvocations.INSTANTIATION).get(0).invokedComponent().equals("React"));
    }

    @Test
    public void testIfParsedES6MethodTypeDeclarationFromStaticMethodCall() throws Exception {

        final String code = "import React from '/test.js'; class Polygon { constructor() {  React.test(); } }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent().equals("React"));
    }
}
