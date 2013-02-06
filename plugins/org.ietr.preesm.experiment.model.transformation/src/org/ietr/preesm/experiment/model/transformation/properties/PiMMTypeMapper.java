package org.ietr.preesm.experiment.model.transformation.properties;

import org.eclipse.ui.views.properties.tabbed.ITypeMapper;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.Parameter;

public class PiMMTypeMapper  implements ITypeMapper {

	
	public PiMMTypeMapper(){
		System.out.println("Clase PiMMTypeMapper");
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Class mapType(Object object) {
		System.out.println("en clase TypeMapper");
		Class type = object.getClass();
		if (object instanceof Graph) {
			type = ((Graph) object).getDependencies().getClass();
					//getModel().getClass();
			//type = ((Parameter) object).getClass();
		}
		return type;
	}

}
