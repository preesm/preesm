
package org.ietr.preesm.editor;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class IDLLanguageStandaloneSetup extends IDLLanguageStandaloneSetupGenerated{

	public static void doSetup() {
		new IDLLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

