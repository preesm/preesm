package org.ietr.preesm.experiment.pimm.cppgenerator.utils;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;

public final class CppTypeConverter {
	public enum PiSDFType {
		PISDF_TYPE_BODY, 
		PISDF_TYPE_CONFIG, 
		PISDF_TYPE_IF		
	}
	
	public enum PiSDFSubType {
		PISDF_SUBTYPE_NORMAL,
		PISDF_SUBTYPE_BROADCAST,
		PISDF_SUBTYPE_FORK,
		PISDF_SUBTYPE_JOIN,
		PISDF_SUBTYPE_END,
		PISDF_SUBTYPE_INPUT_IF,
		PISDF_SUBTYPE_OUTPUT_IF
	}
	
	/** Private constructor: prevents instantiation by client code */
	private CppTypeConverter(){		
	}
	
	public static PiSDFType getType(AbstractVertex aa){
		if(aa instanceof InterfaceActor)
			return PiSDFType.PISDF_TYPE_IF;
		else if(aa instanceof Actor && ((Actor)aa).isConfigurationActor())
			return PiSDFType.PISDF_TYPE_CONFIG;
		else return PiSDFType.PISDF_TYPE_BODY;
	}
	
	public static PiSDFSubType getSubType(AbstractVertex aa){
		switch(getType(aa)){
		case PISDF_TYPE_BODY:
		case PISDF_TYPE_CONFIG:
			return PiSDFSubType.PISDF_SUBTYPE_NORMAL;
		case PISDF_TYPE_IF:
			if(((AbstractActor)aa).getDataInputPorts().size() > 0)
				return PiSDFSubType.PISDF_SUBTYPE_OUTPUT_IF;
			else
				return PiSDFSubType.PISDF_SUBTYPE_INPUT_IF;
		}
		return null;
	}

}
