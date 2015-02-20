package org.ietr.preesm.experiment.pimm.cppgenerator.utils;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

public final class CppNameGenerator {
	/** Private constructor: prevents instantiation by client code */
	private CppNameGenerator(){
		
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
		switch (CppTypeConverter.getType(aa)) {
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
		return aa.getName();
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
