/**
 * 
 */
package org.ietr.preesm.plugin.abc.transaction;

import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.PrecedenceEdge;

/**
 * Transaction executing the addition of a {@link PrecedenceEdge}.
 * 
 * @author mpelcat
 */
public class AddPrecedenceEdgeTransaction extends Transaction {

	// Inputs
	/**
	 * Implementation DAG to which the edge is added
	 */
	private MapperDAG implementation = null;

	/**
	 * Source of the added edge
	 */
	private MapperDAGVertex source = null;

	/**
	 * Source of the added edge
	 */
	private MapperDAGVertex destination = null;

	// Generated objects
	/**
	 * edges added
	 */
	private PrecedenceEdge precedenceEdge = null;

	public AddPrecedenceEdgeTransaction(MapperDAG implementation,
			MapperDAGVertex source, MapperDAGVertex destination) {
		super();
		this.destination = destination;
		this.implementation = implementation;
		this.source = source;
	}

	@Override
	public void execute() {
		super.execute();

		precedenceEdge = new PrecedenceEdge();
		precedenceEdge.getTimingEdgeProperty().setCost(0);
		implementation.addEdge(source, destination, precedenceEdge);
	}

	@Override
	public void undo() {
		super.undo();

		implementation.removeEdge(precedenceEdge);
	}

}
