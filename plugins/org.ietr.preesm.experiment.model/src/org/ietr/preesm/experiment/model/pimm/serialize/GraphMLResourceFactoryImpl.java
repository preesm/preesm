package org.ietr.preesm.experiment.model.pimm.serialize;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

public class GraphMLResourceFactoryImpl extends ResourceFactoryImpl {

	public GraphMLResourceFactoryImpl() {
		super();
	}
	
	@Override
	public Resource createResource(URI uri) {
		Resource result = new GraphMLResourceImpl();
		result.setURI(uri);
		return result;
	}

}
