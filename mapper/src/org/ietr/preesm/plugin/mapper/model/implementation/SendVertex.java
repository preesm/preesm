/**
 * 
 */
package org.ietr.preesm.plugin.mapper.model.implementation;

import org.ietr.preesm.plugin.mapper.model.MapperDAG;

/**
 * Vertex corresponding to sending a data.
 * This vertex is mapped on the sender of 
 * the corresponding route step.
 * 
 * @author mpelcat
 */
public class SendVertex extends TransferVertex {

	public SendVertex(String id, MapperDAG base) {
		super(id, base);
		// TODO Auto-generated constructor stub
	}

}
