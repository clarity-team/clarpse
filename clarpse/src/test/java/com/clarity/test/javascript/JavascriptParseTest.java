package com.clarity.test.javascript;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clarity.parser.ClarpseProject;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;

public class JavascriptParseTest {

    @Test
    public void testIfParsedES6ClassExists() throws Exception {

        final String code = "class Polygon extends Test {get prop() {return 'getter'; }}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon"));
    }

    @Test
    public void testIfParsedES6ClassDoc() throws Exception {

        final String code = "/**Test*/ class Polygon extends Test {get prop() {return 'getter'; }}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").comment().equals("/**Test*/"));
    }

    @Test
    public void testIfParsedES6ClassesExists() throws Exception {

        final String code = "class Polygon {} class LolCakes{}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon")
                && generatedSourceModel.containsComponent("LolCakes"));
    }

    @Test
    public void testIfParsedES6ClassExpressionComponentExists() throws Exception {

        final String code = "const test = class Polygon {};";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon"));
    }

    @Test
    public void testIfParsedES6ClassHasCorrectSourceFileAttr() throws Exception {

        final String code = "class Polygon {}";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").sourceFile().equals("polygon.js"));
    }

    @Test
    public void testIfParsedES6ClassHasCorrectComponentType() throws Exception {

        final String code = "\n\n class Polygon { }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").componentType() == ComponentType.CLASS);
    }

    @Test
    public void testIfParseES6ClassHasCorrectExtendsAttr() throws Exception {

        final String code = "class Polygon extends Shape { }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        // assert the Polygon class component has one type extension component
        // invocation
        assertTrue(generatedSourceModel.getComponent("Polygon").componentInvocations(ComponentInvocations.EXTENSION)
                .size() == 1);
        // assert the component being extended is the Shape class
        assertTrue(generatedSourceModel.getComponent("Polygon").componentInvocations(ComponentInvocations.EXTENSION)
                .get(0).invokedComponent().equals("Shape"));
    }

    @Test
    public void testIfParsedES6ConstructorFunctionExists() throws Exception {

        final String code = "class Polygon { constructor() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.constructor"));
    }

    @Test
    public void testIfParsedES6ConstructorDoc() throws Exception {

        final String code = "class Polygon { /** constructor doc */ constructor() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").comment().equals("/** constructor doc */"));
    }

    @Test
    public void testIfParsedES6ConstructorIsChildOfParentClass() throws Exception {

        final String code = "class Polygon { constructor() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").children().get(0).equals("Polygon.constructor"));
    }

    @Test
    public void testIfParsedES6ConstructorFunctionNameIsCorrect() throws Exception {

        final String code = "class Polygon { constructor() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").name().equals("constructor"));
    }

    @Test
    public void testIfParsedES6ConstructorComponentTypeIsCorrect() throws Exception {

        final String code = "class Polygon { constructor() {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(
                generatedSourceModel.getComponent("Polygon.constructor").componentType() == ComponentType.CONSTRUCTOR);
    }

    @Test
    public void testIfParsedES6ConstructorParamComponentExists() throws Exception {

        final String code = "class Polygon { constructor(height) {call();} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.constructor.height"));
    }

    @Test
    public void testIfParsedES6ConstructorParamComponentsExists() throws Exception {

        final String code = "class Polygon { constructor(height, length, width) {call();} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.constructor.height"));
        assertTrue(generatedSourceModel.containsComponent("Polygon.constructor.length"));
        assertTrue(generatedSourceModel.containsComponent("Polygon.constructor.width"));
    }

    @Test
    public void testIfParsedES6ConstructorParamComponentsIsChildOfConstructor() throws Exception {

        final String code = "class Polygon { constructor(height, length) {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").children().get(0)
                .equals("Polygon.constructor.height"));
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").children().get(1)
                .equals("Polygon.constructor.length"));
    }

    @Test
    public void testParsedES6ConstructorParamComponentType() throws Exception {

        final String code = "class Polygon { constructor(height) {} }";
        final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor.height")
                .componentType() == ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT);
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
