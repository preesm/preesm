package org.ietr.preesm.editor.ui.contentassist.antlr.internal; 

import java.io.InputStream;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.xtext.parsetree.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.DFA;
import org.ietr.preesm.editor.services.IDLLanguageGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalIDLLanguageParser extends AbstractInternalContentAssistParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_INT", "RULE_STRING", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'datatype'", "'entity'", "'{'", "'}'", "'extends'", "':'", "'*'"
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
    public String getGrammarFileName() { return "../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g"; }


     
     	private IDLLanguageGrammarAccess grammarAccess;
     	
        public void setGrammarAccess(IDLLanguageGrammarAccess grammarAccess) {
        	this.grammarAccess = grammarAccess;
        }
        
        @Override
        protected Grammar getGrammar() {
        	return grammarAccess.getGrammar();
        }
        
        @Override
        protected String getValueForTokenName(String tokenName) {
        	return tokenName;
        }




    // $ANTLR start entryRuleIDL
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:61:1: entryRuleIDL : ruleIDL EOF ;
    public final void entryRuleIDL() throws RecognitionException {
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:62:1: ( ruleIDL EOF )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:63:1: ruleIDL EOF
            {
             before(grammarAccess.getIDLRule()); 
            pushFollow(FOLLOW_ruleIDL_in_entryRuleIDL61);
            ruleIDL();
            _fsp--;

             after(grammarAccess.getIDLRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleIDL68); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleIDL


    // $ANTLR start ruleIDL
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:70:1: ruleIDL : ( ( rule__IDL__ElementsAssignment )* ) ;
    public final void ruleIDL() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:74:2: ( ( ( rule__IDL__ElementsAssignment )* ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:75:1: ( ( rule__IDL__ElementsAssignment )* )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:75:1: ( ( rule__IDL__ElementsAssignment )* )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:76:1: ( rule__IDL__ElementsAssignment )*
            {
             before(grammarAccess.getIDLAccess().getElementsAssignment()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:77:1: ( rule__IDL__ElementsAssignment )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>=11 && LA1_0<=12)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:77:2: rule__IDL__ElementsAssignment
            	    {
            	    pushFollow(FOLLOW_rule__IDL__ElementsAssignment_in_ruleIDL94);
            	    rule__IDL__ElementsAssignment();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

             after(grammarAccess.getIDLAccess().getElementsAssignment()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleIDL


    // $ANTLR start entryRuleType
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:89:1: entryRuleType : ruleType EOF ;
    public final void entryRuleType() throws RecognitionException {
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:90:1: ( ruleType EOF )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:91:1: ruleType EOF
            {
             before(grammarAccess.getTypeRule()); 
            pushFollow(FOLLOW_ruleType_in_entryRuleType122);
            ruleType();
            _fsp--;

             after(grammarAccess.getTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleType129); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleType


    // $ANTLR start ruleType
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:98:1: ruleType : ( ( rule__Type__Alternatives ) ) ;
    public final void ruleType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:102:2: ( ( ( rule__Type__Alternatives ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:103:1: ( ( rule__Type__Alternatives ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:103:1: ( ( rule__Type__Alternatives ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:104:1: ( rule__Type__Alternatives )
            {
             before(grammarAccess.getTypeAccess().getAlternatives()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:105:1: ( rule__Type__Alternatives )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:105:2: rule__Type__Alternatives
            {
            pushFollow(FOLLOW_rule__Type__Alternatives_in_ruleType155);
            rule__Type__Alternatives();
            _fsp--;


            }

             after(grammarAccess.getTypeAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleType


    // $ANTLR start entryRuleDataType
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:117:1: entryRuleDataType : ruleDataType EOF ;
    public final void entryRuleDataType() throws RecognitionException {
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:118:1: ( ruleDataType EOF )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:119:1: ruleDataType EOF
            {
             before(grammarAccess.getDataTypeRule()); 
            pushFollow(FOLLOW_ruleDataType_in_entryRuleDataType182);
            ruleDataType();
            _fsp--;

             after(grammarAccess.getDataTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDataType189); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleDataType


    // $ANTLR start ruleDataType
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:126:1: ruleDataType : ( ( rule__DataType__Group__0 ) ) ;
    public final void ruleDataType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:130:2: ( ( ( rule__DataType__Group__0 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:131:1: ( ( rule__DataType__Group__0 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:131:1: ( ( rule__DataType__Group__0 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:132:1: ( rule__DataType__Group__0 )
            {
             before(grammarAccess.getDataTypeAccess().getGroup()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:133:1: ( rule__DataType__Group__0 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:133:2: rule__DataType__Group__0
            {
            pushFollow(FOLLOW_rule__DataType__Group__0_in_ruleDataType215);
            rule__DataType__Group__0();
            _fsp--;


            }

             after(grammarAccess.getDataTypeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleDataType


    // $ANTLR start entryRuleEntity
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:145:1: entryRuleEntity : ruleEntity EOF ;
    public final void entryRuleEntity() throws RecognitionException {
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:146:1: ( ruleEntity EOF )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:147:1: ruleEntity EOF
            {
             before(grammarAccess.getEntityRule()); 
            pushFollow(FOLLOW_ruleEntity_in_entryRuleEntity242);
            ruleEntity();
            _fsp--;

             after(grammarAccess.getEntityRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleEntity249); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleEntity


    // $ANTLR start ruleEntity
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:154:1: ruleEntity : ( ( rule__Entity__Group__0 ) ) ;
    public final void ruleEntity() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:158:2: ( ( ( rule__Entity__Group__0 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:159:1: ( ( rule__Entity__Group__0 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:159:1: ( ( rule__Entity__Group__0 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:160:1: ( rule__Entity__Group__0 )
            {
             before(grammarAccess.getEntityAccess().getGroup()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:161:1: ( rule__Entity__Group__0 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:161:2: rule__Entity__Group__0
            {
            pushFollow(FOLLOW_rule__Entity__Group__0_in_ruleEntity275);
            rule__Entity__Group__0();
            _fsp--;


            }

             after(grammarAccess.getEntityAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleEntity


    // $ANTLR start entryRuleFeature
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:173:1: entryRuleFeature : ruleFeature EOF ;
    public final void entryRuleFeature() throws RecognitionException {
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:174:1: ( ruleFeature EOF )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:175:1: ruleFeature EOF
            {
             before(grammarAccess.getFeatureRule()); 
            pushFollow(FOLLOW_ruleFeature_in_entryRuleFeature302);
            ruleFeature();
            _fsp--;

             after(grammarAccess.getFeatureRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleFeature309); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleFeature


    // $ANTLR start ruleFeature
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:182:1: ruleFeature : ( ( rule__Feature__Group__0 ) ) ;
    public final void ruleFeature() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:186:2: ( ( ( rule__Feature__Group__0 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:187:1: ( ( rule__Feature__Group__0 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:187:1: ( ( rule__Feature__Group__0 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:188:1: ( rule__Feature__Group__0 )
            {
             before(grammarAccess.getFeatureAccess().getGroup()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:189:1: ( rule__Feature__Group__0 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:189:2: rule__Feature__Group__0
            {
            pushFollow(FOLLOW_rule__Feature__Group__0_in_ruleFeature335);
            rule__Feature__Group__0();
            _fsp--;


            }

             after(grammarAccess.getFeatureAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleFeature


    // $ANTLR start entryRuleTypeRef
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:201:1: entryRuleTypeRef : ruleTypeRef EOF ;
    public final void entryRuleTypeRef() throws RecognitionException {
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:202:1: ( ruleTypeRef EOF )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:203:1: ruleTypeRef EOF
            {
             before(grammarAccess.getTypeRefRule()); 
            pushFollow(FOLLOW_ruleTypeRef_in_entryRuleTypeRef362);
            ruleTypeRef();
            _fsp--;

             after(grammarAccess.getTypeRefRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTypeRef369); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleTypeRef


    // $ANTLR start ruleTypeRef
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:210:1: ruleTypeRef : ( ( rule__TypeRef__Group__0 ) ) ;
    public final void ruleTypeRef() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:214:2: ( ( ( rule__TypeRef__Group__0 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:215:1: ( ( rule__TypeRef__Group__0 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:215:1: ( ( rule__TypeRef__Group__0 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:216:1: ( rule__TypeRef__Group__0 )
            {
             before(grammarAccess.getTypeRefAccess().getGroup()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:217:1: ( rule__TypeRef__Group__0 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:217:2: rule__TypeRef__Group__0
            {
            pushFollow(FOLLOW_rule__TypeRef__Group__0_in_ruleTypeRef395);
            rule__TypeRef__Group__0();
            _fsp--;


            }

             after(grammarAccess.getTypeRefAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleTypeRef


    // $ANTLR start rule__Type__Alternatives
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:229:1: rule__Type__Alternatives : ( ( ruleDataType ) | ( ruleEntity ) );
    public final void rule__Type__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:233:1: ( ( ruleDataType ) | ( ruleEntity ) )
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
                    new NoViableAltException("229:1: rule__Type__Alternatives : ( ( ruleDataType ) | ( ruleEntity ) );", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:234:1: ( ruleDataType )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:234:1: ( ruleDataType )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:235:1: ruleDataType
                    {
                     before(grammarAccess.getTypeAccess().getDataTypeParserRuleCall_0()); 
                    pushFollow(FOLLOW_ruleDataType_in_rule__Type__Alternatives431);
                    ruleDataType();
                    _fsp--;

                     after(grammarAccess.getTypeAccess().getDataTypeParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:240:6: ( ruleEntity )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:240:6: ( ruleEntity )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:241:1: ruleEntity
                    {
                     before(grammarAccess.getTypeAccess().getEntityParserRuleCall_1()); 
                    pushFollow(FOLLOW_ruleEntity_in_rule__Type__Alternatives448);
                    ruleEntity();
                    _fsp--;

                     after(grammarAccess.getTypeAccess().getEntityParserRuleCall_1()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Type__Alternatives


    // $ANTLR start rule__DataType__Group__0
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:253:1: rule__DataType__Group__0 : rule__DataType__Group__0__Impl rule__DataType__Group__1 ;
    public final void rule__DataType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:257:1: ( rule__DataType__Group__0__Impl rule__DataType__Group__1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:258:2: rule__DataType__Group__0__Impl rule__DataType__Group__1
            {
            pushFollow(FOLLOW_rule__DataType__Group__0__Impl_in_rule__DataType__Group__0478);
            rule__DataType__Group__0__Impl();
            _fsp--;

            pushFollow(FOLLOW_rule__DataType__Group__1_in_rule__DataType__Group__0481);
            rule__DataType__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__DataType__Group__0


    // $ANTLR start rule__DataType__Group__0__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:265:1: rule__DataType__Group__0__Impl : ( 'datatype' ) ;
    public final void rule__DataType__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:269:1: ( ( 'datatype' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:270:1: ( 'datatype' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:270:1: ( 'datatype' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:271:1: 'datatype'
            {
             before(grammarAccess.getDataTypeAccess().getDatatypeKeyword_0()); 
            match(input,11,FOLLOW_11_in_rule__DataType__Group__0__Impl509); 
             after(grammarAccess.getDataTypeAccess().getDatatypeKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__DataType__Group__0__Impl


    // $ANTLR start rule__DataType__Group__1
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:284:1: rule__DataType__Group__1 : rule__DataType__Group__1__Impl ;
    public final void rule__DataType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:288:1: ( rule__DataType__Group__1__Impl )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:289:2: rule__DataType__Group__1__Impl
            {
            pushFollow(FOLLOW_rule__DataType__Group__1__Impl_in_rule__DataType__Group__1540);
            rule__DataType__Group__1__Impl();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__DataType__Group__1


    // $ANTLR start rule__DataType__Group__1__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:295:1: rule__DataType__Group__1__Impl : ( ( rule__DataType__NameAssignment_1 ) ) ;
    public final void rule__DataType__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:299:1: ( ( ( rule__DataType__NameAssignment_1 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:300:1: ( ( rule__DataType__NameAssignment_1 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:300:1: ( ( rule__DataType__NameAssignment_1 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:301:1: ( rule__DataType__NameAssignment_1 )
            {
             before(grammarAccess.getDataTypeAccess().getNameAssignment_1()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:302:1: ( rule__DataType__NameAssignment_1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:302:2: rule__DataType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__DataType__NameAssignment_1_in_rule__DataType__Group__1__Impl567);
            rule__DataType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getDataTypeAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__DataType__Group__1__Impl


    // $ANTLR start rule__Entity__Group__0
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:316:1: rule__Entity__Group__0 : rule__Entity__Group__0__Impl rule__Entity__Group__1 ;
    public final void rule__Entity__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:320:1: ( rule__Entity__Group__0__Impl rule__Entity__Group__1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:321:2: rule__Entity__Group__0__Impl rule__Entity__Group__1
            {
            pushFollow(FOLLOW_rule__Entity__Group__0__Impl_in_rule__Entity__Group__0601);
            rule__Entity__Group__0__Impl();
            _fsp--;

            pushFollow(FOLLOW_rule__Entity__Group__1_in_rule__Entity__Group__0604);
            rule__Entity__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group__0


    // $ANTLR start rule__Entity__Group__0__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:328:1: rule__Entity__Group__0__Impl : ( 'entity' ) ;
    public final void rule__Entity__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:332:1: ( ( 'entity' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:333:1: ( 'entity' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:333:1: ( 'entity' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:334:1: 'entity'
            {
             before(grammarAccess.getEntityAccess().getEntityKeyword_0()); 
            match(input,12,FOLLOW_12_in_rule__Entity__Group__0__Impl632); 
             after(grammarAccess.getEntityAccess().getEntityKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group__0__Impl


    // $ANTLR start rule__Entity__Group__1
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:347:1: rule__Entity__Group__1 : rule__Entity__Group__1__Impl rule__Entity__Group__2 ;
    public final void rule__Entity__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:351:1: ( rule__Entity__Group__1__Impl rule__Entity__Group__2 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:352:2: rule__Entity__Group__1__Impl rule__Entity__Group__2
            {
            pushFollow(FOLLOW_rule__Entity__Group__1__Impl_in_rule__Entity__Group__1663);
            rule__Entity__Group__1__Impl();
            _fsp--;

            pushFollow(FOLLOW_rule__Entity__Group__2_in_rule__Entity__Group__1666);
            rule__Entity__Group__2();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group__1


    // $ANTLR start rule__Entity__Group__1__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:359:1: rule__Entity__Group__1__Impl : ( ( rule__Entity__NameAssignment_1 ) ) ;
    public final void rule__Entity__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:363:1: ( ( ( rule__Entity__NameAssignment_1 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:364:1: ( ( rule__Entity__NameAssignment_1 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:364:1: ( ( rule__Entity__NameAssignment_1 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:365:1: ( rule__Entity__NameAssignment_1 )
            {
             before(grammarAccess.getEntityAccess().getNameAssignment_1()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:366:1: ( rule__Entity__NameAssignment_1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:366:2: rule__Entity__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__Entity__NameAssignment_1_in_rule__Entity__Group__1__Impl693);
            rule__Entity__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getEntityAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group__1__Impl


    // $ANTLR start rule__Entity__Group__2
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:376:1: rule__Entity__Group__2 : rule__Entity__Group__2__Impl rule__Entity__Group__3 ;
    public final void rule__Entity__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:380:1: ( rule__Entity__Group__2__Impl rule__Entity__Group__3 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:381:2: rule__Entity__Group__2__Impl rule__Entity__Group__3
            {
            pushFollow(FOLLOW_rule__Entity__Group__2__Impl_in_rule__Entity__Group__2723);
            rule__Entity__Group__2__Impl();
            _fsp--;

            pushFollow(FOLLOW_rule__Entity__Group__3_in_rule__Entity__Group__2726);
            rule__Entity__Group__3();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group__2


    // $ANTLR start rule__Entity__Group__2__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:388:1: rule__Entity__Group__2__Impl : ( ( rule__Entity__Group_2__0 )? ) ;
    public final void rule__Entity__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:392:1: ( ( ( rule__Entity__Group_2__0 )? ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:393:1: ( ( rule__Entity__Group_2__0 )? )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:393:1: ( ( rule__Entity__Group_2__0 )? )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:394:1: ( rule__Entity__Group_2__0 )?
            {
             before(grammarAccess.getEntityAccess().getGroup_2()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:395:1: ( rule__Entity__Group_2__0 )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==15) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:395:2: rule__Entity__Group_2__0
                    {
                    pushFollow(FOLLOW_rule__Entity__Group_2__0_in_rule__Entity__Group__2__Impl753);
                    rule__Entity__Group_2__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getEntityAccess().getGroup_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group__2__Impl


    // $ANTLR start rule__Entity__Group__3
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:405:1: rule__Entity__Group__3 : rule__Entity__Group__3__Impl rule__Entity__Group__4 ;
    public final void rule__Entity__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:409:1: ( rule__Entity__Group__3__Impl rule__Entity__Group__4 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:410:2: rule__Entity__Group__3__Impl rule__Entity__Group__4
            {
            pushFollow(FOLLOW_rule__Entity__Group__3__Impl_in_rule__Entity__Group__3784);
            rule__Entity__Group__3__Impl();
            _fsp--;

            pushFollow(FOLLOW_rule__Entity__Group__4_in_rule__Entity__Group__3787);
            rule__Entity__Group__4();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group__3


    // $ANTLR start rule__Entity__Group__3__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:417:1: rule__Entity__Group__3__Impl : ( '{' ) ;
    public final void rule__Entity__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:421:1: ( ( '{' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:422:1: ( '{' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:422:1: ( '{' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:423:1: '{'
            {
             before(grammarAccess.getEntityAccess().getLeftCurlyBracketKeyword_3()); 
            match(input,13,FOLLOW_13_in_rule__Entity__Group__3__Impl815); 
             after(grammarAccess.getEntityAccess().getLeftCurlyBracketKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group__3__Impl


    // $ANTLR start rule__Entity__Group__4
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:436:1: rule__Entity__Group__4 : rule__Entity__Group__4__Impl rule__Entity__Group__5 ;
    public final void rule__Entity__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:440:1: ( rule__Entity__Group__4__Impl rule__Entity__Group__5 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:441:2: rule__Entity__Group__4__Impl rule__Entity__Group__5
            {
            pushFollow(FOLLOW_rule__Entity__Group__4__Impl_in_rule__Entity__Group__4846);
            rule__Entity__Group__4__Impl();
            _fsp--;

            pushFollow(FOLLOW_rule__Entity__Group__5_in_rule__Entity__Group__4849);
            rule__Entity__Group__5();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group__4


    // $ANTLR start rule__Entity__Group__4__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:448:1: rule__Entity__Group__4__Impl : ( ( rule__Entity__FeaturesAssignment_4 )* ) ;
    public final void rule__Entity__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:452:1: ( ( ( rule__Entity__FeaturesAssignment_4 )* ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:453:1: ( ( rule__Entity__FeaturesAssignment_4 )* )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:453:1: ( ( rule__Entity__FeaturesAssignment_4 )* )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:454:1: ( rule__Entity__FeaturesAssignment_4 )*
            {
             before(grammarAccess.getEntityAccess().getFeaturesAssignment_4()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:455:1: ( rule__Entity__FeaturesAssignment_4 )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==RULE_ID) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:455:2: rule__Entity__FeaturesAssignment_4
            	    {
            	    pushFollow(FOLLOW_rule__Entity__FeaturesAssignment_4_in_rule__Entity__Group__4__Impl876);
            	    rule__Entity__FeaturesAssignment_4();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

             after(grammarAccess.getEntityAccess().getFeaturesAssignment_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group__4__Impl


    // $ANTLR start rule__Entity__Group__5
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:465:1: rule__Entity__Group__5 : rule__Entity__Group__5__Impl ;
    public final void rule__Entity__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:469:1: ( rule__Entity__Group__5__Impl )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:470:2: rule__Entity__Group__5__Impl
            {
            pushFollow(FOLLOW_rule__Entity__Group__5__Impl_in_rule__Entity__Group__5907);
            rule__Entity__Group__5__Impl();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group__5


    // $ANTLR start rule__Entity__Group__5__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:476:1: rule__Entity__Group__5__Impl : ( '}' ) ;
    public final void rule__Entity__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:480:1: ( ( '}' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:481:1: ( '}' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:481:1: ( '}' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:482:1: '}'
            {
             before(grammarAccess.getEntityAccess().getRightCurlyBracketKeyword_5()); 
            match(input,14,FOLLOW_14_in_rule__Entity__Group__5__Impl935); 
             after(grammarAccess.getEntityAccess().getRightCurlyBracketKeyword_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group__5__Impl


    // $ANTLR start rule__Entity__Group_2__0
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:507:1: rule__Entity__Group_2__0 : rule__Entity__Group_2__0__Impl rule__Entity__Group_2__1 ;
    public final void rule__Entity__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:511:1: ( rule__Entity__Group_2__0__Impl rule__Entity__Group_2__1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:512:2: rule__Entity__Group_2__0__Impl rule__Entity__Group_2__1
            {
            pushFollow(FOLLOW_rule__Entity__Group_2__0__Impl_in_rule__Entity__Group_2__0978);
            rule__Entity__Group_2__0__Impl();
            _fsp--;

            pushFollow(FOLLOW_rule__Entity__Group_2__1_in_rule__Entity__Group_2__0981);
            rule__Entity__Group_2__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group_2__0


    // $ANTLR start rule__Entity__Group_2__0__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:519:1: rule__Entity__Group_2__0__Impl : ( 'extends' ) ;
    public final void rule__Entity__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:523:1: ( ( 'extends' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:524:1: ( 'extends' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:524:1: ( 'extends' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:525:1: 'extends'
            {
             before(grammarAccess.getEntityAccess().getExtendsKeyword_2_0()); 
            match(input,15,FOLLOW_15_in_rule__Entity__Group_2__0__Impl1009); 
             after(grammarAccess.getEntityAccess().getExtendsKeyword_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group_2__0__Impl


    // $ANTLR start rule__Entity__Group_2__1
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:538:1: rule__Entity__Group_2__1 : rule__Entity__Group_2__1__Impl ;
    public final void rule__Entity__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:542:1: ( rule__Entity__Group_2__1__Impl )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:543:2: rule__Entity__Group_2__1__Impl
            {
            pushFollow(FOLLOW_rule__Entity__Group_2__1__Impl_in_rule__Entity__Group_2__11040);
            rule__Entity__Group_2__1__Impl();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group_2__1


    // $ANTLR start rule__Entity__Group_2__1__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:549:1: rule__Entity__Group_2__1__Impl : ( ( rule__Entity__SuperTypeAssignment_2_1 ) ) ;
    public final void rule__Entity__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:553:1: ( ( ( rule__Entity__SuperTypeAssignment_2_1 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:554:1: ( ( rule__Entity__SuperTypeAssignment_2_1 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:554:1: ( ( rule__Entity__SuperTypeAssignment_2_1 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:555:1: ( rule__Entity__SuperTypeAssignment_2_1 )
            {
             before(grammarAccess.getEntityAccess().getSuperTypeAssignment_2_1()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:556:1: ( rule__Entity__SuperTypeAssignment_2_1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:556:2: rule__Entity__SuperTypeAssignment_2_1
            {
            pushFollow(FOLLOW_rule__Entity__SuperTypeAssignment_2_1_in_rule__Entity__Group_2__1__Impl1067);
            rule__Entity__SuperTypeAssignment_2_1();
            _fsp--;


            }

             after(grammarAccess.getEntityAccess().getSuperTypeAssignment_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__Group_2__1__Impl


    // $ANTLR start rule__Feature__Group__0
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:570:1: rule__Feature__Group__0 : rule__Feature__Group__0__Impl rule__Feature__Group__1 ;
    public final void rule__Feature__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:574:1: ( rule__Feature__Group__0__Impl rule__Feature__Group__1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:575:2: rule__Feature__Group__0__Impl rule__Feature__Group__1
            {
            pushFollow(FOLLOW_rule__Feature__Group__0__Impl_in_rule__Feature__Group__01101);
            rule__Feature__Group__0__Impl();
            _fsp--;

            pushFollow(FOLLOW_rule__Feature__Group__1_in_rule__Feature__Group__01104);
            rule__Feature__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Feature__Group__0


    // $ANTLR start rule__Feature__Group__0__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:582:1: rule__Feature__Group__0__Impl : ( ( rule__Feature__NameAssignment_0 ) ) ;
    public final void rule__Feature__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:586:1: ( ( ( rule__Feature__NameAssignment_0 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:587:1: ( ( rule__Feature__NameAssignment_0 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:587:1: ( ( rule__Feature__NameAssignment_0 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:588:1: ( rule__Feature__NameAssignment_0 )
            {
             before(grammarAccess.getFeatureAccess().getNameAssignment_0()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:589:1: ( rule__Feature__NameAssignment_0 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:589:2: rule__Feature__NameAssignment_0
            {
            pushFollow(FOLLOW_rule__Feature__NameAssignment_0_in_rule__Feature__Group__0__Impl1131);
            rule__Feature__NameAssignment_0();
            _fsp--;


            }

             after(grammarAccess.getFeatureAccess().getNameAssignment_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Feature__Group__0__Impl


    // $ANTLR start rule__Feature__Group__1
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:599:1: rule__Feature__Group__1 : rule__Feature__Group__1__Impl rule__Feature__Group__2 ;
    public final void rule__Feature__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:603:1: ( rule__Feature__Group__1__Impl rule__Feature__Group__2 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:604:2: rule__Feature__Group__1__Impl rule__Feature__Group__2
            {
            pushFollow(FOLLOW_rule__Feature__Group__1__Impl_in_rule__Feature__Group__11161);
            rule__Feature__Group__1__Impl();
            _fsp--;

            pushFollow(FOLLOW_rule__Feature__Group__2_in_rule__Feature__Group__11164);
            rule__Feature__Group__2();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Feature__Group__1


    // $ANTLR start rule__Feature__Group__1__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:611:1: rule__Feature__Group__1__Impl : ( ':' ) ;
    public final void rule__Feature__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:615:1: ( ( ':' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:616:1: ( ':' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:616:1: ( ':' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:617:1: ':'
            {
             before(grammarAccess.getFeatureAccess().getColonKeyword_1()); 
            match(input,16,FOLLOW_16_in_rule__Feature__Group__1__Impl1192); 
             after(grammarAccess.getFeatureAccess().getColonKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Feature__Group__1__Impl


    // $ANTLR start rule__Feature__Group__2
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:630:1: rule__Feature__Group__2 : rule__Feature__Group__2__Impl ;
    public final void rule__Feature__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:634:1: ( rule__Feature__Group__2__Impl )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:635:2: rule__Feature__Group__2__Impl
            {
            pushFollow(FOLLOW_rule__Feature__Group__2__Impl_in_rule__Feature__Group__21223);
            rule__Feature__Group__2__Impl();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Feature__Group__2


    // $ANTLR start rule__Feature__Group__2__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:641:1: rule__Feature__Group__2__Impl : ( ( rule__Feature__TypeAssignment_2 ) ) ;
    public final void rule__Feature__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:645:1: ( ( ( rule__Feature__TypeAssignment_2 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:646:1: ( ( rule__Feature__TypeAssignment_2 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:646:1: ( ( rule__Feature__TypeAssignment_2 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:647:1: ( rule__Feature__TypeAssignment_2 )
            {
             before(grammarAccess.getFeatureAccess().getTypeAssignment_2()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:648:1: ( rule__Feature__TypeAssignment_2 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:648:2: rule__Feature__TypeAssignment_2
            {
            pushFollow(FOLLOW_rule__Feature__TypeAssignment_2_in_rule__Feature__Group__2__Impl1250);
            rule__Feature__TypeAssignment_2();
            _fsp--;


            }

             after(grammarAccess.getFeatureAccess().getTypeAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Feature__Group__2__Impl


    // $ANTLR start rule__TypeRef__Group__0
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:664:1: rule__TypeRef__Group__0 : rule__TypeRef__Group__0__Impl rule__TypeRef__Group__1 ;
    public final void rule__TypeRef__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:668:1: ( rule__TypeRef__Group__0__Impl rule__TypeRef__Group__1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:669:2: rule__TypeRef__Group__0__Impl rule__TypeRef__Group__1
            {
            pushFollow(FOLLOW_rule__TypeRef__Group__0__Impl_in_rule__TypeRef__Group__01286);
            rule__TypeRef__Group__0__Impl();
            _fsp--;

            pushFollow(FOLLOW_rule__TypeRef__Group__1_in_rule__TypeRef__Group__01289);
            rule__TypeRef__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__TypeRef__Group__0


    // $ANTLR start rule__TypeRef__Group__0__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:676:1: rule__TypeRef__Group__0__Impl : ( ( rule__TypeRef__ReferencedAssignment_0 ) ) ;
    public final void rule__TypeRef__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:680:1: ( ( ( rule__TypeRef__ReferencedAssignment_0 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:681:1: ( ( rule__TypeRef__ReferencedAssignment_0 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:681:1: ( ( rule__TypeRef__ReferencedAssignment_0 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:682:1: ( rule__TypeRef__ReferencedAssignment_0 )
            {
             before(grammarAccess.getTypeRefAccess().getReferencedAssignment_0()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:683:1: ( rule__TypeRef__ReferencedAssignment_0 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:683:2: rule__TypeRef__ReferencedAssignment_0
            {
            pushFollow(FOLLOW_rule__TypeRef__ReferencedAssignment_0_in_rule__TypeRef__Group__0__Impl1316);
            rule__TypeRef__ReferencedAssignment_0();
            _fsp--;


            }

             after(grammarAccess.getTypeRefAccess().getReferencedAssignment_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__TypeRef__Group__0__Impl


    // $ANTLR start rule__TypeRef__Group__1
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:693:1: rule__TypeRef__Group__1 : rule__TypeRef__Group__1__Impl ;
    public final void rule__TypeRef__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:697:1: ( rule__TypeRef__Group__1__Impl )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:698:2: rule__TypeRef__Group__1__Impl
            {
            pushFollow(FOLLOW_rule__TypeRef__Group__1__Impl_in_rule__TypeRef__Group__11346);
            rule__TypeRef__Group__1__Impl();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__TypeRef__Group__1


    // $ANTLR start rule__TypeRef__Group__1__Impl
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:704:1: rule__TypeRef__Group__1__Impl : ( ( rule__TypeRef__MultiAssignment_1 )? ) ;
    public final void rule__TypeRef__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:708:1: ( ( ( rule__TypeRef__MultiAssignment_1 )? ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:709:1: ( ( rule__TypeRef__MultiAssignment_1 )? )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:709:1: ( ( rule__TypeRef__MultiAssignment_1 )? )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:710:1: ( rule__TypeRef__MultiAssignment_1 )?
            {
             before(grammarAccess.getTypeRefAccess().getMultiAssignment_1()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:711:1: ( rule__TypeRef__MultiAssignment_1 )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==17) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:711:2: rule__TypeRef__MultiAssignment_1
                    {
                    pushFollow(FOLLOW_rule__TypeRef__MultiAssignment_1_in_rule__TypeRef__Group__1__Impl1373);
                    rule__TypeRef__MultiAssignment_1();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getTypeRefAccess().getMultiAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__TypeRef__Group__1__Impl


    // $ANTLR start rule__IDL__ElementsAssignment
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:726:1: rule__IDL__ElementsAssignment : ( ruleType ) ;
    public final void rule__IDL__ElementsAssignment() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:730:1: ( ( ruleType ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:731:1: ( ruleType )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:731:1: ( ruleType )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:732:1: ruleType
            {
             before(grammarAccess.getIDLAccess().getElementsTypeParserRuleCall_0()); 
            pushFollow(FOLLOW_ruleType_in_rule__IDL__ElementsAssignment1413);
            ruleType();
            _fsp--;

             after(grammarAccess.getIDLAccess().getElementsTypeParserRuleCall_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__IDL__ElementsAssignment


    // $ANTLR start rule__DataType__NameAssignment_1
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:741:1: rule__DataType__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__DataType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:745:1: ( ( RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:746:1: ( RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:746:1: ( RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:747:1: RULE_ID
            {
             before(grammarAccess.getDataTypeAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__DataType__NameAssignment_11444); 
             after(grammarAccess.getDataTypeAccess().getNameIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__DataType__NameAssignment_1


    // $ANTLR start rule__Entity__NameAssignment_1
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:756:1: rule__Entity__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__Entity__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:760:1: ( ( RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:761:1: ( RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:761:1: ( RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:762:1: RULE_ID
            {
             before(grammarAccess.getEntityAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__Entity__NameAssignment_11475); 
             after(grammarAccess.getEntityAccess().getNameIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__NameAssignment_1


    // $ANTLR start rule__Entity__SuperTypeAssignment_2_1
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:771:1: rule__Entity__SuperTypeAssignment_2_1 : ( ( RULE_ID ) ) ;
    public final void rule__Entity__SuperTypeAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:775:1: ( ( ( RULE_ID ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:776:1: ( ( RULE_ID ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:776:1: ( ( RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:777:1: ( RULE_ID )
            {
             before(grammarAccess.getEntityAccess().getSuperTypeEntityCrossReference_2_1_0()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:778:1: ( RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:779:1: RULE_ID
            {
             before(grammarAccess.getEntityAccess().getSuperTypeEntityIDTerminalRuleCall_2_1_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__Entity__SuperTypeAssignment_2_11510); 
             after(grammarAccess.getEntityAccess().getSuperTypeEntityIDTerminalRuleCall_2_1_0_1()); 

            }

             after(grammarAccess.getEntityAccess().getSuperTypeEntityCrossReference_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__SuperTypeAssignment_2_1


    // $ANTLR start rule__Entity__FeaturesAssignment_4
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:790:1: rule__Entity__FeaturesAssignment_4 : ( ruleFeature ) ;
    public final void rule__Entity__FeaturesAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:794:1: ( ( ruleFeature ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:795:1: ( ruleFeature )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:795:1: ( ruleFeature )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:796:1: ruleFeature
            {
             before(grammarAccess.getEntityAccess().getFeaturesFeatureParserRuleCall_4_0()); 
            pushFollow(FOLLOW_ruleFeature_in_rule__Entity__FeaturesAssignment_41545);
            ruleFeature();
            _fsp--;

             after(grammarAccess.getEntityAccess().getFeaturesFeatureParserRuleCall_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Entity__FeaturesAssignment_4


    // $ANTLR start rule__Feature__NameAssignment_0
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:805:1: rule__Feature__NameAssignment_0 : ( RULE_ID ) ;
    public final void rule__Feature__NameAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:809:1: ( ( RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:810:1: ( RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:810:1: ( RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:811:1: RULE_ID
            {
             before(grammarAccess.getFeatureAccess().getNameIDTerminalRuleCall_0_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__Feature__NameAssignment_01576); 
             after(grammarAccess.getFeatureAccess().getNameIDTerminalRuleCall_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Feature__NameAssignment_0


    // $ANTLR start rule__Feature__TypeAssignment_2
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:820:1: rule__Feature__TypeAssignment_2 : ( ruleTypeRef ) ;
    public final void rule__Feature__TypeAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:824:1: ( ( ruleTypeRef ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:825:1: ( ruleTypeRef )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:825:1: ( ruleTypeRef )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:826:1: ruleTypeRef
            {
             before(grammarAccess.getFeatureAccess().getTypeTypeRefParserRuleCall_2_0()); 
            pushFollow(FOLLOW_ruleTypeRef_in_rule__Feature__TypeAssignment_21607);
            ruleTypeRef();
            _fsp--;

             after(grammarAccess.getFeatureAccess().getTypeTypeRefParserRuleCall_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Feature__TypeAssignment_2


    // $ANTLR start rule__TypeRef__ReferencedAssignment_0
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:835:1: rule__TypeRef__ReferencedAssignment_0 : ( ( RULE_ID ) ) ;
    public final void rule__TypeRef__ReferencedAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:839:1: ( ( ( RULE_ID ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:840:1: ( ( RULE_ID ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:840:1: ( ( RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:841:1: ( RULE_ID )
            {
             before(grammarAccess.getTypeRefAccess().getReferencedTypeCrossReference_0_0()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:842:1: ( RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:843:1: RULE_ID
            {
             before(grammarAccess.getTypeRefAccess().getReferencedTypeIDTerminalRuleCall_0_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__TypeRef__ReferencedAssignment_01642); 
             after(grammarAccess.getTypeRefAccess().getReferencedTypeIDTerminalRuleCall_0_0_1()); 

            }

             after(grammarAccess.getTypeRefAccess().getReferencedTypeCrossReference_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__TypeRef__ReferencedAssignment_0


    // $ANTLR start rule__TypeRef__MultiAssignment_1
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:854:1: rule__TypeRef__MultiAssignment_1 : ( ( '*' ) ) ;
    public final void rule__TypeRef__MultiAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:858:1: ( ( ( '*' ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:859:1: ( ( '*' ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:859:1: ( ( '*' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:860:1: ( '*' )
            {
             before(grammarAccess.getTypeRefAccess().getMultiAsteriskKeyword_1_0()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:861:1: ( '*' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:862:1: '*'
            {
             before(grammarAccess.getTypeRefAccess().getMultiAsteriskKeyword_1_0()); 
            match(input,17,FOLLOW_17_in_rule__TypeRef__MultiAssignment_11682); 
             after(grammarAccess.getTypeRefAccess().getMultiAsteriskKeyword_1_0()); 

            }

             after(grammarAccess.getTypeRefAccess().getMultiAsteriskKeyword_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__TypeRef__MultiAssignment_1


 

    public static final BitSet FOLLOW_ruleIDL_in_entryRuleIDL61 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleIDL68 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__IDL__ElementsAssignment_in_ruleIDL94 = new BitSet(new long[]{0x0000000000001802L});
    public static final BitSet FOLLOW_ruleType_in_entryRuleType122 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleType129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Type__Alternatives_in_ruleType155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDataType_in_entryRuleDataType182 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDataType189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DataType__Group__0_in_ruleDataType215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEntity_in_entryRuleEntity242 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEntity249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Entity__Group__0_in_ruleEntity275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFeature_in_entryRuleFeature302 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFeature309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Feature__Group__0_in_ruleFeature335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTypeRef_in_entryRuleTypeRef362 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTypeRef369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__TypeRef__Group__0_in_ruleTypeRef395 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDataType_in_rule__Type__Alternatives431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEntity_in_rule__Type__Alternatives448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DataType__Group__0__Impl_in_rule__DataType__Group__0478 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__DataType__Group__1_in_rule__DataType__Group__0481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_11_in_rule__DataType__Group__0__Impl509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DataType__Group__1__Impl_in_rule__DataType__Group__1540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DataType__NameAssignment_1_in_rule__DataType__Group__1__Impl567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Entity__Group__0__Impl_in_rule__Entity__Group__0601 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__Entity__Group__1_in_rule__Entity__Group__0604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_rule__Entity__Group__0__Impl632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Entity__Group__1__Impl_in_rule__Entity__Group__1663 = new BitSet(new long[]{0x000000000000A000L});
    public static final BitSet FOLLOW_rule__Entity__Group__2_in_rule__Entity__Group__1666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Entity__NameAssignment_1_in_rule__Entity__Group__1__Impl693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Entity__Group__2__Impl_in_rule__Entity__Group__2723 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_rule__Entity__Group__3_in_rule__Entity__Group__2726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Entity__Group_2__0_in_rule__Entity__Group__2__Impl753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Entity__Group__3__Impl_in_rule__Entity__Group__3784 = new BitSet(new long[]{0x0000000000004010L});
    public static final BitSet FOLLOW_rule__Entity__Group__4_in_rule__Entity__Group__3787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_rule__Entity__Group__3__Impl815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Entity__Group__4__Impl_in_rule__Entity__Group__4846 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_rule__Entity__Group__5_in_rule__Entity__Group__4849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Entity__FeaturesAssignment_4_in_rule__Entity__Group__4__Impl876 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_rule__Entity__Group__5__Impl_in_rule__Entity__Group__5907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_rule__Entity__Group__5__Impl935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Entity__Group_2__0__Impl_in_rule__Entity__Group_2__0978 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__Entity__Group_2__1_in_rule__Entity__Group_2__0981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_rule__Entity__Group_2__0__Impl1009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Entity__Group_2__1__Impl_in_rule__Entity__Group_2__11040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Entity__SuperTypeAssignment_2_1_in_rule__Entity__Group_2__1__Impl1067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Feature__Group__0__Impl_in_rule__Feature__Group__01101 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_rule__Feature__Group__1_in_rule__Feature__Group__01104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Feature__NameAssignment_0_in_rule__Feature__Group__0__Impl1131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Feature__Group__1__Impl_in_rule__Feature__Group__11161 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__Feature__Group__2_in_rule__Feature__Group__11164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_rule__Feature__Group__1__Impl1192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Feature__Group__2__Impl_in_rule__Feature__Group__21223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Feature__TypeAssignment_2_in_rule__Feature__Group__2__Impl1250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__TypeRef__Group__0__Impl_in_rule__TypeRef__Group__01286 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_rule__TypeRef__Group__1_in_rule__TypeRef__Group__01289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__TypeRef__ReferencedAssignment_0_in_rule__TypeRef__Group__0__Impl1316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__TypeRef__Group__1__Impl_in_rule__TypeRef__Group__11346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__TypeRef__MultiAssignment_1_in_rule__TypeRef__Group__1__Impl1373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleType_in_rule__IDL__ElementsAssignment1413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__DataType__NameAssignment_11444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__Entity__NameAssignment_11475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__Entity__SuperTypeAssignment_2_11510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFeature_in_rule__Entity__FeaturesAssignment_41545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__Feature__NameAssignment_01576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTypeRef_in_rule__Feature__TypeAssignment_21607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__TypeRef__ReferencedAssignment_01642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_rule__TypeRef__MultiAssignment_11682 = new BitSet(new long[]{0x0000000000000002L});

}