// $ANTLR 3.1.2 D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g 2009-06-29 17:33:25

package net.sf.graphiti.grammar.idl;


import org.antlr.runtime.BitSet;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteEarlyExitException;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;

@SuppressWarnings("unused")
public class IDLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "Module", "Parameter", "Id", "InputArgument", "OutputArgument", "MODULE", "ID", "LBRACE", "RBRACE", "SEMICOLON", "TYPEDEF", "PARAMETER", "INTERFACE", "LPAREN", "COMMA", "RPAREN", "OUT", "IN", "CHAR", "INT", "LONG", "BOOLEAN", "ANY", "VOID", "INOUT", "TRUE", "FALSE", "FLOAT", "INTEGER", "STRING", "LINE_COMMENT", "MULTI_LINE_COMMENT", "WHITESPACE", "EQ", "GE", "GT", "LE", "LT", "NE", "ARROW", "COLON", "COLON_EQUAL", "DOT", "DOUBLE_DASH_ARROW", "DOUBLE_EQUAL_ARROW", "DOUBLE_DOT", "DOUBLE_COLON", "LBRACKET", "RBRACKET", "CARET", "DIV", "MINUS", "PLUS", "TIMES", "SHARP"
    };
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
    public static final int COLON_EQUAL=45;
    public static final int BOOLEAN=25;
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
    public static final int GT=39;
    public static final int ARROW=43;
    public static final int INTERFACE=16;
    public static final int DIV=54;
    public static final int TIMES=57;
    public static final int LONG=24;
    public static final int FALSE=30;
    public static final int LE=40;
    public static final int STRING=33;

    // delegates
    // delegators


        public IDLParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public IDLParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return IDLParser.tokenNames; }
    public String getGrammarFileName() { return "D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g"; }


    public static class module_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "module"
    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:53:1: module : MODULE id= ID LBRACE moduleContent RBRACE SEMICOLON EOF -> ^( Module ^( Id ID ) moduleContent ) ;
    public final IDLParser.module_return module() throws RecognitionException {
        IDLParser.module_return retval = new IDLParser.module_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        Token MODULE1=null;
        Token LBRACE2=null;
        Token RBRACE4=null;
        Token SEMICOLON5=null;
        Token EOF6=null;
        IDLParser.moduleContent_return moduleContent3 = null;


        Object id_tree=null;
        Object MODULE1_tree=null;
        Object LBRACE2_tree=null;
        Object RBRACE4_tree=null;
        Object SEMICOLON5_tree=null;
        Object EOF6_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleTokenStream stream_MODULE=new RewriteRuleTokenStream(adaptor,"token MODULE");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleTokenStream stream_LBRACE=new RewriteRuleTokenStream(adaptor,"token LBRACE");
        RewriteRuleSubtreeStream stream_moduleContent=new RewriteRuleSubtreeStream(adaptor,"rule moduleContent");
        try {
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:53:8: ( MODULE id= ID LBRACE moduleContent RBRACE SEMICOLON EOF -> ^( Module ^( Id ID ) moduleContent ) )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:53:10: MODULE id= ID LBRACE moduleContent RBRACE SEMICOLON EOF
            {
            MODULE1=(Token)match(input,MODULE,FOLLOW_MODULE_in_module72);  
            stream_MODULE.add(MODULE1);

            id=(Token)match(input,ID,FOLLOW_ID_in_module76);  
            stream_ID.add(id);

            LBRACE2=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_module78);  
            stream_LBRACE.add(LBRACE2);

            pushFollow(FOLLOW_moduleContent_in_module80);
            moduleContent3=moduleContent();

            state._fsp--;

            stream_moduleContent.add(moduleContent3.getTree());
            RBRACE4=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_module82);  
            stream_RBRACE.add(RBRACE4);

            SEMICOLON5=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_module84);  
            stream_SEMICOLON.add(SEMICOLON5);

            EOF6=(Token)match(input,EOF,FOLLOW_EOF_in_module86);  
            stream_EOF.add(EOF6);



            // AST REWRITE
            // elements: moduleContent, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 53:65: -> ^( Module ^( Id ID ) moduleContent )
            {
                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:53:68: ^( Module ^( Id ID ) moduleContent )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(Module, "Module"), root_1);

                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:53:77: ^( Id ID )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(Id, "Id"), root_2);

                adaptor.addChild(root_2, stream_ID.nextNode());

                adaptor.addChild(root_1, root_2);
                }
                adaptor.addChild(root_1, stream_moduleContent.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "module"

    public static class moduleContent_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "moduleContent"
    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:55:1: moduleContent : ( ( typedef ) | ( moduleInterface -> ( moduleInterface )? ) )* ;
    public final IDLParser.moduleContent_return moduleContent() throws RecognitionException {
        IDLParser.moduleContent_return retval = new IDLParser.moduleContent_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        IDLParser.typedef_return typedef7 = null;

        IDLParser.moduleInterface_return moduleInterface8 = null;


        RewriteRuleSubtreeStream stream_typedef=new RewriteRuleSubtreeStream(adaptor,"rule typedef");
        RewriteRuleSubtreeStream stream_moduleInterface=new RewriteRuleSubtreeStream(adaptor,"rule moduleInterface");
        try {
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:55:15: ( ( ( typedef ) | ( moduleInterface -> ( moduleInterface )? ) )* )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:55:17: ( ( typedef ) | ( moduleInterface -> ( moduleInterface )? ) )*
            {
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:55:17: ( ( typedef ) | ( moduleInterface -> ( moduleInterface )? ) )*
            loop1:
            do {
                int alt1=3;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==TYPEDEF) ) {
                    alt1=1;
                }
                else if ( (LA1_0==INTERFACE) ) {
                    alt1=2;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:55:18: ( typedef )
            	    {
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:55:18: ( typedef )
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:55:19: typedef
            	    {
            	    pushFollow(FOLLOW_typedef_in_moduleContent110);
            	    typedef7=typedef();

            	    state._fsp--;

            	    stream_typedef.add(typedef7.getTree());

            	    }


            	    }
            	    break;
            	case 2 :
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:56:3: ( moduleInterface -> ( moduleInterface )? )
            	    {
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:56:3: ( moduleInterface -> ( moduleInterface )? )
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:56:4: moduleInterface
            	    {
            	    pushFollow(FOLLOW_moduleInterface_in_moduleContent119);
            	    moduleInterface8=moduleInterface();

            	    state._fsp--;

            	    stream_moduleInterface.add(moduleInterface8.getTree());


            	    // AST REWRITE
            	    // elements: moduleInterface
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (Object)adaptor.nil();
            	    // 56:20: -> ( moduleInterface )?
            	    {
            	        // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:56:23: ( moduleInterface )?
            	        if ( stream_moduleInterface.hasNext() ) {
            	            adaptor.addChild(root_0, stream_moduleInterface.nextTree());

            	        }
            	        stream_moduleInterface.reset();

            	    }

            	    retval.tree = root_0;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "moduleContent"

    public static class typedef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typedef"
    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:58:1: typedef : TYPEDEF type ( ID | PARAMETER ) SEMICOLON ;
    public final IDLParser.typedef_return typedef() throws RecognitionException {
        IDLParser.typedef_return retval = new IDLParser.typedef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TYPEDEF9=null;
        Token set11=null;
        Token SEMICOLON12=null;
        IDLParser.type_return type10 = null;


        Object TYPEDEF9_tree=null;
        Object set11_tree=null;
        Object SEMICOLON12_tree=null;

        try {
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:58:9: ( TYPEDEF type ( ID | PARAMETER ) SEMICOLON )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:58:11: TYPEDEF type ( ID | PARAMETER ) SEMICOLON
            {
            root_0 = (Object)adaptor.nil();

            TYPEDEF9=(Token)match(input,TYPEDEF,FOLLOW_TYPEDEF_in_typedef136); 
            TYPEDEF9_tree = (Object)adaptor.create(TYPEDEF9);
            adaptor.addChild(root_0, TYPEDEF9_tree);

            pushFollow(FOLLOW_type_in_typedef138);
            type10=type();

            state._fsp--;

            adaptor.addChild(root_0, type10.getTree());
            set11=(Token)input.LT(1);
            if ( input.LA(1)==ID||input.LA(1)==PARAMETER ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set11));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            SEMICOLON12=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_typedef148); 
            SEMICOLON12_tree = (Object)adaptor.create(SEMICOLON12);
            adaptor.addChild(root_0, SEMICOLON12_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "typedef"

    public static class moduleInterface_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "moduleInterface"
    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:60:1: moduleInterface : INTERFACE ID LBRACE ( prototype SEMICOLON )* RBRACE SEMICOLON -> prototype ;
    public final IDLParser.moduleInterface_return moduleInterface() throws RecognitionException {
        IDLParser.moduleInterface_return retval = new IDLParser.moduleInterface_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INTERFACE13=null;
        Token ID14=null;
        Token LBRACE15=null;
        Token SEMICOLON17=null;
        Token RBRACE18=null;
        Token SEMICOLON19=null;
        IDLParser.prototype_return prototype16 = null;


        Object INTERFACE13_tree=null;
        Object ID14_tree=null;
        Object LBRACE15_tree=null;
        Object SEMICOLON17_tree=null;
        Object RBRACE18_tree=null;
        Object SEMICOLON19_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON=new RewriteRuleTokenStream(adaptor,"token SEMICOLON");
        RewriteRuleTokenStream stream_INTERFACE=new RewriteRuleTokenStream(adaptor,"token INTERFACE");
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_LBRACE=new RewriteRuleTokenStream(adaptor,"token LBRACE");
        RewriteRuleSubtreeStream stream_prototype=new RewriteRuleSubtreeStream(adaptor,"rule prototype");
        try {
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:60:16: ( INTERFACE ID LBRACE ( prototype SEMICOLON )* RBRACE SEMICOLON -> prototype )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:60:18: INTERFACE ID LBRACE ( prototype SEMICOLON )* RBRACE SEMICOLON
            {
            INTERFACE13=(Token)match(input,INTERFACE,FOLLOW_INTERFACE_in_moduleInterface155);  
            stream_INTERFACE.add(INTERFACE13);

            ID14=(Token)match(input,ID,FOLLOW_ID_in_moduleInterface157);  
            stream_ID.add(ID14);

            LBRACE15=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_moduleInterface159);  
            stream_LBRACE.add(LBRACE15);

            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:60:38: ( prototype SEMICOLON )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=CHAR && LA2_0<=VOID)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:60:39: prototype SEMICOLON
            	    {
            	    pushFollow(FOLLOW_prototype_in_moduleInterface162);
            	    prototype16=prototype();

            	    state._fsp--;

            	    stream_prototype.add(prototype16.getTree());
            	    SEMICOLON17=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_moduleInterface164);  
            	    stream_SEMICOLON.add(SEMICOLON17);


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            RBRACE18=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_moduleInterface168);  
            stream_RBRACE.add(RBRACE18);

            SEMICOLON19=(Token)match(input,SEMICOLON,FOLLOW_SEMICOLON_in_moduleInterface170);  
            stream_SEMICOLON.add(SEMICOLON19);



            // AST REWRITE
            // elements: prototype
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 60:78: -> prototype
            {
                adaptor.addChild(root_0, stream_prototype.nextTree());

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "moduleInterface"

    public static class prototype_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "prototype"
    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:62:1: prototype : type ID LPAREN ( arg ) ( COMMA ( arg ) )* RPAREN -> ( arg )+ ;
    public final IDLParser.prototype_return prototype() throws RecognitionException {
        IDLParser.prototype_return retval = new IDLParser.prototype_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID21=null;
        Token LPAREN22=null;
        Token COMMA24=null;
        Token RPAREN26=null;
        IDLParser.type_return type20 = null;

        IDLParser.arg_return arg23 = null;

        IDLParser.arg_return arg25 = null;


        Object ID21_tree=null;
        Object LPAREN22_tree=null;
        Object COMMA24_tree=null;
        Object RPAREN26_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        try {
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:62:11: ( type ID LPAREN ( arg ) ( COMMA ( arg ) )* RPAREN -> ( arg )+ )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:62:13: type ID LPAREN ( arg ) ( COMMA ( arg ) )* RPAREN
            {
            pushFollow(FOLLOW_type_in_prototype184);
            type20=type();

            state._fsp--;

            stream_type.add(type20.getTree());
            ID21=(Token)match(input,ID,FOLLOW_ID_in_prototype186);  
            stream_ID.add(ID21);

            LPAREN22=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_prototype188);  
            stream_LPAREN.add(LPAREN22);

            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:62:28: ( arg )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:62:29: arg
            {
            pushFollow(FOLLOW_arg_in_prototype191);
            arg23=arg();

            state._fsp--;

            stream_arg.add(arg23.getTree());

            }

            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:62:34: ( COMMA ( arg ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==COMMA) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:62:35: COMMA ( arg )
            	    {
            	    COMMA24=(Token)match(input,COMMA,FOLLOW_COMMA_in_prototype195);  
            	    stream_COMMA.add(COMMA24);

            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:62:41: ( arg )
            	    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:62:42: arg
            	    {
            	    pushFollow(FOLLOW_arg_in_prototype198);
            	    arg25=arg();

            	    state._fsp--;

            	    stream_arg.add(arg25.getTree());

            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            RPAREN26=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_prototype204);  
            stream_RPAREN.add(RPAREN26);



            // AST REWRITE
            // elements: arg
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 62:58: -> ( arg )+
            {
                if ( !(stream_arg.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_arg.hasNext() ) {
                    adaptor.addChild(root_0, stream_arg.nextTree());

                }
                stream_arg.reset();

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "prototype"

    public static class arg_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arg"
    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:64:1: arg : ( ( OUT type ID -> ^( OutputArgument ^( Id ID ) ) ) | ( IN ( ( PARAMETER ID -> ^( Parameter ^( Id ID ) ) ) | ( type ID -> ^( InputArgument ^( Id ID ) ) ) ) ) );
    public final IDLParser.arg_return arg() throws RecognitionException {
        IDLParser.arg_return retval = new IDLParser.arg_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OUT27=null;
        Token ID29=null;
        Token IN30=null;
        Token PARAMETER31=null;
        Token ID32=null;
        Token ID34=null;
        IDLParser.type_return type28 = null;

        IDLParser.type_return type33 = null;


        Object OUT27_tree=null;
        Object ID29_tree=null;
        Object IN30_tree=null;
        Object PARAMETER31_tree=null;
        Object ID32_tree=null;
        Object ID34_tree=null;
        RewriteRuleTokenStream stream_IN=new RewriteRuleTokenStream(adaptor,"token IN");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_PARAMETER=new RewriteRuleTokenStream(adaptor,"token PARAMETER");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        try {
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:64:6: ( ( OUT type ID -> ^( OutputArgument ^( Id ID ) ) ) | ( IN ( ( PARAMETER ID -> ^( Parameter ^( Id ID ) ) ) | ( type ID -> ^( InputArgument ^( Id ID ) ) ) ) ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==OUT) ) {
                alt5=1;
            }
            else if ( (LA5_0==IN) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:64:8: ( OUT type ID -> ^( OutputArgument ^( Id ID ) ) )
                    {
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:64:8: ( OUT type ID -> ^( OutputArgument ^( Id ID ) ) )
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:64:9: OUT type ID
                    {
                    OUT27=(Token)match(input,OUT,FOLLOW_OUT_in_arg224);  
                    stream_OUT.add(OUT27);

                    pushFollow(FOLLOW_type_in_arg226);
                    type28=type();

                    state._fsp--;

                    stream_type.add(type28.getTree());
                    ID29=(Token)match(input,ID,FOLLOW_ID_in_arg228);  
                    stream_ID.add(ID29);



                    // AST REWRITE
                    // elements: ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 64:21: -> ^( OutputArgument ^( Id ID ) )
                    {
                        // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:64:24: ^( OutputArgument ^( Id ID ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(OutputArgument, "OutputArgument"), root_1);

                        // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:64:41: ^( Id ID )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(Id, "Id"), root_2);

                        adaptor.addChild(root_2, stream_ID.nextNode());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }


                    }
                    break;
                case 2 :
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:4: ( IN ( ( PARAMETER ID -> ^( Parameter ^( Id ID ) ) ) | ( type ID -> ^( InputArgument ^( Id ID ) ) ) ) )
                    {
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:4: ( IN ( ( PARAMETER ID -> ^( Parameter ^( Id ID ) ) ) | ( type ID -> ^( InputArgument ^( Id ID ) ) ) ) )
                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:5: IN ( ( PARAMETER ID -> ^( Parameter ^( Id ID ) ) ) | ( type ID -> ^( InputArgument ^( Id ID ) ) ) )
                    {
                    IN30=(Token)match(input,IN,FOLLOW_IN_in_arg247);  
                    stream_IN.add(IN30);

                    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:8: ( ( PARAMETER ID -> ^( Parameter ^( Id ID ) ) ) | ( type ID -> ^( InputArgument ^( Id ID ) ) ) )
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==PARAMETER) ) {
                        alt4=1;
                    }
                    else if ( ((LA4_0>=CHAR && LA4_0<=VOID)) ) {
                        alt4=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                        throw nvae;
                    }
                    switch (alt4) {
                        case 1 :
                            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:9: ( PARAMETER ID -> ^( Parameter ^( Id ID ) ) )
                            {
                            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:9: ( PARAMETER ID -> ^( Parameter ^( Id ID ) ) )
                            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:10: PARAMETER ID
                            {
                            PARAMETER31=(Token)match(input,PARAMETER,FOLLOW_PARAMETER_in_arg251);  
                            stream_PARAMETER.add(PARAMETER31);

                            ID32=(Token)match(input,ID,FOLLOW_ID_in_arg253);  
                            stream_ID.add(ID32);



                            // AST REWRITE
                            // elements: ID
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 65:23: -> ^( Parameter ^( Id ID ) )
                            {
                                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:26: ^( Parameter ^( Id ID ) )
                                {
                                Object root_1 = (Object)adaptor.nil();
                                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(Parameter, "Parameter"), root_1);

                                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:38: ^( Id ID )
                                {
                                Object root_2 = (Object)adaptor.nil();
                                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(Id, "Id"), root_2);

                                adaptor.addChild(root_2, stream_ID.nextNode());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;
                            }


                            }
                            break;
                        case 2 :
                            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:51: ( type ID -> ^( InputArgument ^( Id ID ) ) )
                            {
                            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:51: ( type ID -> ^( InputArgument ^( Id ID ) ) )
                            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:52: type ID
                            {
                            pushFollow(FOLLOW_type_in_arg271);
                            type33=type();

                            state._fsp--;

                            stream_type.add(type33.getTree());
                            ID34=(Token)match(input,ID,FOLLOW_ID_in_arg273);  
                            stream_ID.add(ID34);



                            // AST REWRITE
                            // elements: ID
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 65:60: -> ^( InputArgument ^( Id ID ) )
                            {
                                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:63: ^( InputArgument ^( Id ID ) )
                                {
                                Object root_1 = (Object)adaptor.nil();
                                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(InputArgument, "InputArgument"), root_1);

                                // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:65:79: ^( Id ID )
                                {
                                Object root_2 = (Object)adaptor.nil();
                                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(Id, "Id"), root_2);

                                adaptor.addChild(root_2, stream_ID.nextNode());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;
                            }


                            }
                            break;

                    }


                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "arg"

    public static class type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type"
    // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:67:1: type : ( CHAR | INT | LONG | BOOLEAN | ANY | VOID ) ;
    public final IDLParser.type_return type() throws RecognitionException {
        IDLParser.type_return retval = new IDLParser.type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set35=null;

        Object set35_tree=null;

        try {
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:67:6: ( ( CHAR | INT | LONG | BOOLEAN | ANY | VOID ) )
            // D:\\Graphiti\\configuration\\src\\net\\sf\\graphiti\\grammar\\idl\\IDL.g:67:7: ( CHAR | INT | LONG | BOOLEAN | ANY | VOID )
            {
            root_0 = (Object)adaptor.nil();

            set35=(Token)input.LT(1);
            if ( (input.LA(1)>=CHAR && input.LA(1)<=VOID) ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set35));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "type"

    // Delegated rules


 

    public static final BitSet FOLLOW_MODULE_in_module72 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_ID_in_module76 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LBRACE_in_module78 = new BitSet(new long[]{0x0000000000015000L});
    public static final BitSet FOLLOW_moduleContent_in_module80 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RBRACE_in_module82 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMICOLON_in_module84 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_module86 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typedef_in_moduleContent110 = new BitSet(new long[]{0x0000000000014002L});
    public static final BitSet FOLLOW_moduleInterface_in_moduleContent119 = new BitSet(new long[]{0x0000000000014002L});
    public static final BitSet FOLLOW_TYPEDEF_in_typedef136 = new BitSet(new long[]{0x000000000FC00000L});
    public static final BitSet FOLLOW_type_in_typedef138 = new BitSet(new long[]{0x0000000000008400L});
    public static final BitSet FOLLOW_set_in_typedef140 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMICOLON_in_typedef148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTERFACE_in_moduleInterface155 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_ID_in_moduleInterface157 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LBRACE_in_moduleInterface159 = new BitSet(new long[]{0x000000000FC01000L});
    public static final BitSet FOLLOW_prototype_in_moduleInterface162 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMICOLON_in_moduleInterface164 = new BitSet(new long[]{0x000000000FC01000L});
    public static final BitSet FOLLOW_RBRACE_in_moduleInterface168 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMICOLON_in_moduleInterface170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_prototype184 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_ID_in_prototype186 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_LPAREN_in_prototype188 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_arg_in_prototype191 = new BitSet(new long[]{0x00000000000C0000L});
    public static final BitSet FOLLOW_COMMA_in_prototype195 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_arg_in_prototype198 = new BitSet(new long[]{0x00000000000C0000L});
    public static final BitSet FOLLOW_RPAREN_in_prototype204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg224 = new BitSet(new long[]{0x000000000FC00000L});
    public static final BitSet FOLLOW_type_in_arg226 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_ID_in_arg228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_arg247 = new BitSet(new long[]{0x000000000FC08000L});
    public static final BitSet FOLLOW_PARAMETER_in_arg251 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_ID_in_arg253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_arg271 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_ID_in_arg273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_type295 = new BitSet(new long[]{0x0000000000000002L});

}