package org.ietr.preesm.experiment.pimm.cppgenerator.utils;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

public class CPPNameGenerator {
	/**
	 * Returns the name of the subgraph pg
	 */
	public String getSubraphName(PiGraph pg) {
		return pg.getName() + "_subGraph";
	}

	/**
	 * Returns the name of the variable pointing to the C++ object corresponding
	 * to AbstractActor aa
	 */
	public String getVertexName(AbstractActor aa) {
		return "vx" + aa.getName();
	}

	/**
	 * Returns the name of the building method for the PiGraph pg
	 */
	public String getMethodName(PiGraph pg) {
		return pg.getName();
	}

	/**
	 * Returns the name of the parameter
	 */
	public String getParameterName(Parameter p) {
		return "param_" + p.getName();
	}
}
