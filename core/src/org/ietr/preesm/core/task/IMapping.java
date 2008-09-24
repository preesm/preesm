/**
 * 
 */
package org.ietr.preesm.core.task;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.constraints.IScenario;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * This interface defines methods to transform an algorithm or an algorithm
 * *and* an architecture.
 * 
 * @author Matthieu Wipliez
 * 
 */
public interface IMapping extends ITransformation {

	/**
	 * Transforms an algorithm and an architecture.
	 * 
	 * @param algorithm
	 *            A {@link SDFGraph} graph.
	 * @param architecture
	 *            An {@link IArchitecture}.
	 * @param transformedAlgorithm
	 *            The transformed algorithm as a {@link SDFGraph} graph.
	 * @param transformedArchitecture
	 *            The transformed architecture as an {@link IArchitecture}.
	 * @param scenario
	 *            The transformation constraints.
	 */
	public TaskResult transform(SDFGraph algorithm, IArchitecture architecture,
			TextParameters algorithmParameters,
			IScenario scenario);

	/**
	 * Transforms an algorithm.
	 * 
	 * @param algorithm
	 *            A {@link SDFGraph} graph.
	 * @param transformedAlgorithm
	 *            The transformed algorithm as a {@link SDFGraph} graph.
	 */
	public void transform(SDFGraph algorithm, SDFGraph transformedAlgorithm);

}
