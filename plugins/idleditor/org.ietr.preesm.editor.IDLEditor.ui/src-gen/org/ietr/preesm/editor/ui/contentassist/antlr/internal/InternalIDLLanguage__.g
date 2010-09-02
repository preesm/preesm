lexer grammar InternalIDLLanguage;
@header {
package org.ietr.preesm.editor.ui.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.Lexer;
}

T11 : 'int' ;
T12 : 'long' ;
T13 : 'char' ;
T14 : 'init' ;
T15 : 'loop' ;
T16 : 'end' ;
T17 : 'in' ;
T18 : 'out' ;
T19 : 'module' ;
T20 : '{' ;
T21 : '}' ;
T22 : ';' ;
T23 : 'typedef' ;
T24 : 'interface' ;
T25 : 'void' ;
T26 : '(' ;
T27 : ')' ;
T28 : ',' ;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 1634
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 1636
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 1638
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 1640
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 1642
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 1644
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 1646
RULE_ANY_OTHER : .;


