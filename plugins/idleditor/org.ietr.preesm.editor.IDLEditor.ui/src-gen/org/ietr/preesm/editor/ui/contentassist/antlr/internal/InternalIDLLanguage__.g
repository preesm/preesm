lexer grammar InternalIDLLanguage;
@header {
package org.ietr.preesm.editor.ui.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.Lexer;
}

T11 : 'datatype' ;
T12 : 'entity' ;
T13 : '{' ;
T14 : '}' ;
T15 : 'extends' ;
T16 : ':' ;
T17 : '*' ;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 878
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 880
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 882
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 884
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 886
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 888
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g" 890
RULE_ANY_OTHER : .;


