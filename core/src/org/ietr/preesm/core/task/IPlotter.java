/**
 * 
 */
package org.ietr.preesm.core.task;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * This interface defines methods to plot a graph or an implementation
 * 
 * @author mpelcat
 */
public interface IPlotter extends ITask {

	/**
	 * Method to plot a given implementation using the given parameters
	 * @param algorithm The algorithm to export
	 * @param params The parameters rulling the exportation
	 */
	public void transform(DirectedAcyclicGraph dag, SDFGraph sdf, IArchitecture archi, IScenario scenario, TextParameters params);
	
}
