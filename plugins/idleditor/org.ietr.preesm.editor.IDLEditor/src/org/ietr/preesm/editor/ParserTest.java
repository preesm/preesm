/**
 * 
 */
package org.ietr.preesm.editor;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.ietr.preesm.editor.iDLLanguage.IDL;

/**
 * @author mpelcat
 *
 */
public class ParserTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new IDLLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
		ResourceSet rs = new ResourceSetImpl();
		
		Resource resource = rs.getResource(URI.createURI("file:/D:/Projets/Preesm/trunk/tutorials/tutorial-image/Code/IDL/displayPic.idl"), true);
		
		EObject eobject = resource.getContents().get(0);

		if(eobject instanceof IDL){
			int i=0; i++;
		}
		else{
			
		}
	}

}
