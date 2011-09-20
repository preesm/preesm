/**
 * 
 */
package org.ietr.preesm.core.architecture;

import java.util.Set;

/**
 * Interface to a distributed architecture to make accesses to architectures
 * more generic
 * 
 * @author mpelcat
 */
public interface IDistributedArchitecture {

	/**
	 * Getting all IDs of operator definitions in the architecture
	 */
	public Set<String> getOperatorDefinitionIds();
}
