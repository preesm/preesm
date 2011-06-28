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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_INT", "RULE_STRING", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'int'", "'long'", "'char'", "'init'", "'loop'", "'end'", "'in'", "'out'", "'module'", "'{'", "'}'", "';'", "'typedef'", "'interface'", "'void'", "'('", "')'", "','"
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




    // $ANTLR start "entryRuleIDL"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:61:1: entryRuleIDL : ruleIDL EOF ;
    public final void entryRuleIDL() throws RecognitionException {
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:62:1: ( ruleIDL EOF )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:63:1: ruleIDL EOF
            {
             before(grammarAccess.getIDLRule()); 
            pushFollow(FOLLOW_ruleIDL_in_entryRuleIDL61);
            ruleIDL();

            state._fsp--;

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
    // $ANTLR end "entryRuleIDL"


    // $ANTLR start "ruleIDL"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:70:1: ruleIDL : ( ( rule__IDL__ElementsAssignment ) ) ;
    public final void ruleIDL() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:74:2: ( ( ( rule__IDL__ElementsAssignment ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:75:1: ( ( rule__IDL__ElementsAssignment ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:75:1: ( ( rule__IDL__ElementsAssignment ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:76:1: ( rule__IDL__ElementsAssignment )
            {
             before(grammarAccess.getIDLAccess().getElementsAssignment()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:77:1: ( rule__IDL__ElementsAssignment )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:77:2: rule__IDL__ElementsAssignment
            {
            pushFollow(FOLLOW_rule__IDL__ElementsAssignment_in_ruleIDL94);
            rule__IDL__ElementsAssignment();

            state._fsp--;


            }

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
    // $ANTLR end "ruleIDL"


    // $ANTLR start "entryRuleModule"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:89:1: entryRuleModule : ruleModule EOF ;
    public final void entryRuleModule() throws RecognitionException {
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:90:1: ( ruleModule EOF )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:91:1: ruleModule EOF
            {
             before(grammarAccess.getModuleRule()); 
            pushFollow(FOLLOW_ruleModule_in_entryRuleModule121);
            ruleModule();

            state._fsp--;

             after(grammarAccess.getModuleRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleModule128); 

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
    // $ANTLR end "entryRuleModule"


    // $ANTLR start "ruleModule"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:98:1: ruleModule : ( ( rule__Module__Group__0 ) ) ;
    public final void ruleModule() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:102:2: ( ( ( rule__Module__Group__0 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:103:1: ( ( rule__Module__Group__0 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:103:1: ( ( rule__Module__Group__0 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:104:1: ( rule__Module__Group__0 )
            {
             before(grammarAccess.getModuleAccess().getGroup()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:105:1: ( rule__Module__Group__0 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:105:2: rule__Module__Group__0
            {
            pushFollow(FOLLOW_rule__Module__Group__0_in_ruleModule154);
            rule__Module__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getModuleAccess().getGroup()); 

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
    // $ANTLR end "ruleModule"


    // $ANTLR start "entryRuleDataType"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:117:1: entryRuleDataType : ruleDataType EOF ;
    public final void entryRuleDataType() throws RecognitionException {
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:118:1: ( ruleDataType EOF )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:119:1: ruleDataType EOF
            {
             before(grammarAccess.getDataTypeRule()); 
            pushFollow(FOLLOW_ruleDataType_in_entryRuleDataType181);
            ruleDataType();

            state._fsp--;

             after(grammarAccess.getDataTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDataType188); 

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
    // $ANTLR end "entryRuleDataType"


    // $ANTLR start "ruleDataType"
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
            pushFollow(FOLLOW_rule__DataType__Group__0_in_ruleDataType214);
            rule__DataType__Group__0();

            state._fsp--;


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
    // $ANTLR end "ruleDataType"


    // $ANTLR start "entryRuleInterface"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:145:1: entryRuleInterface : ruleInterface EOF ;
    public final void entryRuleInterface() throws RecognitionException {
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:146:1: ( ruleInterface EOF )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:147:1: ruleInterface EOF
            {
             before(grammarAccess.getInterfaceRule()); 
            pushFollow(FOLLOW_ruleInterface_in_entryRuleInterface241);
            ruleInterface();

            state._fsp--;

             after(grammarAccess.getInterfaceRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleInterface248); 

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
    // $ANTLR end "entryRuleInterface"


    // $ANTLR start "ruleInterface"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:154:1: ruleInterface : ( ( rule__Interface__Group__0 ) ) ;
    public final void ruleInterface() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:158:2: ( ( ( rule__Interface__Group__0 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:159:1: ( ( rule__Interface__Group__0 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:159:1: ( ( rule__Interface__Group__0 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:160:1: ( rule__Interface__Group__0 )
            {
             before(grammarAccess.getInterfaceAccess().getGroup()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:161:1: ( rule__Interface__Group__0 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:161:2: rule__Interface__Group__0
            {
            pushFollow(FOLLOW_rule__Interface__Group__0_in_ruleInterface274);
            rule__Interface__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getInterfaceAccess().getGroup()); 

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
    // $ANTLR end "ruleInterface"


    // $ANTLR start "entryRuleFunction"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:173:1: entryRuleFunction : ruleFunction EOF ;
    public final void entryRuleFunction() throws RecognitionException {
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:174:1: ( ruleFunction EOF )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:175:1: ruleFunction EOF
            {
             before(grammarAccess.getFunctionRule()); 
            pushFollow(FOLLOW_ruleFunction_in_entryRuleFunction301);
            ruleFunction();

            state._fsp--;

             after(grammarAccess.getFunctionRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleFunction308); 

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
    // $ANTLR end "entryRuleFunction"


    // $ANTLR start "ruleFunction"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:182:1: ruleFunction : ( ( rule__Function__Group__0 ) ) ;
    public final void ruleFunction() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:186:2: ( ( ( rule__Function__Group__0 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:187:1: ( ( rule__Function__Group__0 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:187:1: ( ( rule__Function__Group__0 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:188:1: ( rule__Function__Group__0 )
            {
             before(grammarAccess.getFunctionAccess().getGroup()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:189:1: ( rule__Function__Group__0 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:189:2: rule__Function__Group__0
            {
            pushFollow(FOLLOW_rule__Function__Group__0_in_ruleFunction334);
            rule__Function__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getFunctionAccess().getGroup()); 

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
    // $ANTLR end "ruleFunction"


    // $ANTLR start "entryRuleParameter"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:201:1: entryRuleParameter : ruleParameter EOF ;
    public final void entryRuleParameter() throws RecognitionException {
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:202:1: ( ruleParameter EOF )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:203:1: ruleParameter EOF
            {
             before(grammarAccess.getParameterRule()); 
            pushFollow(FOLLOW_ruleParameter_in_entryRuleParameter361);
            ruleParameter();

            state._fsp--;

             after(grammarAccess.getParameterRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleParameter368); 

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
    // $ANTLR end "entryRuleParameter"


    // $ANTLR start "ruleParameter"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:210:1: ruleParameter : ( ( rule__Parameter__Group__0 ) ) ;
    public final void ruleParameter() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:214:2: ( ( ( rule__Parameter__Group__0 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:215:1: ( ( rule__Parameter__Group__0 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:215:1: ( ( rule__Parameter__Group__0 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:216:1: ( rule__Parameter__Group__0 )
            {
             before(grammarAccess.getParameterAccess().getGroup()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:217:1: ( rule__Parameter__Group__0 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:217:2: rule__Parameter__Group__0
            {
            pushFollow(FOLLOW_rule__Parameter__Group__0_in_ruleParameter394);
            rule__Parameter__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getParameterAccess().getGroup()); 

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
    // $ANTLR end "ruleParameter"


    // $ANTLR start "entryRuleTypeStar"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:229:1: entryRuleTypeStar : ruleTypeStar EOF ;
    public final void entryRuleTypeStar() throws RecognitionException {
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:230:1: ( ruleTypeStar EOF )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:231:1: ruleTypeStar EOF
            {
             before(grammarAccess.getTypeStarRule()); 
            pushFollow(FOLLOW_ruleTypeStar_in_entryRuleTypeStar421);
            ruleTypeStar();

            state._fsp--;

             after(grammarAccess.getTypeStarRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleTypeStar428); 

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
    // $ANTLR end "entryRuleTypeStar"


    // $ANTLR start "ruleTypeStar"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:238:1: ruleTypeStar : ( ( rule__TypeStar__Alternatives ) ) ;
    public final void ruleTypeStar() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:242:2: ( ( ( rule__TypeStar__Alternatives ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:243:1: ( ( rule__TypeStar__Alternatives ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:243:1: ( ( rule__TypeStar__Alternatives ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:244:1: ( rule__TypeStar__Alternatives )
            {
             before(grammarAccess.getTypeStarAccess().getAlternatives()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:245:1: ( rule__TypeStar__Alternatives )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:245:2: rule__TypeStar__Alternatives
            {
            pushFollow(FOLLOW_rule__TypeStar__Alternatives_in_ruleTypeStar454);
            rule__TypeStar__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getTypeStarAccess().getAlternatives()); 

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
    // $ANTLR end "ruleTypeStar"


    // $ANTLR start "ruleBaseType"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:258:1: ruleBaseType : ( ( rule__BaseType__Alternatives ) ) ;
    public final void ruleBaseType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:262:1: ( ( ( rule__BaseType__Alternatives ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:263:1: ( ( rule__BaseType__Alternatives ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:263:1: ( ( rule__BaseType__Alternatives ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:264:1: ( rule__BaseType__Alternatives )
            {
             before(grammarAccess.getBaseTypeAccess().getAlternatives()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:265:1: ( rule__BaseType__Alternatives )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:265:2: rule__BaseType__Alternatives
            {
            pushFollow(FOLLOW_rule__BaseType__Alternatives_in_ruleBaseType491);
            rule__BaseType__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getBaseTypeAccess().getAlternatives()); 

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
    // $ANTLR end "ruleBaseType"


    // $ANTLR start "ruleInterfaceName"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:277:1: ruleInterfaceName : ( ( rule__InterfaceName__Alternatives ) ) ;
    public final void ruleInterfaceName() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:281:1: ( ( ( rule__InterfaceName__Alternatives ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:282:1: ( ( rule__InterfaceName__Alternatives ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:282:1: ( ( rule__InterfaceName__Alternatives ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:283:1: ( rule__InterfaceName__Alternatives )
            {
             before(grammarAccess.getInterfaceNameAccess().getAlternatives()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:284:1: ( rule__InterfaceName__Alternatives )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:284:2: rule__InterfaceName__Alternatives
            {
            pushFollow(FOLLOW_rule__InterfaceName__Alternatives_in_ruleInterfaceName527);
            rule__InterfaceName__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getInterfaceNameAccess().getAlternatives()); 

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
    // $ANTLR end "ruleInterfaceName"


    // $ANTLR start "ruleDirection"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:296:1: ruleDirection : ( ( rule__Direction__Alternatives ) ) ;
    public final void ruleDirection() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:300:1: ( ( ( rule__Direction__Alternatives ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:301:1: ( ( rule__Direction__Alternatives ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:301:1: ( ( rule__Direction__Alternatives ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:302:1: ( rule__Direction__Alternatives )
            {
             before(grammarAccess.getDirectionAccess().getAlternatives()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:303:1: ( rule__Direction__Alternatives )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:303:2: rule__Direction__Alternatives
            {
            pushFollow(FOLLOW_rule__Direction__Alternatives_in_ruleDirection563);
            rule__Direction__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getDirectionAccess().getAlternatives()); 

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
    // $ANTLR end "ruleDirection"


    // $ANTLR start "rule__DataType__Alternatives_1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:314:1: rule__DataType__Alternatives_1 : ( ( ( rule__DataType__BtypeAssignment_1_0 ) ) | ( ( rule__DataType__CtypeAssignment_1_1 ) ) );
    public final void rule__DataType__Alternatives_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:318:1: ( ( ( rule__DataType__BtypeAssignment_1_0 ) ) | ( ( rule__DataType__CtypeAssignment_1_1 ) ) )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( ((LA1_0>=11 && LA1_0<=13)) ) {
                alt1=1;
            }
            else if ( (LA1_0==RULE_ID) ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:319:1: ( ( rule__DataType__BtypeAssignment_1_0 ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:319:1: ( ( rule__DataType__BtypeAssignment_1_0 ) )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:320:1: ( rule__DataType__BtypeAssignment_1_0 )
                    {
                     before(grammarAccess.getDataTypeAccess().getBtypeAssignment_1_0()); 
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:321:1: ( rule__DataType__BtypeAssignment_1_0 )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:321:2: rule__DataType__BtypeAssignment_1_0
                    {
                    pushFollow(FOLLOW_rule__DataType__BtypeAssignment_1_0_in_rule__DataType__Alternatives_1598);
                    rule__DataType__BtypeAssignment_1_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getDataTypeAccess().getBtypeAssignment_1_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:325:6: ( ( rule__DataType__CtypeAssignment_1_1 ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:325:6: ( ( rule__DataType__CtypeAssignment_1_1 ) )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:326:1: ( rule__DataType__CtypeAssignment_1_1 )
                    {
                     before(grammarAccess.getDataTypeAccess().getCtypeAssignment_1_1()); 
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:327:1: ( rule__DataType__CtypeAssignment_1_1 )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:327:2: rule__DataType__CtypeAssignment_1_1
                    {
                    pushFollow(FOLLOW_rule__DataType__CtypeAssignment_1_1_in_rule__DataType__Alternatives_1616);
                    rule__DataType__CtypeAssignment_1_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getDataTypeAccess().getCtypeAssignment_1_1()); 

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
    // $ANTLR end "rule__DataType__Alternatives_1"


    // $ANTLR start "rule__TypeStar__Alternatives"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:336:1: rule__TypeStar__Alternatives : ( ( ( rule__TypeStar__BtypeAssignment_0 ) ) | ( ( rule__TypeStar__CtypeAssignment_1 ) ) );
    public final void rule__TypeStar__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:340:1: ( ( ( rule__TypeStar__BtypeAssignment_0 ) ) | ( ( rule__TypeStar__CtypeAssignment_1 ) ) )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( ((LA2_0>=11 && LA2_0<=13)) ) {
                alt2=1;
            }
            else if ( (LA2_0==RULE_ID) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:341:1: ( ( rule__TypeStar__BtypeAssignment_0 ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:341:1: ( ( rule__TypeStar__BtypeAssignment_0 ) )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:342:1: ( rule__TypeStar__BtypeAssignment_0 )
                    {
                     before(grammarAccess.getTypeStarAccess().getBtypeAssignment_0()); 
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:343:1: ( rule__TypeStar__BtypeAssignment_0 )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:343:2: rule__TypeStar__BtypeAssignment_0
                    {
                    pushFollow(FOLLOW_rule__TypeStar__BtypeAssignment_0_in_rule__TypeStar__Alternatives649);
                    rule__TypeStar__BtypeAssignment_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getTypeStarAccess().getBtypeAssignment_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:347:6: ( ( rule__TypeStar__CtypeAssignment_1 ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:347:6: ( ( rule__TypeStar__CtypeAssignment_1 ) )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:348:1: ( rule__TypeStar__CtypeAssignment_1 )
                    {
                     before(grammarAccess.getTypeStarAccess().getCtypeAssignment_1()); 
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:349:1: ( rule__TypeStar__CtypeAssignment_1 )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:349:2: rule__TypeStar__CtypeAssignment_1
                    {
                    pushFollow(FOLLOW_rule__TypeStar__CtypeAssignment_1_in_rule__TypeStar__Alternatives667);
                    rule__TypeStar__CtypeAssignment_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getTypeStarAccess().getCtypeAssignment_1()); 

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
    // $ANTLR end "rule__TypeStar__Alternatives"


    // $ANTLR start "rule__BaseType__Alternatives"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:358:1: rule__BaseType__Alternatives : ( ( ( 'int' ) ) | ( ( 'long' ) ) | ( ( 'char' ) ) );
    public final void rule__BaseType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:362:1: ( ( ( 'int' ) ) | ( ( 'long' ) ) | ( ( 'char' ) ) )
            int alt3=3;
            switch ( input.LA(1) ) {
            case 11:
                {
                alt3=1;
                }
                break;
            case 12:
                {
                alt3=2;
                }
                break;
            case 13:
                {
                alt3=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:363:1: ( ( 'int' ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:363:1: ( ( 'int' ) )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:364:1: ( 'int' )
                    {
                     before(grammarAccess.getBaseTypeAccess().getIntEnumLiteralDeclaration_0()); 
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:365:1: ( 'int' )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:365:3: 'int'
                    {
                    match(input,11,FOLLOW_11_in_rule__BaseType__Alternatives701); 

                    }

                     after(grammarAccess.getBaseTypeAccess().getIntEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:370:6: ( ( 'long' ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:370:6: ( ( 'long' ) )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:371:1: ( 'long' )
                    {
                     before(grammarAccess.getBaseTypeAccess().getLongEnumLiteralDeclaration_1()); 
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:372:1: ( 'long' )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:372:3: 'long'
                    {
                    match(input,12,FOLLOW_12_in_rule__BaseType__Alternatives722); 

                    }

                     after(grammarAccess.getBaseTypeAccess().getLongEnumLiteralDeclaration_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:377:6: ( ( 'char' ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:377:6: ( ( 'char' ) )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:378:1: ( 'char' )
                    {
                     before(grammarAccess.getBaseTypeAccess().getCharEnumLiteralDeclaration_2()); 
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:379:1: ( 'char' )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:379:3: 'char'
                    {
                    match(input,13,FOLLOW_13_in_rule__BaseType__Alternatives743); 

                    }

                     after(grammarAccess.getBaseTypeAccess().getCharEnumLiteralDeclaration_2()); 

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
    // $ANTLR end "rule__BaseType__Alternatives"


    // $ANTLR start "rule__InterfaceName__Alternatives"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:389:1: rule__InterfaceName__Alternatives : ( ( ( 'init' ) ) | ( ( 'loop' ) ) | ( ( 'end' ) ) );
    public final void rule__InterfaceName__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:393:1: ( ( ( 'init' ) ) | ( ( 'loop' ) ) | ( ( 'end' ) ) )
            int alt4=3;
            switch ( input.LA(1) ) {
            case 14:
                {
                alt4=1;
                }
                break;
            case 15:
                {
                alt4=2;
                }
                break;
            case 16:
                {
                alt4=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:394:1: ( ( 'init' ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:394:1: ( ( 'init' ) )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:395:1: ( 'init' )
                    {
                     before(grammarAccess.getInterfaceNameAccess().getInitEnumLiteralDeclaration_0()); 
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:396:1: ( 'init' )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:396:3: 'init'
                    {
                    match(input,14,FOLLOW_14_in_rule__InterfaceName__Alternatives779); 

                    }

                     after(grammarAccess.getInterfaceNameAccess().getInitEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:401:6: ( ( 'loop' ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:401:6: ( ( 'loop' ) )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:402:1: ( 'loop' )
                    {
                     before(grammarAccess.getInterfaceNameAccess().getLoopEnumLiteralDeclaration_1()); 
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:403:1: ( 'loop' )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:403:3: 'loop'
                    {
                    match(input,15,FOLLOW_15_in_rule__InterfaceName__Alternatives800); 

                    }

                     after(grammarAccess.getInterfaceNameAccess().getLoopEnumLiteralDeclaration_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:408:6: ( ( 'end' ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:408:6: ( ( 'end' ) )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:409:1: ( 'end' )
                    {
                     before(grammarAccess.getInterfaceNameAccess().getEndEnumLiteralDeclaration_2()); 
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:410:1: ( 'end' )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:410:3: 'end'
                    {
                    match(input,16,FOLLOW_16_in_rule__InterfaceName__Alternatives821); 

                    }

                     after(grammarAccess.getInterfaceNameAccess().getEndEnumLiteralDeclaration_2()); 

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
    // $ANTLR end "rule__InterfaceName__Alternatives"


    // $ANTLR start "rule__Direction__Alternatives"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:420:1: rule__Direction__Alternatives : ( ( ( 'in' ) ) | ( ( 'out' ) ) );
    public final void rule__Direction__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:424:1: ( ( ( 'in' ) ) | ( ( 'out' ) ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==17) ) {
                alt5=1;
            }
            else if ( (LA5_0==18) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:425:1: ( ( 'in' ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:425:1: ( ( 'in' ) )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:426:1: ( 'in' )
                    {
                     before(grammarAccess.getDirectionAccess().getInEnumLiteralDeclaration_0()); 
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:427:1: ( 'in' )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:427:3: 'in'
                    {
                    match(input,17,FOLLOW_17_in_rule__Direction__Alternatives857); 

                    }

                     after(grammarAccess.getDirectionAccess().getInEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:432:6: ( ( 'out' ) )
                    {
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:432:6: ( ( 'out' ) )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:433:1: ( 'out' )
                    {
                     before(grammarAccess.getDirectionAccess().getOutEnumLiteralDeclaration_1()); 
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:434:1: ( 'out' )
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:434:3: 'out'
                    {
                    match(input,18,FOLLOW_18_in_rule__Direction__Alternatives878); 

                    }

                     after(grammarAccess.getDirectionAccess().getOutEnumLiteralDeclaration_1()); 

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
    // $ANTLR end "rule__Direction__Alternatives"


    // $ANTLR start "rule__Module__Group__0"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:446:1: rule__Module__Group__0 : rule__Module__Group__0__Impl rule__Module__Group__1 ;
    public final void rule__Module__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:450:1: ( rule__Module__Group__0__Impl rule__Module__Group__1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:451:2: rule__Module__Group__0__Impl rule__Module__Group__1
            {
            pushFollow(FOLLOW_rule__Module__Group__0__Impl_in_rule__Module__Group__0911);
            rule__Module__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Module__Group__1_in_rule__Module__Group__0914);
            rule__Module__Group__1();

            state._fsp--;


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
    // $ANTLR end "rule__Module__Group__0"


    // $ANTLR start "rule__Module__Group__0__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:458:1: rule__Module__Group__0__Impl : ( 'module' ) ;
    public final void rule__Module__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:462:1: ( ( 'module' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:463:1: ( 'module' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:463:1: ( 'module' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:464:1: 'module'
            {
             before(grammarAccess.getModuleAccess().getModuleKeyword_0()); 
            match(input,19,FOLLOW_19_in_rule__Module__Group__0__Impl942); 
             after(grammarAccess.getModuleAccess().getModuleKeyword_0()); 

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
    // $ANTLR end "rule__Module__Group__0__Impl"


    // $ANTLR start "rule__Module__Group__1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:477:1: rule__Module__Group__1 : rule__Module__Group__1__Impl rule__Module__Group__2 ;
    public final void rule__Module__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:481:1: ( rule__Module__Group__1__Impl rule__Module__Group__2 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:482:2: rule__Module__Group__1__Impl rule__Module__Group__2
            {
            pushFollow(FOLLOW_rule__Module__Group__1__Impl_in_rule__Module__Group__1973);
            rule__Module__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Module__Group__2_in_rule__Module__Group__1976);
            rule__Module__Group__2();

            state._fsp--;


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
    // $ANTLR end "rule__Module__Group__1"


    // $ANTLR start "rule__Module__Group__1__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:489:1: rule__Module__Group__1__Impl : ( ( rule__Module__NameAssignment_1 ) ) ;
    public final void rule__Module__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:493:1: ( ( ( rule__Module__NameAssignment_1 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:494:1: ( ( rule__Module__NameAssignment_1 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:494:1: ( ( rule__Module__NameAssignment_1 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:495:1: ( rule__Module__NameAssignment_1 )
            {
             before(grammarAccess.getModuleAccess().getNameAssignment_1()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:496:1: ( rule__Module__NameAssignment_1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:496:2: rule__Module__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__Module__NameAssignment_1_in_rule__Module__Group__1__Impl1003);
            rule__Module__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getModuleAccess().getNameAssignment_1()); 

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
    // $ANTLR end "rule__Module__Group__1__Impl"


    // $ANTLR start "rule__Module__Group__2"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:506:1: rule__Module__Group__2 : rule__Module__Group__2__Impl rule__Module__Group__3 ;
    public final void rule__Module__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:510:1: ( rule__Module__Group__2__Impl rule__Module__Group__3 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:511:2: rule__Module__Group__2__Impl rule__Module__Group__3
            {
            pushFollow(FOLLOW_rule__Module__Group__2__Impl_in_rule__Module__Group__21033);
            rule__Module__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Module__Group__3_in_rule__Module__Group__21036);
            rule__Module__Group__3();

            state._fsp--;


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
    // $ANTLR end "rule__Module__Group__2"


    // $ANTLR start "rule__Module__Group__2__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:518:1: rule__Module__Group__2__Impl : ( '{' ) ;
    public final void rule__Module__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:522:1: ( ( '{' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:523:1: ( '{' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:523:1: ( '{' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:524:1: '{'
            {
             before(grammarAccess.getModuleAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,20,FOLLOW_20_in_rule__Module__Group__2__Impl1064); 
             after(grammarAccess.getModuleAccess().getLeftCurlyBracketKeyword_2()); 

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
    // $ANTLR end "rule__Module__Group__2__Impl"


    // $ANTLR start "rule__Module__Group__3"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:537:1: rule__Module__Group__3 : rule__Module__Group__3__Impl rule__Module__Group__4 ;
    public final void rule__Module__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:541:1: ( rule__Module__Group__3__Impl rule__Module__Group__4 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:542:2: rule__Module__Group__3__Impl rule__Module__Group__4
            {
            pushFollow(FOLLOW_rule__Module__Group__3__Impl_in_rule__Module__Group__31095);
            rule__Module__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Module__Group__4_in_rule__Module__Group__31098);
            rule__Module__Group__4();

            state._fsp--;


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
    // $ANTLR end "rule__Module__Group__3"


    // $ANTLR start "rule__Module__Group__3__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:549:1: rule__Module__Group__3__Impl : ( ( rule__Module__TypesAssignment_3 )* ) ;
    public final void rule__Module__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:553:1: ( ( ( rule__Module__TypesAssignment_3 )* ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:554:1: ( ( rule__Module__TypesAssignment_3 )* )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:554:1: ( ( rule__Module__TypesAssignment_3 )* )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:555:1: ( rule__Module__TypesAssignment_3 )*
            {
             before(grammarAccess.getModuleAccess().getTypesAssignment_3()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:556:1: ( rule__Module__TypesAssignment_3 )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==23) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:556:2: rule__Module__TypesAssignment_3
            	    {
            	    pushFollow(FOLLOW_rule__Module__TypesAssignment_3_in_rule__Module__Group__3__Impl1125);
            	    rule__Module__TypesAssignment_3();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

             after(grammarAccess.getModuleAccess().getTypesAssignment_3()); 

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
    // $ANTLR end "rule__Module__Group__3__Impl"


    // $ANTLR start "rule__Module__Group__4"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:566:1: rule__Module__Group__4 : rule__Module__Group__4__Impl rule__Module__Group__5 ;
    public final void rule__Module__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:570:1: ( rule__Module__Group__4__Impl rule__Module__Group__5 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:571:2: rule__Module__Group__4__Impl rule__Module__Group__5
            {
            pushFollow(FOLLOW_rule__Module__Group__4__Impl_in_rule__Module__Group__41156);
            rule__Module__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Module__Group__5_in_rule__Module__Group__41159);
            rule__Module__Group__5();

            state._fsp--;


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
    // $ANTLR end "rule__Module__Group__4"


    // $ANTLR start "rule__Module__Group__4__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:578:1: rule__Module__Group__4__Impl : ( ( rule__Module__InterfacesAssignment_4 )* ) ;
    public final void rule__Module__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:582:1: ( ( ( rule__Module__InterfacesAssignment_4 )* ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:583:1: ( ( rule__Module__InterfacesAssignment_4 )* )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:583:1: ( ( rule__Module__InterfacesAssignment_4 )* )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:584:1: ( rule__Module__InterfacesAssignment_4 )*
            {
             before(grammarAccess.getModuleAccess().getInterfacesAssignment_4()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:585:1: ( rule__Module__InterfacesAssignment_4 )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==24) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:585:2: rule__Module__InterfacesAssignment_4
            	    {
            	    pushFollow(FOLLOW_rule__Module__InterfacesAssignment_4_in_rule__Module__Group__4__Impl1186);
            	    rule__Module__InterfacesAssignment_4();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

             after(grammarAccess.getModuleAccess().getInterfacesAssignment_4()); 

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
    // $ANTLR end "rule__Module__Group__4__Impl"


    // $ANTLR start "rule__Module__Group__5"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:595:1: rule__Module__Group__5 : rule__Module__Group__5__Impl rule__Module__Group__6 ;
    public final void rule__Module__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:599:1: ( rule__Module__Group__5__Impl rule__Module__Group__6 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:600:2: rule__Module__Group__5__Impl rule__Module__Group__6
            {
            pushFollow(FOLLOW_rule__Module__Group__5__Impl_in_rule__Module__Group__51217);
            rule__Module__Group__5__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Module__Group__6_in_rule__Module__Group__51220);
            rule__Module__Group__6();

            state._fsp--;


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
    // $ANTLR end "rule__Module__Group__5"


    // $ANTLR start "rule__Module__Group__5__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:607:1: rule__Module__Group__5__Impl : ( '}' ) ;
    public final void rule__Module__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:611:1: ( ( '}' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:612:1: ( '}' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:612:1: ( '}' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:613:1: '}'
            {
             before(grammarAccess.getModuleAccess().getRightCurlyBracketKeyword_5()); 
            match(input,21,FOLLOW_21_in_rule__Module__Group__5__Impl1248); 
             after(grammarAccess.getModuleAccess().getRightCurlyBracketKeyword_5()); 

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
    // $ANTLR end "rule__Module__Group__5__Impl"


    // $ANTLR start "rule__Module__Group__6"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:626:1: rule__Module__Group__6 : rule__Module__Group__6__Impl ;
    public final void rule__Module__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:630:1: ( rule__Module__Group__6__Impl )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:631:2: rule__Module__Group__6__Impl
            {
            pushFollow(FOLLOW_rule__Module__Group__6__Impl_in_rule__Module__Group__61279);
            rule__Module__Group__6__Impl();

            state._fsp--;


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
    // $ANTLR end "rule__Module__Group__6"


    // $ANTLR start "rule__Module__Group__6__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:637:1: rule__Module__Group__6__Impl : ( ';' ) ;
    public final void rule__Module__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:641:1: ( ( ';' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:642:1: ( ';' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:642:1: ( ';' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:643:1: ';'
            {
             before(grammarAccess.getModuleAccess().getSemicolonKeyword_6()); 
            match(input,22,FOLLOW_22_in_rule__Module__Group__6__Impl1307); 
             after(grammarAccess.getModuleAccess().getSemicolonKeyword_6()); 

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
    // $ANTLR end "rule__Module__Group__6__Impl"


    // $ANTLR start "rule__DataType__Group__0"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:670:1: rule__DataType__Group__0 : rule__DataType__Group__0__Impl rule__DataType__Group__1 ;
    public final void rule__DataType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:674:1: ( rule__DataType__Group__0__Impl rule__DataType__Group__1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:675:2: rule__DataType__Group__0__Impl rule__DataType__Group__1
            {
            pushFollow(FOLLOW_rule__DataType__Group__0__Impl_in_rule__DataType__Group__01352);
            rule__DataType__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__DataType__Group__1_in_rule__DataType__Group__01355);
            rule__DataType__Group__1();

            state._fsp--;


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
    // $ANTLR end "rule__DataType__Group__0"


    // $ANTLR start "rule__DataType__Group__0__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:682:1: rule__DataType__Group__0__Impl : ( 'typedef' ) ;
    public final void rule__DataType__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:686:1: ( ( 'typedef' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:687:1: ( 'typedef' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:687:1: ( 'typedef' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:688:1: 'typedef'
            {
             before(grammarAccess.getDataTypeAccess().getTypedefKeyword_0()); 
            match(input,23,FOLLOW_23_in_rule__DataType__Group__0__Impl1383); 
             after(grammarAccess.getDataTypeAccess().getTypedefKeyword_0()); 

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
    // $ANTLR end "rule__DataType__Group__0__Impl"


    // $ANTLR start "rule__DataType__Group__1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:701:1: rule__DataType__Group__1 : rule__DataType__Group__1__Impl rule__DataType__Group__2 ;
    public final void rule__DataType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:705:1: ( rule__DataType__Group__1__Impl rule__DataType__Group__2 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:706:2: rule__DataType__Group__1__Impl rule__DataType__Group__2
            {
            pushFollow(FOLLOW_rule__DataType__Group__1__Impl_in_rule__DataType__Group__11414);
            rule__DataType__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__DataType__Group__2_in_rule__DataType__Group__11417);
            rule__DataType__Group__2();

            state._fsp--;


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
    // $ANTLR end "rule__DataType__Group__1"


    // $ANTLR start "rule__DataType__Group__1__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:713:1: rule__DataType__Group__1__Impl : ( ( rule__DataType__Alternatives_1 ) ) ;
    public final void rule__DataType__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:717:1: ( ( ( rule__DataType__Alternatives_1 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:718:1: ( ( rule__DataType__Alternatives_1 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:718:1: ( ( rule__DataType__Alternatives_1 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:719:1: ( rule__DataType__Alternatives_1 )
            {
             before(grammarAccess.getDataTypeAccess().getAlternatives_1()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:720:1: ( rule__DataType__Alternatives_1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:720:2: rule__DataType__Alternatives_1
            {
            pushFollow(FOLLOW_rule__DataType__Alternatives_1_in_rule__DataType__Group__1__Impl1444);
            rule__DataType__Alternatives_1();

            state._fsp--;


            }

             after(grammarAccess.getDataTypeAccess().getAlternatives_1()); 

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
    // $ANTLR end "rule__DataType__Group__1__Impl"


    // $ANTLR start "rule__DataType__Group__2"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:730:1: rule__DataType__Group__2 : rule__DataType__Group__2__Impl rule__DataType__Group__3 ;
    public final void rule__DataType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:734:1: ( rule__DataType__Group__2__Impl rule__DataType__Group__3 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:735:2: rule__DataType__Group__2__Impl rule__DataType__Group__3
            {
            pushFollow(FOLLOW_rule__DataType__Group__2__Impl_in_rule__DataType__Group__21474);
            rule__DataType__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__DataType__Group__3_in_rule__DataType__Group__21477);
            rule__DataType__Group__3();

            state._fsp--;


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
    // $ANTLR end "rule__DataType__Group__2"


    // $ANTLR start "rule__DataType__Group__2__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:742:1: rule__DataType__Group__2__Impl : ( ( rule__DataType__NameAssignment_2 ) ) ;
    public final void rule__DataType__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:746:1: ( ( ( rule__DataType__NameAssignment_2 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:747:1: ( ( rule__DataType__NameAssignment_2 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:747:1: ( ( rule__DataType__NameAssignment_2 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:748:1: ( rule__DataType__NameAssignment_2 )
            {
             before(grammarAccess.getDataTypeAccess().getNameAssignment_2()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:749:1: ( rule__DataType__NameAssignment_2 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:749:2: rule__DataType__NameAssignment_2
            {
            pushFollow(FOLLOW_rule__DataType__NameAssignment_2_in_rule__DataType__Group__2__Impl1504);
            rule__DataType__NameAssignment_2();

            state._fsp--;


            }

             after(grammarAccess.getDataTypeAccess().getNameAssignment_2()); 

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
    // $ANTLR end "rule__DataType__Group__2__Impl"


    // $ANTLR start "rule__DataType__Group__3"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:759:1: rule__DataType__Group__3 : rule__DataType__Group__3__Impl ;
    public final void rule__DataType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:763:1: ( rule__DataType__Group__3__Impl )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:764:2: rule__DataType__Group__3__Impl
            {
            pushFollow(FOLLOW_rule__DataType__Group__3__Impl_in_rule__DataType__Group__31534);
            rule__DataType__Group__3__Impl();

            state._fsp--;


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
    // $ANTLR end "rule__DataType__Group__3"


    // $ANTLR start "rule__DataType__Group__3__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:770:1: rule__DataType__Group__3__Impl : ( ';' ) ;
    public final void rule__DataType__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:774:1: ( ( ';' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:775:1: ( ';' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:775:1: ( ';' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:776:1: ';'
            {
             before(grammarAccess.getDataTypeAccess().getSemicolonKeyword_3()); 
            match(input,22,FOLLOW_22_in_rule__DataType__Group__3__Impl1562); 
             after(grammarAccess.getDataTypeAccess().getSemicolonKeyword_3()); 

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
    // $ANTLR end "rule__DataType__Group__3__Impl"


    // $ANTLR start "rule__Interface__Group__0"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:797:1: rule__Interface__Group__0 : rule__Interface__Group__0__Impl rule__Interface__Group__1 ;
    public final void rule__Interface__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:801:1: ( rule__Interface__Group__0__Impl rule__Interface__Group__1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:802:2: rule__Interface__Group__0__Impl rule__Interface__Group__1
            {
            pushFollow(FOLLOW_rule__Interface__Group__0__Impl_in_rule__Interface__Group__01601);
            rule__Interface__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Interface__Group__1_in_rule__Interface__Group__01604);
            rule__Interface__Group__1();

            state._fsp--;


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
    // $ANTLR end "rule__Interface__Group__0"


    // $ANTLR start "rule__Interface__Group__0__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:809:1: rule__Interface__Group__0__Impl : ( 'interface' ) ;
    public final void rule__Interface__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:813:1: ( ( 'interface' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:814:1: ( 'interface' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:814:1: ( 'interface' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:815:1: 'interface'
            {
             before(grammarAccess.getInterfaceAccess().getInterfaceKeyword_0()); 
            match(input,24,FOLLOW_24_in_rule__Interface__Group__0__Impl1632); 
             after(grammarAccess.getInterfaceAccess().getInterfaceKeyword_0()); 

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
    // $ANTLR end "rule__Interface__Group__0__Impl"


    // $ANTLR start "rule__Interface__Group__1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:828:1: rule__Interface__Group__1 : rule__Interface__Group__1__Impl rule__Interface__Group__2 ;
    public final void rule__Interface__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:832:1: ( rule__Interface__Group__1__Impl rule__Interface__Group__2 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:833:2: rule__Interface__Group__1__Impl rule__Interface__Group__2
            {
            pushFollow(FOLLOW_rule__Interface__Group__1__Impl_in_rule__Interface__Group__11663);
            rule__Interface__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Interface__Group__2_in_rule__Interface__Group__11666);
            rule__Interface__Group__2();

            state._fsp--;


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
    // $ANTLR end "rule__Interface__Group__1"


    // $ANTLR start "rule__Interface__Group__1__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:840:1: rule__Interface__Group__1__Impl : ( ( rule__Interface__NameAssignment_1 ) ) ;
    public final void rule__Interface__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:844:1: ( ( ( rule__Interface__NameAssignment_1 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:845:1: ( ( rule__Interface__NameAssignment_1 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:845:1: ( ( rule__Interface__NameAssignment_1 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:846:1: ( rule__Interface__NameAssignment_1 )
            {
             before(grammarAccess.getInterfaceAccess().getNameAssignment_1()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:847:1: ( rule__Interface__NameAssignment_1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:847:2: rule__Interface__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__Interface__NameAssignment_1_in_rule__Interface__Group__1__Impl1693);
            rule__Interface__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getInterfaceAccess().getNameAssignment_1()); 

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
    // $ANTLR end "rule__Interface__Group__1__Impl"


    // $ANTLR start "rule__Interface__Group__2"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:857:1: rule__Interface__Group__2 : rule__Interface__Group__2__Impl rule__Interface__Group__3 ;
    public final void rule__Interface__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:861:1: ( rule__Interface__Group__2__Impl rule__Interface__Group__3 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:862:2: rule__Interface__Group__2__Impl rule__Interface__Group__3
            {
            pushFollow(FOLLOW_rule__Interface__Group__2__Impl_in_rule__Interface__Group__21723);
            rule__Interface__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Interface__Group__3_in_rule__Interface__Group__21726);
            rule__Interface__Group__3();

            state._fsp--;


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
    // $ANTLR end "rule__Interface__Group__2"


    // $ANTLR start "rule__Interface__Group__2__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:869:1: rule__Interface__Group__2__Impl : ( '{' ) ;
    public final void rule__Interface__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:873:1: ( ( '{' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:874:1: ( '{' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:874:1: ( '{' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:875:1: '{'
            {
             before(grammarAccess.getInterfaceAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,20,FOLLOW_20_in_rule__Interface__Group__2__Impl1754); 
             after(grammarAccess.getInterfaceAccess().getLeftCurlyBracketKeyword_2()); 

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
    // $ANTLR end "rule__Interface__Group__2__Impl"


    // $ANTLR start "rule__Interface__Group__3"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:888:1: rule__Interface__Group__3 : rule__Interface__Group__3__Impl rule__Interface__Group__4 ;
    public final void rule__Interface__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:892:1: ( rule__Interface__Group__3__Impl rule__Interface__Group__4 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:893:2: rule__Interface__Group__3__Impl rule__Interface__Group__4
            {
            pushFollow(FOLLOW_rule__Interface__Group__3__Impl_in_rule__Interface__Group__31785);
            rule__Interface__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Interface__Group__4_in_rule__Interface__Group__31788);
            rule__Interface__Group__4();

            state._fsp--;


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
    // $ANTLR end "rule__Interface__Group__3"


    // $ANTLR start "rule__Interface__Group__3__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:900:1: rule__Interface__Group__3__Impl : ( ( rule__Interface__FunctionAssignment_3 ) ) ;
    public final void rule__Interface__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:904:1: ( ( ( rule__Interface__FunctionAssignment_3 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:905:1: ( ( rule__Interface__FunctionAssignment_3 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:905:1: ( ( rule__Interface__FunctionAssignment_3 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:906:1: ( rule__Interface__FunctionAssignment_3 )
            {
             before(grammarAccess.getInterfaceAccess().getFunctionAssignment_3()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:907:1: ( rule__Interface__FunctionAssignment_3 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:907:2: rule__Interface__FunctionAssignment_3
            {
            pushFollow(FOLLOW_rule__Interface__FunctionAssignment_3_in_rule__Interface__Group__3__Impl1815);
            rule__Interface__FunctionAssignment_3();

            state._fsp--;


            }

             after(grammarAccess.getInterfaceAccess().getFunctionAssignment_3()); 

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
    // $ANTLR end "rule__Interface__Group__3__Impl"


    // $ANTLR start "rule__Interface__Group__4"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:917:1: rule__Interface__Group__4 : rule__Interface__Group__4__Impl rule__Interface__Group__5 ;
    public final void rule__Interface__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:921:1: ( rule__Interface__Group__4__Impl rule__Interface__Group__5 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:922:2: rule__Interface__Group__4__Impl rule__Interface__Group__5
            {
            pushFollow(FOLLOW_rule__Interface__Group__4__Impl_in_rule__Interface__Group__41845);
            rule__Interface__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Interface__Group__5_in_rule__Interface__Group__41848);
            rule__Interface__Group__5();

            state._fsp--;


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
    // $ANTLR end "rule__Interface__Group__4"


    // $ANTLR start "rule__Interface__Group__4__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:929:1: rule__Interface__Group__4__Impl : ( '}' ) ;
    public final void rule__Interface__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:933:1: ( ( '}' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:934:1: ( '}' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:934:1: ( '}' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:935:1: '}'
            {
             before(grammarAccess.getInterfaceAccess().getRightCurlyBracketKeyword_4()); 
            match(input,21,FOLLOW_21_in_rule__Interface__Group__4__Impl1876); 
             after(grammarAccess.getInterfaceAccess().getRightCurlyBracketKeyword_4()); 

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
    // $ANTLR end "rule__Interface__Group__4__Impl"


    // $ANTLR start "rule__Interface__Group__5"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:948:1: rule__Interface__Group__5 : rule__Interface__Group__5__Impl ;
    public final void rule__Interface__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:952:1: ( rule__Interface__Group__5__Impl )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:953:2: rule__Interface__Group__5__Impl
            {
            pushFollow(FOLLOW_rule__Interface__Group__5__Impl_in_rule__Interface__Group__51907);
            rule__Interface__Group__5__Impl();

            state._fsp--;


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
    // $ANTLR end "rule__Interface__Group__5"


    // $ANTLR start "rule__Interface__Group__5__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:959:1: rule__Interface__Group__5__Impl : ( ';' ) ;
    public final void rule__Interface__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:963:1: ( ( ';' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:964:1: ( ';' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:964:1: ( ';' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:965:1: ';'
            {
             before(grammarAccess.getInterfaceAccess().getSemicolonKeyword_5()); 
            match(input,22,FOLLOW_22_in_rule__Interface__Group__5__Impl1935); 
             after(grammarAccess.getInterfaceAccess().getSemicolonKeyword_5()); 

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
    // $ANTLR end "rule__Interface__Group__5__Impl"


    // $ANTLR start "rule__Function__Group__0"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:990:1: rule__Function__Group__0 : rule__Function__Group__0__Impl rule__Function__Group__1 ;
    public final void rule__Function__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:994:1: ( rule__Function__Group__0__Impl rule__Function__Group__1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:995:2: rule__Function__Group__0__Impl rule__Function__Group__1
            {
            pushFollow(FOLLOW_rule__Function__Group__0__Impl_in_rule__Function__Group__01978);
            rule__Function__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Function__Group__1_in_rule__Function__Group__01981);
            rule__Function__Group__1();

            state._fsp--;


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
    // $ANTLR end "rule__Function__Group__0"


    // $ANTLR start "rule__Function__Group__0__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1002:1: rule__Function__Group__0__Impl : ( 'void' ) ;
    public final void rule__Function__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1006:1: ( ( 'void' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1007:1: ( 'void' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1007:1: ( 'void' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1008:1: 'void'
            {
             before(grammarAccess.getFunctionAccess().getVoidKeyword_0()); 
            match(input,25,FOLLOW_25_in_rule__Function__Group__0__Impl2009); 
             after(grammarAccess.getFunctionAccess().getVoidKeyword_0()); 

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
    // $ANTLR end "rule__Function__Group__0__Impl"


    // $ANTLR start "rule__Function__Group__1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1021:1: rule__Function__Group__1 : rule__Function__Group__1__Impl rule__Function__Group__2 ;
    public final void rule__Function__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1025:1: ( rule__Function__Group__1__Impl rule__Function__Group__2 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1026:2: rule__Function__Group__1__Impl rule__Function__Group__2
            {
            pushFollow(FOLLOW_rule__Function__Group__1__Impl_in_rule__Function__Group__12040);
            rule__Function__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Function__Group__2_in_rule__Function__Group__12043);
            rule__Function__Group__2();

            state._fsp--;


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
    // $ANTLR end "rule__Function__Group__1"


    // $ANTLR start "rule__Function__Group__1__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1033:1: rule__Function__Group__1__Impl : ( ( rule__Function__NameAssignment_1 ) ) ;
    public final void rule__Function__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1037:1: ( ( ( rule__Function__NameAssignment_1 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1038:1: ( ( rule__Function__NameAssignment_1 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1038:1: ( ( rule__Function__NameAssignment_1 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1039:1: ( rule__Function__NameAssignment_1 )
            {
             before(grammarAccess.getFunctionAccess().getNameAssignment_1()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1040:1: ( rule__Function__NameAssignment_1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1040:2: rule__Function__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__Function__NameAssignment_1_in_rule__Function__Group__1__Impl2070);
            rule__Function__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getFunctionAccess().getNameAssignment_1()); 

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
    // $ANTLR end "rule__Function__Group__1__Impl"


    // $ANTLR start "rule__Function__Group__2"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1050:1: rule__Function__Group__2 : rule__Function__Group__2__Impl rule__Function__Group__3 ;
    public final void rule__Function__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1054:1: ( rule__Function__Group__2__Impl rule__Function__Group__3 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1055:2: rule__Function__Group__2__Impl rule__Function__Group__3
            {
            pushFollow(FOLLOW_rule__Function__Group__2__Impl_in_rule__Function__Group__22100);
            rule__Function__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Function__Group__3_in_rule__Function__Group__22103);
            rule__Function__Group__3();

            state._fsp--;


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
    // $ANTLR end "rule__Function__Group__2"


    // $ANTLR start "rule__Function__Group__2__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1062:1: rule__Function__Group__2__Impl : ( '(' ) ;
    public final void rule__Function__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1066:1: ( ( '(' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1067:1: ( '(' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1067:1: ( '(' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1068:1: '('
            {
             before(grammarAccess.getFunctionAccess().getLeftParenthesisKeyword_2()); 
            match(input,26,FOLLOW_26_in_rule__Function__Group__2__Impl2131); 
             after(grammarAccess.getFunctionAccess().getLeftParenthesisKeyword_2()); 

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
    // $ANTLR end "rule__Function__Group__2__Impl"


    // $ANTLR start "rule__Function__Group__3"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1081:1: rule__Function__Group__3 : rule__Function__Group__3__Impl rule__Function__Group__4 ;
    public final void rule__Function__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1085:1: ( rule__Function__Group__3__Impl rule__Function__Group__4 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1086:2: rule__Function__Group__3__Impl rule__Function__Group__4
            {
            pushFollow(FOLLOW_rule__Function__Group__3__Impl_in_rule__Function__Group__32162);
            rule__Function__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Function__Group__4_in_rule__Function__Group__32165);
            rule__Function__Group__4();

            state._fsp--;


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
    // $ANTLR end "rule__Function__Group__3"


    // $ANTLR start "rule__Function__Group__3__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1093:1: rule__Function__Group__3__Impl : ( ( rule__Function__ParametersAssignment_3 )? ) ;
    public final void rule__Function__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1097:1: ( ( ( rule__Function__ParametersAssignment_3 )? ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1098:1: ( ( rule__Function__ParametersAssignment_3 )? )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1098:1: ( ( rule__Function__ParametersAssignment_3 )? )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1099:1: ( rule__Function__ParametersAssignment_3 )?
            {
             before(grammarAccess.getFunctionAccess().getParametersAssignment_3()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1100:1: ( rule__Function__ParametersAssignment_3 )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( ((LA8_0>=17 && LA8_0<=18)) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1100:2: rule__Function__ParametersAssignment_3
                    {
                    pushFollow(FOLLOW_rule__Function__ParametersAssignment_3_in_rule__Function__Group__3__Impl2192);
                    rule__Function__ParametersAssignment_3();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getFunctionAccess().getParametersAssignment_3()); 

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
    // $ANTLR end "rule__Function__Group__3__Impl"


    // $ANTLR start "rule__Function__Group__4"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1110:1: rule__Function__Group__4 : rule__Function__Group__4__Impl rule__Function__Group__5 ;
    public final void rule__Function__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1114:1: ( rule__Function__Group__4__Impl rule__Function__Group__5 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1115:2: rule__Function__Group__4__Impl rule__Function__Group__5
            {
            pushFollow(FOLLOW_rule__Function__Group__4__Impl_in_rule__Function__Group__42223);
            rule__Function__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Function__Group__5_in_rule__Function__Group__42226);
            rule__Function__Group__5();

            state._fsp--;


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
    // $ANTLR end "rule__Function__Group__4"


    // $ANTLR start "rule__Function__Group__4__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1122:1: rule__Function__Group__4__Impl : ( ( rule__Function__Group_4__0 )* ) ;
    public final void rule__Function__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1126:1: ( ( ( rule__Function__Group_4__0 )* ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1127:1: ( ( rule__Function__Group_4__0 )* )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1127:1: ( ( rule__Function__Group_4__0 )* )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1128:1: ( rule__Function__Group_4__0 )*
            {
             before(grammarAccess.getFunctionAccess().getGroup_4()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1129:1: ( rule__Function__Group_4__0 )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==28) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1129:2: rule__Function__Group_4__0
            	    {
            	    pushFollow(FOLLOW_rule__Function__Group_4__0_in_rule__Function__Group__4__Impl2253);
            	    rule__Function__Group_4__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

             after(grammarAccess.getFunctionAccess().getGroup_4()); 

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
    // $ANTLR end "rule__Function__Group__4__Impl"


    // $ANTLR start "rule__Function__Group__5"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1139:1: rule__Function__Group__5 : rule__Function__Group__5__Impl rule__Function__Group__6 ;
    public final void rule__Function__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1143:1: ( rule__Function__Group__5__Impl rule__Function__Group__6 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1144:2: rule__Function__Group__5__Impl rule__Function__Group__6
            {
            pushFollow(FOLLOW_rule__Function__Group__5__Impl_in_rule__Function__Group__52284);
            rule__Function__Group__5__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Function__Group__6_in_rule__Function__Group__52287);
            rule__Function__Group__6();

            state._fsp--;


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
    // $ANTLR end "rule__Function__Group__5"


    // $ANTLR start "rule__Function__Group__5__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1151:1: rule__Function__Group__5__Impl : ( ')' ) ;
    public final void rule__Function__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1155:1: ( ( ')' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1156:1: ( ')' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1156:1: ( ')' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1157:1: ')'
            {
             before(grammarAccess.getFunctionAccess().getRightParenthesisKeyword_5()); 
            match(input,27,FOLLOW_27_in_rule__Function__Group__5__Impl2315); 
             after(grammarAccess.getFunctionAccess().getRightParenthesisKeyword_5()); 

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
    // $ANTLR end "rule__Function__Group__5__Impl"


    // $ANTLR start "rule__Function__Group__6"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1170:1: rule__Function__Group__6 : rule__Function__Group__6__Impl ;
    public final void rule__Function__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1174:1: ( rule__Function__Group__6__Impl )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1175:2: rule__Function__Group__6__Impl
            {
            pushFollow(FOLLOW_rule__Function__Group__6__Impl_in_rule__Function__Group__62346);
            rule__Function__Group__6__Impl();

            state._fsp--;


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
    // $ANTLR end "rule__Function__Group__6"


    // $ANTLR start "rule__Function__Group__6__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1181:1: rule__Function__Group__6__Impl : ( ';' ) ;
    public final void rule__Function__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1185:1: ( ( ';' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1186:1: ( ';' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1186:1: ( ';' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1187:1: ';'
            {
             before(grammarAccess.getFunctionAccess().getSemicolonKeyword_6()); 
            match(input,22,FOLLOW_22_in_rule__Function__Group__6__Impl2374); 
             after(grammarAccess.getFunctionAccess().getSemicolonKeyword_6()); 

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
    // $ANTLR end "rule__Function__Group__6__Impl"


    // $ANTLR start "rule__Function__Group_4__0"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1214:1: rule__Function__Group_4__0 : rule__Function__Group_4__0__Impl rule__Function__Group_4__1 ;
    public final void rule__Function__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1218:1: ( rule__Function__Group_4__0__Impl rule__Function__Group_4__1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1219:2: rule__Function__Group_4__0__Impl rule__Function__Group_4__1
            {
            pushFollow(FOLLOW_rule__Function__Group_4__0__Impl_in_rule__Function__Group_4__02419);
            rule__Function__Group_4__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Function__Group_4__1_in_rule__Function__Group_4__02422);
            rule__Function__Group_4__1();

            state._fsp--;


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
    // $ANTLR end "rule__Function__Group_4__0"


    // $ANTLR start "rule__Function__Group_4__0__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1226:1: rule__Function__Group_4__0__Impl : ( ',' ) ;
    public final void rule__Function__Group_4__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1230:1: ( ( ',' ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1231:1: ( ',' )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1231:1: ( ',' )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1232:1: ','
            {
             before(grammarAccess.getFunctionAccess().getCommaKeyword_4_0()); 
            match(input,28,FOLLOW_28_in_rule__Function__Group_4__0__Impl2450); 
             after(grammarAccess.getFunctionAccess().getCommaKeyword_4_0()); 

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
    // $ANTLR end "rule__Function__Group_4__0__Impl"


    // $ANTLR start "rule__Function__Group_4__1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1245:1: rule__Function__Group_4__1 : rule__Function__Group_4__1__Impl ;
    public final void rule__Function__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1249:1: ( rule__Function__Group_4__1__Impl )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1250:2: rule__Function__Group_4__1__Impl
            {
            pushFollow(FOLLOW_rule__Function__Group_4__1__Impl_in_rule__Function__Group_4__12481);
            rule__Function__Group_4__1__Impl();

            state._fsp--;


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
    // $ANTLR end "rule__Function__Group_4__1"


    // $ANTLR start "rule__Function__Group_4__1__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1256:1: rule__Function__Group_4__1__Impl : ( ( rule__Function__ParametersAssignment_4_1 ) ) ;
    public final void rule__Function__Group_4__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1260:1: ( ( ( rule__Function__ParametersAssignment_4_1 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1261:1: ( ( rule__Function__ParametersAssignment_4_1 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1261:1: ( ( rule__Function__ParametersAssignment_4_1 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1262:1: ( rule__Function__ParametersAssignment_4_1 )
            {
             before(grammarAccess.getFunctionAccess().getParametersAssignment_4_1()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1263:1: ( rule__Function__ParametersAssignment_4_1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1263:2: rule__Function__ParametersAssignment_4_1
            {
            pushFollow(FOLLOW_rule__Function__ParametersAssignment_4_1_in_rule__Function__Group_4__1__Impl2508);
            rule__Function__ParametersAssignment_4_1();

            state._fsp--;


            }

             after(grammarAccess.getFunctionAccess().getParametersAssignment_4_1()); 

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
    // $ANTLR end "rule__Function__Group_4__1__Impl"


    // $ANTLR start "rule__Parameter__Group__0"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1277:1: rule__Parameter__Group__0 : rule__Parameter__Group__0__Impl rule__Parameter__Group__1 ;
    public final void rule__Parameter__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1281:1: ( rule__Parameter__Group__0__Impl rule__Parameter__Group__1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1282:2: rule__Parameter__Group__0__Impl rule__Parameter__Group__1
            {
            pushFollow(FOLLOW_rule__Parameter__Group__0__Impl_in_rule__Parameter__Group__02542);
            rule__Parameter__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Parameter__Group__1_in_rule__Parameter__Group__02545);
            rule__Parameter__Group__1();

            state._fsp--;


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
    // $ANTLR end "rule__Parameter__Group__0"


    // $ANTLR start "rule__Parameter__Group__0__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1289:1: rule__Parameter__Group__0__Impl : ( ( rule__Parameter__DirectionAssignment_0 ) ) ;
    public final void rule__Parameter__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1293:1: ( ( ( rule__Parameter__DirectionAssignment_0 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1294:1: ( ( rule__Parameter__DirectionAssignment_0 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1294:1: ( ( rule__Parameter__DirectionAssignment_0 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1295:1: ( rule__Parameter__DirectionAssignment_0 )
            {
             before(grammarAccess.getParameterAccess().getDirectionAssignment_0()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1296:1: ( rule__Parameter__DirectionAssignment_0 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1296:2: rule__Parameter__DirectionAssignment_0
            {
            pushFollow(FOLLOW_rule__Parameter__DirectionAssignment_0_in_rule__Parameter__Group__0__Impl2572);
            rule__Parameter__DirectionAssignment_0();

            state._fsp--;


            }

             after(grammarAccess.getParameterAccess().getDirectionAssignment_0()); 

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
    // $ANTLR end "rule__Parameter__Group__0__Impl"


    // $ANTLR start "rule__Parameter__Group__1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1306:1: rule__Parameter__Group__1 : rule__Parameter__Group__1__Impl rule__Parameter__Group__2 ;
    public final void rule__Parameter__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1310:1: ( rule__Parameter__Group__1__Impl rule__Parameter__Group__2 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1311:2: rule__Parameter__Group__1__Impl rule__Parameter__Group__2
            {
            pushFollow(FOLLOW_rule__Parameter__Group__1__Impl_in_rule__Parameter__Group__12602);
            rule__Parameter__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_rule__Parameter__Group__2_in_rule__Parameter__Group__12605);
            rule__Parameter__Group__2();

            state._fsp--;


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
    // $ANTLR end "rule__Parameter__Group__1"


    // $ANTLR start "rule__Parameter__Group__1__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1318:1: rule__Parameter__Group__1__Impl : ( ( rule__Parameter__TypeAssignment_1 ) ) ;
    public final void rule__Parameter__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1322:1: ( ( ( rule__Parameter__TypeAssignment_1 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1323:1: ( ( rule__Parameter__TypeAssignment_1 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1323:1: ( ( rule__Parameter__TypeAssignment_1 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1324:1: ( rule__Parameter__TypeAssignment_1 )
            {
             before(grammarAccess.getParameterAccess().getTypeAssignment_1()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1325:1: ( rule__Parameter__TypeAssignment_1 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1325:2: rule__Parameter__TypeAssignment_1
            {
            pushFollow(FOLLOW_rule__Parameter__TypeAssignment_1_in_rule__Parameter__Group__1__Impl2632);
            rule__Parameter__TypeAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getParameterAccess().getTypeAssignment_1()); 

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
    // $ANTLR end "rule__Parameter__Group__1__Impl"


    // $ANTLR start "rule__Parameter__Group__2"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1335:1: rule__Parameter__Group__2 : rule__Parameter__Group__2__Impl ;
    public final void rule__Parameter__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1339:1: ( rule__Parameter__Group__2__Impl )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1340:2: rule__Parameter__Group__2__Impl
            {
            pushFollow(FOLLOW_rule__Parameter__Group__2__Impl_in_rule__Parameter__Group__22662);
            rule__Parameter__Group__2__Impl();

            state._fsp--;


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
    // $ANTLR end "rule__Parameter__Group__2"


    // $ANTLR start "rule__Parameter__Group__2__Impl"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1346:1: rule__Parameter__Group__2__Impl : ( ( rule__Parameter__NameAssignment_2 ) ) ;
    public final void rule__Parameter__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1350:1: ( ( ( rule__Parameter__NameAssignment_2 ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1351:1: ( ( rule__Parameter__NameAssignment_2 ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1351:1: ( ( rule__Parameter__NameAssignment_2 ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1352:1: ( rule__Parameter__NameAssignment_2 )
            {
             before(grammarAccess.getParameterAccess().getNameAssignment_2()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1353:1: ( rule__Parameter__NameAssignment_2 )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1353:2: rule__Parameter__NameAssignment_2
            {
            pushFollow(FOLLOW_rule__Parameter__NameAssignment_2_in_rule__Parameter__Group__2__Impl2689);
            rule__Parameter__NameAssignment_2();

            state._fsp--;


            }

             after(grammarAccess.getParameterAccess().getNameAssignment_2()); 

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
    // $ANTLR end "rule__Parameter__Group__2__Impl"


    // $ANTLR start "rule__IDL__ElementsAssignment"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1370:1: rule__IDL__ElementsAssignment : ( ruleModule ) ;
    public final void rule__IDL__ElementsAssignment() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1374:1: ( ( ruleModule ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1375:1: ( ruleModule )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1375:1: ( ruleModule )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1376:1: ruleModule
            {
             before(grammarAccess.getIDLAccess().getElementsModuleParserRuleCall_0()); 
            pushFollow(FOLLOW_ruleModule_in_rule__IDL__ElementsAssignment2730);
            ruleModule();

            state._fsp--;

             after(grammarAccess.getIDLAccess().getElementsModuleParserRuleCall_0()); 

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
    // $ANTLR end "rule__IDL__ElementsAssignment"


    // $ANTLR start "rule__Module__NameAssignment_1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1385:1: rule__Module__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__Module__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1389:1: ( ( RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1390:1: ( RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1390:1: ( RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1391:1: RULE_ID
            {
             before(grammarAccess.getModuleAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__Module__NameAssignment_12761); 
             after(grammarAccess.getModuleAccess().getNameIDTerminalRuleCall_1_0()); 

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
    // $ANTLR end "rule__Module__NameAssignment_1"


    // $ANTLR start "rule__Module__TypesAssignment_3"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1400:1: rule__Module__TypesAssignment_3 : ( ruleDataType ) ;
    public final void rule__Module__TypesAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1404:1: ( ( ruleDataType ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1405:1: ( ruleDataType )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1405:1: ( ruleDataType )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1406:1: ruleDataType
            {
             before(grammarAccess.getModuleAccess().getTypesDataTypeParserRuleCall_3_0()); 
            pushFollow(FOLLOW_ruleDataType_in_rule__Module__TypesAssignment_32792);
            ruleDataType();

            state._fsp--;

             after(grammarAccess.getModuleAccess().getTypesDataTypeParserRuleCall_3_0()); 

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
    // $ANTLR end "rule__Module__TypesAssignment_3"


    // $ANTLR start "rule__Module__InterfacesAssignment_4"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1415:1: rule__Module__InterfacesAssignment_4 : ( ruleInterface ) ;
    public final void rule__Module__InterfacesAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1419:1: ( ( ruleInterface ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1420:1: ( ruleInterface )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1420:1: ( ruleInterface )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1421:1: ruleInterface
            {
             before(grammarAccess.getModuleAccess().getInterfacesInterfaceParserRuleCall_4_0()); 
            pushFollow(FOLLOW_ruleInterface_in_rule__Module__InterfacesAssignment_42823);
            ruleInterface();

            state._fsp--;

             after(grammarAccess.getModuleAccess().getInterfacesInterfaceParserRuleCall_4_0()); 

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
    // $ANTLR end "rule__Module__InterfacesAssignment_4"


    // $ANTLR start "rule__DataType__BtypeAssignment_1_0"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1430:1: rule__DataType__BtypeAssignment_1_0 : ( ruleBaseType ) ;
    public final void rule__DataType__BtypeAssignment_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1434:1: ( ( ruleBaseType ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1435:1: ( ruleBaseType )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1435:1: ( ruleBaseType )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1436:1: ruleBaseType
            {
             before(grammarAccess.getDataTypeAccess().getBtypeBaseTypeEnumRuleCall_1_0_0()); 
            pushFollow(FOLLOW_ruleBaseType_in_rule__DataType__BtypeAssignment_1_02854);
            ruleBaseType();

            state._fsp--;

             after(grammarAccess.getDataTypeAccess().getBtypeBaseTypeEnumRuleCall_1_0_0()); 

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
    // $ANTLR end "rule__DataType__BtypeAssignment_1_0"


    // $ANTLR start "rule__DataType__CtypeAssignment_1_1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1445:1: rule__DataType__CtypeAssignment_1_1 : ( ( RULE_ID ) ) ;
    public final void rule__DataType__CtypeAssignment_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1449:1: ( ( ( RULE_ID ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1450:1: ( ( RULE_ID ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1450:1: ( ( RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1451:1: ( RULE_ID )
            {
             before(grammarAccess.getDataTypeAccess().getCtypeDataTypeCrossReference_1_1_0()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1452:1: ( RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1453:1: RULE_ID
            {
             before(grammarAccess.getDataTypeAccess().getCtypeDataTypeIDTerminalRuleCall_1_1_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__DataType__CtypeAssignment_1_12889); 
             after(grammarAccess.getDataTypeAccess().getCtypeDataTypeIDTerminalRuleCall_1_1_0_1()); 

            }

             after(grammarAccess.getDataTypeAccess().getCtypeDataTypeCrossReference_1_1_0()); 

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
    // $ANTLR end "rule__DataType__CtypeAssignment_1_1"


    // $ANTLR start "rule__DataType__NameAssignment_2"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1464:1: rule__DataType__NameAssignment_2 : ( RULE_ID ) ;
    public final void rule__DataType__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1468:1: ( ( RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1469:1: ( RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1469:1: ( RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1470:1: RULE_ID
            {
             before(grammarAccess.getDataTypeAccess().getNameIDTerminalRuleCall_2_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__DataType__NameAssignment_22924); 
             after(grammarAccess.getDataTypeAccess().getNameIDTerminalRuleCall_2_0()); 

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
    // $ANTLR end "rule__DataType__NameAssignment_2"


    // $ANTLR start "rule__Interface__NameAssignment_1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1479:1: rule__Interface__NameAssignment_1 : ( ruleInterfaceName ) ;
    public final void rule__Interface__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1483:1: ( ( ruleInterfaceName ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1484:1: ( ruleInterfaceName )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1484:1: ( ruleInterfaceName )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1485:1: ruleInterfaceName
            {
             before(grammarAccess.getInterfaceAccess().getNameInterfaceNameEnumRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleInterfaceName_in_rule__Interface__NameAssignment_12955);
            ruleInterfaceName();

            state._fsp--;

             after(grammarAccess.getInterfaceAccess().getNameInterfaceNameEnumRuleCall_1_0()); 

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
    // $ANTLR end "rule__Interface__NameAssignment_1"


    // $ANTLR start "rule__Interface__FunctionAssignment_3"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1494:1: rule__Interface__FunctionAssignment_3 : ( ruleFunction ) ;
    public final void rule__Interface__FunctionAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1498:1: ( ( ruleFunction ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1499:1: ( ruleFunction )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1499:1: ( ruleFunction )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1500:1: ruleFunction
            {
             before(grammarAccess.getInterfaceAccess().getFunctionFunctionParserRuleCall_3_0()); 
            pushFollow(FOLLOW_ruleFunction_in_rule__Interface__FunctionAssignment_32986);
            ruleFunction();

            state._fsp--;

             after(grammarAccess.getInterfaceAccess().getFunctionFunctionParserRuleCall_3_0()); 

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
    // $ANTLR end "rule__Interface__FunctionAssignment_3"


    // $ANTLR start "rule__Function__NameAssignment_1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1509:1: rule__Function__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__Function__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1513:1: ( ( RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1514:1: ( RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1514:1: ( RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1515:1: RULE_ID
            {
             before(grammarAccess.getFunctionAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__Function__NameAssignment_13017); 
             after(grammarAccess.getFunctionAccess().getNameIDTerminalRuleCall_1_0()); 

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
    // $ANTLR end "rule__Function__NameAssignment_1"


    // $ANTLR start "rule__Function__ParametersAssignment_3"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1524:1: rule__Function__ParametersAssignment_3 : ( ruleParameter ) ;
    public final void rule__Function__ParametersAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1528:1: ( ( ruleParameter ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1529:1: ( ruleParameter )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1529:1: ( ruleParameter )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1530:1: ruleParameter
            {
             before(grammarAccess.getFunctionAccess().getParametersParameterParserRuleCall_3_0()); 
            pushFollow(FOLLOW_ruleParameter_in_rule__Function__ParametersAssignment_33048);
            ruleParameter();

            state._fsp--;

             after(grammarAccess.getFunctionAccess().getParametersParameterParserRuleCall_3_0()); 

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
    // $ANTLR end "rule__Function__ParametersAssignment_3"


    // $ANTLR start "rule__Function__ParametersAssignment_4_1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1539:1: rule__Function__ParametersAssignment_4_1 : ( ruleParameter ) ;
    public final void rule__Function__ParametersAssignment_4_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1543:1: ( ( ruleParameter ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1544:1: ( ruleParameter )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1544:1: ( ruleParameter )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1545:1: ruleParameter
            {
             before(grammarAccess.getFunctionAccess().getParametersParameterParserRuleCall_4_1_0()); 
            pushFollow(FOLLOW_ruleParameter_in_rule__Function__ParametersAssignment_4_13079);
            ruleParameter();

            state._fsp--;

             after(grammarAccess.getFunctionAccess().getParametersParameterParserRuleCall_4_1_0()); 

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
    // $ANTLR end "rule__Function__ParametersAssignment_4_1"


    // $ANTLR start "rule__Parameter__DirectionAssignment_0"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1554:1: rule__Parameter__DirectionAssignment_0 : ( ruleDirection ) ;
    public final void rule__Parameter__DirectionAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1558:1: ( ( ruleDirection ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1559:1: ( ruleDirection )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1559:1: ( ruleDirection )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1560:1: ruleDirection
            {
             before(grammarAccess.getParameterAccess().getDirectionDirectionEnumRuleCall_0_0()); 
            pushFollow(FOLLOW_ruleDirection_in_rule__Parameter__DirectionAssignment_03110);
            ruleDirection();

            state._fsp--;

             after(grammarAccess.getParameterAccess().getDirectionDirectionEnumRuleCall_0_0()); 

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
    // $ANTLR end "rule__Parameter__DirectionAssignment_0"


    // $ANTLR start "rule__Parameter__TypeAssignment_1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1569:1: rule__Parameter__TypeAssignment_1 : ( ruleTypeStar ) ;
    public final void rule__Parameter__TypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1573:1: ( ( ruleTypeStar ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1574:1: ( ruleTypeStar )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1574:1: ( ruleTypeStar )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1575:1: ruleTypeStar
            {
             before(grammarAccess.getParameterAccess().getTypeTypeStarParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleTypeStar_in_rule__Parameter__TypeAssignment_13141);
            ruleTypeStar();

            state._fsp--;

             after(grammarAccess.getParameterAccess().getTypeTypeStarParserRuleCall_1_0()); 

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
    // $ANTLR end "rule__Parameter__TypeAssignment_1"


    // $ANTLR start "rule__Parameter__NameAssignment_2"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1584:1: rule__Parameter__NameAssignment_2 : ( RULE_ID ) ;
    public final void rule__Parameter__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1588:1: ( ( RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1589:1: ( RULE_ID )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1589:1: ( RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1590:1: RULE_ID
            {
             before(grammarAccess.getParameterAccess().getNameIDTerminalRuleCall_2_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__Parameter__NameAssignment_23172); 
             after(grammarAccess.getParameterAccess().getNameIDTerminalRuleCall_2_0()); 

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
    // $ANTLR end "rule__Parameter__NameAssignment_2"


    // $ANTLR start "rule__TypeStar__BtypeAssignment_0"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1599:1: rule__TypeStar__BtypeAssignment_0 : ( ruleBaseType ) ;
    public final void rule__TypeStar__BtypeAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1603:1: ( ( ruleBaseType ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1604:1: ( ruleBaseType )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1604:1: ( ruleBaseType )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1605:1: ruleBaseType
            {
             before(grammarAccess.getTypeStarAccess().getBtypeBaseTypeEnumRuleCall_0_0()); 
            pushFollow(FOLLOW_ruleBaseType_in_rule__TypeStar__BtypeAssignment_03203);
            ruleBaseType();

            state._fsp--;

             after(grammarAccess.getTypeStarAccess().getBtypeBaseTypeEnumRuleCall_0_0()); 

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
    // $ANTLR end "rule__TypeStar__BtypeAssignment_0"


    // $ANTLR start "rule__TypeStar__CtypeAssignment_1"
    // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1614:1: rule__TypeStar__CtypeAssignment_1 : ( ( RULE_ID ) ) ;
    public final void rule__TypeStar__CtypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1618:1: ( ( ( RULE_ID ) ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1619:1: ( ( RULE_ID ) )
            {
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1619:1: ( ( RULE_ID ) )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1620:1: ( RULE_ID )
            {
             before(grammarAccess.getTypeStarAccess().getCtypeDataTypeCrossReference_1_0()); 
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1621:1: ( RULE_ID )
            // ../org.ietr.preesm.editor.IDLEditor.ui/src-gen/org/ietr/preesm/editor/ui/contentassist/antlr/internal/InternalIDLLanguage.g:1622:1: RULE_ID
            {
             before(grammarAccess.getTypeStarAccess().getCtypeDataTypeIDTerminalRuleCall_1_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__TypeStar__CtypeAssignment_13238); 
             after(grammarAccess.getTypeStarAccess().getCtypeDataTypeIDTerminalRuleCall_1_0_1()); 

            }

             after(grammarAccess.getTypeStarAccess().getCtypeDataTypeCrossReference_1_0()); 

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
    // $ANTLR end "rule__TypeStar__CtypeAssignment_1"

    // Delegated rules


 

    public static final BitSet FOLLOW_ruleIDL_in_entryRuleIDL61 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleIDL68 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__IDL__ElementsAssignment_in_ruleIDL94 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleModule_in_entryRuleModule121 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleModule128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Module__Group__0_in_ruleModule154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDataType_in_entryRuleDataType181 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDataType188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DataType__Group__0_in_ruleDataType214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleInterface_in_entryRuleInterface241 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleInterface248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Interface__Group__0_in_ruleInterface274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunction_in_entryRuleFunction301 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFunction308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Function__Group__0_in_ruleFunction334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleParameter_in_entryRuleParameter361 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleParameter368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Parameter__Group__0_in_ruleParameter394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTypeStar_in_entryRuleTypeStar421 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTypeStar428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__TypeStar__Alternatives_in_ruleTypeStar454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__BaseType__Alternatives_in_ruleBaseType491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__InterfaceName__Alternatives_in_ruleInterfaceName527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Direction__Alternatives_in_ruleDirection563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DataType__BtypeAssignment_1_0_in_rule__DataType__Alternatives_1598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DataType__CtypeAssignment_1_1_in_rule__DataType__Alternatives_1616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__TypeStar__BtypeAssignment_0_in_rule__TypeStar__Alternatives649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__TypeStar__CtypeAssignment_1_in_rule__TypeStar__Alternatives667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_11_in_rule__BaseType__Alternatives701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_rule__BaseType__Alternatives722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_rule__BaseType__Alternatives743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_rule__InterfaceName__Alternatives779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_rule__InterfaceName__Alternatives800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_rule__InterfaceName__Alternatives821 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_rule__Direction__Alternatives857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_rule__Direction__Alternatives878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Module__Group__0__Impl_in_rule__Module__Group__0911 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__Module__Group__1_in_rule__Module__Group__0914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_rule__Module__Group__0__Impl942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Module__Group__1__Impl_in_rule__Module__Group__1973 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_rule__Module__Group__2_in_rule__Module__Group__1976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Module__NameAssignment_1_in_rule__Module__Group__1__Impl1003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Module__Group__2__Impl_in_rule__Module__Group__21033 = new BitSet(new long[]{0x0000000001A00000L});
    public static final BitSet FOLLOW_rule__Module__Group__3_in_rule__Module__Group__21036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_rule__Module__Group__2__Impl1064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Module__Group__3__Impl_in_rule__Module__Group__31095 = new BitSet(new long[]{0x0000000001A00000L});
    public static final BitSet FOLLOW_rule__Module__Group__4_in_rule__Module__Group__31098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Module__TypesAssignment_3_in_rule__Module__Group__3__Impl1125 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_rule__Module__Group__4__Impl_in_rule__Module__Group__41156 = new BitSet(new long[]{0x0000000001A00000L});
    public static final BitSet FOLLOW_rule__Module__Group__5_in_rule__Module__Group__41159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Module__InterfacesAssignment_4_in_rule__Module__Group__4__Impl1186 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_rule__Module__Group__5__Impl_in_rule__Module__Group__51217 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_rule__Module__Group__6_in_rule__Module__Group__51220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_rule__Module__Group__5__Impl1248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Module__Group__6__Impl_in_rule__Module__Group__61279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_rule__Module__Group__6__Impl1307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DataType__Group__0__Impl_in_rule__DataType__Group__01352 = new BitSet(new long[]{0x0000000000003810L});
    public static final BitSet FOLLOW_rule__DataType__Group__1_in_rule__DataType__Group__01355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_rule__DataType__Group__0__Impl1383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DataType__Group__1__Impl_in_rule__DataType__Group__11414 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__DataType__Group__2_in_rule__DataType__Group__11417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DataType__Alternatives_1_in_rule__DataType__Group__1__Impl1444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DataType__Group__2__Impl_in_rule__DataType__Group__21474 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_rule__DataType__Group__3_in_rule__DataType__Group__21477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DataType__NameAssignment_2_in_rule__DataType__Group__2__Impl1504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DataType__Group__3__Impl_in_rule__DataType__Group__31534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_rule__DataType__Group__3__Impl1562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Interface__Group__0__Impl_in_rule__Interface__Group__01601 = new BitSet(new long[]{0x000000000001C000L});
    public static final BitSet FOLLOW_rule__Interface__Group__1_in_rule__Interface__Group__01604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_rule__Interface__Group__0__Impl1632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Interface__Group__1__Impl_in_rule__Interface__Group__11663 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_rule__Interface__Group__2_in_rule__Interface__Group__11666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Interface__NameAssignment_1_in_rule__Interface__Group__1__Impl1693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Interface__Group__2__Impl_in_rule__Interface__Group__21723 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_rule__Interface__Group__3_in_rule__Interface__Group__21726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_rule__Interface__Group__2__Impl1754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Interface__Group__3__Impl_in_rule__Interface__Group__31785 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_rule__Interface__Group__4_in_rule__Interface__Group__31788 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Interface__FunctionAssignment_3_in_rule__Interface__Group__3__Impl1815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Interface__Group__4__Impl_in_rule__Interface__Group__41845 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_rule__Interface__Group__5_in_rule__Interface__Group__41848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_rule__Interface__Group__4__Impl1876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Interface__Group__5__Impl_in_rule__Interface__Group__51907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_rule__Interface__Group__5__Impl1935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Function__Group__0__Impl_in_rule__Function__Group__01978 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__Function__Group__1_in_rule__Function__Group__01981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_rule__Function__Group__0__Impl2009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Function__Group__1__Impl_in_rule__Function__Group__12040 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_rule__Function__Group__2_in_rule__Function__Group__12043 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Function__NameAssignment_1_in_rule__Function__Group__1__Impl2070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Function__Group__2__Impl_in_rule__Function__Group__22100 = new BitSet(new long[]{0x0000000018060000L});
    public static final BitSet FOLLOW_rule__Function__Group__3_in_rule__Function__Group__22103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_rule__Function__Group__2__Impl2131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Function__Group__3__Impl_in_rule__Function__Group__32162 = new BitSet(new long[]{0x0000000018060000L});
    public static final BitSet FOLLOW_rule__Function__Group__4_in_rule__Function__Group__32165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Function__ParametersAssignment_3_in_rule__Function__Group__3__Impl2192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Function__Group__4__Impl_in_rule__Function__Group__42223 = new BitSet(new long[]{0x0000000018060000L});
    public static final BitSet FOLLOW_rule__Function__Group__5_in_rule__Function__Group__42226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Function__Group_4__0_in_rule__Function__Group__4__Impl2253 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_rule__Function__Group__5__Impl_in_rule__Function__Group__52284 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_rule__Function__Group__6_in_rule__Function__Group__52287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_rule__Function__Group__5__Impl2315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Function__Group__6__Impl_in_rule__Function__Group__62346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_rule__Function__Group__6__Impl2374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Function__Group_4__0__Impl_in_rule__Function__Group_4__02419 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_rule__Function__Group_4__1_in_rule__Function__Group_4__02422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_rule__Function__Group_4__0__Impl2450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Function__Group_4__1__Impl_in_rule__Function__Group_4__12481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Function__ParametersAssignment_4_1_in_rule__Function__Group_4__1__Impl2508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Parameter__Group__0__Impl_in_rule__Parameter__Group__02542 = new BitSet(new long[]{0x0000000000003810L});
    public static final BitSet FOLLOW_rule__Parameter__Group__1_in_rule__Parameter__Group__02545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Parameter__DirectionAssignment_0_in_rule__Parameter__Group__0__Impl2572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Parameter__Group__1__Impl_in_rule__Parameter__Group__12602 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__Parameter__Group__2_in_rule__Parameter__Group__12605 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Parameter__TypeAssignment_1_in_rule__Parameter__Group__1__Impl2632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Parameter__Group__2__Impl_in_rule__Parameter__Group__22662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Parameter__NameAssignment_2_in_rule__Parameter__Group__2__Impl2689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleModule_in_rule__IDL__ElementsAssignment2730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__Module__NameAssignment_12761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDataType_in_rule__Module__TypesAssignment_32792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleInterface_in_rule__Module__InterfacesAssignment_42823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBaseType_in_rule__DataType__BtypeAssignment_1_02854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__DataType__CtypeAssignment_1_12889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__DataType__NameAssignment_22924 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleInterfaceName_in_rule__Interface__NameAssignment_12955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunction_in_rule__Interface__FunctionAssignment_32986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__Function__NameAssignment_13017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleParameter_in_rule__Function__ParametersAssignment_33048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleParameter_in_rule__Function__ParametersAssignment_4_13079 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDirection_in_rule__Parameter__DirectionAssignment_03110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTypeStar_in_rule__Parameter__TypeAssignment_13141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__Parameter__NameAssignment_23172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBaseType_in_rule__TypeStar__BtypeAssignment_03203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__TypeStar__CtypeAssignment_13238 = new BitSet(new long[]{0x0000000000000002L});

}