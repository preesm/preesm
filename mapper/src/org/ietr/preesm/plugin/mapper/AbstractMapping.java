/**
 * 
 */
package org.ietr.preesm.plugin.mapper;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.constraints.IScenario;
import org.ietr.preesm.core.task.IMapping;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Generic class representing the scheduling algorithm behaviour
 * 
 * @author pmenuet
 */
public abstract class AbstractMapping implements IMapping {
	
	@Override
	public abstract void transform(SDFGraph algorithm, SDFGraph transformedAlgorithm);

	
	@Override
	public abstract TaskResult transform(SDFGraph algorithm, IArchitecture architecture,
			TextParameters textParameters,
			IScenario scenatio);



}
