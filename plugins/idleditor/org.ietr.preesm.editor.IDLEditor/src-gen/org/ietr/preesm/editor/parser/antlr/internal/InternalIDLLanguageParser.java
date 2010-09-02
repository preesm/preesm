package org.ietr.preesm.editor.parser.antlr.internal; 

import java.io.InputStream;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.xtext.parsetree.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import org.eclipse.xtext.conversion.ValueConverterException;
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
    public static final int RULE_STRING=6;
    public static final int RULE_ANY_OTHER=10;
    public static final int RULE_INT=5;
    public static final int RULE_WS=9;
    public static final int RULE_SL_COMMENT=8;
    public static final int EOF=-1;
    public static final int RULE_ML_COMMENT=7;

        public InternalIDLLanguageParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g"; }



     	private IDLLanguageGrammarAccess grammarAccess;
     	
        public InternalIDLLanguageParser(TokenStream input, IAstFactory factory, IDLLanguageGrammarAccess grammarAccess) {
            this(input);
            this.factory = factory;
            registerRules(grammarAccess.getGrammar());
            this.grammarAccess = grammarAccess;
        }
        
        @Override
        protected InputStream getTokenFile() {
        	ClassLoader classLoader = getClass().getClassLoader();
        	return classLoader.getResourceAsStream("org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.tokens");
        }
        
        @Override
        protected String getFirstRuleName() {
        	return "IDL";	
       	}
       	
       	@Override
       	protected IDLLanguageGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}



    // $ANTLR start entryRuleIDL
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:78:1: entryRuleIDL returns [EObject current=null] : iv_ruleIDL= ruleIDL EOF ;
    public final EObject entryRuleIDL() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIDL = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:79:2: (iv_ruleIDL= ruleIDL EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:80:2: iv_ruleIDL= ruleIDL EOF
            {
             currentNode = createCompositeNode(grammarAccess.getIDLRule(), currentNode); 
            pushFollow(FOLLOW_ruleIDL_in_entryRuleIDL75);
            iv_ruleIDL=ruleIDL();
            _fsp--;

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
    // $ANTLR end entryRuleIDL


    // $ANTLR start ruleIDL
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:87:1: ruleIDL returns [EObject current=null] : ( (lv_elements_0_0= ruleModule ) ) ;
    public final EObject ruleIDL() throws RecognitionException {
        EObject current = null;

        EObject lv_elements_0_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:92:6: ( ( (lv_elements_0_0= ruleModule ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:93:1: ( (lv_elements_0_0= ruleModule ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:93:1: ( (lv_elements_0_0= ruleModule ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:94:1: (lv_elements_0_0= ruleModule )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:94:1: (lv_elements_0_0= ruleModule )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:95:3: lv_elements_0_0= ruleModule
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getIDLAccess().getElementsModuleParserRuleCall_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleModule_in_ruleIDL130);
            lv_elements_0_0=ruleModule();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getIDLRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		add(
            	       			current, 
            	       			"elements",
            	        		lv_elements_0_0, 
            	        		"Module", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleIDL


    // $ANTLR start entryRuleModule
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:125:1: entryRuleModule returns [EObject current=null] : iv_ruleModule= ruleModule EOF ;
    public final EObject entryRuleModule() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleModule = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:126:2: (iv_ruleModule= ruleModule EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:127:2: iv_ruleModule= ruleModule EOF
            {
             currentNode = createCompositeNode(grammarAccess.getModuleRule(), currentNode); 
            pushFollow(FOLLOW_ruleModule_in_entryRuleModule165);
            iv_ruleModule=ruleModule();
            _fsp--;

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
    // $ANTLR end entryRuleModule


    // $ANTLR start ruleModule
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:134:1: ruleModule returns [EObject current=null] : ( 'module' ( (lv_name_1_0= RULE_ID ) ) '{' ( (lv_types_3_0= ruleDataType ) )* ( (lv_interfaces_4_0= ruleInterface ) )* '}' ';' ) ;
    public final EObject ruleModule() throws RecognitionException {
        EObject current = null;

        Token lv_name_1_0=null;
        EObject lv_types_3_0 = null;

        EObject lv_interfaces_4_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:139:6: ( ( 'module' ( (lv_name_1_0= RULE_ID ) ) '{' ( (lv_types_3_0= ruleDataType ) )* ( (lv_interfaces_4_0= ruleInterface ) )* '}' ';' ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:140:1: ( 'module' ( (lv_name_1_0= RULE_ID ) ) '{' ( (lv_types_3_0= ruleDataType ) )* ( (lv_interfaces_4_0= ruleInterface ) )* '}' ';' )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:140:1: ( 'module' ( (lv_name_1_0= RULE_ID ) ) '{' ( (lv_types_3_0= ruleDataType ) )* ( (lv_interfaces_4_0= ruleInterface ) )* '}' ';' )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:140:3: 'module' ( (lv_name_1_0= RULE_ID ) ) '{' ( (lv_types_3_0= ruleDataType ) )* ( (lv_interfaces_4_0= ruleInterface ) )* '}' ';'
            {
            match(input,11,FOLLOW_11_in_ruleModule210); 

                    createLeafNode(grammarAccess.getModuleAccess().getModuleKeyword_0(), null); 
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:144:1: ( (lv_name_1_0= RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:145:1: (lv_name_1_0= RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:145:1: (lv_name_1_0= RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:146:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)input.LT(1);
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleModule227); 

            			createLeafNode(grammarAccess.getModuleAccess().getNameIDTerminalRuleCall_1_0(), "name"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getModuleRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_1_0, 
            	        		"ID", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            match(input,12,FOLLOW_12_in_ruleModule242); 

                    createLeafNode(grammarAccess.getModuleAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:172:1: ( (lv_types_3_0= ruleDataType ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==15) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:173:1: (lv_types_3_0= ruleDataType )
            	    {
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:173:1: (lv_types_3_0= ruleDataType )
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:174:3: lv_types_3_0= ruleDataType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getModuleAccess().getTypesDataTypeParserRuleCall_3_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleDataType_in_ruleModule263);
            	    lv_types_3_0=ruleDataType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getModuleRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"types",
            	    	        		lv_types_3_0, 
            	    	        		"DataType", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:196:3: ( (lv_interfaces_4_0= ruleInterface ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==16) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:197:1: (lv_interfaces_4_0= ruleInterface )
            	    {
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:197:1: (lv_interfaces_4_0= ruleInterface )
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:198:3: lv_interfaces_4_0= ruleInterface
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getModuleAccess().getInterfacesInterfaceParserRuleCall_4_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleInterface_in_ruleModule285);
            	    lv_interfaces_4_0=ruleInterface();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getModuleRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"interfaces",
            	    	        		lv_interfaces_4_0, 
            	    	        		"Interface", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            match(input,13,FOLLOW_13_in_ruleModule296); 

                    createLeafNode(grammarAccess.getModuleAccess().getRightCurlyBracketKeyword_5(), null); 
                
            match(input,14,FOLLOW_14_in_ruleModule306); 

                    createLeafNode(grammarAccess.getModuleAccess().getSemicolonKeyword_6(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleModule


    // $ANTLR start entryRuleDataType
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:236:1: entryRuleDataType returns [EObject current=null] : iv_ruleDataType= ruleDataType EOF ;
    public final EObject entryRuleDataType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDataType = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:237:2: (iv_ruleDataType= ruleDataType EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:238:2: iv_ruleDataType= ruleDataType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getDataTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleDataType_in_entryRuleDataType342);
            iv_ruleDataType=ruleDataType();
            _fsp--;

             current =iv_ruleDataType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDataType352); 

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
    // $ANTLR end entryRuleDataType


    // $ANTLR start ruleDataType
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:245:1: ruleDataType returns [EObject current=null] : ( 'typedef' ( ( (lv_btype_1_0= ruleBaseType ) ) | ( ( RULE_ID ) ) ) ( (lv_name_3_0= RULE_ID ) ) ';' ) ;
    public final EObject ruleDataType() throws RecognitionException {
        EObject current = null;

        Token lv_name_3_0=null;
        Enumerator lv_btype_1_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:250:6: ( ( 'typedef' ( ( (lv_btype_1_0= ruleBaseType ) ) | ( ( RULE_ID ) ) ) ( (lv_name_3_0= RULE_ID ) ) ';' ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:251:1: ( 'typedef' ( ( (lv_btype_1_0= ruleBaseType ) ) | ( ( RULE_ID ) ) ) ( (lv_name_3_0= RULE_ID ) ) ';' )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:251:1: ( 'typedef' ( ( (lv_btype_1_0= ruleBaseType ) ) | ( ( RULE_ID ) ) ) ( (lv_name_3_0= RULE_ID ) ) ';' )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:251:3: 'typedef' ( ( (lv_btype_1_0= ruleBaseType ) ) | ( ( RULE_ID ) ) ) ( (lv_name_3_0= RULE_ID ) ) ';'
            {
            match(input,15,FOLLOW_15_in_ruleDataType387); 

                    createLeafNode(grammarAccess.getDataTypeAccess().getTypedefKeyword_0(), null); 
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:255:1: ( ( (lv_btype_1_0= ruleBaseType ) ) | ( ( RULE_ID ) ) )
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
                    new NoViableAltException("255:1: ( ( (lv_btype_1_0= ruleBaseType ) ) | ( ( RULE_ID ) ) )", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:255:2: ( (lv_btype_1_0= ruleBaseType ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:255:2: ( (lv_btype_1_0= ruleBaseType ) )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:256:1: (lv_btype_1_0= ruleBaseType )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:256:1: (lv_btype_1_0= ruleBaseType )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:257:3: lv_btype_1_0= ruleBaseType
                    {
                     
                    	        currentNode=createCompositeNode(grammarAccess.getDataTypeAccess().getBtypeBaseTypeEnumRuleCall_1_0_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleBaseType_in_ruleDataType409);
                    lv_btype_1_0=ruleBaseType();
                    _fsp--;


                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getDataTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode.getParent(), current);
                    	        }
                    	        try {
                    	       		set(
                    	       			current, 
                    	       			"btype",
                    	        		lv_btype_1_0, 
                    	        		"BaseType", 
                    	        		currentNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:280:6: ( ( RULE_ID ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:280:6: ( ( RULE_ID ) )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:281:1: ( RULE_ID )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:281:1: ( RULE_ID )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:282:3: RULE_ID
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getDataTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDataType433); 

                    		createLeafNode(grammarAccess.getDataTypeAccess().getCtypeDataTypeCrossReference_1_1_0(), "ctype"); 
                    	

                    }


                    }


                    }
                    break;

            }

            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:294:3: ( (lv_name_3_0= RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:295:1: (lv_name_3_0= RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:295:1: (lv_name_3_0= RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:296:3: lv_name_3_0= RULE_ID
            {
            lv_name_3_0=(Token)input.LT(1);
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDataType451); 

            			createLeafNode(grammarAccess.getDataTypeAccess().getNameIDTerminalRuleCall_2_0(), "name"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getDataTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_3_0, 
            	        		"ID", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            match(input,14,FOLLOW_14_in_ruleDataType466); 

                    createLeafNode(grammarAccess.getDataTypeAccess().getSemicolonKeyword_3(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleDataType


    // $ANTLR start entryRuleInterface
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:330:1: entryRuleInterface returns [EObject current=null] : iv_ruleInterface= ruleInterface EOF ;
    public final EObject entryRuleInterface() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleInterface = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:331:2: (iv_ruleInterface= ruleInterface EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:332:2: iv_ruleInterface= ruleInterface EOF
            {
             currentNode = createCompositeNode(grammarAccess.getInterfaceRule(), currentNode); 
            pushFollow(FOLLOW_ruleInterface_in_entryRuleInterface502);
            iv_ruleInterface=ruleInterface();
            _fsp--;

             current =iv_ruleInterface; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleInterface512); 

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
    // $ANTLR end entryRuleInterface


    // $ANTLR start ruleInterface
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:339:1: ruleInterface returns [EObject current=null] : ( 'interface' ( (lv_name_1_0= ruleInterfaceName ) ) '{' ( (lv_function_3_0= ruleFunction ) ) '}' ';' ) ;
    public final EObject ruleInterface() throws RecognitionException {
        EObject current = null;

        Enumerator lv_name_1_0 = null;

        EObject lv_function_3_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:344:6: ( ( 'interface' ( (lv_name_1_0= ruleInterfaceName ) ) '{' ( (lv_function_3_0= ruleFunction ) ) '}' ';' ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:345:1: ( 'interface' ( (lv_name_1_0= ruleInterfaceName ) ) '{' ( (lv_function_3_0= ruleFunction ) ) '}' ';' )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:345:1: ( 'interface' ( (lv_name_1_0= ruleInterfaceName ) ) '{' ( (lv_function_3_0= ruleFunction ) ) '}' ';' )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:345:3: 'interface' ( (lv_name_1_0= ruleInterfaceName ) ) '{' ( (lv_function_3_0= ruleFunction ) ) '}' ';'
            {
            match(input,16,FOLLOW_16_in_ruleInterface547); 

                    createLeafNode(grammarAccess.getInterfaceAccess().getInterfaceKeyword_0(), null); 
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:349:1: ( (lv_name_1_0= ruleInterfaceName ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:350:1: (lv_name_1_0= ruleInterfaceName )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:350:1: (lv_name_1_0= ruleInterfaceName )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:351:3: lv_name_1_0= ruleInterfaceName
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getInterfaceAccess().getNameInterfaceNameEnumRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleInterfaceName_in_ruleInterface568);
            lv_name_1_0=ruleInterfaceName();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getInterfaceRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_1_0, 
            	        		"InterfaceName", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,12,FOLLOW_12_in_ruleInterface578); 

                    createLeafNode(grammarAccess.getInterfaceAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:377:1: ( (lv_function_3_0= ruleFunction ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:378:1: (lv_function_3_0= ruleFunction )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:378:1: (lv_function_3_0= ruleFunction )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:379:3: lv_function_3_0= ruleFunction
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getInterfaceAccess().getFunctionFunctionParserRuleCall_3_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleFunction_in_ruleInterface599);
            lv_function_3_0=ruleFunction();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getInterfaceRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"function",
            	        		lv_function_3_0, 
            	        		"Function", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            match(input,13,FOLLOW_13_in_ruleInterface609); 

                    createLeafNode(grammarAccess.getInterfaceAccess().getRightCurlyBracketKeyword_4(), null); 
                
            match(input,14,FOLLOW_14_in_ruleInterface619); 

                    createLeafNode(grammarAccess.getInterfaceAccess().getSemicolonKeyword_5(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleInterface


    // $ANTLR start entryRuleFunction
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:417:1: entryRuleFunction returns [EObject current=null] : iv_ruleFunction= ruleFunction EOF ;
    public final EObject entryRuleFunction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFunction = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:418:2: (iv_ruleFunction= ruleFunction EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:419:2: iv_ruleFunction= ruleFunction EOF
            {
             currentNode = createCompositeNode(grammarAccess.getFunctionRule(), currentNode); 
            pushFollow(FOLLOW_ruleFunction_in_entryRuleFunction655);
            iv_ruleFunction=ruleFunction();
            _fsp--;

             current =iv_ruleFunction; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleFunction665); 

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
    // $ANTLR end entryRuleFunction


    // $ANTLR start ruleFunction
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:426:1: ruleFunction returns [EObject current=null] : ( 'void' ( (lv_name_1_0= RULE_ID ) ) '(' ( (lv_parameters_3_0= ruleParameter ) ) ( ',' ( (lv_parameters_5_0= ruleParameter ) ) )* ')' ';' ) ;
    public final EObject ruleFunction() throws RecognitionException {
        EObject current = null;

        Token lv_name_1_0=null;
        EObject lv_parameters_3_0 = null;

        EObject lv_parameters_5_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:431:6: ( ( 'void' ( (lv_name_1_0= RULE_ID ) ) '(' ( (lv_parameters_3_0= ruleParameter ) ) ( ',' ( (lv_parameters_5_0= ruleParameter ) ) )* ')' ';' ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:432:1: ( 'void' ( (lv_name_1_0= RULE_ID ) ) '(' ( (lv_parameters_3_0= ruleParameter ) ) ( ',' ( (lv_parameters_5_0= ruleParameter ) ) )* ')' ';' )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:432:1: ( 'void' ( (lv_name_1_0= RULE_ID ) ) '(' ( (lv_parameters_3_0= ruleParameter ) ) ( ',' ( (lv_parameters_5_0= ruleParameter ) ) )* ')' ';' )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:432:3: 'void' ( (lv_name_1_0= RULE_ID ) ) '(' ( (lv_parameters_3_0= ruleParameter ) ) ( ',' ( (lv_parameters_5_0= ruleParameter ) ) )* ')' ';'
            {
            match(input,17,FOLLOW_17_in_ruleFunction700); 

                    createLeafNode(grammarAccess.getFunctionAccess().getVoidKeyword_0(), null); 
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:436:1: ( (lv_name_1_0= RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:437:1: (lv_name_1_0= RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:437:1: (lv_name_1_0= RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:438:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)input.LT(1);
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleFunction717); 

            			createLeafNode(grammarAccess.getFunctionAccess().getNameIDTerminalRuleCall_1_0(), "name"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getFunctionRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_1_0, 
            	        		"ID", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            match(input,18,FOLLOW_18_in_ruleFunction732); 

                    createLeafNode(grammarAccess.getFunctionAccess().getLeftParenthesisKeyword_2(), null); 
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:464:1: ( (lv_parameters_3_0= ruleParameter ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:465:1: (lv_parameters_3_0= ruleParameter )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:465:1: (lv_parameters_3_0= ruleParameter )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:466:3: lv_parameters_3_0= ruleParameter
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getFunctionAccess().getParametersParameterParserRuleCall_3_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleParameter_in_ruleFunction753);
            lv_parameters_3_0=ruleParameter();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getFunctionRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		add(
            	       			current, 
            	       			"parameters",
            	        		lv_parameters_3_0, 
            	        		"Parameter", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:488:2: ( ',' ( (lv_parameters_5_0= ruleParameter ) ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==19) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:488:4: ',' ( (lv_parameters_5_0= ruleParameter ) )
            	    {
            	    match(input,19,FOLLOW_19_in_ruleFunction764); 

            	            createLeafNode(grammarAccess.getFunctionAccess().getCommaKeyword_4_0(), null); 
            	        
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:492:1: ( (lv_parameters_5_0= ruleParameter ) )
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:493:1: (lv_parameters_5_0= ruleParameter )
            	    {
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:493:1: (lv_parameters_5_0= ruleParameter )
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:494:3: lv_parameters_5_0= ruleParameter
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getFunctionAccess().getParametersParameterParserRuleCall_4_1_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleParameter_in_ruleFunction785);
            	    lv_parameters_5_0=ruleParameter();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getFunctionRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"parameters",
            	    	        		lv_parameters_5_0, 
            	    	        		"Parameter", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            match(input,20,FOLLOW_20_in_ruleFunction797); 

                    createLeafNode(grammarAccess.getFunctionAccess().getRightParenthesisKeyword_5(), null); 
                
            match(input,14,FOLLOW_14_in_ruleFunction807); 

                    createLeafNode(grammarAccess.getFunctionAccess().getSemicolonKeyword_6(), null); 
                

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleFunction


    // $ANTLR start entryRuleParameter
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:532:1: entryRuleParameter returns [EObject current=null] : iv_ruleParameter= ruleParameter EOF ;
    public final EObject entryRuleParameter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParameter = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:533:2: (iv_ruleParameter= ruleParameter EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:534:2: iv_ruleParameter= ruleParameter EOF
            {
             currentNode = createCompositeNode(grammarAccess.getParameterRule(), currentNode); 
            pushFollow(FOLLOW_ruleParameter_in_entryRuleParameter843);
            iv_ruleParameter=ruleParameter();
            _fsp--;

             current =iv_ruleParameter; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleParameter853); 

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
    // $ANTLR end entryRuleParameter


    // $ANTLR start ruleParameter
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:541:1: ruleParameter returns [EObject current=null] : ( ( (lv_direction_0_0= ruleDirection ) ) ( (lv_type_1_0= ruleTypeStar ) ) ( (lv_name_2_0= RULE_ID ) ) ) ;
    public final EObject ruleParameter() throws RecognitionException {
        EObject current = null;

        Token lv_name_2_0=null;
        Enumerator lv_direction_0_0 = null;

        EObject lv_type_1_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:546:6: ( ( ( (lv_direction_0_0= ruleDirection ) ) ( (lv_type_1_0= ruleTypeStar ) ) ( (lv_name_2_0= RULE_ID ) ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:547:1: ( ( (lv_direction_0_0= ruleDirection ) ) ( (lv_type_1_0= ruleTypeStar ) ) ( (lv_name_2_0= RULE_ID ) ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:547:1: ( ( (lv_direction_0_0= ruleDirection ) ) ( (lv_type_1_0= ruleTypeStar ) ) ( (lv_name_2_0= RULE_ID ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:547:2: ( (lv_direction_0_0= ruleDirection ) ) ( (lv_type_1_0= ruleTypeStar ) ) ( (lv_name_2_0= RULE_ID ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:547:2: ( (lv_direction_0_0= ruleDirection ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:548:1: (lv_direction_0_0= ruleDirection )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:548:1: (lv_direction_0_0= ruleDirection )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:549:3: lv_direction_0_0= ruleDirection
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getParameterAccess().getDirectionDirectionEnumRuleCall_0_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleDirection_in_ruleParameter899);
            lv_direction_0_0=ruleDirection();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getParameterRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"direction",
            	        		lv_direction_0_0, 
            	        		"Direction", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:571:2: ( (lv_type_1_0= ruleTypeStar ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:572:1: (lv_type_1_0= ruleTypeStar )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:572:1: (lv_type_1_0= ruleTypeStar )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:573:3: lv_type_1_0= ruleTypeStar
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getParameterAccess().getTypeTypeStarParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleTypeStar_in_ruleParameter920);
            lv_type_1_0=ruleTypeStar();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getParameterRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"type",
            	        		lv_type_1_0, 
            	        		"TypeStar", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:595:2: ( (lv_name_2_0= RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:596:1: (lv_name_2_0= RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:596:1: (lv_name_2_0= RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:597:3: lv_name_2_0= RULE_ID
            {
            lv_name_2_0=(Token)input.LT(1);
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleParameter937); 

            			createLeafNode(grammarAccess.getParameterAccess().getNameIDTerminalRuleCall_2_0(), "name"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getParameterRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_2_0, 
            	        		"ID", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }


            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleParameter


    // $ANTLR start entryRuleTypeStar
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:627:1: entryRuleTypeStar returns [EObject current=null] : iv_ruleTypeStar= ruleTypeStar EOF ;
    public final EObject entryRuleTypeStar() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTypeStar = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:628:2: (iv_ruleTypeStar= ruleTypeStar EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:629:2: iv_ruleTypeStar= ruleTypeStar EOF
            {
             currentNode = createCompositeNode(grammarAccess.getTypeStarRule(), currentNode); 
            pushFollow(FOLLOW_ruleTypeStar_in_entryRuleTypeStar978);
            iv_ruleTypeStar=ruleTypeStar();
            _fsp--;

             current =iv_ruleTypeStar; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTypeStar988); 

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
    // $ANTLR end entryRuleTypeStar


    // $ANTLR start ruleTypeStar
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:636:1: ruleTypeStar returns [EObject current=null] : ( ( (lv_btype_0_0= ruleBaseType ) ) | ( ( RULE_ID ) ) ) ;
    public final EObject ruleTypeStar() throws RecognitionException {
        EObject current = null;

        Enumerator lv_btype_0_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:641:6: ( ( ( (lv_btype_0_0= ruleBaseType ) ) | ( ( RULE_ID ) ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:642:1: ( ( (lv_btype_0_0= ruleBaseType ) ) | ( ( RULE_ID ) ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:642:1: ( ( (lv_btype_0_0= ruleBaseType ) ) | ( ( RULE_ID ) ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( ((LA5_0>=21 && LA5_0<=23)) ) {
                alt5=1;
            }
            else if ( (LA5_0==RULE_ID) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("642:1: ( ( (lv_btype_0_0= ruleBaseType ) ) | ( ( RULE_ID ) ) )", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:642:2: ( (lv_btype_0_0= ruleBaseType ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:642:2: ( (lv_btype_0_0= ruleBaseType ) )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:643:1: (lv_btype_0_0= ruleBaseType )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:643:1: (lv_btype_0_0= ruleBaseType )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:644:3: lv_btype_0_0= ruleBaseType
                    {
                     
                    	        currentNode=createCompositeNode(grammarAccess.getTypeStarAccess().getBtypeBaseTypeEnumRuleCall_0_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleBaseType_in_ruleTypeStar1034);
                    lv_btype_0_0=ruleBaseType();
                    _fsp--;


                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getTypeStarRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode.getParent(), current);
                    	        }
                    	        try {
                    	       		set(
                    	       			current, 
                    	       			"btype",
                    	        		lv_btype_0_0, 
                    	        		"BaseType", 
                    	        		currentNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:667:6: ( ( RULE_ID ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:667:6: ( ( RULE_ID ) )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:668:1: ( RULE_ID )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:668:1: ( RULE_ID )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:669:3: RULE_ID
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getTypeStarRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleTypeStar1058); 

                    		createLeafNode(grammarAccess.getTypeStarAccess().getCtypeDataTypeCrossReference_1_0(), "ctype"); 
                    	

                    }


                    }


                    }
                    break;

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleTypeStar


    // $ANTLR start ruleBaseType
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:689:1: ruleBaseType returns [Enumerator current=null] : ( ( 'int' ) | ( 'long' ) | ( 'char' ) ) ;
    public final Enumerator ruleBaseType() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:693:6: ( ( ( 'int' ) | ( 'long' ) | ( 'char' ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:694:1: ( ( 'int' ) | ( 'long' ) | ( 'char' ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:694:1: ( ( 'int' ) | ( 'long' ) | ( 'char' ) )
            int alt6=3;
            switch ( input.LA(1) ) {
            case 21:
                {
                alt6=1;
                }
                break;
            case 22:
                {
                alt6=2;
                }
                break;
            case 23:
                {
                alt6=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("694:1: ( ( 'int' ) | ( 'long' ) | ( 'char' ) )", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:694:2: ( 'int' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:694:2: ( 'int' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:694:4: 'int'
                    {
                    match(input,21,FOLLOW_21_in_ruleBaseType1106); 

                            current = grammarAccess.getBaseTypeAccess().getIntEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getBaseTypeAccess().getIntEnumLiteralDeclaration_0(), null); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:700:6: ( 'long' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:700:6: ( 'long' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:700:8: 'long'
                    {
                    match(input,22,FOLLOW_22_in_ruleBaseType1121); 

                            current = grammarAccess.getBaseTypeAccess().getLongEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getBaseTypeAccess().getLongEnumLiteralDeclaration_1(), null); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:706:6: ( 'char' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:706:6: ( 'char' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:706:8: 'char'
                    {
                    match(input,23,FOLLOW_23_in_ruleBaseType1136); 

                            current = grammarAccess.getBaseTypeAccess().getCharEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getBaseTypeAccess().getCharEnumLiteralDeclaration_2(), null); 
                        

                    }


                    }
                    break;

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleBaseType


    // $ANTLR start ruleInterfaceName
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:716:1: ruleInterfaceName returns [Enumerator current=null] : ( ( 'init' ) | ( 'loop' ) | ( 'end' ) ) ;
    public final Enumerator ruleInterfaceName() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:720:6: ( ( ( 'init' ) | ( 'loop' ) | ( 'end' ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:721:1: ( ( 'init' ) | ( 'loop' ) | ( 'end' ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:721:1: ( ( 'init' ) | ( 'loop' ) | ( 'end' ) )
            int alt7=3;
            switch ( input.LA(1) ) {
            case 24:
                {
                alt7=1;
                }
                break;
            case 25:
                {
                alt7=2;
                }
                break;
            case 26:
                {
                alt7=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("721:1: ( ( 'init' ) | ( 'loop' ) | ( 'end' ) )", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:721:2: ( 'init' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:721:2: ( 'init' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:721:4: 'init'
                    {
                    match(input,24,FOLLOW_24_in_ruleInterfaceName1179); 

                            current = grammarAccess.getInterfaceNameAccess().getInitEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getInterfaceNameAccess().getInitEnumLiteralDeclaration_0(), null); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:727:6: ( 'loop' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:727:6: ( 'loop' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:727:8: 'loop'
                    {
                    match(input,25,FOLLOW_25_in_ruleInterfaceName1194); 

                            current = grammarAccess.getInterfaceNameAccess().getLoopEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getInterfaceNameAccess().getLoopEnumLiteralDeclaration_1(), null); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:733:6: ( 'end' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:733:6: ( 'end' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:733:8: 'end'
                    {
                    match(input,26,FOLLOW_26_in_ruleInterfaceName1209); 

                            current = grammarAccess.getInterfaceNameAccess().getEndEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getInterfaceNameAccess().getEndEnumLiteralDeclaration_2(), null); 
                        

                    }


                    }
                    break;

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleInterfaceName


    // $ANTLR start ruleDirection
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:743:1: ruleDirection returns [Enumerator current=null] : ( ( 'in' ) | ( 'out' ) ) ;
    public final Enumerator ruleDirection() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:747:6: ( ( ( 'in' ) | ( 'out' ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:748:1: ( ( 'in' ) | ( 'out' ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:748:1: ( ( 'in' ) | ( 'out' ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==27) ) {
                alt8=1;
            }
            else if ( (LA8_0==28) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("748:1: ( ( 'in' ) | ( 'out' ) )", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:748:2: ( 'in' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:748:2: ( 'in' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:748:4: 'in'
                    {
                    match(input,27,FOLLOW_27_in_ruleDirection1252); 

                            current = grammarAccess.getDirectionAccess().getInEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getDirectionAccess().getInEnumLiteralDeclaration_0(), null); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:754:6: ( 'out' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:754:6: ( 'out' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:754:8: 'out'
                    {
                    match(input,28,FOLLOW_28_in_ruleDirection1267); 

                            current = grammarAccess.getDirectionAccess().getOutEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getDirectionAccess().getOutEnumLiteralDeclaration_1(), null); 
                        

                    }


                    }
                    break;

            }


            }

             resetLookahead(); 
                	lastConsumedNode = currentNode;
                
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end ruleDirection


 

    public static final BitSet FOLLOW_ruleIDL_in_entryRuleIDL75 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleIDL85 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleModule_in_ruleIDL130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleModule_in_entryRuleModule165 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleModule175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_11_in_ruleModule210 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleModule227 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_ruleModule242 = new BitSet(new long[]{0x000000000001A000L});
    public static final BitSet FOLLOW_ruleDataType_in_ruleModule263 = new BitSet(new long[]{0x000000000001A000L});
    public static final BitSet FOLLOW_ruleInterface_in_ruleModule285 = new BitSet(new long[]{0x0000000000012000L});
    public static final BitSet FOLLOW_13_in_ruleModule296 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleModule306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDataType_in_entryRuleDataType342 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDataType352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_ruleDataType387 = new BitSet(new long[]{0x0000000000E00010L});
    public static final BitSet FOLLOW_ruleBaseType_in_ruleDataType409 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDataType433 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDataType451 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleDataType466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleInterface_in_entryRuleInterface502 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleInterface512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_ruleInterface547 = new BitSet(new long[]{0x0000000007000000L});
    public static final BitSet FOLLOW_ruleInterfaceName_in_ruleInterface568 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_ruleInterface578 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_ruleFunction_in_ruleInterface599 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_ruleInterface609 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleInterface619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunction_in_entryRuleFunction655 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFunction665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_ruleFunction700 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleFunction717 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleFunction732 = new BitSet(new long[]{0x0000000018000000L});
    public static final BitSet FOLLOW_ruleParameter_in_ruleFunction753 = new BitSet(new long[]{0x0000000000180000L});
    public static final BitSet FOLLOW_19_in_ruleFunction764 = new BitSet(new long[]{0x0000000018000000L});
    public static final BitSet FOLLOW_ruleParameter_in_ruleFunction785 = new BitSet(new long[]{0x0000000000180000L});
    public static final BitSet FOLLOW_20_in_ruleFunction797 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleFunction807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleParameter_in_entryRuleParameter843 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleParameter853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDirection_in_ruleParameter899 = new BitSet(new long[]{0x0000000000E00010L});
    public static final BitSet FOLLOW_ruleTypeStar_in_ruleParameter920 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleParameter937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTypeStar_in_entryRuleTypeStar978 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTypeStar988 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBaseType_in_ruleTypeStar1034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleTypeStar1058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_ruleBaseType1106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_ruleBaseType1121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleBaseType1136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_ruleInterfaceName1179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_ruleInterfaceName1194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_ruleInterfaceName1209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_ruleDirection1252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_ruleDirection1267 = new BitSet(new long[]{0x0000000000000002L});

}