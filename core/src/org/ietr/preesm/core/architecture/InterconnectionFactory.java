/**
 * 
 */
package org.ietr.preesm.core.architecture;

import org.jgrapht.EdgeFactory;

/**
 * @author mpelcat
 * 
 */
public class InterconnectionFactory implements
		EdgeFactory<Component, Interconnection> {

	boolean isDirected = false;

	public void isDirected(boolean isDirected) {
		this.isDirected = isDirected;
	}

	@Override
	public Interconnection createEdge(Component cmp1, Component cmp2) {
		return new Interconnection();
	}

}
