package org.ietr.preesm.editor.parser.antlr.internal; 

import java.io.InputStream;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.xtext.parsetree.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_INT", "RULE_STRING", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'datatype'", "'entity'", "'extends'", "'{'", "'}'", "':'", "'*'"
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
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:77:1: entryRuleIDL returns [EObject current=null] : iv_ruleIDL= ruleIDL EOF ;
    public final EObject entryRuleIDL() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIDL = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:78:2: (iv_ruleIDL= ruleIDL EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:79:2: iv_ruleIDL= ruleIDL EOF
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
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:86:1: ruleIDL returns [EObject current=null] : ( (lv_elements_0_0= ruleType ) )* ;
    public final EObject ruleIDL() throws RecognitionException {
        EObject current = null;

        EObject lv_elements_0_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:91:6: ( ( (lv_elements_0_0= ruleType ) )* )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:92:1: ( (lv_elements_0_0= ruleType ) )*
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:92:1: ( (lv_elements_0_0= ruleType ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>=11 && LA1_0<=12)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:93:1: (lv_elements_0_0= ruleType )
            	    {
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:93:1: (lv_elements_0_0= ruleType )
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:94:3: lv_elements_0_0= ruleType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getIDLAccess().getElementsTypeParserRuleCall_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleType_in_ruleIDL130);
            	    lv_elements_0_0=ruleType();
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
            	    	        		"Type", 
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


    // $ANTLR start entryRuleType
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:124:1: entryRuleType returns [EObject current=null] : iv_ruleType= ruleType EOF ;
    public final EObject entryRuleType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleType = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:125:2: (iv_ruleType= ruleType EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:126:2: iv_ruleType= ruleType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleType_in_entryRuleType166);
            iv_ruleType=ruleType();
            _fsp--;

             current =iv_ruleType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleType176); 

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
    // $ANTLR end entryRuleType


    // $ANTLR start ruleType
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:133:1: ruleType returns [EObject current=null] : (this_DataType_0= ruleDataType | this_Entity_1= ruleEntity ) ;
    public final EObject ruleType() throws RecognitionException {
        EObject current = null;

        EObject this_DataType_0 = null;

        EObject this_Entity_1 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:138:6: ( (this_DataType_0= ruleDataType | this_Entity_1= ruleEntity ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:139:1: (this_DataType_0= ruleDataType | this_Entity_1= ruleEntity )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:139:1: (this_DataType_0= ruleDataType | this_Entity_1= ruleEntity )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==11) ) {
                alt2=1;
            }
            else if ( (LA2_0==12) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("139:1: (this_DataType_0= ruleDataType | this_Entity_1= ruleEntity )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:140:5: this_DataType_0= ruleDataType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getTypeAccess().getDataTypeParserRuleCall_0(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleDataType_in_ruleType223);
                    this_DataType_0=ruleDataType();
                    _fsp--;

                     
                            current = this_DataType_0; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:150:5: this_Entity_1= ruleEntity
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getTypeAccess().getEntityParserRuleCall_1(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleEntity_in_ruleType250);
                    this_Entity_1=ruleEntity();
                    _fsp--;

                     
                            current = this_Entity_1; 
                            currentNode = currentNode.getParent();
                        

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
    // $ANTLR end ruleType


    // $ANTLR start entryRuleDataType
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:166:1: entryRuleDataType returns [EObject current=null] : iv_ruleDataType= ruleDataType EOF ;
    public final EObject entryRuleDataType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDataType = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:167:2: (iv_ruleDataType= ruleDataType EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:168:2: iv_ruleDataType= ruleDataType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getDataTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleDataType_in_entryRuleDataType285);
            iv_ruleDataType=ruleDataType();
            _fsp--;

             current =iv_ruleDataType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDataType295); 

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
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:175:1: ruleDataType returns [EObject current=null] : ( 'datatype' ( (lv_name_1_0= RULE_ID ) ) ) ;
    public final EObject ruleDataType() throws RecognitionException {
        EObject current = null;

        Token lv_name_1_0=null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:180:6: ( ( 'datatype' ( (lv_name_1_0= RULE_ID ) ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:181:1: ( 'datatype' ( (lv_name_1_0= RULE_ID ) ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:181:1: ( 'datatype' ( (lv_name_1_0= RULE_ID ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:181:3: 'datatype' ( (lv_name_1_0= RULE_ID ) )
            {
            match(input,11,FOLLOW_11_in_ruleDataType330); 

                    createLeafNode(grammarAccess.getDataTypeAccess().getDatatypeKeyword_0(), null); 
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:185:1: ( (lv_name_1_0= RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:186:1: (lv_name_1_0= RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:186:1: (lv_name_1_0= RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:187:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)input.LT(1);
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDataType347); 

            			createLeafNode(grammarAccess.getDataTypeAccess().getNameIDTerminalRuleCall_1_0(), "name"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getDataTypeRule().getType().getClassifier());
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


    // $ANTLR start entryRuleEntity
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:217:1: entryRuleEntity returns [EObject current=null] : iv_ruleEntity= ruleEntity EOF ;
    public final EObject entryRuleEntity() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEntity = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:218:2: (iv_ruleEntity= ruleEntity EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:219:2: iv_ruleEntity= ruleEntity EOF
            {
             currentNode = createCompositeNode(grammarAccess.getEntityRule(), currentNode); 
            pushFollow(FOLLOW_ruleEntity_in_entryRuleEntity388);
            iv_ruleEntity=ruleEntity();
            _fsp--;

             current =iv_ruleEntity; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleEntity398); 

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
    // $ANTLR end entryRuleEntity


    // $ANTLR start ruleEntity
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:226:1: ruleEntity returns [EObject current=null] : ( 'entity' ( (lv_name_1_0= RULE_ID ) ) ( 'extends' ( ( RULE_ID ) ) )? '{' ( (lv_features_5_0= ruleFeature ) )* '}' ) ;
    public final EObject ruleEntity() throws RecognitionException {
        EObject current = null;

        Token lv_name_1_0=null;
        EObject lv_features_5_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:231:6: ( ( 'entity' ( (lv_name_1_0= RULE_ID ) ) ( 'extends' ( ( RULE_ID ) ) )? '{' ( (lv_features_5_0= ruleFeature ) )* '}' ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:232:1: ( 'entity' ( (lv_name_1_0= RULE_ID ) ) ( 'extends' ( ( RULE_ID ) ) )? '{' ( (lv_features_5_0= ruleFeature ) )* '}' )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:232:1: ( 'entity' ( (lv_name_1_0= RULE_ID ) ) ( 'extends' ( ( RULE_ID ) ) )? '{' ( (lv_features_5_0= ruleFeature ) )* '}' )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:232:3: 'entity' ( (lv_name_1_0= RULE_ID ) ) ( 'extends' ( ( RULE_ID ) ) )? '{' ( (lv_features_5_0= ruleFeature ) )* '}'
            {
            match(input,12,FOLLOW_12_in_ruleEntity433); 

                    createLeafNode(grammarAccess.getEntityAccess().getEntityKeyword_0(), null); 
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:236:1: ( (lv_name_1_0= RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:237:1: (lv_name_1_0= RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:237:1: (lv_name_1_0= RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:238:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)input.LT(1);
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleEntity450); 

            			createLeafNode(grammarAccess.getEntityAccess().getNameIDTerminalRuleCall_1_0(), "name"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getEntityRule().getType().getClassifier());
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

            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:260:2: ( 'extends' ( ( RULE_ID ) ) )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==13) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:260:4: 'extends' ( ( RULE_ID ) )
                    {
                    match(input,13,FOLLOW_13_in_ruleEntity466); 

                            createLeafNode(grammarAccess.getEntityAccess().getExtendsKeyword_2_0(), null); 
                        
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:264:1: ( ( RULE_ID ) )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:265:1: ( RULE_ID )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:265:1: ( RULE_ID )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:266:3: RULE_ID
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getEntityRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleEntity484); 

                    		createLeafNode(grammarAccess.getEntityAccess().getSuperTypeEntityCrossReference_2_1_0(), "superType"); 
                    	

                    }


                    }


                    }
                    break;

            }

            match(input,14,FOLLOW_14_in_ruleEntity496); 

                    createLeafNode(grammarAccess.getEntityAccess().getLeftCurlyBracketKeyword_3(), null); 
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:282:1: ( (lv_features_5_0= ruleFeature ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==RULE_ID) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:283:1: (lv_features_5_0= ruleFeature )
            	    {
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:283:1: (lv_features_5_0= ruleFeature )
            	    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:284:3: lv_features_5_0= ruleFeature
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getEntityAccess().getFeaturesFeatureParserRuleCall_4_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleFeature_in_ruleEntity517);
            	    lv_features_5_0=ruleFeature();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getEntityRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        try {
            	    	       		add(
            	    	       			current, 
            	    	       			"features",
            	    	        		lv_features_5_0, 
            	    	        		"Feature", 
            	    	        		currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            match(input,15,FOLLOW_15_in_ruleEntity528); 

                    createLeafNode(grammarAccess.getEntityAccess().getRightCurlyBracketKeyword_5(), null); 
                

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
    // $ANTLR end ruleEntity


    // $ANTLR start entryRuleFeature
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:318:1: entryRuleFeature returns [EObject current=null] : iv_ruleFeature= ruleFeature EOF ;
    public final EObject entryRuleFeature() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFeature = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:319:2: (iv_ruleFeature= ruleFeature EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:320:2: iv_ruleFeature= ruleFeature EOF
            {
             currentNode = createCompositeNode(grammarAccess.getFeatureRule(), currentNode); 
            pushFollow(FOLLOW_ruleFeature_in_entryRuleFeature564);
            iv_ruleFeature=ruleFeature();
            _fsp--;

             current =iv_ruleFeature; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleFeature574); 

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
    // $ANTLR end entryRuleFeature


    // $ANTLR start ruleFeature
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:327:1: ruleFeature returns [EObject current=null] : ( ( (lv_name_0_0= RULE_ID ) ) ':' ( (lv_type_2_0= ruleTypeRef ) ) ) ;
    public final EObject ruleFeature() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        EObject lv_type_2_0 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:332:6: ( ( ( (lv_name_0_0= RULE_ID ) ) ':' ( (lv_type_2_0= ruleTypeRef ) ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:333:1: ( ( (lv_name_0_0= RULE_ID ) ) ':' ( (lv_type_2_0= ruleTypeRef ) ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:333:1: ( ( (lv_name_0_0= RULE_ID ) ) ':' ( (lv_type_2_0= ruleTypeRef ) ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:333:2: ( (lv_name_0_0= RULE_ID ) ) ':' ( (lv_type_2_0= ruleTypeRef ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:333:2: ( (lv_name_0_0= RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:334:1: (lv_name_0_0= RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:334:1: (lv_name_0_0= RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:335:3: lv_name_0_0= RULE_ID
            {
            lv_name_0_0=(Token)input.LT(1);
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleFeature616); 

            			createLeafNode(grammarAccess.getFeatureAccess().getNameIDTerminalRuleCall_0_0(), "name"); 
            		

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getFeatureRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"name",
            	        		lv_name_0_0, 
            	        		"ID", 
            	        		lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }


            }

            match(input,16,FOLLOW_16_in_ruleFeature631); 

                    createLeafNode(grammarAccess.getFeatureAccess().getColonKeyword_1(), null); 
                
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:361:1: ( (lv_type_2_0= ruleTypeRef ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:362:1: (lv_type_2_0= ruleTypeRef )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:362:1: (lv_type_2_0= ruleTypeRef )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:363:3: lv_type_2_0= ruleTypeRef
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getFeatureAccess().getTypeTypeRefParserRuleCall_2_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleTypeRef_in_ruleFeature652);
            lv_type_2_0=ruleTypeRef();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getFeatureRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        try {
            	       		set(
            	       			current, 
            	       			"type",
            	        		lv_type_2_0, 
            	        		"TypeRef", 
            	        		currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

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
    // $ANTLR end ruleFeature


    // $ANTLR start entryRuleTypeRef
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:393:1: entryRuleTypeRef returns [EObject current=null] : iv_ruleTypeRef= ruleTypeRef EOF ;
    public final EObject entryRuleTypeRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTypeRef = null;


        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:394:2: (iv_ruleTypeRef= ruleTypeRef EOF )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:395:2: iv_ruleTypeRef= ruleTypeRef EOF
            {
             currentNode = createCompositeNode(grammarAccess.getTypeRefRule(), currentNode); 
            pushFollow(FOLLOW_ruleTypeRef_in_entryRuleTypeRef688);
            iv_ruleTypeRef=ruleTypeRef();
            _fsp--;

             current =iv_ruleTypeRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTypeRef698); 

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
    // $ANTLR end entryRuleTypeRef


    // $ANTLR start ruleTypeRef
    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:402:1: ruleTypeRef returns [EObject current=null] : ( ( ( RULE_ID ) ) ( (lv_multi_1_0= '*' ) )? ) ;
    public final EObject ruleTypeRef() throws RecognitionException {
        EObject current = null;

        Token lv_multi_1_0=null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:407:6: ( ( ( ( RULE_ID ) ) ( (lv_multi_1_0= '*' ) )? ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:408:1: ( ( ( RULE_ID ) ) ( (lv_multi_1_0= '*' ) )? )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:408:1: ( ( ( RULE_ID ) ) ( (lv_multi_1_0= '*' ) )? )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:408:2: ( ( RULE_ID ) ) ( (lv_multi_1_0= '*' ) )?
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:408:2: ( ( RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:409:1: ( RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:409:1: ( RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:410:3: RULE_ID
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getTypeRefRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleTypeRef741); 

            		createLeafNode(grammarAccess.getTypeRefAccess().getReferencedTypeCrossReference_0_0(), "referenced"); 
            	

            }


            }

            // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:422:2: ( (lv_multi_1_0= '*' ) )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==17) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:423:1: (lv_multi_1_0= '*' )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:423:1: (lv_multi_1_0= '*' )
                    // ../org.ietr.preesm.editor.IDLEditor/src-gen/org/ietr/preesm/editor/parser/antlr/internal/InternalIDLLanguage.g:424:3: lv_multi_1_0= '*'
                    {
                    lv_multi_1_0=(Token)input.LT(1);
                    match(input,17,FOLLOW_17_in_ruleTypeRef759); 

                            createLeafNode(grammarAccess.getTypeRefAccess().getMultiAsteriskKeyword_1_0(), "multi"); 
                        

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getTypeRefRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "multi", true, "*", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

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
    // $ANTLR end ruleTypeRef


 

    public static final BitSet FOLLOW_ruleIDL_in_entryRuleIDL75 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleIDL85 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleType_in_ruleIDL130 = new BitSet(new long[]{0x0000000000001802L});
    public static final BitSet FOLLOW_ruleType_in_entryRuleType166 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleType176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDataType_in_ruleType223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEntity_in_ruleType250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDataType_in_entryRuleDataType285 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDataType295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_11_in_ruleDataType330 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDataType347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEntity_in_entryRuleEntity388 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEntity398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_ruleEntity433 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleEntity450 = new BitSet(new long[]{0x0000000000006000L});
    public static final BitSet FOLLOW_13_in_ruleEntity466 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleEntity484 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleEntity496 = new BitSet(new long[]{0x0000000000008010L});
    public static final BitSet FOLLOW_ruleFeature_in_ruleEntity517 = new BitSet(new long[]{0x0000000000008010L});
    public static final BitSet FOLLOW_15_in_ruleEntity528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFeature_in_entryRuleFeature564 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFeature574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleFeature616 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleFeature631 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleTypeRef_in_ruleFeature652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTypeRef_in_entryRuleTypeRef688 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTypeRef698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleTypeRef741 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_17_in_ruleTypeRef759 = new BitSet(new long[]{0x0000000000000002L});

}