/**
 * 
 */
package org.ietr.preesm.core.workflow.sources;

import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.IArchitecture;

/**
 * @author mpelcat
 *
 */
public class ArchitectureRetriever {

	static public IArchitecture ExampleArchitecture(){
		return Examples.get2C64Archi();
	}

	IArchitecture architecture = null;
	
	public ArchitectureRetriever(ArchitectureConfiguration architectureConfiguration) {
		super();
		// TODO: load the architecture into the "architecture" variable
	}

	public IArchitecture getArchitecture() {
		return architecture;
	}
			
}
