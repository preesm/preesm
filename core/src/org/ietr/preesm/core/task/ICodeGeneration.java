/**
 * 
 */
package org.ietr.preesm.core.task;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * This interface defines methods to generate generic source code from an
 * algorithm and an architecture.
 * 
 * @author Matthieu Wipliez
 * 
 */
public interface ICodeGeneration extends ITransformation {

	/**
	 * Transforms an algorithm and an architecture to generic source code.
	 * 
	 * @param algorithm
	 *            A {@link SDFGraph} graph.
	 * @param architecture
	 *            An {@link IArchitecture}.
	 * @param list
	 *            A {@link SourceFileList}.
	 */
	public TaskResult transform(DirectedAcyclicGraph algorithm, IArchitecture architecture, TextParameters parameters);

}
