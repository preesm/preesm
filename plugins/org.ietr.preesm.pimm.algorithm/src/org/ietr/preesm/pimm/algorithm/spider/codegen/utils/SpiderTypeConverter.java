/*******************************************************************************
 * Copyright or © or Copr. 2016 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2016)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.pimm.algorithm.spider.codegen.utils;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;

public final class SpiderTypeConverter {
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
	private SpiderTypeConverter(){		
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
