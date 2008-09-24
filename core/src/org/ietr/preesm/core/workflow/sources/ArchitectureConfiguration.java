/**
 * 
 */
package org.ietr.preesm.core.workflow.sources;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Representing the configuration for the architecture retrieved from launch tab.
 * It feeds an {@link ArchitectureRetriever} to create the input architecture.
 * 
 * @author mpelcat
 */
public class ArchitectureConfiguration {

	/**
	 * ID used to save architecture file name in tab attributes
	 */
	public static final String ATTR_ARCHITECTURE_FILE_NAME = "org.ietr.preesm.core.architectureFileName";

	private String architectureFileName = null;

	public ArchitectureConfiguration(ILaunchConfiguration configuration) throws CoreException {
		super();
		this.architectureFileName = configuration.getAttribute(
				ATTR_ARCHITECTURE_FILE_NAME, "");
	}

	public String getArchitectureFileName() {
		return architectureFileName;
	}
}
