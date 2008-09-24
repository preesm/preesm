package org.ietr.preesm.core.task;

import org.sdf4j.model.sdf.SDFGraph;

/**
 * Describe the interface to GraphTransformation
 * @author jpiat
 *
 */
public interface IGraphTransformation extends ITransformation{
	
	/**
	 * Transforms an algorithm into another algorithm.
	 * 
	 * @param algorithm
	 *            A {@link SDFGraph} graph to be transformed.
	 */
	public TaskResult transform(SDFGraph algorithm,  TextParameters params);

}
