parser grammar FizParser;

@header {
package dev.fiz.bootstrap.antlr;
}

options {
    tokenVocab = FizLexer;
}

// literals
bool: TRUE | FALSE;
decimalNumber: DIGIT? (DIGIT | SEPARATOR | UNDERSCORE)* DOT DIGIT (DIGIT | SEPARATOR | UNDERSCORE)*;
integer: DIGIT (DIGIT | SEPARATOR | UNDERSCORE)*;
literal: bool | CHAR_LIT | STRING_LIT | integer | decimalNumber;

// expression -> evaluate and push on to stack
// variableDeclaration -> declare variable and optionally assign value from stack
// assignment -> assign value from stack to variable or field
// returnStatement -> return value from stack
// conditional -> conditionally control flow, using { } to group statements
statement: expression | variableDeclaration | assignment | returnStatement | conditional;
block: BODY_OPEN statement* BODY_CLOSE;

// for loop
for_loop: FOR ID SET range block;
for_each_loop: FOR ID SET expression block;
range: expression? DOT DOT expression;

// conditionals
conditional: r_if | assertion | r_while | for_loop | for_each_loop;
r_if: R_IF condition (statement | block) r_else?;
r_else: R_ELSE (statement | block);
assertion: ASSERT condition;
r_while: WHILE condition block;

component: ID | methodCall | literal | indexAccess | subSequence | R_NULL;
operator: DOT | ADD | SUB | DIV | MUL | MOD | AND | OR | BIT_AND | BIT_OR;
expression: component (operator expression)?;

condition: expression (comparator expression)?;
comparator: EQUAL | NOT_EQUAL | REF_EQUAL | REF_NOT_EQUAL | MORE_THAN | LESS_THAN | MORE_OR_EQUAL | LESS_OR_EQUAL;

variableDeclaration: details (SEPARATOR ID)* (SET expression)?;
assignment: expression ((SET expression) | operatorAssign);
operatorAssign: operator SET expression;
details: type MUTABLE? ID;
indexAccess: ID BRACE_OPEN expression BRACE_CLOSE;
subSequence: ID BRACE_OPEN range BRACE_CLOSE;

// method calls
methodCall: ID parameterSet;
parameterSet: PARAM_OPEN (expression (SEPARATOR expression)*)? PARAM_CLOSE;

// method definitions
methodDefinition: (details | ID MUTABLE?) paramSet block;
paramSet: PARAM_OPEN ((ID | param) (SEPARATOR param)*)? PARAM_CLOSE;
param: details (SET expression)?;

returnStatement: RETURN expression?;

type: (basetype | ID) (BRACE_OPEN BRACE_CLOSE)*;
basetype: BOOLEAN | BYTE | SHORT | CHAR | INT | FLOAT | LONG | DOUBLE | STRING;

qualifiedName: ID (DOT ID)*;

source: package? imports? clazz EOF;
package: PACKAGE qualifiedName;
imports: IMPORT BODY_OPEN qualifiedName+ BODY_CLOSE;
clazz: TYPE ID BODY_OPEN (constantDef | fieldDef | main | methodDefinition)* BODY_CLOSE;
constantDef: CONST ID SET expression;
fieldDef: variableDeclaration;
main: MAIN block;
