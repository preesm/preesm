/**
 * 
 */
package org.ietr.preesm.plugin.mapper.model.implementation;

import org.ietr.preesm.plugin.mapper.model.MapperDAG;

/**
 * Vertex corresponding to receiving a data.
 * This vertex is mapped on the receiver of 
 * the corresponding route step.
 * 
 * @author mpelcat
 */
public class ReceiveVertex extends TransferVertex {

	public ReceiveVertex(String id, MapperDAG base) {
		super(id, base);
	}

}
