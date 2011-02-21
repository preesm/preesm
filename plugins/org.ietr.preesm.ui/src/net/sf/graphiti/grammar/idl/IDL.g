/*
 * Copyright (c) 2008, IETR/INSA of Rennes
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the IETR/INSA of Rennes nor the names of its
 *     contributors may be used to endorse or promote products derived from this
 *     software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

grammar IDL;
options {
  output = AST;
  k=1;
}
tokens {
Module;
Parameter;
Id;
InputArgument;
OutputArgument;
}

@lexer::header {
package net.sf.graphiti.grammar.idl;
}

@parser::header {
package net.sf.graphiti.grammar.idl;
}



module	:	MODULE id=ID LBRACE moduleContent RBRACE SEMICOLON EOF -> ^(Module ^(Id ID) moduleContent);

moduleContent	: ((typedef) | 
		(moduleInterface -> moduleInterface?))* ;

typedef	: TYPEDEF type (ID | PARAMETER) SEMICOLON;

moduleInterface: INTERFACE ID LBRACE (prototype SEMICOLON)* RBRACE SEMICOLON -> prototype ; 

prototype : type ID LPAREN (arg) (COMMA (arg))*  RPAREN  -> (arg )+ ;

arg 	: (OUT type ID -> ^(OutputArgument ^(Id ID)))
	| (IN ((PARAMETER ID -> ^(Parameter ^(Id ID))) | (type ID -> ^(InputArgument ^(Id ID)))));

type	:(CHAR |INT | LONG | BOOLEAN | ANY | VOID);

MODULE	: 'module' ;
INTERFACE: 'interface';
TYPEDEF	:	'typedef';
PARAMETER :	 'parameter';

IN	:	'in';
OUT	:	'out';
INOUT	:	'inout';

CHAR	:	'char';
INT	:	'int';
LONG	:	'long';
ANY	:	'any';
BOOLEAN	:	'boolean';
VOID	:	'void';
	

TRUE: 'true';
FALSE: 'false';

ID: ('a'..'z' | 'A'..'Z' | '_' | '$') ('a'..'z' | 'A'..'Z' | '_' | '$' | '0' .. '9')* ;
FLOAT: '-'? (('0'..'9')+ '.' ('0'..'9')* (('e' | 'E') ('+' | '-')? ('0'..'9')+)?
	| '.' ('0'..'9')+ (('e' | 'E') ('+' | '-')? ('0'..'9')+)?
	| ('0'..'9')+ (('e' | 'E') ('+' | '-')? ('0'..'9')+));
INTEGER: '-'? ('0'..'9')+ ;
STRING: '\"' .* '\"';

LINE_COMMENT: '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;};
MULTI_LINE_COMMENT: '/*' .* '*/' {$channel=HIDDEN;};
WHITESPACE: (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;};

EQ: '=';
GE: '>=';
GT: '>';
LE: '<=';
LT: '<';
NE: '!=';

ARROW: '->';
COLON: ':';
COLON_EQUAL: ':=';
COMMA: ',';
DOT: '.';
DOUBLE_DASH_ARROW: '-->';
DOUBLE_EQUAL_ARROW: '==>';
DOUBLE_DOT: '..';
DOUBLE_COLON: '::';

LBRACE: '{';
RBRACE: '}';
LBRACKET: '[';
RBRACKET: ']';
LPAREN: '(';
RPAREN: ')';

CARET: '^';
DIV: '/';
MINUS: '-';
PLUS: '+';
TIMES: '*';

SEMICOLON: ';';
SHARP: '#';
