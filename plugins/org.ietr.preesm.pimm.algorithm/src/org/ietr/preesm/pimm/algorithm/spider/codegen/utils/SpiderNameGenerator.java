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
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

public final class SpiderNameGenerator {
	/** Private constructor: prevents instantiation by client code */
	private SpiderNameGenerator(){
		
	}
	
	/**
	 * Returns the name of the subgraph pg
	 */
	public static String getSubraphName(PiGraph pg) {
		return pg.getName() + "_subGraph";
	}
	
	public static String getCoreTypeName(String coreType) {
		return "CORE_TYPE_" + coreType.toUpperCase();
	}

	/**
	 * Returns the name of the variable pointing to the C++ object corresponding
	 * to AbstractActor aa
	 */
	public static String getVertexName(AbstractVertex aa) {
		switch (SpiderTypeConverter.getType(aa)) {
		case PISDF_TYPE_BODY:
			return "bo_" + aa.getName();
		case PISDF_TYPE_CONFIG:
			return "cf_" + aa.getName();
		case PISDF_TYPE_IF:
			return "if_" + aa.getName();
		}
		return null;
	}

	/**
	 * Returns the name of the building method for the PiGraph pg
	 */
	public static String getMethodName(PiGraph pg) {
		return pg.getName();
	}
	
	public static String getFunctionName(AbstractActor aa) {
		return ((PiGraph)aa.eContainer()).getName() + "_" + aa.getName();
	}

	/**
	 * Returns the name of the parameter
	 */
	public static String getParameterName(Parameter p) {
		return "param_" + p.getName();
	}

	public static String getCoreName(String core) {
		return "CORE_" + core.toUpperCase();
	}
}
