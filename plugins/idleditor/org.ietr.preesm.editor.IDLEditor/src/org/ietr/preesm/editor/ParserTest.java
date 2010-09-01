/**
 * 
 */
package org.ietr.preesm.editor;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * @author mpelcat
 *
 */
public class ParserTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//new IDLStandaloneSetup().createInjectorAndDoEMFRegistration();
		ResourceSet rs = new ResourceSetImpl();
		Resource resource = rs.getResource(URI.createURI("file:/D:/testEclipse/Xtext/runtime-EclipseApplication/MyIDLPjt/myIDL.idl"), true);
		EObject eobject = resource.getContents().get(0);
		int i=0;
		i++;
	}

}
