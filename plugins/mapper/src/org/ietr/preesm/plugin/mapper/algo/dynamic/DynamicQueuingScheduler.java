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

package org.ietr.preesm.plugin.mapper.algo.dynamic;

import java.util.List;

import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.order.VertexOrderList;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;

/**
 * Scheduler that simulates a dynamic queuing system
 * 
 * @author mpelcat
 */
public class DynamicQueuingScheduler {

	/**
	 * The queue of vertices to map
	 */
	private VertexOrderList orderList;

	/**
	 * Parameters of the workflow
	 */
	private TextParameters textParameters;

	/**
	 * constructor
	 */
	public DynamicQueuingScheduler(VertexOrderList orderList, TextParameters textParameters) {
		super();
		this.orderList = orderList;
		this.textParameters = textParameters;
	}

	/**
	 * implants the vertices on the operator with lowest final cost (soonest
	 * available)
	 */
	public void implantVertices(IAbc abc) {

		// Type of order to use while mapping/scheduling
		String listType = textParameters.getVariable("listType");

		if(listType.isEmpty()){
			listType = "optimised";
		}
		
		if (listType.equalsIgnoreCase("optimised")) {

			for (VertexOrderList.OrderProperty vP : orderList.elements()) {
				MapperDAGVertex currentvertex = (MapperDAGVertex) abc.getDAG()
						.getVertex(vP.getName());

				implantOnBestOp(abc, currentvertex);

			}
		} else if (listType.equalsIgnoreCase("topological")) {
			TopologicalDAGIterator it = new TopologicalDAGIterator(abc.getDAG());

			while (it.hasNext()) {
				MapperDAGVertex v = (MapperDAGVertex) it.next();
				MapperDAGVertex currentvertex = (MapperDAGVertex) abc.getDAG()
						.getVertex(v.getName());

				implantOnBestOp(abc, currentvertex);

			}
		}
	}

	public void implantOnBestOp(IAbc abc, MapperDAGVertex currentvertex) {
		List<Operator> adequateOps = currentvertex.getImplementationVertexProperty().getAdaptiveOperatorList();
		long currentMinCost = Long.MAX_VALUE;
		Operator currentMinOp = null;

		for (Operator op : adequateOps) {
			abc.updateFinalCosts();
			long newCost = abc.getFinalCost(op);
			if (newCost < currentMinCost) {
				currentMinCost = newCost;
				currentMinOp = op;
			}
		}

		// Implanting on operator with minimal final cost
		if (currentMinOp != null) {
			abc.implant(currentvertex, currentMinOp, true);
		}
	}
}
