package com.clarity.test.javascript;

import com.clarity.compiler.ClarpseProject;
import com.clarity.compiler.Lang;
import com.clarity.compiler.RawFile;
import com.clarity.compiler.SourceFiles;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

@Ignore
public class JavascriptParseTest {

    @Test
    public void ES6ClassExists() throws Exception {

        final String code = "class Polygon extends Test {get prop() {return 'getter'; }}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon"));
    }

    @Test
    public void ES6ClassDoc() throws Exception {

        final String code = "/**Test*/ class Polygon extends Test {get prop() {return 'getter'; }}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").comment().equals("/**Test*/"));
    }

    @Test
    public void ES6ClassesExists() throws Exception {

        final String code = "class Polygon {} class LolCakes{}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon")
                && generatedSourceModel.containsComponent("LolCakes"));
    }

    @Test
    public void ES6ClassExpressionComponentExists() throws Exception {

        final String code = "const test = class Polygon {};";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon"));
    }

    @Test
    public void ES6ClassHasCorrectSourceFileAttr() throws Exception {

        final String code = "class Polygon {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").sourceFile().equals("polygon.js"));
    }

    @Test
    public void ES6ClassHasCorrectComponentType() throws Exception {

        final String code = "\n\n class Polygon { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").componentType() == ComponentType.CLASS);
    }

    @Test
    public void testIfParseES6ClassHasCorrectExtendsAttr() throws Exception {

        final String code = "class Polygon extends Shape { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
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
    public void testIfParseES6ClassHasCorrectExtendsAttrComplex() throws Exception {

        final String code = "import { Shape as Shape} from 'shape.js' \n class Polygon extends Shape { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
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
    public void ES6ConstructorFunctionExists() throws Exception {

        final String code = "class Polygon { constructor() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.constructor"));
    }

    @Test
    public void ES6ConstructorDoc() throws Exception {

        final String code = "class Polygon { /** constructor doc */ constructor() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").comment().equals("/** constructor doc */"));
    }

    @Test
    public void ES6ConstructorIsChildOfParentClass() throws Exception {

        final String code = "class Polygon { constructor() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").children().get(0).equals("Polygon.constructor"));
    }

    @Test
    public void ES6ConstructorFunctionNameIsCorrect() throws Exception {

        final String code = "class Polygon { constructor() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor").name().equals("constructor"));
    }

    @Test
    public void lineNumberTest() {
    }

    @Test
    public void fieldComponentValue() {
    }

    @Test
    public void methodComponentValue() {
    }

    @Test
    public void ES6ConstructorComponentTypeIsCorrect() throws Exception {

        final String code = "class Polygon { constructor() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(
                generatedSourceModel.getComponent("Polygon.constructor").componentType() == ComponentType.CONSTRUCTOR);
    }

    @Test
    public void ES6ConstructorParamComponentExists() throws Exception {

        final String code = "class Polygon { constructor(height) {call();} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.constructor.height"));
    }

    @Test
    public void ES6ConstructorParamComponentsExists() throws Exception {

        final String code = "class Polygon { constructor(height, length, width) {call();} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.constructor.height"));
        assertTrue(generatedSourceModel.containsComponent("Polygon.constructor.length"));
        assertTrue(generatedSourceModel.containsComponent("Polygon.constructor.width"));
    }

    @Test
    public void ES6ConstructorParamComponentsIsChildOfConstructor() throws Exception {

        final String code = "class Polygon { constructor(height, length) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
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
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor.height")
                .componentType() == ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT);
    }

    @Test
    public void ES6InstanceMethodExists() throws Exception {

        final String code = "class Polygon { say() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.say"));
    }

    @Test
    public void ES6InstanceMethodDoc() throws Exception {

        final String code = "class Polygon { /** say doc \n comment */ say() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").comment().equals("/** say doc \n comment */"));
    }

    @Test
    public void ES6InstanceMethodIsChildOfParentClass() throws Exception {

        final String code = "class Polygon { say() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").children().get(0).equals("Polygon.say"));
    }

    @Test
    public void ES6InstanceMethodNameIsCorrect() throws Exception {

        final String code = "class Polygon { say() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").name().equals("say"));
    }

    @Test
    public void ES6InstanceMethodComponentTypeIsCorrect() throws Exception {

        final String code = "class Polygon { say() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").componentType() == ComponentType.METHOD);
    }

    @Test
    public void ES6StaticInstanceMethodExists() throws Exception {

        final String code = "class Polygon { static say() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.say"));
    }

    @Test
    public void ES6StaticInstanceMethodAccessModifier() throws Exception {

        final String code = "class Polygon { static say() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").modifiers().contains("static"));
    }

    @Test
    public void ES6StaticConstantModifier() throws Exception {

        final String code = "class Polygon { static constant1 = 33; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constant1").modifiers().contains("static"));
    }

    @Test
    public void ES6AsyncInstanceMethodExists() throws Exception {

        final String code = "class Polygon { static say() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.say"));
    }

    @Test
    public void ES6InstanceMethodParamComponentExists() throws Exception {

        final String code = "class Polygon { constructor() {} call(height) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.call.height"));
    }

    @Test
    public void ES6InstanceMethodParamComponentsExists() throws Exception {

        final String code = "class Polygon { constructor() {} call(height, length, width) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.call.height"));
        assertTrue(generatedSourceModel.containsComponent("Polygon.call.length"));
        assertTrue(generatedSourceModel.containsComponent("Polygon.call.width"));
    }

    @Test
    public void ES6InstanceMethodParamComponentIsChildOfInstanceMethod() throws Exception {

        final String code = "class Polygon { constructor() {} say(height, length) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").children().get(0).equals("Polygon.say.height"));
        assertTrue(generatedSourceModel.getComponent("Polygon.say").children().get(1).equals("Polygon.say.length"));
    }

    @Test
    public void testParsedES6InstanceMethodParamComponentType() throws Exception {

        final String code = "class Polygon { constructor() {} say(height) {}}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.height")
                .componentType() == ComponentType.METHOD_PARAMETER_COMPONENT);
    }

    @Test
    public void ES6GetterMethodExists() throws Exception {

        final String code = "class Polygon { get height() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.get_height"));
    }

    @Test
    public void ES6GetterIsChildOfParentClass() throws Exception {

        final String code = "class Polygon { get height() {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").children().get(0).equals("Polygon.get_height"));
    }

    @Test
    public void ES6SetterMethodComponentTypeIsCorrect() throws Exception {

        final String code = "class Polygon { set height(value) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.set_height").componentType() == ComponentType.METHOD);
    }

    @Test
    public void ES6SetterMethodExists() throws Exception {

        final String code = "class Polygon { set height(str) {} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.set_height"));
    }

    @Test
    public void ES6FieldVariableComponentExists() throws Exception {

        final String code = "class Polygon { constructor() {this.height = 4;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.height"));
    }

    @Test
    public void ES6FieldVariableComponentName() throws Exception {

        final String code = "class Polygon { constructor() {this.height = 4;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").name().equals("height"));
    }

    @Test
    public void ES6FieldVariableComponentType() throws Exception {

        final String code = "class Polygon { constructor() {this.height = 4;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").componentType() == ComponentType.FIELD);
    }

    @Test
    public void ES6FieldVariableMultipleComponentTypes() throws Exception {

        final String code = "class Polygon { constructor() {this.height = 4; this.width = false;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.height")
                && generatedSourceModel.containsComponent("Polygon.width"));

        assertTrue(generatedSourceModel.getComponent("Polygon.width").codeFragment().equals("Boolean"));
    }

    @Test
    public void ES6FieldVariableIsChildOfClass() throws Exception {

        final String code = "class Polygon { constructor() {this.height = 4;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon").children().get(1).equals("Polygon.height"));
    }

    @Test
    public void ES6FieldVariableBooleanValue() throws Exception {

        final String code = "class Polygon { constructor(height) {this.height = true;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").codeFragment().equals("true"));
    }

    @Test
    public void ES6FieldVariableStringValue() throws Exception {

        final String code = "class Polygon { constructor(height) {this.height = \"test\";} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").codeFragment().equals("test"));
    }

    @Test
    public void ES6FieldVariableNumberValue() throws Exception {

        final String code = "class Polygon { constructor(height) {this.height = 56;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").codeFragment().equals("56"));
    }

    @Test
    public void ES6FieldVariableNumberDeclarationTypeSnippet() throws Exception {

        final String code = "class Polygon { constructor(height) {this.height = 56;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").codeFragment().equals("Number"));
    }

    @Test
    public void ES6FieldVariableStringDeclarationTypeSnippet() throws Exception {

        final String code = "class Polygon { constructor(height) {this.height = \"test\";} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").codeFragment().equals("String"));
    }

    @Test
    public void ES6FieldVariableBooleanDeclarationTypeSnippet() throws Exception {

        final String code = "class Polygon { constructor(height) {this.height = false;} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").codeFragment().equals("Boolean"));
    }

    @Test
    public void ES6FieldVariableTypeInstantiation() throws Exception {

        final String code = "class React() {} class Polygon { constructor(height) {this.height = new React();} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").codeFragment().equals("React"));
    }

    @Test
    public void ES6FieldVariablePackageName() throws Exception {

        final String code = "class React() {} class Polygon { constructor(height) {this.height = new React();} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("/github/http/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.http.Polygon.height").packageName().equals("github.http"));
    }

    @Test
    public void ES6FieldVariableTypeInstantiationWithValues() throws Exception {

        final String code = "class React() {} class Polygon { constructor(height) {this.height = new React(2,4,\"fe\");} }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.height").codeFragment().equals("React"));
    }

    @Test
    public void ES6LocalVariableExists() throws Exception {

        final String code = "class React() {} class Polygon { say() { var test = new React(); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.say.test"));
    }

    @Test
    public void ES6LocalVariableComponentType() throws Exception {

        final String code = "class Polygon { say() { var test = new React(); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test").componentType() == ComponentType.LOCAL);
    }

    @Test
    public void ES6LocalLetVariableComponentType() throws Exception {

        final String code = "class Polygon { say() { let test = new React(); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test").componentType() == ComponentType.LOCAL);
    }

    @Test
    public void ES6LocalLetVariableComponentExists() throws Exception {

        final String code = "class Polygon { say() { let test = new React(); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.say.test"));
    }

    @Test
    public void ES6LocalLetVariableTypeDeclaration() throws Exception {

        final String code = "class Polygon { say() { let test = new React(); }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test")
                .componentInvocations(ComponentInvocations.INSTANTIATION).get(0).invokedComponent().equals("React"));
    }

    @Test
    public void ES6LocalVariableIsChildOfParentMethod() throws Exception {

        final String code = "class Polygon { say() { var test = new React(); var lol = 4; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say").children().size() == 2);
        assertTrue(generatedSourceModel.getComponent("Polygon.say").children().get(1).equals("Polygon.say.lol"));
    }

    @Test
    public void ES6LocalVariableTypeInstantiation() throws Exception {

        final String code = "class Polygon { say() { var test = new React(); var lol = 4; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test")
                .componentInvocations(ComponentInvocations.INSTANTIATION).get(0).invokedComponent().equals("React"));
    }

    @Test
    public void ES6LocalVariablePackageName() throws Exception {

        final String code = "class Polygon { say() { var test = new React(); var lol = 4; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("src/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.say.test").packageName().equals(""));
    }

    @Test
    public void MultipleES6LocalVariables() throws Exception {

        final String code = "class Polygon { say() { var test = new React(); var lol = 4; }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.say.test")
                && generatedSourceModel.containsComponent("Polygon.say.lol"));
    }

    @Test
    public void testDefaultExportClassHasCorrectUniqueName() throws Exception {

        final String code = "export default class { }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("/src/github/test.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("github.test"));
    }

    @Test
    public void ES6ConstructorLocalVar() throws Exception {

        final String code = "class Polygon { constructor() {  this.width = 4;  var test = new React(); } }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.containsComponent("Polygon.constructor.test"));
    }

    @Test
    public void ES6MethodTypeDeclaration() throws Exception {

        final String code = "class Polygon { constructor() {  new React().test(); } }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("Polygon.constructor")
                .componentInvocations(ComponentInvocations.INSTANTIATION).get(0).invokedComponent().equals("React"));
    }

    @Test
    public void ES6MethodPackageName() throws Exception {

        final String code = "class Polygon { constructor() {  new React().test(); } }";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("/github/polygon.js", code));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("github.Polygon.constructor").packageName().equals("github"));
    }

    @Test
    public void ES6MethodTypeDeclarationFromStaticMethodCall() throws Exception {

        final String code = "import { React } from \'github/react.js\'; \n class Polygon { constructor() {  React.test(); } }";
        final String codeB = "class React {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("/src/test/polygon.js", code));
        rawData.insertFile(new RawFile("/src/test/github/react.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Polygon.constructor")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("test.github.React"));
    }

    @Test
    public void testResolvingOfAbsoluteImportPath() throws Exception {

        final String code = "import { React } from \'/src/test/github/react.js\'; \n class Polygon { constructor() {  React.test(); } }";
        final String codeB = "class React {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("/src/test/polygon.js", code));
        rawData.insertFile(new RawFile("/src/test/github/react.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Polygon.constructor")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("test.github.React"));
    }

    @Test
    public void testResolvingOfAliasImportType() throws Exception {

        final String code = "import { LoL as React } from \'/src/test/github/react.js\'; \n class Polygon { constructor() {  LoL.test(); } }";
        final String codeB = "class React {}";
        final SourceFiles rawData = new SourceFiles(Lang.JAVASCRIPT);
        rawData.insertFile(new RawFile("/src/test/polygon.js", code));
        rawData.insertFile(new RawFile("/src/test/github/react.js", codeB));
        final ClarpseProject parseService = new ClarpseProject(rawData);
        final OOPSourceCodeModel generatedSourceModel = parseService.result();
        assertTrue(generatedSourceModel.getComponent("test.Polygon.constructor")
                .componentInvocations(ComponentInvocations.DECLARATION).get(0).invokedComponent()
                .equals("test.github.React"));
    }
}
