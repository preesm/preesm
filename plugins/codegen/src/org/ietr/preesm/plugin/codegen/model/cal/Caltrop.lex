/* Caltrop lexer

 Copyright (c) 2002 The Regents of the University of California.
 Copyright (c) 2007 Xilinx Inc.
 All rights reserved.
 Permission is hereby granted, without written agreement and without
 license or royalty fees, to use, copy, modify, and distribute this
 software and its documentation for any purpose, provided that the above
 copyright notice and the following two paragraphs appear in all copies
 of this software.

 IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
 FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
 ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
 PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 ENHANCEMENTS, OR MODIFICATIONS.

                                        PT_COPYRIGHT_VERSION_2
                                        COPYRIGHTENDKEY

@ProposedRating Red (Ed.Willink@uk.thalesgroup.com)
@AcceptedRating Red

EDW September 2002 Original 
EDW 18-Sep-2002 Lex # token too..

*/
package net.sf.opendf.cal.parser;

import java_cup.runtime.Symbol;
import java.lang.Integer;
import java.lang.String;

/**
Lexing class for Caltrop.
<p>
This JFlex lexer supports the CUP Parser via the java_cup.runtime.Scanner interface.
<p>
{Build as "java -jar ../../lib/jflex.jar Caltrop.lex").

@author Ed Willink
@version $Id: Caltrop.lex 57 2007-01-26 06:44:42Z jornj $
*/
%%

%public
%class Lexer
%cupsym Terminal
%cup

%{
    public final String kwAction = new String("action");
    public final String kwActor = new String("actor");
    public final String kwAll = new String("all");
    public final String kwAnd = new String("and");
    public final String kwAny = new String("any");
    public final String kwAssign = new String("assign");
    public final String kwAt = new String("at");
    public final String kwAtN = new String("at*");
    public final String kwBegin = new String("begin");
    public final String kwChoose = new String("choose");
    public final String kwConst = new String("const");
    public final String kwDefault = new String("default");
    public final String kwDelay = new String("delay");
    public final String kwDiv = new String("div");
    public final String kwDo = new String("do");
    public final String kwDom = new String("dom");
    public final String kwElse = new String("else");
    public final String kwEnd = new String("end");
    public final String kwEndAction = new String("endaction");
    public final String kwEndActor = new String("endactor");
    public final String kwEndAssign = new String("endassign");
    public final String kwEndBegin = new String("endbegin");
    public final String kwEndChoose = new String("endchoose");
    public final String kwEndForeach = new String("endforeach");
    public final String kwEndFunction = new String("endfunction");
    public final String kwEndIf = new String("endif");
    public final String kwEndInitialize = new String("endinitialize");
    public final String kwEndInvariant = new String("endinvariant");
    public final String kwEndLambda = new String("endlambda");
    public final String kwEndLet = new String("endlet");
    public final String kwEndPriority = new String("endpriority");
    public final String kwEndProc = new String("endproc");
    public final String kwEndProcedure = new String("endprocedure");
    public final String kwEndSchedule = new String("endschedule");
    public final String kwEndWhile = new String("endwhile");
    public final String kwEnsure = new String("ensure");
    public final String kwFalse = new String("false");
    public final String kwFor = new String("for");
    public final String kwForeach = new String("foreach");
    public final String kwFsm = new String("fsm");
    public final String kwFunction = new String("function");
    public final String kwGuard = new String("guard");
    public final String kwIf = new String("if");
    public final String kwImport = new String("import");
    public final String kwIn = new String("in");
    public final String kwInitialize = new String("initialize");
    public final String kwInvariant = new String("invariant");
    public final String kwLambda = new String("lambda");
    public final String kwLet = new String("let");
    public final String kwMap = new String("map");
    public final String kwMod = new String("mod");
    public final String kwMulti = new String("multi");
    public final String kwMutable = new String("mutable");
    public final String kwNot = new String("not");
    public final String kwNull = new String("null");
    public final String kwOld = new String("old");
    public final String kwOr = new String("or");
    public final String kwPackage = new String("package");
    public final String kwPriority = new String("priority");
    public final String kwProc = new String("proc");
    public final String kwProcedure = new String("procedure");
    public final String kwRegexp = new String("regexp");
    public final String kwRepeat = new String("repeat");
    public final String kwRequire = new String("require");
    public final String kwSchedule = new String("schedule");
    public final String kwThen = new String("then");
    public final String kwTime = new String("time");
    public final String kwTo = new String("to");
    public final String kwTrue = new String("true");
    public final String kwVar = new String("var");
    public final String kwWhile = new String("while");

    public final String opColon = new String(":");
    public final String opColonEquals = new String(":=");
    public final String opComma = new String(",");
    public final String opDashDashGt = new String("-->");
    public final String opDashGt = new String("->");
    public final String opDot = new String(".");
    public final String opDotDot = new String("..");
    public final String opEquals = new String("=");
    public final String opEqualsEqualsGt = new String("==>");
    public final String opGt = new String(">");
    public final String opHash = new String("#");
    public final String opLBrace = new String("{");
    public final String opLBrack = new String("[");
    public final String opLPar = new String("(");
    public final String opLt = new String("<");
    public final String opPlus = new String("+");
    public final String opQmark = new String("?");
    public final String opRBrace = new String("}");
    public final String opRBrack = new String("]");
    public final String opRPar = new String(")");
    public final String opSemi = new String(";");
    public final String opStar = new String("*");
    public final String opUnderScore = new String("_");
    public final String opVBar = new String("|");

    /*--INSERT-CONSTANTS--*/

    //
    //  Return 's' with escape sequences replaced by their binary equivalents.
    //
    static public String decode(String s) {
        int iMax = s.length();
        StringBuffer result = new StringBuffer(iMax);
        for (int i = 0; i < iMax; ) {
        
            char c = s.charAt(i);           // FIXME: where did the 16 bit Unicode characters go ?
            if (c != '\\') {
                result.append(c);
                i++;
            }
            else if ((i + 1 <= iMax) && (s.charAt(i+1) == 'b')) {
                result.append('\b');
                i += 2;
            }
            else if ((i + 1 <= iMax) && (s.charAt(i+1) == 'f')) {
                result.append('\f');
                i += 2;
            }
            else if ((i + 1 <= iMax) && (s.charAt(i+1) == 'n')) {
                result.append('\n');
                i += 2;
            }
            else if ((i + 1 <= iMax) && (s.charAt(i+1) == 'r')) {
                result.append('\r');
                i += 2;
            }
            else if ((i + 1 <= iMax) && (s.charAt(i+1) == 't')) {
                result.append('\t');
                i += 2;
            }
            else if ((i + 4 <= iMax)
                  && (('0' <= s.charAt(i+1)) && (s.charAt(i+1) <= '3'))
                  && (('0' <= s.charAt(i+2)) && (s.charAt(i+2) <= '7'))
                  && (('0' <= s.charAt(i+3)) && (s.charAt(i+3) <= '7'))) {
                String n = s.substring(i+1 , i+3);
                result.append((char)Integer.parseInt(n, 8));
                i += 4;
            }
            else if ((i + 3 <= iMax)
                  && (('0' <= s.charAt(i+1)) && (s.charAt(i+1) <= '7'))
                  && (('0' <= s.charAt(i+2)) && (s.charAt(i+2) <= '7'))) {
                String n = s.substring(i+1 , i+2);
                result.append((char)Integer.parseInt(n, 8));
                i += 3;
            }
            else if ((i + 2 <= iMax)
                  && (('0' <= s.charAt(i+1)) && (s.charAt(i+1) <= '7'))) {
                String n = s.substring(i+1 , i+1);
                result.append((char)Integer.parseInt(n, 8));
                i += 2;
            }
            else if ((i + 4 <= iMax) && ((s.charAt(i+1) == 'x') || (s.charAt(i+1) == 'X'))) {
                String n = s.substring(i+2 , i+3);
                result.append((char)Integer.parseInt(n, 16));
                i +=4;
            }
            else if ((i + 6 <= iMax) && (s.charAt(i+1) == 'u')) {
                String n = s.substring(i+2 , i+5);
                result.append((char)Integer.parseInt(n, 16));
                i += 6;
            }
            else {
                result.append(s.charAt(i+1));
                i += 2;
            }
        }
        return result.toString();
    }
%}

%line
%column
%unicode

SingleLineComment = ("//"[^\n\r]*[\n\r])
MultiLineComment = ("/*"([^*]|"*"[^/])*"*/")

DecimalLiteral = ([1-9][0-9]*)
HexLiteral = (0[xX][0-9A-Fa-f]+)
OctalLiteral = (0[0-7]*)
Integer = ({DecimalLiteral}|{HexLiteral}|{OctalLiteral})

Exponent = ([eE][+-]?[0-9]+)
Real = (([0-9]+"."[0-9]+{Exponent}?[fFdD]?) | ("."[0-9]+{Exponent}?[fFdD]?) | ([0-9]+{Exponent}[fFdD]?) | ([0-9]+{Exponent}?[fFdD]))

//EscapeChar = ("\\"[ntbrf\\'\"]|([0-7][0-7]?)|([0-3][0-7][0-7]))
EscapeChar = ("\\"( [ntbrf\\'\"] | ([0-7][0-7]?)|([0-3][0-7][0-7])))
Character = ("'"([^'\\\n\r]|{EscapeChar})"'")
UnterminatedCharacter = ("'"([^'\\\n\r]|{EscapeChar})[\n\r])
String = ("\""([^\"\\\n\r]|{EscapeChar})*"\"")
UnterminatedString = ("\""([^\"\\\n\r]|{EscapeChar})*[\n\r])

Digit=([0-9]
     | [\u0660-\u0669] | [\u06f0-\u06f9]
     | [\u0966-\u096f] | [\u09e6-\u09ef]
     | [\u0a66-\u0a6f] | [\u0ae6-\u0aef]
     | [\u0b66-\u0b6f] | [\u0be7-\u0bef]
     | [\u0c66-\u0c6f] | [\u0ce6-\u0cef]
     | [\u0d66-\u0d6f]
     | [\u0e50-\u0e59] | [\u0ed0-\u0ed9]
     | [\u1040-\u1049])
Letter=([$A-Z_a-z]
     | [\u00c0-\u00d6] | [\u00d8-\u00f6] |[\u00f8-\u00ff]
     | [\u0100-\u1fff]
     | [\u3040-\u318f] | [\u3300-\u337f] | [\u3400-\u3d2d]
     | [\u4e00-\u9fff] | [\uf900-\ufaff])
SimpleId = ({Letter}({Letter}|{Digit})*)
EscapedId = ("\\"([^\"\\\n\r]|{EscapeChar})*"\\")

OpLetter = ([-!@#$%\^&*/+?~|<=>]|\u00c5|\u00e5|\u00d8|\u00f8|\u00d6|\u00f6)
Op = ({OpLetter}+)

%%
"action" { return new Symbol(Terminal.ACTION, yyline+1, yycolumn+1, kwAction); }
"actor" { return new Symbol(Terminal.ACTOR, yyline+1, yycolumn+1, kwActor); }
"all" { return new Symbol(Terminal.ALL, yyline+1, yycolumn+1, kwAll); }
"and" { return new Symbol(Terminal.AND, yyline+1, yycolumn+1, kwAnd); }
"any" { return new Symbol(Terminal.ANY, yyline+1, yycolumn+1, kwAny); }
"assign" { return new Symbol(Terminal.ASSIGN, yyline+1, yycolumn+1, kwAssign); }
"at" { return new Symbol(Terminal.AT, yyline+1, yycolumn+1, kwAt); }
"at*" { return new Symbol(Terminal.ATN, yyline+1, yycolumn+1, kwAtN); }
"begin" { return new Symbol(Terminal.BEGIN, yyline+1, yycolumn+1, kwBegin); }
"const" { return new Symbol(Terminal.CONST, yyline+1, yycolumn+1, kwConst); }
"choose" { return new Symbol(Terminal.CHOOSE, yyline+1, yycolumn+1, kwChoose); }
"default" { return new Symbol(Terminal.DEFAULT, yyline+1, yycolumn+1, kwDefault); }
"delay" { return new Symbol(Terminal.DELAY, yyline+1, yycolumn+1, kwDelay); }
"div" { return new Symbol(Terminal.DIV, yyline+1, yycolumn+1, kwDiv); }
"do" { return new Symbol(Terminal.DO, yyline+1, yycolumn+1, kwDo); }
"dom" { return new Symbol(Terminal.DOM, yyline+1, yycolumn+1, kwDom); }
"else" { return new Symbol(Terminal.ELSE, yyline+1, yycolumn+1, kwElse); }
"end" { return new Symbol(Terminal.END, yyline+1, yycolumn+1, kwEnd); }
"endaction" { return new Symbol(Terminal.END_ACTION, yyline+1, yycolumn+1, kwEndAction); }
"endactor" { return new Symbol(Terminal.END_ACTOR, yyline+1, yycolumn+1, kwEndActor); }
"endassign" { return new Symbol(Terminal.END_ASSIGN, yyline+1, yycolumn+1, kwEndAssign); }
"endbegin" { return new Symbol(Terminal.END_BEGIN, yyline+1, yycolumn+1, kwEndBegin); }
"endchoose" { return new Symbol(Terminal.END_CHOOSE, yyline+1, yycolumn+1, kwEndChoose); }
"endforeach" { return new Symbol(Terminal.END_FOREACH, yyline+1, yycolumn+1, kwEndForeach); }
"endfunction" { return new Symbol(Terminal.END_FUNCTION, yyline+1, yycolumn+1, kwEndFunction); }
"endif" { return new Symbol(Terminal.END_IF, yyline+1, yycolumn+1, kwEndIf); }
"endinitialize" { return new Symbol(Terminal.END_INITIALIZE, yyline+1, yycolumn+1, kwEndInitialize); }
"endinvariant" { return new Symbol(Terminal.END_INVARIANT, yyline+1, yycolumn+1, kwEndInvariant); }
"endlambda" { return new Symbol(Terminal.END_LAMBDA, yyline+1, yycolumn+1, kwEndLambda); }
"endlet" { return new Symbol(Terminal.END_LET, yyline+1, yycolumn+1, kwEndLet); }
"endpriority" { return new Symbol(Terminal.END_PRIORITY, yyline+1, yycolumn+1, kwEndPriority); }
"endproc" { return new Symbol(Terminal.END_PROC, yyline+1, yycolumn+1, kwEndProc); }
"endprocedure" { return new Symbol(Terminal.END_PROCEDURE, yyline+1, yycolumn+1, kwEndProcedure); }
"endschedule" { return new Symbol(Terminal.END_SCHEDULE, yyline+1, yycolumn+1, kwEndSchedule); }
"endwhile" { return new Symbol(Terminal.END_WHILE, yyline+1, yycolumn+1, kwEndWhile); }
"ensure" { return new Symbol(Terminal.ENSURE, yyline+1, yycolumn+1, kwEnsure); }
"false" { return new Symbol(Terminal.FALSE, yyline+1, yycolumn+1, kwFalse); }
"for" { return new Symbol(Terminal.FOR, yyline+1, yycolumn+1, kwFor); }
"foreach" { return new Symbol(Terminal.FOREACH, yyline+1, yycolumn+1, kwForeach); }
"fsm" { return new Symbol(Terminal.FSM, yyline+1, yycolumn+1, kwFunction); }
"function" { return new Symbol(Terminal.FUNCTION, yyline+1, yycolumn+1, kwFunction); }
"guard" { return new Symbol(Terminal.GUARD, yyline+1, yycolumn+1, kwGuard); }
"if" { return new Symbol(Terminal.IF, yyline+1, yycolumn+1, kwIf); }
"import" { return new Symbol(Terminal.IMPORT, yyline+1, yycolumn+1, kwImport); }
"in" { return new Symbol(Terminal.IN, yyline+1, yycolumn+1, kwIn); }
"initialize" { return new Symbol(Terminal.INITIALIZE, yyline+1, yycolumn+1, kwInitialize); }
"invariant" { return new Symbol(Terminal.INVARIANT, yyline+1, yycolumn+1, kwInvariant); }
"lambda" { return new Symbol(Terminal.LAMBDA, yyline+1, yycolumn+1, kwLambda); }
"let" { return new Symbol(Terminal.LET, yyline+1, yycolumn+1, kwLet); }
"map" { return new Symbol(Terminal.MAP, yyline+1, yycolumn+1, kwMap); }
"mod" { return new Symbol(Terminal.MOD, yyline+1, yycolumn+1, kwMod); }
"multi" { return new Symbol(Terminal.MULTI, yyline+1, yycolumn+1, kwMulti); }
"mutable" { return new Symbol(Terminal.MUTABLE, yyline+1, yycolumn+1, kwMutable); }
"not" { return new Symbol(Terminal.NOT, yyline+1, yycolumn+1, kwNot); }
"null" { return new Symbol(Terminal.NULL, yyline+1, yycolumn+1, kwNull); }
"old" { return new Symbol(Terminal.OLD, yyline+1, yycolumn+1, kwOld); }
"or" { return new Symbol(Terminal.OR, yyline+1, yycolumn+1, kwOr); }
"package" { return new Symbol(Terminal.PACKAGE, yyline+1, yycolumn+1, kwPackage); }
"priority" { return new Symbol(Terminal.PRIORITY, yyline+1, yycolumn+1, kwPriority); }
"proc" { return new Symbol(Terminal.PROC, yyline+1, yycolumn+1, kwProc); }
"procedure" { return new Symbol(Terminal.PROCEDURE, yyline+1, yycolumn+1, kwProcedure); }
"regexp" { return new Symbol(Terminal.REGEXP, yyline+1, yycolumn+1, kwRepeat); }
"repeat" { return new Symbol(Terminal.REPEAT, yyline+1, yycolumn+1, kwRepeat); }
"require" { return new Symbol(Terminal.REQUIRE, yyline+1, yycolumn+1, kwRequire); }
"schedule" { return new Symbol(Terminal.SCHEDULE, yyline+1, yycolumn+1, kwSchedule); }
"then" { return new Symbol(Terminal.THEN, yyline+1, yycolumn+1, kwThen); }
"time" { return new Symbol(Terminal.TIME, yyline+1, yycolumn+1, kwTime); }
"to" { return new Symbol(Terminal.TO, yyline+1, yycolumn+1, kwTo); }
"true" { return new Symbol(Terminal.TRUE, yyline+1, yycolumn+1, kwTrue); }
"var" { return new Symbol(Terminal.VAR, yyline+1, yycolumn+1, kwVar); }
"while" { return new Symbol(Terminal.WHILE, yyline+1, yycolumn+1, kwWhile); }

/*--INSERT-KEYWORDS--*/

":" { return new Symbol(Terminal.COLON, yyline+1, yycolumn+1, opColon); }
":=" { return new Symbol(Terminal.COLON_EQUALS, yyline+1, yycolumn+1, opColonEquals); }
"," { return new Symbol(Terminal.COMMA, yyline+1, yycolumn+1, opComma); }
"-->" { return new Symbol(Terminal.DASH_DASH_GT, yyline+1, yycolumn+1, opDashDashGt); }
"->" { return new Symbol(Terminal.DASH_GT, yyline+1, yycolumn+1, opDashGt); }
"." { return new Symbol(Terminal.DOT, yyline+1, yycolumn+1, opDot); }
".." { return new Symbol(Terminal.DOTDOT, yyline+1, yycolumn+1, opDotDot); }
"=" { return new Symbol(Terminal.EQUALS, yyline+1, yycolumn+1, opEquals); }
"==>" { return new Symbol(Terminal.EQUALS_EQUALS_GT, yyline+1, yycolumn+1, opEqualsEqualsGt); }
"#" { return new Symbol(Terminal.HASH, yyline+1, yycolumn+1, opHash); }
"{" { return new Symbol(Terminal.LBRACE, yyline+1, yycolumn+1, opLBrace); }
"[" { return new Symbol(Terminal.LBRACK, yyline+1, yycolumn+1, opLBrack); }
"(" { return new Symbol(Terminal.LPAR, yyline+1, yycolumn+1, opLPar); }
"<" { return new Symbol(Terminal.LT, yyline+1, yycolumn+1, opLt); }
">" { return new Symbol(Terminal.GT, yyline+1, yycolumn+1, opGt); }
"+" { return new Symbol(Terminal.PLUS, yyline+1, yycolumn+1, opPlus); }
"?" { return new Symbol(Terminal.QMARK, yyline+1, yycolumn+1, opQmark); }
"}" { return new Symbol(Terminal.RBRACE, yyline+1, yycolumn+1, opRBrace); }
"]" { return new Symbol(Terminal.RBRACK, yyline+1, yycolumn+1, opRBrack); }
")" { return new Symbol(Terminal.RPAR, yyline+1, yycolumn+1, opRPar); }
";" { return new Symbol(Terminal.SEMI, yyline+1, yycolumn+1, opSemi); }
"*" { return new Symbol(Terminal.STAR, yyline+1, yycolumn+1, opStar); }
"_" { return new Symbol(Terminal.UNDER_SCORE, yyline+1, yycolumn+1, opUnderScore); }
"|" { return new Symbol(Terminal.VBAR, yyline+1, yycolumn+1, opVBar); }

/*--INSERT-PUNCTUATION--*/

{MultiLineComment} { /* ignore multi-line comment. */ }
{SingleLineComment} { /* ignore single line comment. */ }

{Character} { return new Symbol(Terminal.Character, yyline+1, yycolumn+1,
                            decode(yytext().substring(1, yytext().length()-1))); }
{Integer} { return new Symbol(Terminal.Integer, yyline+1, yycolumn+1, yytext()); }
{Real} { return new Symbol(Terminal.Real, yyline+1, yycolumn+1, yytext()); }
{Op} { return new Symbol(Terminal.PartialOp, yyline+1, yycolumn+1, yytext()); }
{EscapedId} { return new Symbol(Terminal.Id, yyline+1, yycolumn+1,
                            decode(yytext().substring(1, yytext().length()-1))); }
{SimpleId} { return new Symbol(Terminal.Id, yyline+1, yycolumn+1, yytext()); }
{String} { return new Symbol(Terminal.String, yyline+1, yycolumn+1,
                            decode(yytext().substring(1, yytext().length()-1))); }
{UnterminatedCharacter} { return new Symbol(Terminal.UnterminatedCharacter, yyline+1, yycolumn+1,
                            decode(yytext().substring(1, yytext().length()))); }
{UnterminatedString} { return new Symbol(Terminal.UnterminatedString, yyline+1, yycolumn+1,
                            decode(yytext().substring(1, yytext().length()))); }

[ \t\r\n\f]* { /* ignore white space. */ }

. { return new Symbol(Terminal.IllegalCharacter, yyline+1, yycolumn+1, yytext()); }
