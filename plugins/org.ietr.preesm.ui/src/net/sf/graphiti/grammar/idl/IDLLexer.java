// $ANTLR 3.1.2 D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g 2009-06-29 17:33:25

package net.sf.graphiti.grammar.idl;


import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

public class IDLLexer extends Lexer {
    public static final int LT=41;
    public static final int InputArgument=7;
    public static final int Module=4;
    public static final int LBRACE=11;
    public static final int CHAR=22;
    public static final int FLOAT=31;
    public static final int ID=10;
    public static final int EOF=-1;
    public static final int DOUBLE_DOT=49;
    public static final int LPAREN=17;
    public static final int INOUT=28;
    public static final int LBRACKET=51;
    public static final int RPAREN=19;
    public static final int BOOLEAN=25;
    public static final int COLON_EQUAL=45;
    public static final int IN=21;
    public static final int COMMA=18;
    public static final int CARET=53;
    public static final int PARAMETER=15;
    public static final int PLUS=56;
    public static final int VOID=27;
    public static final int RBRACKET=52;
    public static final int EQ=37;
    public static final int DOT=46;
    public static final int Id=6;
    public static final int OutputArgument=8;
    public static final int NE=42;
    public static final int DOUBLE_EQUAL_ARROW=48;
    public static final int INTEGER=32;
    public static final int DOUBLE_DASH_ARROW=47;
    public static final int GE=38;
    public static final int SHARP=58;
    public static final int RBRACE=12;
    public static final int LINE_COMMENT=34;
    public static final int TYPEDEF=14;
    public static final int WHITESPACE=36;
    public static final int SEMICOLON=13;
    public static final int INT=23;
    public static final int MINUS=55;
    public static final int MODULE=9;
    public static final int TRUE=29;
    public static final int MULTI_LINE_COMMENT=35;
    public static final int Parameter=5;
    public static final int COLON=44;
    public static final int ANY=26;
    public static final int OUT=20;
    public static final int DOUBLE_COLON=50;
    public static final int ARROW=43;
    public static final int GT=39;
    public static final int INTERFACE=16;
    public static final int DIV=54;
    public static final int TIMES=57;
    public static final int FALSE=30;
    public static final int LONG=24;
    public static final int LE=40;
    public static final int STRING=33;

    // delegates
    // delegators

    public IDLLexer() {;} 
    public IDLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public IDLLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g"; }

    // $ANTLR start "MODULE"
    public final void mMODULE() throws RecognitionException {
        try {
            int _type = MODULE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:69:8: ( 'module' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:69:10: 'module'
            {
            match("module"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MODULE"

    // $ANTLR start "INTERFACE"
    public final void mINTERFACE() throws RecognitionException {
        try {
            int _type = INTERFACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:70:10: ( 'interface' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:70:12: 'interface'
            {
            match("interface"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INTERFACE"

    // $ANTLR start "TYPEDEF"
    public final void mTYPEDEF() throws RecognitionException {
        try {
            int _type = TYPEDEF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:71:9: ( 'typedef' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:71:11: 'typedef'
            {
            match("typedef"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TYPEDEF"

    // $ANTLR start "PARAMETER"
    public final void mPARAMETER() throws RecognitionException {
        try {
            int _type = PARAMETER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:72:11: ( 'parameter' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:72:14: 'parameter'
            {
            match("parameter"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PARAMETER"

    // $ANTLR start "IN"
    public final void mIN() throws RecognitionException {
        try {
            int _type = IN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:74:4: ( 'in' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:74:6: 'in'
            {
            match("in"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IN"

    // $ANTLR start "OUT"
    public final void mOUT() throws RecognitionException {
        try {
            int _type = OUT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:75:5: ( 'out' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:75:7: 'out'
            {
            match("out"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OUT"

    // $ANTLR start "INOUT"
    public final void mINOUT() throws RecognitionException {
        try {
            int _type = INOUT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:76:7: ( 'inout' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:76:9: 'inout'
            {
            match("inout"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INOUT"

    // $ANTLR start "CHAR"
    public final void mCHAR() throws RecognitionException {
        try {
            int _type = CHAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:78:6: ( 'char' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:78:8: 'char'
            {
            match("char"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CHAR"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:79:5: ( 'int' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:79:7: 'int'
            {
            match("int"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "LONG"
    public final void mLONG() throws RecognitionException {
        try {
            int _type = LONG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:80:6: ( 'long' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:80:8: 'long'
            {
            match("long"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LONG"

    // $ANTLR start "ANY"
    public final void mANY() throws RecognitionException {
        try {
            int _type = ANY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:81:5: ( 'any' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:81:7: 'any'
            {
            match("any"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ANY"

    // $ANTLR start "BOOLEAN"
    public final void mBOOLEAN() throws RecognitionException {
        try {
            int _type = BOOLEAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:82:9: ( 'boolean' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:82:11: 'boolean'
            {
            match("boolean"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BOOLEAN"

    // $ANTLR start "VOID"
    public final void mVOID() throws RecognitionException {
        try {
            int _type = VOID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:83:6: ( 'void' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:83:8: 'void'
            {
            match("void"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VOID"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:86:5: ( 'true' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:86:7: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:87:6: ( 'false' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:87:8: 'false'
            {
            match("false"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:89:3: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '0' .. '9' )* )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:89:5: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '0' .. '9' )*
            {
            if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:89:39: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '0' .. '9' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='$'||(LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:
            	    {
            	    if ( input.LA(1)=='$'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:6: ( ( '-' )? ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )? | '.' ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )? | ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ ) ) )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:8: ( '-' )? ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )? | '.' ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )? | ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ ) )
            {
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:8: ( '-' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='-') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:8: '-'
                    {
                    match('-'); 

                    }
                    break;

            }

            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:13: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )? | '.' ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )? | ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ ) )
            int alt15=3;
            alt15 = dfa15.predict(input);
            switch (alt15) {
                case 1 :
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:14: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )?
                    {
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:14: ( '0' .. '9' )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:15: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt3 >= 1 ) break loop3;
                                EarlyExitException eee =
                                    new EarlyExitException(3, input);
                                throw eee;
                        }
                        cnt3++;
                    } while (true);

                    match('.'); 
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:30: ( '0' .. '9' )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:31: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);

                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:42: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0=='E'||LA7_0=='e') ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:43: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
                            {
                            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                                input.consume();

                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;}

                            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:55: ( '+' | '-' )?
                            int alt5=2;
                            int LA5_0 = input.LA(1);

                            if ( (LA5_0=='+'||LA5_0=='-') ) {
                                alt5=1;
                            }
                            switch (alt5) {
                                case 1 :
                                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:
                                    {
                                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                        input.consume();

                                    }
                                    else {
                                        MismatchedSetException mse = new MismatchedSetException(null,input);
                                        recover(mse);
                                        throw mse;}


                                    }
                                    break;

                            }

                            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:68: ( '0' .. '9' )+
                            int cnt6=0;
                            loop6:
                            do {
                                int alt6=2;
                                int LA6_0 = input.LA(1);

                                if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                                    alt6=1;
                                }


                                switch (alt6) {
                            	case 1 :
                            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:90:69: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); 

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt6 >= 1 ) break loop6;
                                        EarlyExitException eee =
                                            new EarlyExitException(6, input);
                                        throw eee;
                                }
                                cnt6++;
                            } while (true);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:91:4: '.' ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )?
                    {
                    match('.'); 
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:91:8: ( '0' .. '9' )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:91:9: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt8 >= 1 ) break loop8;
                                EarlyExitException eee =
                                    new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);

                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:91:20: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0=='E'||LA11_0=='e') ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:91:21: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
                            {
                            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                                input.consume();

                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;}

                            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:91:33: ( '+' | '-' )?
                            int alt9=2;
                            int LA9_0 = input.LA(1);

                            if ( (LA9_0=='+'||LA9_0=='-') ) {
                                alt9=1;
                            }
                            switch (alt9) {
                                case 1 :
                                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:
                                    {
                                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                        input.consume();

                                    }
                                    else {
                                        MismatchedSetException mse = new MismatchedSetException(null,input);
                                        recover(mse);
                                        throw mse;}


                                    }
                                    break;

                            }

                            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:91:46: ( '0' .. '9' )+
                            int cnt10=0;
                            loop10:
                            do {
                                int alt10=2;
                                int LA10_0 = input.LA(1);

                                if ( ((LA10_0>='0' && LA10_0<='9')) ) {
                                    alt10=1;
                                }


                                switch (alt10) {
                            	case 1 :
                            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:91:47: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); 

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt10 >= 1 ) break loop10;
                                        EarlyExitException eee =
                                            new EarlyExitException(10, input);
                                        throw eee;
                                }
                                cnt10++;
                            } while (true);


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:92:4: ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
                    {
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:92:4: ( '0' .. '9' )+
                    int cnt12=0;
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( ((LA12_0>='0' && LA12_0<='9')) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:92:5: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt12 >= 1 ) break loop12;
                                EarlyExitException eee =
                                    new EarlyExitException(12, input);
                                throw eee;
                        }
                        cnt12++;
                    } while (true);

                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:92:16: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:92:17: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
                    {
                    if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:92:29: ( '+' | '-' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0=='+'||LA13_0=='-') ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:
                            {
                            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                input.consume();

                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;}


                            }
                            break;

                    }

                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:92:42: ( '0' .. '9' )+
                    int cnt14=0;
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( ((LA14_0>='0' && LA14_0<='9')) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:92:43: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt14 >= 1 ) break loop14;
                                EarlyExitException eee =
                                    new EarlyExitException(14, input);
                                throw eee;
                        }
                        cnt14++;
                    } while (true);


                    }


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "INTEGER"
    public final void mINTEGER() throws RecognitionException {
        try {
            int _type = INTEGER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:93:8: ( ( '-' )? ( '0' .. '9' )+ )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:93:10: ( '-' )? ( '0' .. '9' )+
            {
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:93:10: ( '-' )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0=='-') ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:93:10: '-'
                    {
                    match('-'); 

                    }
                    break;

            }

            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:93:15: ( '0' .. '9' )+
            int cnt17=0;
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0>='0' && LA17_0<='9')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:93:16: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt17 >= 1 ) break loop17;
                        EarlyExitException eee =
                            new EarlyExitException(17, input);
                        throw eee;
                }
                cnt17++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INTEGER"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:94:7: ( '\\\"' ( . )* '\\\"' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:94:9: '\\\"' ( . )* '\\\"'
            {
            match('\"'); 
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:94:14: ( . )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0=='\"') ) {
                    alt18=2;
                }
                else if ( ((LA18_0>='\u0000' && LA18_0<='!')||(LA18_0>='#' && LA18_0<='\uFFFF')) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:94:14: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "LINE_COMMENT"
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:96:13: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:96:15: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match("//"); 

            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:96:20: (~ ( '\\n' | '\\r' ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( ((LA19_0>='\u0000' && LA19_0<='\t')||(LA19_0>='\u000B' && LA19_0<='\f')||(LA19_0>='\u000E' && LA19_0<='\uFFFF')) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:96:20: ~ ( '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:96:34: ( '\\r' )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0=='\r') ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:96:34: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 
            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LINE_COMMENT"

    // $ANTLR start "MULTI_LINE_COMMENT"
    public final void mMULTI_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = MULTI_LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:97:19: ( '/*' ( . )* '*/' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:97:21: '/*' ( . )* '*/'
            {
            match("/*"); 

            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:97:26: ( . )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0=='*') ) {
                    int LA21_1 = input.LA(2);

                    if ( (LA21_1=='/') ) {
                        alt21=2;
                    }
                    else if ( ((LA21_1>='\u0000' && LA21_1<='.')||(LA21_1>='0' && LA21_1<='\uFFFF')) ) {
                        alt21=1;
                    }


                }
                else if ( ((LA21_0>='\u0000' && LA21_0<=')')||(LA21_0>='+' && LA21_0<='\uFFFF')) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:97:26: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

            match("*/"); 

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MULTI_LINE_COMMENT"

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:98:11: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:98:13: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHITESPACE"

    // $ANTLR start "EQ"
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:100:3: ( '=' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:100:5: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQ"

    // $ANTLR start "GE"
    public final void mGE() throws RecognitionException {
        try {
            int _type = GE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:101:3: ( '>=' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:101:5: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GE"

    // $ANTLR start "GT"
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:102:3: ( '>' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:102:5: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GT"

    // $ANTLR start "LE"
    public final void mLE() throws RecognitionException {
        try {
            int _type = LE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:103:3: ( '<=' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:103:5: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LE"

    // $ANTLR start "LT"
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:104:3: ( '<' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:104:5: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LT"

    // $ANTLR start "NE"
    public final void mNE() throws RecognitionException {
        try {
            int _type = NE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:105:3: ( '!=' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:105:5: '!='
            {
            match("!="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NE"

    // $ANTLR start "ARROW"
    public final void mARROW() throws RecognitionException {
        try {
            int _type = ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:107:6: ( '->' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:107:8: '->'
            {
            match("->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ARROW"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:108:6: ( ':' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:108:8: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "COLON_EQUAL"
    public final void mCOLON_EQUAL() throws RecognitionException {
        try {
            int _type = COLON_EQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:109:12: ( ':=' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:109:14: ':='
            {
            match(":="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON_EQUAL"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:110:6: ( ',' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:110:8: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:111:4: ( '.' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:111:6: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOT"

    // $ANTLR start "DOUBLE_DASH_ARROW"
    public final void mDOUBLE_DASH_ARROW() throws RecognitionException {
        try {
            int _type = DOUBLE_DASH_ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:112:18: ( '-->' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:112:20: '-->'
            {
            match("-->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_DASH_ARROW"

    // $ANTLR start "DOUBLE_EQUAL_ARROW"
    public final void mDOUBLE_EQUAL_ARROW() throws RecognitionException {
        try {
            int _type = DOUBLE_EQUAL_ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:113:19: ( '==>' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:113:21: '==>'
            {
            match("==>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_EQUAL_ARROW"

    // $ANTLR start "DOUBLE_DOT"
    public final void mDOUBLE_DOT() throws RecognitionException {
        try {
            int _type = DOUBLE_DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:114:11: ( '..' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:114:13: '..'
            {
            match(".."); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_DOT"

    // $ANTLR start "DOUBLE_COLON"
    public final void mDOUBLE_COLON() throws RecognitionException {
        try {
            int _type = DOUBLE_COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:115:13: ( '::' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:115:15: '::'
            {
            match("::"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_COLON"

    // $ANTLR start "LBRACE"
    public final void mLBRACE() throws RecognitionException {
        try {
            int _type = LBRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:117:7: ( '{' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:117:9: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LBRACE"

    // $ANTLR start "RBRACE"
    public final void mRBRACE() throws RecognitionException {
        try {
            int _type = RBRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:118:7: ( '}' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:118:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RBRACE"

    // $ANTLR start "LBRACKET"
    public final void mLBRACKET() throws RecognitionException {
        try {
            int _type = LBRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:119:9: ( '[' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:119:11: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LBRACKET"

    // $ANTLR start "RBRACKET"
    public final void mRBRACKET() throws RecognitionException {
        try {
            int _type = RBRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:120:9: ( ']' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:120:11: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RBRACKET"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:121:7: ( '(' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:121:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:122:7: ( ')' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:122:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "CARET"
    public final void mCARET() throws RecognitionException {
        try {
            int _type = CARET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:124:6: ( '^' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:124:8: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CARET"

    // $ANTLR start "DIV"
    public final void mDIV() throws RecognitionException {
        try {
            int _type = DIV;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:125:4: ( '/' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:125:6: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIV"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:126:6: ( '-' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:126:8: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:127:5: ( '+' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:127:7: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "TIMES"
    public final void mTIMES() throws RecognitionException {
        try {
            int _type = TIMES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:128:6: ( '*' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:128:8: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TIMES"

    // $ANTLR start "SEMICOLON"
    public final void mSEMICOLON() throws RecognitionException {
        try {
            int _type = SEMICOLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:130:10: ( ';' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:130:12: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMICOLON"

    // $ANTLR start "SHARP"
    public final void mSHARP() throws RecognitionException {
        try {
            int _type = SHARP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:131:6: ( '#' )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:131:8: '#'
            {
            match('#'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SHARP"

    public void mTokens() throws RecognitionException {
        // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:8: ( MODULE | INTERFACE | TYPEDEF | PARAMETER | IN | OUT | INOUT | CHAR | INT | LONG | ANY | BOOLEAN | VOID | TRUE | FALSE | ID | FLOAT | INTEGER | STRING | LINE_COMMENT | MULTI_LINE_COMMENT | WHITESPACE | EQ | GE | GT | LE | LT | NE | ARROW | COLON | COLON_EQUAL | COMMA | DOT | DOUBLE_DASH_ARROW | DOUBLE_EQUAL_ARROW | DOUBLE_DOT | DOUBLE_COLON | LBRACE | RBRACE | LBRACKET | RBRACKET | LPAREN | RPAREN | CARET | DIV | MINUS | PLUS | TIMES | SEMICOLON | SHARP )
        int alt22=50;
        alt22 = dfa22.predict(input);
        switch (alt22) {
            case 1 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:10: MODULE
                {
                mMODULE(); 

                }
                break;
            case 2 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:17: INTERFACE
                {
                mINTERFACE(); 

                }
                break;
            case 3 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:27: TYPEDEF
                {
                mTYPEDEF(); 

                }
                break;
            case 4 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:35: PARAMETER
                {
                mPARAMETER(); 

                }
                break;
            case 5 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:45: IN
                {
                mIN(); 

                }
                break;
            case 6 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:48: OUT
                {
                mOUT(); 

                }
                break;
            case 7 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:52: INOUT
                {
                mINOUT(); 

                }
                break;
            case 8 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:58: CHAR
                {
                mCHAR(); 

                }
                break;
            case 9 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:63: INT
                {
                mINT(); 

                }
                break;
            case 10 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:67: LONG
                {
                mLONG(); 

                }
                break;
            case 11 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:72: ANY
                {
                mANY(); 

                }
                break;
            case 12 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:76: BOOLEAN
                {
                mBOOLEAN(); 

                }
                break;
            case 13 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:84: VOID
                {
                mVOID(); 

                }
                break;
            case 14 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:89: TRUE
                {
                mTRUE(); 

                }
                break;
            case 15 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:94: FALSE
                {
                mFALSE(); 

                }
                break;
            case 16 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:100: ID
                {
                mID(); 

                }
                break;
            case 17 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:103: FLOAT
                {
                mFLOAT(); 

                }
                break;
            case 18 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:109: INTEGER
                {
                mINTEGER(); 

                }
                break;
            case 19 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:117: STRING
                {
                mSTRING(); 

                }
                break;
            case 20 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:124: LINE_COMMENT
                {
                mLINE_COMMENT(); 

                }
                break;
            case 21 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:137: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); 

                }
                break;
            case 22 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:156: WHITESPACE
                {
                mWHITESPACE(); 

                }
                break;
            case 23 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:167: EQ
                {
                mEQ(); 

                }
                break;
            case 24 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:170: GE
                {
                mGE(); 

                }
                break;
            case 25 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:173: GT
                {
                mGT(); 

                }
                break;
            case 26 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:176: LE
                {
                mLE(); 

                }
                break;
            case 27 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:179: LT
                {
                mLT(); 

                }
                break;
            case 28 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:182: NE
                {
                mNE(); 

                }
                break;
            case 29 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:185: ARROW
                {
                mARROW(); 

                }
                break;
            case 30 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:191: COLON
                {
                mCOLON(); 

                }
                break;
            case 31 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:197: COLON_EQUAL
                {
                mCOLON_EQUAL(); 

                }
                break;
            case 32 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:209: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 33 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:215: DOT
                {
                mDOT(); 

                }
                break;
            case 34 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:219: DOUBLE_DASH_ARROW
                {
                mDOUBLE_DASH_ARROW(); 

                }
                break;
            case 35 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:237: DOUBLE_EQUAL_ARROW
                {
                mDOUBLE_EQUAL_ARROW(); 

                }
                break;
            case 36 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:256: DOUBLE_DOT
                {
                mDOUBLE_DOT(); 

                }
                break;
            case 37 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:267: DOUBLE_COLON
                {
                mDOUBLE_COLON(); 

                }
                break;
            case 38 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:280: LBRACE
                {
                mLBRACE(); 

                }
                break;
            case 39 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:287: RBRACE
                {
                mRBRACE(); 

                }
                break;
            case 40 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:294: LBRACKET
                {
                mLBRACKET(); 

                }
                break;
            case 41 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:303: RBRACKET
                {
                mRBRACKET(); 

                }
                break;
            case 42 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:312: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 43 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:319: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 44 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:326: CARET
                {
                mCARET(); 

                }
                break;
            case 45 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:332: DIV
                {
                mDIV(); 

                }
                break;
            case 46 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:336: MINUS
                {
                mMINUS(); 

                }
                break;
            case 47 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:342: PLUS
                {
                mPLUS(); 

                }
                break;
            case 48 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:347: TIMES
                {
                mTIMES(); 

                }
                break;
            case 49 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:353: SEMICOLON
                {
                mSEMICOLON(); 

                }
                break;
            case 50 :
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:1:363: SHARP
                {
                mSHARP(); 

                }
                break;

        }

    }


    protected DFA15 dfa15 = new DFA15(this);
    protected DFA22 dfa22 = new DFA22(this);
    static final String DFA15_eotS =
        "\5\uffff";
    static final String DFA15_eofS =
        "\5\uffff";
    static final String DFA15_minS =
        "\2\56\3\uffff";
    static final String DFA15_maxS =
        "\1\71\1\145\3\uffff";
    static final String DFA15_acceptS =
        "\2\uffff\1\2\1\3\1\1";
    static final String DFA15_specialS =
        "\5\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\4\1\uffff\12\1\13\uffff\1\3\37\uffff\1\3",
            "",
            "",
            ""
    };

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }
        public String getDescription() {
            return "90:13: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )? | '.' ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )? | ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ ) )";
        }
    }
    static final String DFA22_eotS =
        "\1\uffff\13\14\1\uffff\1\63\1\64\1\66\1\uffff\1\71\1\uffff\1\73"+
        "\1\75\1\77\1\uffff\1\102\14\uffff\1\14\1\106\12\14\23\uffff\1\14"+
        "\1\123\1\14\1\uffff\3\14\1\130\2\14\1\133\5\14\1\uffff\2\14\1\143"+
        "\1\14\1\uffff\1\145\1\146\1\uffff\1\14\1\150\3\14\1\154\1\14\1\uffff"+
        "\1\14\2\uffff\1\14\1\uffff\1\160\1\161\1\14\1\uffff\3\14\2\uffff"+
        "\1\14\1\167\1\14\1\171\1\14\1\uffff\1\14\1\uffff\1\174\1\175\2\uffff";
    static final String DFA22_eofS =
        "\176\uffff";
    static final String DFA22_minS =
        "\1\11\1\157\1\156\1\162\1\141\1\165\1\150\1\157\1\156\2\157\1\141"+
        "\1\uffff\1\55\2\56\1\uffff\1\52\1\uffff\3\75\1\uffff\1\72\14\uffff"+
        "\1\144\1\44\1\160\1\165\1\162\1\164\1\141\1\156\1\171\1\157\1\151"+
        "\1\154\23\uffff\1\165\1\44\1\165\1\uffff\2\145\1\141\1\44\1\162"+
        "\1\147\1\44\1\154\1\144\1\163\1\154\1\162\1\uffff\1\164\1\144\1"+
        "\44\1\155\1\uffff\2\44\1\uffff\1\145\1\44\2\145\1\146\1\44\1\145"+
        "\1\uffff\1\145\2\uffff\1\141\1\uffff\2\44\1\141\1\uffff\1\146\1"+
        "\164\1\156\2\uffff\1\143\1\44\1\145\1\44\1\145\1\uffff\1\162\1\uffff"+
        "\2\44\2\uffff";
    static final String DFA22_maxS =
        "\1\175\1\157\1\156\1\171\1\141\1\165\1\150\1\157\1\156\2\157\1"+
        "\141\1\uffff\1\76\1\145\1\71\1\uffff\1\57\1\uffff\3\75\1\uffff\1"+
        "\75\14\uffff\1\144\1\172\1\160\1\165\1\162\1\164\1\141\1\156\1\171"+
        "\1\157\1\151\1\154\23\uffff\1\165\1\172\1\165\1\uffff\2\145\1\141"+
        "\1\172\1\162\1\147\1\172\1\154\1\144\1\163\1\154\1\162\1\uffff\1"+
        "\164\1\144\1\172\1\155\1\uffff\2\172\1\uffff\1\145\1\172\2\145\1"+
        "\146\1\172\1\145\1\uffff\1\145\2\uffff\1\141\1\uffff\2\172\1\141"+
        "\1\uffff\1\146\1\164\1\156\2\uffff\1\143\1\172\1\145\1\172\1\145"+
        "\1\uffff\1\162\1\uffff\2\172\2\uffff";
    static final String DFA22_acceptS =
        "\14\uffff\1\20\3\uffff\1\23\1\uffff\1\26\3\uffff\1\34\1\uffff\1"+
        "\40\1\46\1\47\1\50\1\51\1\52\1\53\1\54\1\57\1\60\1\61\1\62\14\uffff"+
        "\1\35\1\42\1\21\1\56\1\22\1\44\1\41\1\24\1\25\1\55\1\43\1\27\1\30"+
        "\1\31\1\32\1\33\1\37\1\45\1\36\3\uffff\1\5\14\uffff\1\11\4\uffff"+
        "\1\6\2\uffff\1\13\7\uffff\1\16\1\uffff\1\10\1\12\1\uffff\1\15\3"+
        "\uffff\1\7\3\uffff\1\17\1\1\5\uffff\1\3\1\uffff\1\14\2\uffff\1\2"+
        "\1\4";
    static final String DFA22_specialS =
        "\176\uffff}>";
    static final String[] DFA22_transitionS = {
            "\2\22\1\uffff\2\22\22\uffff\1\22\1\26\1\20\1\43\1\14\3\uffff"+
            "\1\35\1\36\1\41\1\40\1\30\1\15\1\17\1\21\12\16\1\27\1\42\1\25"+
            "\1\23\1\24\2\uffff\32\14\1\33\1\uffff\1\34\1\37\1\14\1\uffff"+
            "\1\10\1\11\1\6\2\14\1\13\2\14\1\2\2\14\1\7\1\1\1\14\1\5\1\4"+
            "\3\14\1\3\1\14\1\12\4\14\1\31\1\uffff\1\32",
            "\1\44",
            "\1\45",
            "\1\47\6\uffff\1\46",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\54",
            "\1\55",
            "\1\56",
            "\1\57",
            "",
            "\1\61\1\62\1\uffff\12\16\4\uffff\1\60",
            "\1\62\1\uffff\12\16\13\uffff\1\62\37\uffff\1\62",
            "\1\65\1\uffff\12\62",
            "",
            "\1\70\4\uffff\1\67",
            "",
            "\1\72",
            "\1\74",
            "\1\76",
            "",
            "\1\101\2\uffff\1\100",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\103",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\16"+
            "\14\1\105\4\14\1\104\6\14",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\114",
            "\1\115",
            "\1\116",
            "\1\117",
            "\1\120",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\121",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\4"+
            "\14\1\122\25\14",
            "\1\124",
            "",
            "\1\125",
            "\1\126",
            "\1\127",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32"+
            "\14",
            "\1\131",
            "\1\132",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32"+
            "\14",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "",
            "\1\141",
            "\1\142",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32"+
            "\14",
            "\1\144",
            "",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32"+
            "\14",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32"+
            "\14",
            "",
            "\1\147",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32"+
            "\14",
            "\1\151",
            "\1\152",
            "\1\153",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32"+
            "\14",
            "\1\155",
            "",
            "\1\156",
            "",
            "",
            "\1\157",
            "",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32"+
            "\14",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32"+
            "\14",
            "\1\162",
            "",
            "\1\163",
            "\1\164",
            "\1\165",
            "",
            "",
            "\1\166",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32"+
            "\14",
            "\1\170",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32"+
            "\14",
            "\1\172",
            "",
            "\1\173",
            "",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32"+
            "\14",
            "\1\14\13\uffff\12\14\7\uffff\32\14\4\uffff\1\14\1\uffff\32"+
            "\14",
            "",
            ""
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( MODULE | INTERFACE | TYPEDEF | PARAMETER | IN | OUT | INOUT | CHAR | INT | LONG | ANY | BOOLEAN | VOID | TRUE | FALSE | ID | FLOAT | INTEGER | STRING | LINE_COMMENT | MULTI_LINE_COMMENT | WHITESPACE | EQ | GE | GT | LE | LT | NE | ARROW | COLON | COLON_EQUAL | COMMA | DOT | DOUBLE_DASH_ARROW | DOUBLE_EQUAL_ARROW | DOUBLE_DOT | DOUBLE_COLON | LBRACE | RBRACE | LBRACKET | RBRACKET | LPAREN | RPAREN | CARET | DIV | MINUS | PLUS | TIMES | SEMICOLON | SHARP );";
        }
    }
 

}