/*
 [The "BSD licence"]
 Copyright (c) 2013 Terence Parr, Sam Harwell
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/** A Java 1.7 grammar for ANTLR v4 derived from ANTLR v3 Java grammar.
 *  Uses ANTLR v4's left-recursive expression notation.
 *  It parses ECJ, Netbeans, JDK etc...
 *
 *  Sam Harwell cleaned this up significantly and updated to 1.7!
 *
 *  You can test with
 *
 *  $ antlr4 Java.g4
 *  $ javac *.java
 *  $ grun Java compilationUnit *.java
 */
grammar Java;

// starting point for parsing a java file
compilationUnit
    :   packageDeclaration? importDeclaration* typeDeclaration* EOF
    ;

packageDeclaration
    :   annotation* 'package' qualifiedName ';'
    ;

importDeclaration
    :   'import' 'static'? qualifiedName ('.' '*')? ';'
    ;

typeDeclaration
    :    classDeclaration
    |    enumDeclaration
    |    interfaceDeclaration
    |    annotationTypeDeclaration
    |   ';'
    ;

modifier
    :    regularModifier
    |    annotation
    ;

regularModifier
    :   'native'
    |   'synchronized'
    |   'transient'
    |   'volatile'
    |   'public'
    |   'protected'
    |   'private'
    |   'static'
    |   'abstract'
    |   'final'
    |   'strictfp'
    ;

classDeclaration
    :   modifier* 'class' Identifier typeParameters?
        ('extends' extendsType)?
        ('implements' implementsTypeList)?
        classBody
    ;

typeParameters
    :   '<' typeParameter (',' typeParameter)* '>'
    ;

typeParameter
    :   Identifier ('extends' typeBound)?
    ;

typeBound
    :   type ('&' type)*
    ;

enumDeclaration
    :   modifier* ENUM Identifier ('implements' typeList)?
        '{' enumConstants? ','? enumBodyDeclarations? '}'
    ;

enumConstants
    :   enumConstant (',' enumConstant)*
    ;

enumConstant
    :   annotation* Identifier arguments? classBody?
    ;

enumBodyDeclarations
    :   ';' classBodyDeclaration*
    ;

interfaceDeclaration
    :   modifier* 'interface' Identifier typeParameters? ('extends' typeList)? interfaceBody
    ;

typeList
    :   type (',' type)*
    ;

implementsTypeList
    :   implementsType (',' implementsType)*
    ;

classBody
    :   '{' classBodyDeclaration* '}'
    ;

interfaceBody
    :   '{' interfaceBodyDeclaration* '}'
    ;

classBodyDeclaration
    :   ';'
    |   'static'? block
    |    memberDeclaration
    ;

memberDeclaration
    :   methodDeclaration
    |   fieldDeclaration
    |   constructorDeclaration
    |   interfaceDeclaration
    |   annotationTypeDeclaration
    |   classDeclaration
    |   enumDeclaration
    ;

/* We use rule this even for void methods which cannot have [] after parameters.
   This simplifies grammar and we can consider void to be a type, which
   renders the [] matching as a context-sensitive issue or a semantic check
   for invalid return type after parsing.
 */
methodDeclaration
    :   modifier* typeParameters? (type|'void') Identifier formalParameters ('[' ']')*
        ('throws' qualifiedNameList)?
        (   methodBody
        |   ';'
        )
    ;

constructorDeclaration
    :  modifier* typeParameters? Identifier formalParameters ('throws' qualifiedNameList)?
        constructorBody
    ;
    
fieldDeclaration
    :   modifier* type variableDeclarators ';'
    ;

interfaceBodyDeclaration
    :    interfaceMemberDeclaration
    |   ';'
    ;

interfaceMemberDeclaration
    :	fieldDeclaration
    |   interfaceMethodDeclaration
    |   interfaceDeclaration
    |   annotationTypeDeclaration
    |   classDeclaration
    |   enumDeclaration
    ;

// see matching of [] comment in methodDeclaratorRest
interfaceMethodDeclaration
    :   modifier* typeParameters? (type|'void') Identifier formalParameters ('[' ']')*
        ('throws' qualifiedNameList)?
        ';'
    ;
    
variableDeclarators
    :   variableDeclarator (',' variableDeclarator)*
    ;

variableDeclarator
    :   variableDeclaratorId ('=' variableInitializer)?
    ;

variableDeclaratorId
    :   Identifier ('[' ']')*
    ;

variableInitializer
    :   arrayInitializer
    |   expression
    ;

arrayInitializer
    :   '{' (variableInitializer (',' variableInitializer)* (',')? )? '}'
    ;

type
    :   classOrInterfaceType ('[' ']')*
    |   primitiveType ('[' ']')*
    ;

extendsType
    :   Identifier
    ;
    
implementsType
    :   implementsClassOrInterfaceType ('[' ']')*
    |   implementsPrimitiveType ('[' ']')*
    ;

    
implementsClassOrInterfaceType
    :   Identifier implementsTypeArguments? ('.' Identifier implementsTypeArguments? )*
    ;

implementsPrimitiveType
    :   'boolean'
    |   'char'
    |   'byte'
    |   'short'
    |   'int'
    |   'long'
    |   'float'
    |   'double'
    ;
       
classOrInterfaceType
    :   Identifier typeArguments? ('.' Identifier typeArguments? )*
    ;

primitiveType
    :   'boolean'
    |   'char'
    |   'byte'
    |   'short'
    |   'int'
    |   'long'
    |   'float'
    |   'double'
    ;

typeArguments
    :   '<' typeArgument (',' typeArgument)* '>'
    ;

implementsTypeArguments
    :   '<' implementsTypeArgument (',' implementsTypeArgument)* '>'
    ;
    
typeArgument
    :   type
    |   '?' (('extends' | 'super') type)?
    ;
 
implementsTypeArgument
    :   implementsType
    |   '?' (('extends' | 'super') implementsType)?
    ;

qualifiedNameList
    :   qualifiedName (',' qualifiedName)*
    ;

formalParameters
    :   '(' formalParameterList? ')'
    ;

formalParameterList
    :   formalParameter (',' formalParameter)* (',' lastFormalParameter)?
    |   lastFormalParameter
    ;

formalParameter
    :   modifier* type variableDeclaratorId
    ;

methodBody
    :   block
    ;

constructorBody
    :   block
    ;

qualifiedName
    :   Identifier ('.' Identifier)*
    ;

literal
    :   IntegerLiteral
    |   FloatingPointLiteral
    |   CharacterLiteral
    |   StringLiteral
    |   BooleanLiteral
    |   'null'
    ;

// ANNOTATIONS

annotation
    :   '@' annotationName ( '(' ( elementValuePairs | elementValue )? ')' )?
    ;

annotationName : qualifiedName ;

elementValuePairs
    :   elementValuePair (',' elementValuePair)*
    ;

elementValuePair
    :   Identifier '=' elementValue
    ;

elementValue
    :   expression
    |   annotation
    |   elementValueArrayInitializer
    ;

elementValueArrayInitializer
    :   '{' (elementValue (',' elementValue)*)? (',')? '}'
    ;

annotationTypeDeclaration
    :   modifier* '@' 'interface' Identifier annotationTypeBody
    ;

annotationTypeBody
    :   '{' (annotationTypeElementDeclaration)* '}'
    ;

annotationTypeElementDeclaration
    :   annotationTypeElementRest
    |   ';' // this is not allowed by the grammar, but apparently allowed by the actual compiler
    ;

annotationTypeElementRest
    :   type annotationMethodOrConstantRest ';'
    |   classDeclaration ';'?
    |   interfaceDeclaration ';'?
    |   enumDeclaration ';'?
    |   annotationTypeDeclaration ';'?
    ;

annotationMethodOrConstantRest
    :   annotationMethodRest
    |   annotationConstantRest
    ;

annotationMethodRest
    :   Identifier '(' ')' defaultValue?
    ;

annotationConstantRest
    :   variableDeclarators
    ;

defaultValue
    :   'default' elementValue
    ;

// STATEMENTS / BLOCKS

block
    :   '{' blockStatement* '}'
    ;

blockStatement
    :   localVariableDeclarationStatement
    |   statement
    |   typeDeclaration
    ;

localVariableDeclarationStatement
    :    localVariableDeclaration ';'
    ;

localVariableDeclaration
    :   modifier* type variableDeclarators
    ;

statement
    :   statementWithoutTrailingSubstatement
    |   labeledStatement
    |   ifThenStatement
    |   ifThenElseStatement
    |   whileStatement
    |   forStatement
;

statementNoShortIf
    :   statementWithoutTrailingSubstatement
    |   labeledStatementNoShortIf
    |   ifThenElseStatementNoShortIf
    |   whileStatementNoShortIf
    |   forStatementNoShortIf
    ;

statementWithoutTrailingSubstatement
    :   block
    |   emptyStatement
    |   expressionStatement
    |   assertStatement
    |   switchStatement
    |   doStatement
    |   breakStatement
    |   continueStatement
    |   returnStatement
    |   synchronizedStatement
    |   throwStatement
    |   tryStatement
    ;

emptyStatement
    :   ';'
    ;

labeledStatement
    :   Identifier ':' statement
    ;

labeledStatementNoShortIf
    :   Identifier ':' statementNoShortIf
    ;

expressionStatement
    :   statementExpression ';'
    ;

statementExpression
    :   assignment
    |   preIncrementExpression
    |   preDecrementExpression
    |   postIncrementExpression
    |   postDecrementExpression
    |   methodInvocation
    |   classInstanceCreationExpression
    ;

ifThenStatement
    :   'if' '(' expression ')' statement
    ;

ifThenElseStatement
    :   'if' '(' expression ')' statementNoShortIf 'else' statement
    ;

ifThenElseStatementNoShortIf
    :   'if' '(' expression ')' statementNoShortIf 'else' statementNoShortIf
    ;

assertStatement
    :   'assert' expression ';'
    |   'assert' expression ':' expression ';'
    ;

switchStatement
    :   'switch' '(' expression ')' switchBlock
    ;

switchBlock
    :   '{' switchBlockStatementGroup* switchLabel* '}'
    ;

switchBlockStatementGroup
    :   switchLabels blockStatements
    ;

blockStatements
    :   blockStatement blockStatement*
;

switchLabels
    :   switchLabel switchLabel*
    ;

switchLabel
    :   'case' constantExpression ':'
    |   'case' enumConstantName ':'
    |   'default' ':'
    ;

enumConstantName
    :   Identifier
    ;

whileStatement
    :   'while' '(' expression ')' statement
    ;

whileStatementNoShortIf
    :   'while' '(' expression ')' statementNoShortIf
    ;

doStatement
    :   'do' statement 'while' '(' expression ')' ';'
    ;

forStatement
    :   basicForStatement
    |   enhancedForStatement
    ;

forStatementNoShortIf
    :   basicForStatementNoShortIf
    |   enhancedForStatementNoShortIf
    ;

basicForStatement
    :   'for' '(' forInit? ';' expression? ';' forUpdate? ')' statement
    ;

basicForStatementNoShortIf
    :   'for' '(' forInit? ';' expression? ';' forUpdate? ')' statementNoShortIf
    ;

forInit
    :   statementExpressionList
    |   localVariableDeclaration
    ;

forUpdate
    :   statementExpressionList
    ;

statementExpressionList
    :   statementExpression (',' statementExpression)*
    ;

enhancedForStatement
    :   'for' '(' variableModifier* unannType variableDeclaratorId ':' expression ')' statement
    ;

enhancedForStatementNoShortIf
    :   'for' '(' variableModifier* unannType variableDeclaratorId ':' expression ')' statementNoShortIf
    ;

breakStatement
    :   'break' Identifier? ';'
    ;
    
variableModifier
    :   annotation
    |   'final'
    ;

lastFormalParameter
    :   modifier* type annotation* '...' variableDeclaratorId
    |   formalParameter
    ;

receiverParameter
    :   annotation* unannType (Identifier '.')? 'this'
    ;

throws_
    :   'throws' exceptionTypeList
    ;

unannType
    :   unannPrimitiveType
    |   unannReferenceType
    ;

unannPrimitiveType
    :   numericType
    |   'boolean'
    ;

unannReferenceType
    :   unannClassOrInterfaceType
    |   unannTypeVariable
    |   unannArrayType
    ;

numericType
    :   integralType
    |   floatingPointType
;

integralType
    :   'byte'
    |   'short'
    |   'int'
    |   'long'
    |   'char'
    ;

floatingPointType
    :   'float'
    |   'double'
    ;

arrayType
    :   primitiveType dims
    |   classOrInterfaceType dims
    |   typeVariable dims
;

referenceType
    :   classOrInterfaceType
    |   typeVariable
    |   arrayType
;

unannClassOrInterfaceType
    :   (   unannClassType_lfno_unannClassOrInterfaceType
        |   unannInterfaceType_lfno_unannClassOrInterfaceType
        )
        (   unannClassType_lf_unannClassOrInterfaceType
        |   unannInterfaceType_lf_unannClassOrInterfaceType
        )*
    ;

unannClassType
    :   Identifier typeArguments?
    |   unannClassOrInterfaceType '.' annotation* Identifier typeArguments?
    ;

unannClassType_lf_unannClassOrInterfaceType
    :   '.' annotation* Identifier typeArguments?
    ;

unannClassType_lfno_unannClassOrInterfaceType
    :   Identifier typeArguments?
    ;

unannInterfaceType
    :   unannClassType
    ;

unannInterfaceType_lf_unannClassOrInterfaceType
    :   unannClassType_lf_unannClassOrInterfaceType
    ;

unannInterfaceType_lfno_unannClassOrInterfaceType
    :   unannClassType_lfno_unannClassOrInterfaceType
    ;

unannTypeVariable
    :   Identifier
    ;

unannArrayType
    :   unannPrimitiveType dims
    |   unannClassOrInterfaceType dims
    |   unannTypeVariable dims
;
exceptionTypeList
    :   exceptionType (',' exceptionType)*
    ;

exceptionType
    :   classType
    |   typeVariable
;

dims
    :   annotation* '[' ']' (annotation* '[' ']')*
;

typeVariable
    :   annotation* Identifier
    ;


classType
    :   annotation* Identifier typeArguments?
    |   classOrInterfaceType '.' annotation* Identifier typeArguments?
;
continueStatement
    :   'continue' Identifier? ';'
    ;

returnStatement
    :   'return' expression? ';'
    ;

throwStatement
    :   'throw' expression ';'
    ;

synchronizedStatement
    :   'synchronized' '(' expression ')' block
    ;

tryStatement
    :   'try' block catches
    |   'try' block catches? finally_
    |   tryWithResourcesStatement
    ;

catches
    :   catchClause catchClause*
    ;

catchFormalParameter
    :   variableModifier* catchType variableDeclaratorId
    ;

catchType
    :   unannClassType ('|' classType)*
    ;

finally_
    :   'finally' block
    ;

tryWithResourcesStatement
    :   'try' resourceSpecification block catches? finally_?
    ;

resourceSpecification
    :   '(' resourceList ';'? ')'
    ;

resourceList
    :   resource (';' resource)*
    ;

resource
    :   variableModifier* unannType variableDeclaratorId '=' expression
    ;

catchClause
    :   'catch' '(' modifier* catchType Identifier ')' block
    ;

finallyBlock
    :   'finally' block
    ;

resources
    :   resource (';' resource)*
    ;

forControl
    :   enhancedForControl
    |   forInit? ';' expression? ';' forUpdate?
    ;

enhancedForControl
    :   modifier* type variableDeclaratorId ':' expression
    ;

// EXPRESSIONS

parExpression
    :   '(' expression ')'
    ;

expressionList
    :   expression (',' expression)*
    ;

constantExpression
    :   expression
    ;

expressionName: Identifier;

typeName: Identifier;

methodName: Identifier ;



methodParamExpressionsList
    :   expression (',' expression)*
    ;

creator
    :   nonWildcardTypeArguments createdName classCreatorRest
    |   createdName (arrayCreatorRest | classCreatorRest)
    ;

createdName
    :   Identifier typeArgumentsOrDiamond? ('.' Identifier typeArgumentsOrDiamond?)*
    |   primitiveType
    ;

innerCreator
    :   Identifier nonWildcardTypeArgumentsOrDiamond? classCreatorRest
    ;

arrayCreatorRest
    :   '['
        (   ']' ('[' ']')* arrayInitializer
        |   expression ']' ('[' expression ']')* ('[' ']')*
        )
    ;

classCreatorRest
    :   arguments classBody?
    ;

explicitGenericInvocation
    :   nonWildcardTypeArguments explicitGenericInvocationSuffix
    ;

nonWildcardTypeArguments
    :   '<' typeList '>'
    ;

typeArgumentsOrDiamond
    :   '<' '>'
    |   typeArguments
    ;

nonWildcardTypeArgumentsOrDiamond
    :   '<' '>'
    |   nonWildcardTypeArguments
    ;

superSuffix
    :   arguments
    |   '.' Identifier arguments?
    ;

explicitGenericInvocationSuffix
    :   'super' superSuffix
    |   Identifier arguments
    ;

arguments
    :   '(' expressionList? ')'
    ;

/*
 * Productions from §15 (Expressions)
 */

primary
    :   (   primaryNoNewArray_lfno_primary
        |   arrayCreationExpression
        )
        (   primaryNoNewArray_lf_primary
        )*
    ;

primaryNoNewArray
    :   literal
    |   typeName ('[' ']')* '.' 'class'
    |   'void' '.' 'class'
    |   'this'
    |   typeName '.' 'this'
    |   '(' expression ')'
    |   classInstanceCreationExpression
    |   fieldAccess
    |   arrayAccess
    |   methodInvocation
    |   methodReference
    ;

primaryNoNewArray_lf_arrayAccess
    :
    ;

primaryNoNewArray_lfno_arrayAccess
    :   literal
    |   typeName ('[' ']')* '.' 'class'
    |   'void' '.' 'class'
    |   'this'
    |   typeName '.' 'this'
    |   '(' expression ')'
    |   classInstanceCreationExpression
    |   fieldAccess
    |   methodInvocation
    |   methodReference
    ;

primaryNoNewArray_lf_primary
    :   classInstanceCreationExpression_lf_primary
    |   fieldAccess_lf_primary
    |   arrayAccess_lf_primary
    |   methodInvocation_lf_primary
    |   methodReference_lf_primary
    ;

primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary
    :
    ;

primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary
    :   classInstanceCreationExpression_lf_primary
    |   fieldAccess_lf_primary
    |   methodInvocation_lf_primary
    |   methodReference_lf_primary
    ;

primaryNoNewArray_lfno_primary
    :   literal
    |   typeName ('[' ']')* '.' 'class'
    |   unannPrimitiveType ('[' ']')* '.' 'class'
    |   'void' '.' 'class'
    |   'this'
    |   typeName '.' 'this'
    |   '(' expression ')'
    |   classInstanceCreationExpression_lfno_primary
    |   fieldAccess_lfno_primary
    |   arrayAccess_lfno_primary
    |   methodInvocation_lfno_primary
    |   methodReference_lfno_primary
    ;

primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary
    :
    ;

primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary
    :   literal
    |   typeName ('[' ']')* '.' 'class'
    |   unannPrimitiveType ('[' ']')* '.' 'class'
    |   'void' '.' 'class'
    |   'this'
    |   typeName '.' 'this'
    |   '(' expression ')'
    |   classInstanceCreationExpression_lfno_primary
    |   fieldAccess_lfno_primary
    |   methodInvocation_lfno_primary
    |   methodReference_lfno_primary
    ;

classInstanceCreationExpression
    :   'new' typeArguments? annotation* Identifier ('.' annotation* Identifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
    |   expressionName '.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
    |   primary '.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
    ;

classInstanceCreationExpression_lf_primary
    :   '.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
    ;

classInstanceCreationExpression_lfno_primary
    :   'new' typeArguments? annotation* Identifier ('.' annotation* Identifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
    |   expressionName '.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
    ;

fieldAccess
    :   primary '.' Identifier
    |   'super' '.' Identifier
    |   typeName '.' 'super' '.' Identifier
    ;

fieldAccess_lf_primary
    :   '.' Identifier
    ;

fieldAccess_lfno_primary
    :   'super' '.' Identifier
    |   typeName '.' 'super' '.' Identifier
    ;

arrayAccess
    :   (   expressionName '[' expression ']'
        |   primaryNoNewArray_lfno_arrayAccess '[' expression ']'
        )
        (   primaryNoNewArray_lf_arrayAccess '[' expression ']'
        )*
    ;

arrayAccess_lf_primary
    :   (   primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary '[' expression ']'
        )
        (   primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary '[' expression ']'
        )*
    ;

arrayAccess_lfno_primary
    :   (   expressionName '[' expression ']'
        |   primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary '[' expression ']'
        )
        (   primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary '[' expression ']'
        )*
    ;

methodInvocation
    :   methodName '(' argumentList? ')'
    |   typeName '.' typeArguments? Identifier '(' argumentList? ')'
    |   expressionName '.' typeArguments? Identifier '(' argumentList? ')'
    |   primary '.' typeArguments? Identifier '(' argumentList? ')'
    |   'super' '.' typeArguments? Identifier '(' argumentList? ')'
    |   typeName '.' 'super' '.' typeArguments? Identifier '(' argumentList? ')'
    ;

methodInvocation_lf_primary
    :   '.' typeArguments? Identifier '(' argumentList? ')'
    ;

methodInvocation_lfno_primary
    :   methodName '(' argumentList? ')'
    |   typeName '.' typeArguments? Identifier '(' argumentList? ')'
    |   expressionName '.' typeArguments? Identifier '(' argumentList? ')'
    |   'super' '.' typeArguments? Identifier '(' argumentList? ')'
    |   typeName '.' 'super' '.' typeArguments? Identifier '(' argumentList? ')'
    ;

argumentList
    :   expression (',' expression)*
    ;

methodReference
    :   expressionName '::' typeArguments? Identifier
    |   referenceType '::' typeArguments? Identifier
    |   primary '::' typeArguments? Identifier
    |   'super' '::' typeArguments? Identifier
    |   typeName '.' 'super' '::' typeArguments? Identifier
    |   classType '::' typeArguments? 'new'
    |   arrayType '::' 'new'
    ;

methodReference_lf_primary
    :   '::' typeArguments? Identifier
    ;

methodReference_lfno_primary
    :   expressionName '::' typeArguments? Identifier
    |   referenceType '::' typeArguments? Identifier
    |   'super' '::' typeArguments? Identifier
    |   typeName '.' 'super' '::' typeArguments? Identifier
    |   classType '::' typeArguments? 'new'
    |   arrayType '::' 'new'
    ;

arrayCreationExpression
    :   'new' primitiveType dimExprs dims?
    |   'new' classOrInterfaceType dimExprs dims?
    |   'new' primitiveType dims arrayInitializer
    |   'new' classOrInterfaceType dims arrayInitializer
    ;

dimExprs
    :   dimExpr dimExpr*
    ;

dimExpr
    :   annotation* '[' expression ']'
    ;

expression
    :   lambdaExpression
    |   assignmentExpression
    ;

lambdaExpression
    :   lambdaParameters '->' lambdaBody
    ;

lambdaParameters
    :   Identifier
    |   '(' formalParameterList? ')'
    |   '(' inferredFormalParameterList ')'
    ;

inferredFormalParameterList
    :   Identifier (',' Identifier)*
    ;

lambdaBody
    :   expression
    |   block
    ;

assignmentExpression
    :   conditionalExpression
    |   assignment
    ;

assignment
    :   leftHandSide assignmentOperator expression
    ;

leftHandSide
    :   expressionName
    |   fieldAccess
    |   arrayAccess
    ;

assignmentOperator
    :   '='
    |   '*='
    |   '/='
    |   '%='
    |   '+='
    |   '-='
    |   '<<='
    |   '>>='
    |   '>>>='
    |   '&='
    |   '^='
    |   '|='
    ;

conditionalExpression
    :   conditionalOrExpression
    |   conditionalOrExpression '?' expression ':' conditionalExpression
    ;

conditionalOrExpression
    :   conditionalAndExpression
    |   conditionalOrExpression '||' conditionalAndExpression
    ;

conditionalAndExpression
    :   inclusiveOrExpression
    |   conditionalAndExpression '&&' inclusiveOrExpression
    ;

inclusiveOrExpression
    :   exclusiveOrExpression
    |   inclusiveOrExpression '|' exclusiveOrExpression
    ;

exclusiveOrExpression
    :   andExpression
    |   exclusiveOrExpression '^' andExpression
    ;

andExpression
    :   equalityExpression
    |   andExpression '&' equalityExpression
    ;

equalityExpression
    :   relationalExpression
    |   equalityExpression '==' relationalExpression
    |   equalityExpression '!=' relationalExpression
    ;

relationalExpression
    :   shiftExpression
    |   relationalExpression '<' shiftExpression
    |   relationalExpression '>' shiftExpression
    |   relationalExpression '<=' shiftExpression
    |   relationalExpression '>=' shiftExpression
    |   relationalExpression 'instanceof' referenceType
    ;

shiftExpression
    :   additiveExpression
    |   shiftExpression '<' '<' additiveExpression
    |   shiftExpression '>' '>' additiveExpression
    |   shiftExpression '>' '>' '>' additiveExpression
    ;

additiveExpression
    :   multiplicativeExpression
    |   additiveExpression '+' multiplicativeExpression
    |   additiveExpression '-' multiplicativeExpression
    ;

multiplicativeExpression
    :   unaryExpression
    |   multiplicativeExpression '*' unaryExpression
    |   multiplicativeExpression '/' unaryExpression
    |   multiplicativeExpression '%' unaryExpression
    ;

unaryExpression
    :   preIncrementExpression
    |   preDecrementExpression
    |   '+' unaryExpression
    |   '-' unaryExpression
    |   unaryExpressionNotPlusMinus
    ;

preIncrementExpression
    :   '++' unaryExpression
    ;

preDecrementExpression
    :   '--' unaryExpression
    ;

unaryExpressionNotPlusMinus
    :   postfixExpression
    |   '~' unaryExpression
    |   '!' unaryExpression
    |   castExpression
    ;

postfixExpression
    :   (   primary
        |   expressionName
        )
        (   postIncrementExpression_lf_postfixExpression
        |   postDecrementExpression_lf_postfixExpression
        )*
    ;

postIncrementExpression
    :   postfixExpression '++'
    ;

postIncrementExpression_lf_postfixExpression
    :   '++'
    ;

postDecrementExpression
    :   postfixExpression '--'
    ;

postDecrementExpression_lf_postfixExpression
    :   '--'
    ;

additionalBound
    :   '&' interfaceType
    ;
    
interfaceType: classType ;

castExpression
    :   '(' primitiveType ')' unaryExpression
    |   '(' referenceType additionalBound* ')' unaryExpressionNotPlusMinus
    |   '(' referenceType additionalBound* ')' lambdaExpression
;
// LEXER

// §3.9 Keywords

ABSTRACT : 'abstract';
ASSERT : 'assert';
BOOLEAN : 'boolean';
BREAK : 'break';
BYTE : 'byte';
CASE : 'case';
CATCH : 'catch';
CHAR : 'char';
CLASS : 'class';
CONST : 'const';
CONTINUE : 'continue';
DEFAULT : 'default';
DO : 'do';
DOUBLE : 'double';
ELSE : 'else';
ENUM : 'enum';
EXTENDS : 'extends';
FINAL : 'final';
FINALLY : 'finally';
FLOAT : 'float';
FOR : 'for';
IF : 'if';
GOTO : 'goto';
IMPLEMENTS : 'implements';
IMPORT : 'import';
INSTANCEOF : 'instanceof';
INT : 'int';
INTERFACE : 'interface';
LONG : 'long';
NATIVE : 'native';
NEW : 'new';
PACKAGE : 'package';
PRIVATE : 'private';
PROTECTED : 'protected';
PUBLIC : 'public';
RETURN : 'return';
SHORT : 'short';
STATIC : 'static';
STRICTFP : 'strictfp';
SUPER : 'super';
SWITCH : 'switch';
SYNCHRONIZED : 'synchronized';
THIS : 'this';
THROW : 'throw';
THROWS : 'throws';
TRANSIENT : 'transient';
TRY : 'try';
VOID : 'void';
VOLATILE : 'volatile';
WHILE : 'while';

// §3.10.1 Integer Literals

IntegerLiteral
    :   DecimalIntegerLiteral
    |   HexIntegerLiteral
    |   OctalIntegerLiteral
    |   BinaryIntegerLiteral
    ;

fragment
DecimalIntegerLiteral
    :   DecimalNumeral IntegerTypeSuffix?
    ;

fragment
HexIntegerLiteral
    :   HexNumeral IntegerTypeSuffix?
    ;

fragment
OctalIntegerLiteral
    :   OctalNumeral IntegerTypeSuffix?
    ;

fragment
BinaryIntegerLiteral
    :   BinaryNumeral IntegerTypeSuffix?
    ;

fragment
IntegerTypeSuffix
    :   [lL]
    ;

fragment
DecimalNumeral
    :   '0'
    |   NonZeroDigit (Digits? | Underscores Digits)
    ;

fragment
Digits
    :   Digit (DigitsAndUnderscores? Digit)?
    ;

fragment
Digit
    :   '0'
    |   NonZeroDigit
    ;

fragment
NonZeroDigit
    :   [1-9]
    ;

fragment
DigitsAndUnderscores
    :   DigitOrUnderscore+
    ;

fragment
DigitOrUnderscore
    :   Digit
    |   '_'
    ;

fragment
Underscores
    :   '_'+
    ;

fragment
HexNumeral
    :   '0' [xX] HexDigits
    ;

fragment
HexDigits
    :   HexDigit (HexDigitsAndUnderscores? HexDigit)?
    ;

fragment
HexDigit
    :   [0-9a-fA-F]
    ;

fragment
HexDigitsAndUnderscores
    :   HexDigitOrUnderscore+
    ;

fragment
HexDigitOrUnderscore
    :   HexDigit
    |   '_'
    ;

fragment
OctalNumeral
    :   '0' Underscores? OctalDigits
    ;

fragment
OctalDigits
    :   OctalDigit (OctalDigitsAndUnderscores? OctalDigit)?
    ;

fragment
OctalDigit
    :   [0-7]
    ;

fragment
OctalDigitsAndUnderscores
    :   OctalDigitOrUnderscore+
    ;

fragment
OctalDigitOrUnderscore
    :   OctalDigit
    |   '_'
    ;

fragment
BinaryNumeral
    :   '0' [bB] BinaryDigits
    ;

fragment
BinaryDigits
    :   BinaryDigit (BinaryDigitsAndUnderscores? BinaryDigit)?
    ;

fragment
BinaryDigit
    :   [01]
    ;

fragment
BinaryDigitsAndUnderscores
    :   BinaryDigitOrUnderscore+
    ;

fragment
BinaryDigitOrUnderscore
    :   BinaryDigit
    |   '_'
    ;

// §3.10.2 Floating-Point Literals

FloatingPointLiteral
    :   DecimalFloatingPointLiteral
    |   HexadecimalFloatingPointLiteral
    ;

fragment
DecimalFloatingPointLiteral
    :   Digits '.' Digits? ExponentPart? FloatTypeSuffix?
    |   '.' Digits ExponentPart? FloatTypeSuffix?
    |   Digits ExponentPart FloatTypeSuffix?
    |   Digits FloatTypeSuffix
    ;

fragment
ExponentPart
    :   ExponentIndicator SignedInteger
    ;

fragment
ExponentIndicator
    :   [eE]
    ;

fragment
SignedInteger
    :   Sign? Digits
    ;

fragment
Sign
    :   [+-]
    ;

fragment
FloatTypeSuffix
    :   [fFdD]
    ;

fragment
HexadecimalFloatingPointLiteral
    :   HexSignificand BinaryExponent FloatTypeSuffix?
    ;

fragment
HexSignificand
    :   HexNumeral '.'?
    |   '0' [xX] HexDigits? '.' HexDigits
    ;

fragment
BinaryExponent
    :   BinaryExponentIndicator SignedInteger
    ;

fragment
BinaryExponentIndicator
    :   [pP]
    ;

// §3.10.3 Boolean Literals

BooleanLiteral
    :   'true'
    |   'false'
    ;

// §3.10.4 Character Literals

CharacterLiteral
    :   '\'' SingleCharacter '\''
    |   '\'' EscapeSequence '\''
    ;

fragment
SingleCharacter
    :   ~['\\]
    ;
// §3.10.5 String Literals
StringLiteral
    :   '"' StringCharacters? '"'
    ;
fragment
StringCharacters
    :   StringCharacter+
    ;
fragment
StringCharacter
    :   ~["\\]
    |   EscapeSequence
    ;
// §3.10.6 Escape Sequences for Character and String Literals
fragment
EscapeSequence
    :   '\\' [btnfr"'\\]
    |   OctalEscape
    |   UnicodeEscape // This is not in the spec but prevents having to preprocess the input
    ;

fragment
OctalEscape
    :   '\\' OctalDigit
    |   '\\' OctalDigit OctalDigit
    |   '\\' ZeroToThree OctalDigit OctalDigit
    ;

fragment
ZeroToThree
    :   [0-3]
    ;

// This is not in the spec but prevents having to preprocess the input
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// §3.10.7 The Null Literal

NullLiteral
    :   'null'
    ;

// §3.11 Separators

LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';
LBRACK : '[';
RBRACK : ']';
SEMI : ';';
COMMA : ',';
DOT : '.';

// §3.12 Operators

ASSIGN : '=';
GT : '>';
LT : '<';
BANG : '!';
TILDE : '~';
QUESTION : '?';
COLON : ':';
EQUAL : '==';
LE : '<=';
GE : '>=';
NOTEQUAL : '!=';
AND : '&&';
OR : '||';
INC : '++';
DEC : '--';
ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';
BITAND : '&';
BITOR : '|';
CARET : '^';
MOD : '%';
ARROW : '->';
COLONCOLON : '::';

ADD_ASSIGN : '+=';
SUB_ASSIGN : '-=';
MUL_ASSIGN : '*=';
DIV_ASSIGN : '/=';
AND_ASSIGN : '&=';
OR_ASSIGN : '|=';
XOR_ASSIGN : '^=';
MOD_ASSIGN : '%=';
LSHIFT_ASSIGN : '<<=';
RSHIFT_ASSIGN : '>>=';
URSHIFT_ASSIGN : '>>>=';

// §3.8 Identifiers (must appear after all keywords in the grammar)

Identifier
    :   JavaLetter JavaLetterOrDigit*
    ;

fragment
JavaLetter
    :   [a-zA-Z$_] // these are the "java letters" below 0x7F
    |   // covers all characters above 0x7F which are not a surrogate
        ~[\u0000-\u007F\uD800-\uDBFF]
        {Character.isJavaIdentifierStart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

fragment
JavaLetterOrDigit
    :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
    |   // covers all characters above 0x7F which are not a surrogate
        ~[\u0000-\u007F\uD800-\uDBFF]
        {Character.isJavaIdentifierPart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

//
// Additional symbols not defined in the lexical specification
//

AT : '@';
ELLIPSIS : '...';

//
// Whitespace and comments
//

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
;