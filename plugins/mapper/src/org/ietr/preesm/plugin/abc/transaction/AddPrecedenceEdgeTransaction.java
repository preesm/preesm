/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.plugin.abc.transaction;

import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;

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
