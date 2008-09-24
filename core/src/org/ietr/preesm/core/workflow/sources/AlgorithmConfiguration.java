/**
 * 
 */
package org.ietr.preesm.core.workflow.sources;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Representing the configuration for the algorithm retrieved from launch tab.
 * It feeds an {@link AlgorithmRetriever} to create the input algorithm.
 * 
 * @author mpelcat
 */
public class AlgorithmConfiguration {
	/**
	 * ID used to save algorithm file name in tab attributes
	 */
	public static final String ATTR_ALGORITHM_FILE_NAME = "org.ietr.preesm.core.algorithmFileName";

	
	private String algorithmFileName = null;


	public AlgorithmConfiguration(ILaunchConfiguration configuration) throws CoreException {
		super();
		this.algorithmFileName = configuration.getAttribute(
				ATTR_ALGORITHM_FILE_NAME, "");
	}

	public String getAlgorithmFileName() {
		return algorithmFileName;
	}
}
