/**
 * 
 */
package org.ietr.preesm.core.architecture;

import org.jgrapht.EdgeFactory;

/**
 * @author mpelcat
 *
 */
public class InterconnectionFactory implements EdgeFactory<ArchitectureComponent, Interconnection> {

	boolean isDirected = false;
	
	public void isDirected(boolean isDirected){
		this.isDirected = isDirected;
	}
	
	@Override
	public Interconnection createEdge(ArchitectureComponent cmp1,
			ArchitectureComponent cmp2) {
		return new Interconnection();
	}

}
