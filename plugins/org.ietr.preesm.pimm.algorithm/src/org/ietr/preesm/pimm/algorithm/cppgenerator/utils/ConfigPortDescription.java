package org.ietr.preesm.pimm.algorithm.cppgenerator.utils;

import org.ietr.preesm.experiment.model.pimm.Parameter;

/**
 * Class allowing to stock necessary information about data ports for the
 * edges generation
 */
public class ConfigPortDescription {
	public Parameter param;
	public int index;

	public ConfigPortDescription(Parameter param, int index) {
		this.param = param;
		this.index = index;
	}
}
