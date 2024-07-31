lexer grammar FizLexer;

@header {
package dev.fiz.bootstrap.antlr;
}

channels {
    COMMENTS,
    WHITESPACE
}

// send comments to separate channel
COMMENT: '//' .*? '\n' -> channel(COMMENTS);
BLOCK_COMMENT: '/*' .*? '*/' -> channel(COMMENTS);

// send whitespace to separate channel
WS : [ \t\r\n]+ -> channel(WHITESPACE);

QUOTE: '"';
STRING_LIT: QUOTE (~["\\] | '\\' .)* QUOTE;

// keywords
ASSERT: 'assert';
CONST: 'const';
FOR: 'for';
MAIN: 'main';
PACKAGE: 'package';
RETURN: 'return';
R_ELSE: 'else';
R_IF: 'if';
R_NULL: 'null';
TYPE: 'type';
IMPORT: 'import';
WHILE: 'while';

// base types
BOOLEAN: 'boolean';
BYTE: 'i8';
SHORT: 'i16';
CHAR: 'char';
INT: 'i32';
FLOAT: 'f32';
LONG: 'i64';
DOUBLE: 'f64';
STRING: 'string';

// boolean values
FALSE: 'false';
TRUE: 'true';

// syntax
SET: '=';
BODY_CLOSE: '}'; // closing bracket
BODY_OPEN: '{'; // opening bracket
BRACE_CLOSE: ']';
BRACE_OPEN: '[';
DOT: '.';
MUTABLE: '~';
PARAM_CLOSE: ')'; // closing paren
PARAM_OPEN: '('; // opening paren
SEMICOLON: ';';
SEPARATOR: ',';

// comparator
EQUAL: '==';
LESS_OR_EQUAL: '<=';
LESS_THAN: '<';
MORE_OR_EQUAL: '>=';
MORE_THAN: '>';
NOT_EQUAL: '!=';
REF_EQUAL: '@';
REF_NOT_EQUAL: '!@';

/*
// operator assignments
ADD_SET: ADD SET;
SUB_SET: SUB SET;
DIV_SET: DIV SET;
MUL_SET: MUL SET;
MOD_SET: MOD SET;
*/

// operators
ADD: '+';
SUB: '-';
DIV: '/';
MUL: '*';
MOD: '%';

// logical combinations
AND: '&&';
OR: '||';

// bitwise combinations
BIT_AND: '&';
BIT_OR: '|';

DIGIT               : '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9';
fragment UPLETTER   : [A-Z];
fragment DNLETTER   : [a-z];
fragment LETTER     : UPLETTER | DNLETTER;
fragment ALPHANUM   : LETTER | DIGIT;
UNDERSCORE : '_';
fragment DNTEXT     : DNLETTER+;

// identifiers can be any combination of alphanumeric characters
ID: LETTER (ALPHANUM | UNDERSCORE)*;

// any single character wrapped in single quotes
CHAR_LIT: '\'' . '\'';
