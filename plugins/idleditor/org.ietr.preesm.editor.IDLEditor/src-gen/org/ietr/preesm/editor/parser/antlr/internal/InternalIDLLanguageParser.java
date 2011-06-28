package org.ietr.preesm.editor.parser.antlr.internal; 

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import org.ietr.preesm.editor.services.IDLLanguageGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalIDLLanguageParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_INT", "RULE_STRING", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'module'", "'{'", "'}'", "';'", "'typedef'", "'interface'", "'void'", "'('", "','", "')'", "'int'", "'long'", "'char'", "'init'", "'loop'", "'end'", "'in'", "'out'"
    };
    public static final int RULE_ID=4;
    public static final int T__28=28;
    public static final int T__27=27;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__24=24;
    public static final int T__23=23;
    public static final int T__22=22;
    public static final int RULE_ANY_OTHER=10;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int RULE_SL_COMMENT=8;
    public static final int EOF=-1;
    public static final int RULE_ML_COMMENT=7;
    public static final int T__19=19;
    public static final int RULE_STRING=6;
    public static final int T__16=16;
    public static final int T__15=15;
    public static final int T__18=18;
    public static final int T__17=17;
    public static final int T__12=12;
    public static final int T__11=11;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int RULE_INT=5;
    public static final int RULE_WS=9;

    // delegates
    // delegators


        public InternalIDLLanguageParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalIDLLanguageParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalIDLLanguageParser.tokenNames; }
    public String getGrammarFileName() { return "../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g"; }



     	private IDLLanguageGrammarAccess grammarAccess;
     	
        public InternalIDLLanguageParser(TokenStream input, IDLLanguageGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }
        
        @Override
        protected String getFirstRuleName() {
        	return "IDL";	
       	}
       	
       	@Override
       	protected IDLLanguageGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}



    // $ANTLR start "entryRuleIDL"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:68:1: entryRuleIDL returns [EObject current=null] : iv_ruleIDL= ruleIDL EOF ;
    public final EObject entryRuleIDL() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIDL = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:69:2: (iv_ruleIDL= ruleIDL EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:70:2: iv_ruleIDL= ruleIDL EOF
            {
             newCompositeNode(grammarAccess.getIDLRule()); 
            pushFollow(FOLLOW_ruleIDL_in_entryRuleIDL75);
            iv_ruleIDL=ruleIDL();

            state._fsp--;

             current =iv_ruleIDL; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleIDL85); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleIDL"


    // $ANTLR start "ruleIDL"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:77:1: ruleIDL returns [EObject current=null] : ( (lv_elements_0_0= ruleModule ) ) ;
    public final EObject ruleIDL() throws RecognitionException {
        EObject current = null;

        EObject lv_elements_0_0 = null;


         enterRule(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:80:28: ( ( (lv_elements_0_0= ruleModule ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:81:1: ( (lv_elements_0_0= ruleModule ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:81:1: ( (lv_elements_0_0= ruleModule ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:82:1: (lv_elements_0_0= ruleModule )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:82:1: (lv_elements_0_0= ruleModule )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:83:3: lv_elements_0_0= ruleModule
            {
             
            	        newCompositeNode(grammarAccess.getIDLAccess().getElementsModuleParserRuleCall_0()); 
            	    
            pushFollow(FOLLOW_ruleModule_in_ruleIDL130);
            lv_elements_0_0=ruleModule();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getIDLRule());
            	        }
                   		add(
                   			current, 
                   			"elements",
                    		lv_elements_0_0, 
                    		"Module");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleIDL"


    // $ANTLR start "entryRuleModule"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:107:1: entryRuleModule returns [EObject current=null] : iv_ruleModule= ruleModule EOF ;
    public final EObject entryRuleModule() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleModule = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:108:2: (iv_ruleModule= ruleModule EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:109:2: iv_ruleModule= ruleModule EOF
            {
             newCompositeNode(grammarAccess.getModuleRule()); 
            pushFollow(FOLLOW_ruleModule_in_entryRuleModule165);
            iv_ruleModule=ruleModule();

            state._fsp--;

             current =iv_ruleModule; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleModule175); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleModule"


    // $ANTLR start "ruleModule"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:116:1: ruleModule returns [EObject current=null] : (otherlv_0= 'module' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '{' ( (lv_types_3_0= ruleDataType ) )* ( (lv_interfaces_4_0= ruleInterface ) )* otherlv_5= '}' otherlv_6= ';' ) ;
    public final EObject ruleModule() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        EObject lv_types_3_0 = null;

        EObject lv_interfaces_4_0 = null;


         enterRule(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:119:28: ( (otherlv_0= 'module' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '{' ( (lv_types_3_0= ruleDataType ) )* ( (lv_interfaces_4_0= ruleInterface ) )* otherlv_5= '}' otherlv_6= ';' ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:120:1: (otherlv_0= 'module' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '{' ( (lv_types_3_0= ruleDataType ) )* ( (lv_interfaces_4_0= ruleInterface ) )* otherlv_5= '}' otherlv_6= ';' )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:120:1: (otherlv_0= 'module' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '{' ( (lv_types_3_0= ruleDataType ) )* ( (lv_interfaces_4_0= ruleInterface ) )* otherlv_5= '}' otherlv_6= ';' )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:120:3: otherlv_0= 'module' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '{' ( (lv_types_3_0= ruleDataType ) )* ( (lv_interfaces_4_0= ruleInterface ) )* otherlv_5= '}' otherlv_6= ';'
            {
            otherlv_0=(Token)match(input,11,FOLLOW_11_in_ruleModule212); 

                	newLeafNode(otherlv_0, grammarAccess.getModuleAccess().getModuleKeyword_0());
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:124:1: ( (lv_name_1_0= RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:125:1: (lv_name_1_0= RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:125:1: (lv_name_1_0= RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:126:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleModule229); 

            			newLeafNode(lv_name_1_0, grammarAccess.getModuleAccess().getNameIDTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getModuleRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"ID");
            	    

            }


            }

            otherlv_2=(Token)match(input,12,FOLLOW_12_in_ruleModule246); 

                	newLeafNode(otherlv_2, grammarAccess.getModuleAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:146:1: ( (lv_types_3_0= ruleDataType ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==15) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:147:1: (lv_types_3_0= ruleDataType )
            	    {
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:147:1: (lv_types_3_0= ruleDataType )
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:148:3: lv_types_3_0= ruleDataType
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getModuleAccess().getTypesDataTypeParserRuleCall_3_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleDataType_in_ruleModule267);
            	    lv_types_3_0=ruleDataType();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getModuleRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"types",
            	            		lv_types_3_0, 
            	            		"DataType");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:164:3: ( (lv_interfaces_4_0= ruleInterface ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==16) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:165:1: (lv_interfaces_4_0= ruleInterface )
            	    {
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:165:1: (lv_interfaces_4_0= ruleInterface )
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:166:3: lv_interfaces_4_0= ruleInterface
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getModuleAccess().getInterfacesInterfaceParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleInterface_in_ruleModule289);
            	    lv_interfaces_4_0=ruleInterface();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getModuleRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"interfaces",
            	            		lv_interfaces_4_0, 
            	            		"Interface");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            otherlv_5=(Token)match(input,13,FOLLOW_13_in_ruleModule302); 

                	newLeafNode(otherlv_5, grammarAccess.getModuleAccess().getRightCurlyBracketKeyword_5());
                
            otherlv_6=(Token)match(input,14,FOLLOW_14_in_ruleModule314); 

                	newLeafNode(otherlv_6, grammarAccess.getModuleAccess().getSemicolonKeyword_6());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleModule"


    // $ANTLR start "entryRuleDataType"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:198:1: entryRuleDataType returns [EObject current=null] : iv_ruleDataType= ruleDataType EOF ;
    public final EObject entryRuleDataType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDataType = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:199:2: (iv_ruleDataType= ruleDataType EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:200:2: iv_ruleDataType= ruleDataType EOF
            {
             newCompositeNode(grammarAccess.getDataTypeRule()); 
            pushFollow(FOLLOW_ruleDataType_in_entryRuleDataType350);
            iv_ruleDataType=ruleDataType();

            state._fsp--;

             current =iv_ruleDataType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDataType360); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDataType"


    // $ANTLR start "ruleDataType"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:207:1: ruleDataType returns [EObject current=null] : (otherlv_0= 'typedef' ( ( (lv_btype_1_0= ruleBaseType ) ) | ( (otherlv_2= RULE_ID ) ) ) ( (lv_name_3_0= RULE_ID ) ) otherlv_4= ';' ) ;
    public final EObject ruleDataType() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        Token lv_name_3_0=null;
        Token otherlv_4=null;
        Enumerator lv_btype_1_0 = null;


         enterRule(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:210:28: ( (otherlv_0= 'typedef' ( ( (lv_btype_1_0= ruleBaseType ) ) | ( (otherlv_2= RULE_ID ) ) ) ( (lv_name_3_0= RULE_ID ) ) otherlv_4= ';' ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:211:1: (otherlv_0= 'typedef' ( ( (lv_btype_1_0= ruleBaseType ) ) | ( (otherlv_2= RULE_ID ) ) ) ( (lv_name_3_0= RULE_ID ) ) otherlv_4= ';' )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:211:1: (otherlv_0= 'typedef' ( ( (lv_btype_1_0= ruleBaseType ) ) | ( (otherlv_2= RULE_ID ) ) ) ( (lv_name_3_0= RULE_ID ) ) otherlv_4= ';' )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:211:3: otherlv_0= 'typedef' ( ( (lv_btype_1_0= ruleBaseType ) ) | ( (otherlv_2= RULE_ID ) ) ) ( (lv_name_3_0= RULE_ID ) ) otherlv_4= ';'
            {
            otherlv_0=(Token)match(input,15,FOLLOW_15_in_ruleDataType397); 

                	newLeafNode(otherlv_0, grammarAccess.getDataTypeAccess().getTypedefKeyword_0());
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:215:1: ( ( (lv_btype_1_0= ruleBaseType ) ) | ( (otherlv_2= RULE_ID ) ) )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>=21 && LA3_0<=23)) ) {
                alt3=1;
            }
            else if ( (LA3_0==RULE_ID) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:215:2: ( (lv_btype_1_0= ruleBaseType ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:215:2: ( (lv_btype_1_0= ruleBaseType ) )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:216:1: (lv_btype_1_0= ruleBaseType )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:216:1: (lv_btype_1_0= ruleBaseType )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:217:3: lv_btype_1_0= ruleBaseType
                    {
                     
                    	        newCompositeNode(grammarAccess.getDataTypeAccess().getBtypeBaseTypeEnumRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBaseType_in_ruleDataType419);
                    lv_btype_1_0=ruleBaseType();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getDataTypeRule());
                    	        }
                           		set(
                           			current, 
                           			"btype",
                            		lv_btype_1_0, 
                            		"BaseType");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:234:6: ( (otherlv_2= RULE_ID ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:234:6: ( (otherlv_2= RULE_ID ) )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:235:1: (otherlv_2= RULE_ID )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:235:1: (otherlv_2= RULE_ID )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:236:3: otherlv_2= RULE_ID
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getDataTypeRule());
                    	        }
                            
                    otherlv_2=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDataType445); 

                    		newLeafNode(otherlv_2, grammarAccess.getDataTypeAccess().getCtypeDataTypeCrossReference_1_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }

            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:247:3: ( (lv_name_3_0= RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:248:1: (lv_name_3_0= RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:248:1: (lv_name_3_0= RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:249:3: lv_name_3_0= RULE_ID
            {
            lv_name_3_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDataType463); 

            			newLeafNode(lv_name_3_0, grammarAccess.getDataTypeAccess().getNameIDTerminalRuleCall_2_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getDataTypeRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_3_0, 
                    		"ID");
            	    

            }


            }

            otherlv_4=(Token)match(input,14,FOLLOW_14_in_ruleDataType480); 

                	newLeafNode(otherlv_4, grammarAccess.getDataTypeAccess().getSemicolonKeyword_3());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDataType"


    // $ANTLR start "entryRuleInterface"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:277:1: entryRuleInterface returns [EObject current=null] : iv_ruleInterface= ruleInterface EOF ;
    public final EObject entryRuleInterface() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleInterface = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:278:2: (iv_ruleInterface= ruleInterface EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:279:2: iv_ruleInterface= ruleInterface EOF
            {
             newCompositeNode(grammarAccess.getInterfaceRule()); 
            pushFollow(FOLLOW_ruleInterface_in_entryRuleInterface516);
            iv_ruleInterface=ruleInterface();

            state._fsp--;

             current =iv_ruleInterface; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleInterface526); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleInterface"


    // $ANTLR start "ruleInterface"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:286:1: ruleInterface returns [EObject current=null] : (otherlv_0= 'interface' ( (lv_name_1_0= ruleInterfaceName ) ) otherlv_2= '{' ( (lv_function_3_0= ruleFunction ) ) otherlv_4= '}' otherlv_5= ';' ) ;
    public final EObject ruleInterface() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Enumerator lv_name_1_0 = null;

        EObject lv_function_3_0 = null;


         enterRule(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:289:28: ( (otherlv_0= 'interface' ( (lv_name_1_0= ruleInterfaceName ) ) otherlv_2= '{' ( (lv_function_3_0= ruleFunction ) ) otherlv_4= '}' otherlv_5= ';' ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:290:1: (otherlv_0= 'interface' ( (lv_name_1_0= ruleInterfaceName ) ) otherlv_2= '{' ( (lv_function_3_0= ruleFunction ) ) otherlv_4= '}' otherlv_5= ';' )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:290:1: (otherlv_0= 'interface' ( (lv_name_1_0= ruleInterfaceName ) ) otherlv_2= '{' ( (lv_function_3_0= ruleFunction ) ) otherlv_4= '}' otherlv_5= ';' )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:290:3: otherlv_0= 'interface' ( (lv_name_1_0= ruleInterfaceName ) ) otherlv_2= '{' ( (lv_function_3_0= ruleFunction ) ) otherlv_4= '}' otherlv_5= ';'
            {
            otherlv_0=(Token)match(input,16,FOLLOW_16_in_ruleInterface563); 

                	newLeafNode(otherlv_0, grammarAccess.getInterfaceAccess().getInterfaceKeyword_0());
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:294:1: ( (lv_name_1_0= ruleInterfaceName ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:295:1: (lv_name_1_0= ruleInterfaceName )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:295:1: (lv_name_1_0= ruleInterfaceName )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:296:3: lv_name_1_0= ruleInterfaceName
            {
             
            	        newCompositeNode(grammarAccess.getInterfaceAccess().getNameInterfaceNameEnumRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleInterfaceName_in_ruleInterface584);
            lv_name_1_0=ruleInterfaceName();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getInterfaceRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"InterfaceName");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_2=(Token)match(input,12,FOLLOW_12_in_ruleInterface596); 

                	newLeafNode(otherlv_2, grammarAccess.getInterfaceAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:316:1: ( (lv_function_3_0= ruleFunction ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:317:1: (lv_function_3_0= ruleFunction )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:317:1: (lv_function_3_0= ruleFunction )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:318:3: lv_function_3_0= ruleFunction
            {
             
            	        newCompositeNode(grammarAccess.getInterfaceAccess().getFunctionFunctionParserRuleCall_3_0()); 
            	    
            pushFollow(FOLLOW_ruleFunction_in_ruleInterface617);
            lv_function_3_0=ruleFunction();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getInterfaceRule());
            	        }
                   		set(
                   			current, 
                   			"function",
                    		lv_function_3_0, 
                    		"Function");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_4=(Token)match(input,13,FOLLOW_13_in_ruleInterface629); 

                	newLeafNode(otherlv_4, grammarAccess.getInterfaceAccess().getRightCurlyBracketKeyword_4());
                
            otherlv_5=(Token)match(input,14,FOLLOW_14_in_ruleInterface641); 

                	newLeafNode(otherlv_5, grammarAccess.getInterfaceAccess().getSemicolonKeyword_5());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleInterface"


    // $ANTLR start "entryRuleFunction"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:350:1: entryRuleFunction returns [EObject current=null] : iv_ruleFunction= ruleFunction EOF ;
    public final EObject entryRuleFunction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFunction = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:351:2: (iv_ruleFunction= ruleFunction EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:352:2: iv_ruleFunction= ruleFunction EOF
            {
             newCompositeNode(grammarAccess.getFunctionRule()); 
            pushFollow(FOLLOW_ruleFunction_in_entryRuleFunction677);
            iv_ruleFunction=ruleFunction();

            state._fsp--;

             current =iv_ruleFunction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleFunction687); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleFunction"


    // $ANTLR start "ruleFunction"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:359:1: ruleFunction returns [EObject current=null] : (otherlv_0= 'void' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_parameters_3_0= ruleParameter ) )? (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )* otherlv_6= ')' otherlv_7= ';' ) ;
    public final EObject ruleFunction() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        EObject lv_parameters_3_0 = null;

        EObject lv_parameters_5_0 = null;


         enterRule(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:362:28: ( (otherlv_0= 'void' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_parameters_3_0= ruleParameter ) )? (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )* otherlv_6= ')' otherlv_7= ';' ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:363:1: (otherlv_0= 'void' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_parameters_3_0= ruleParameter ) )? (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )* otherlv_6= ')' otherlv_7= ';' )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:363:1: (otherlv_0= 'void' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_parameters_3_0= ruleParameter ) )? (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )* otherlv_6= ')' otherlv_7= ';' )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:363:3: otherlv_0= 'void' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_parameters_3_0= ruleParameter ) )? (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )* otherlv_6= ')' otherlv_7= ';'
            {
            otherlv_0=(Token)match(input,17,FOLLOW_17_in_ruleFunction724); 

                	newLeafNode(otherlv_0, grammarAccess.getFunctionAccess().getVoidKeyword_0());
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:367:1: ( (lv_name_1_0= RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:368:1: (lv_name_1_0= RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:368:1: (lv_name_1_0= RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:369:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleFunction741); 

            			newLeafNode(lv_name_1_0, grammarAccess.getFunctionAccess().getNameIDTerminalRuleCall_1_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getFunctionRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_1_0, 
                    		"ID");
            	    

            }


            }

            otherlv_2=(Token)match(input,18,FOLLOW_18_in_ruleFunction758); 

                	newLeafNode(otherlv_2, grammarAccess.getFunctionAccess().getLeftParenthesisKeyword_2());
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:389:1: ( (lv_parameters_3_0= ruleParameter ) )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( ((LA4_0>=27 && LA4_0<=28)) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:390:1: (lv_parameters_3_0= ruleParameter )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:390:1: (lv_parameters_3_0= ruleParameter )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:391:3: lv_parameters_3_0= ruleParameter
                    {
                     
                    	        newCompositeNode(grammarAccess.getFunctionAccess().getParametersParameterParserRuleCall_3_0()); 
                    	    
                    pushFollow(FOLLOW_ruleParameter_in_ruleFunction779);
                    lv_parameters_3_0=ruleParameter();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getFunctionRule());
                    	        }
                           		add(
                           			current, 
                           			"parameters",
                            		lv_parameters_3_0, 
                            		"Parameter");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }
                    break;

            }

            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:407:3: (otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) ) )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==19) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:407:5: otherlv_4= ',' ( (lv_parameters_5_0= ruleParameter ) )
            	    {
            	    otherlv_4=(Token)match(input,19,FOLLOW_19_in_ruleFunction793); 

            	        	newLeafNode(otherlv_4, grammarAccess.getFunctionAccess().getCommaKeyword_4_0());
            	        
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:411:1: ( (lv_parameters_5_0= ruleParameter ) )
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:412:1: (lv_parameters_5_0= ruleParameter )
            	    {
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:412:1: (lv_parameters_5_0= ruleParameter )
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:413:3: lv_parameters_5_0= ruleParameter
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getFunctionAccess().getParametersParameterParserRuleCall_4_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleParameter_in_ruleFunction814);
            	    lv_parameters_5_0=ruleParameter();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getFunctionRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"parameters",
            	            		lv_parameters_5_0, 
            	            		"Parameter");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            otherlv_6=(Token)match(input,20,FOLLOW_20_in_ruleFunction828); 

                	newLeafNode(otherlv_6, grammarAccess.getFunctionAccess().getRightParenthesisKeyword_5());
                
            otherlv_7=(Token)match(input,14,FOLLOW_14_in_ruleFunction840); 

                	newLeafNode(otherlv_7, grammarAccess.getFunctionAccess().getSemicolonKeyword_6());
                

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleFunction"


    // $ANTLR start "entryRuleParameter"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:445:1: entryRuleParameter returns [EObject current=null] : iv_ruleParameter= ruleParameter EOF ;
    public final EObject entryRuleParameter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParameter = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:446:2: (iv_ruleParameter= ruleParameter EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:447:2: iv_ruleParameter= ruleParameter EOF
            {
             newCompositeNode(grammarAccess.getParameterRule()); 
            pushFollow(FOLLOW_ruleParameter_in_entryRuleParameter876);
            iv_ruleParameter=ruleParameter();

            state._fsp--;

             current =iv_ruleParameter; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleParameter886); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleParameter"


    // $ANTLR start "ruleParameter"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:454:1: ruleParameter returns [EObject current=null] : ( ( (lv_direction_0_0= ruleDirection ) ) ( (lv_type_1_0= ruleTypeStar ) ) ( (lv_name_2_0= RULE_ID ) ) ) ;
    public final EObject ruleParameter() throws RecognitionException {
        EObject current = null;

        Token lv_name_2_0=null;
        Enumerator lv_direction_0_0 = null;

        EObject lv_type_1_0 = null;


         enterRule(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:457:28: ( ( ( (lv_direction_0_0= ruleDirection ) ) ( (lv_type_1_0= ruleTypeStar ) ) ( (lv_name_2_0= RULE_ID ) ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:458:1: ( ( (lv_direction_0_0= ruleDirection ) ) ( (lv_type_1_0= ruleTypeStar ) ) ( (lv_name_2_0= RULE_ID ) ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:458:1: ( ( (lv_direction_0_0= ruleDirection ) ) ( (lv_type_1_0= ruleTypeStar ) ) ( (lv_name_2_0= RULE_ID ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:458:2: ( (lv_direction_0_0= ruleDirection ) ) ( (lv_type_1_0= ruleTypeStar ) ) ( (lv_name_2_0= RULE_ID ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:458:2: ( (lv_direction_0_0= ruleDirection ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:459:1: (lv_direction_0_0= ruleDirection )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:459:1: (lv_direction_0_0= ruleDirection )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:460:3: lv_direction_0_0= ruleDirection
            {
             
            	        newCompositeNode(grammarAccess.getParameterAccess().getDirectionDirectionEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleDirection_in_ruleParameter932);
            lv_direction_0_0=ruleDirection();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getParameterRule());
            	        }
                   		set(
                   			current, 
                   			"direction",
                    		lv_direction_0_0, 
                    		"Direction");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:476:2: ( (lv_type_1_0= ruleTypeStar ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:477:1: (lv_type_1_0= ruleTypeStar )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:477:1: (lv_type_1_0= ruleTypeStar )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:478:3: lv_type_1_0= ruleTypeStar
            {
             
            	        newCompositeNode(grammarAccess.getParameterAccess().getTypeTypeStarParserRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleTypeStar_in_ruleParameter953);
            lv_type_1_0=ruleTypeStar();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getParameterRule());
            	        }
                   		set(
                   			current, 
                   			"type",
                    		lv_type_1_0, 
                    		"TypeStar");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:494:2: ( (lv_name_2_0= RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:495:1: (lv_name_2_0= RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:495:1: (lv_name_2_0= RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:496:3: lv_name_2_0= RULE_ID
            {
            lv_name_2_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleParameter970); 

            			newLeafNode(lv_name_2_0, grammarAccess.getParameterAccess().getNameIDTerminalRuleCall_2_0()); 
            		

            	        if (current==null) {
            	            current = createModelElement(grammarAccess.getParameterRule());
            	        }
                   		setWithLastConsumed(
                   			current, 
                   			"name",
                    		lv_name_2_0, 
                    		"ID");
            	    

            }


            }


            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleParameter"


    // $ANTLR start "entryRuleTypeStar"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:520:1: entryRuleTypeStar returns [EObject current=null] : iv_ruleTypeStar= ruleTypeStar EOF ;
    public final EObject entryRuleTypeStar() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTypeStar = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:521:2: (iv_ruleTypeStar= ruleTypeStar EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:522:2: iv_ruleTypeStar= ruleTypeStar EOF
            {
             newCompositeNode(grammarAccess.getTypeStarRule()); 
            pushFollow(FOLLOW_ruleTypeStar_in_entryRuleTypeStar1011);
            iv_ruleTypeStar=ruleTypeStar();

            state._fsp--;

             current =iv_ruleTypeStar; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTypeStar1021); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTypeStar"


    // $ANTLR start "ruleTypeStar"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:529:1: ruleTypeStar returns [EObject current=null] : ( ( (lv_btype_0_0= ruleBaseType ) ) | ( (otherlv_1= RULE_ID ) ) ) ;
    public final EObject ruleTypeStar() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Enumerator lv_btype_0_0 = null;


         enterRule(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:532:28: ( ( ( (lv_btype_0_0= ruleBaseType ) ) | ( (otherlv_1= RULE_ID ) ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:533:1: ( ( (lv_btype_0_0= ruleBaseType ) ) | ( (otherlv_1= RULE_ID ) ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:533:1: ( ( (lv_btype_0_0= ruleBaseType ) ) | ( (otherlv_1= RULE_ID ) ) )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( ((LA6_0>=21 && LA6_0<=23)) ) {
                alt6=1;
            }
            else if ( (LA6_0==RULE_ID) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:533:2: ( (lv_btype_0_0= ruleBaseType ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:533:2: ( (lv_btype_0_0= ruleBaseType ) )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:534:1: (lv_btype_0_0= ruleBaseType )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:534:1: (lv_btype_0_0= ruleBaseType )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:535:3: lv_btype_0_0= ruleBaseType
                    {
                     
                    	        newCompositeNode(grammarAccess.getTypeStarAccess().getBtypeBaseTypeEnumRuleCall_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleBaseType_in_ruleTypeStar1067);
                    lv_btype_0_0=ruleBaseType();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getTypeStarRule());
                    	        }
                           		set(
                           			current, 
                           			"btype",
                            		lv_btype_0_0, 
                            		"BaseType");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:552:6: ( (otherlv_1= RULE_ID ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:552:6: ( (otherlv_1= RULE_ID ) )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:553:1: (otherlv_1= RULE_ID )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:553:1: (otherlv_1= RULE_ID )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:554:3: otherlv_1= RULE_ID
                    {

                    			if (current==null) {
                    	            current = createModelElement(grammarAccess.getTypeStarRule());
                    	        }
                            
                    otherlv_1=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleTypeStar1093); 

                    		newLeafNode(otherlv_1, grammarAccess.getTypeStarAccess().getCtypeDataTypeCrossReference_1_0()); 
                    	

                    }


                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTypeStar"


    // $ANTLR start "ruleBaseType"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:573:1: ruleBaseType returns [Enumerator current=null] : ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'long' ) | (enumLiteral_2= 'char' ) ) ;
    public final Enumerator ruleBaseType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:575:28: ( ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'long' ) | (enumLiteral_2= 'char' ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:576:1: ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'long' ) | (enumLiteral_2= 'char' ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:576:1: ( (enumLiteral_0= 'int' ) | (enumLiteral_1= 'long' ) | (enumLiteral_2= 'char' ) )
            int alt7=3;
            switch ( input.LA(1) ) {
            case 21:
                {
                alt7=1;
                }
                break;
            case 22:
                {
                alt7=2;
                }
                break;
            case 23:
                {
                alt7=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:576:2: (enumLiteral_0= 'int' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:576:2: (enumLiteral_0= 'int' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:576:4: enumLiteral_0= 'int'
                    {
                    enumLiteral_0=(Token)match(input,21,FOLLOW_21_in_ruleBaseType1143); 

                            current = grammarAccess.getBaseTypeAccess().getIntEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getBaseTypeAccess().getIntEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:582:6: (enumLiteral_1= 'long' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:582:6: (enumLiteral_1= 'long' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:582:8: enumLiteral_1= 'long'
                    {
                    enumLiteral_1=(Token)match(input,22,FOLLOW_22_in_ruleBaseType1160); 

                            current = grammarAccess.getBaseTypeAccess().getLongEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getBaseTypeAccess().getLongEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:588:6: (enumLiteral_2= 'char' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:588:6: (enumLiteral_2= 'char' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:588:8: enumLiteral_2= 'char'
                    {
                    enumLiteral_2=(Token)match(input,23,FOLLOW_23_in_ruleBaseType1177); 

                            current = grammarAccess.getBaseTypeAccess().getCharEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getBaseTypeAccess().getCharEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBaseType"


    // $ANTLR start "ruleInterfaceName"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:598:1: ruleInterfaceName returns [Enumerator current=null] : ( (enumLiteral_0= 'init' ) | (enumLiteral_1= 'loop' ) | (enumLiteral_2= 'end' ) ) ;
    public final Enumerator ruleInterfaceName() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:600:28: ( ( (enumLiteral_0= 'init' ) | (enumLiteral_1= 'loop' ) | (enumLiteral_2= 'end' ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:601:1: ( (enumLiteral_0= 'init' ) | (enumLiteral_1= 'loop' ) | (enumLiteral_2= 'end' ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:601:1: ( (enumLiteral_0= 'init' ) | (enumLiteral_1= 'loop' ) | (enumLiteral_2= 'end' ) )
            int alt8=3;
            switch ( input.LA(1) ) {
            case 24:
                {
                alt8=1;
                }
                break;
            case 25:
                {
                alt8=2;
                }
                break;
            case 26:
                {
                alt8=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:601:2: (enumLiteral_0= 'init' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:601:2: (enumLiteral_0= 'init' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:601:4: enumLiteral_0= 'init'
                    {
                    enumLiteral_0=(Token)match(input,24,FOLLOW_24_in_ruleInterfaceName1222); 

                            current = grammarAccess.getInterfaceNameAccess().getInitEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getInterfaceNameAccess().getInitEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:607:6: (enumLiteral_1= 'loop' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:607:6: (enumLiteral_1= 'loop' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:607:8: enumLiteral_1= 'loop'
                    {
                    enumLiteral_1=(Token)match(input,25,FOLLOW_25_in_ruleInterfaceName1239); 

                            current = grammarAccess.getInterfaceNameAccess().getLoopEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getInterfaceNameAccess().getLoopEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:613:6: (enumLiteral_2= 'end' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:613:6: (enumLiteral_2= 'end' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:613:8: enumLiteral_2= 'end'
                    {
                    enumLiteral_2=(Token)match(input,26,FOLLOW_26_in_ruleInterfaceName1256); 

                            current = grammarAccess.getInterfaceNameAccess().getEndEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getInterfaceNameAccess().getEndEnumLiteralDeclaration_2()); 
                        

                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleInterfaceName"


    // $ANTLR start "ruleDirection"
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:623:1: ruleDirection returns [Enumerator current=null] : ( (enumLiteral_0= 'in' ) | (enumLiteral_1= 'out' ) ) ;
    public final Enumerator ruleDirection() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:625:28: ( ( (enumLiteral_0= 'in' ) | (enumLiteral_1= 'out' ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:626:1: ( (enumLiteral_0= 'in' ) | (enumLiteral_1= 'out' ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:626:1: ( (enumLiteral_0= 'in' ) | (enumLiteral_1= 'out' ) )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==27) ) {
                alt9=1;
            }
            else if ( (LA9_0==28) ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:626:2: (enumLiteral_0= 'in' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:626:2: (enumLiteral_0= 'in' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:626:4: enumLiteral_0= 'in'
                    {
                    enumLiteral_0=(Token)match(input,27,FOLLOW_27_in_ruleDirection1301); 

                            current = grammarAccess.getDirectionAccess().getInEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getDirectionAccess().getInEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:632:6: (enumLiteral_1= 'out' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:632:6: (enumLiteral_1= 'out' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:632:8: enumLiteral_1= 'out'
                    {
                    enumLiteral_1=(Token)match(input,28,FOLLOW_28_in_ruleDirection1318); 

                            current = grammarAccess.getDirectionAccess().getOutEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getDirectionAccess().getOutEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;

            }


            }

             leaveRule(); 
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDirection"

    // Delegated rules


 

    public static final BitSet FOLLOW_ruleIDL_in_entryRuleIDL75 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleIDL85 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleModule_in_ruleIDL130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleModule_in_entryRuleModule165 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleModule175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_11_in_ruleModule212 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleModule229 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_ruleModule246 = new BitSet(new long[]{0x000000000001A000L});
    public static final BitSet FOLLOW_ruleDataType_in_ruleModule267 = new BitSet(new long[]{0x000000000001A000L});
    public static final BitSet FOLLOW_ruleInterface_in_ruleModule289 = new BitSet(new long[]{0x0000000000012000L});
    public static final BitSet FOLLOW_13_in_ruleModule302 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleModule314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDataType_in_entryRuleDataType350 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDataType360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_ruleDataType397 = new BitSet(new long[]{0x0000000000E00010L});
    public static final BitSet FOLLOW_ruleBaseType_in_ruleDataType419 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDataType445 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDataType463 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleDataType480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleInterface_in_entryRuleInterface516 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleInterface526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_ruleInterface563 = new BitSet(new long[]{0x0000000007000000L});
    public static final BitSet FOLLOW_ruleInterfaceName_in_ruleInterface584 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_ruleInterface596 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_ruleFunction_in_ruleInterface617 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_ruleInterface629 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleInterface641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunction_in_entryRuleFunction677 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFunction687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_ruleFunction724 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleFunction741 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleFunction758 = new BitSet(new long[]{0x0000000018180000L});
    public static final BitSet FOLLOW_ruleParameter_in_ruleFunction779 = new BitSet(new long[]{0x0000000000180000L});
    public static final BitSet FOLLOW_19_in_ruleFunction793 = new BitSet(new long[]{0x0000000018000000L});
    public static final BitSet FOLLOW_ruleParameter_in_ruleFunction814 = new BitSet(new long[]{0x0000000000180000L});
    public static final BitSet FOLLOW_20_in_ruleFunction828 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleFunction840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleParameter_in_entryRuleParameter876 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleParameter886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDirection_in_ruleParameter932 = new BitSet(new long[]{0x0000000000E00010L});
    public static final BitSet FOLLOW_ruleTypeStar_in_ruleParameter953 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleParameter970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTypeStar_in_entryRuleTypeStar1011 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTypeStar1021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBaseType_in_ruleTypeStar1067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleTypeStar1093 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_ruleBaseType1143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_ruleBaseType1160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleBaseType1177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_ruleInterfaceName1222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_ruleInterfaceName1239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_ruleInterfaceName1256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_ruleDirection1301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_ruleDirection1318 = new BitSet(new long[]{0x0000000000000002L});

}