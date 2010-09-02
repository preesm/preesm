lexer grammar InternalIDLLanguage;
@header {
package org.ietr.preesm.editor.parser.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;
}

T11 : 'module' ;
T12 : '{' ;
T13 : '}' ;
T14 : ';' ;
T15 : 'typedef' ;
T16 : 'interface' ;
T17 : 'void' ;
T18 : '(' ;
T19 : ',' ;
T20 : ')' ;
T21 : 'int' ;
T22 : 'long' ;
T23 : 'char' ;
T24 : 'init' ;
T25 : 'loop' ;
T26 : 'end' ;
T27 : 'in' ;
T28 : 'out' ;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g" 763
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g" 765
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g" 767
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g" 769
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g" 771
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g" 773
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g" 775
RULE_ANY_OTHER : .;


