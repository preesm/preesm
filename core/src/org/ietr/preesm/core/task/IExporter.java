package org.ietr.preesm.core.task;

import org.sdf4j.model.sdf.SDFGraph;

public interface IExporter extends ITask{

	/**
	 * Method to export a given graph using the given parameters
	 * @param algorithm The algorithm to export
	 * @param params The parameters rulling the exportation
	 */
	public void transform(SDFGraph algorithm, TextParameters params);

}
