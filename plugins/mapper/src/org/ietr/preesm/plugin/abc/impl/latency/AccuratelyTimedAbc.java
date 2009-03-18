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

package org.ietr.preesm.plugin.abc.impl.latency;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.edgescheduling.AbstractEdgeSched;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.route.TransferVertexAdder;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * The accurately timed ABC schedules edges and set-up times
 * 
 * @author mpelcat
 */
public class AccuratelyTimedAbc extends LatencyAbc {

	/**
	 * Transfer vertex adder for edge scheduling
	 */
	protected TransferVertexAdder tvertexAdder;

	/**
	 * Scheduling the transfer vertices on the media
	 */
	protected IEdgeSched edgeScheduler;

	/**
	 * Constructor of the simulator from a "blank" implementation where every
	 * vertex has not been implanted yet.
	 */
	public AccuratelyTimedAbc(EdgeSchedType edgeSchedType, MapperDAG dag,
			MultiCoreArchitecture archi, AbcType abcType) {
		super(edgeSchedType, dag, archi, abcType);

		// The media simulator calculates the edges costs
		edgeScheduler = AbstractEdgeSched.getInstance(edgeSchedType,
				orderManager);
		tvertexAdder = new TransferVertexAdder(edgeScheduler, router,
				orderManager, false, false, true);
	}

	/**
	 * Called when a new vertex operator is set
	 */
	@Override
	protected void fireNewMappedVertex(MapperDAGVertex vertex,
			boolean updateRank) {

		super.fireNewMappedVertex(vertex,updateRank);

		Operator effectiveOp = vertex.getImplementationVertexProperty()
				.getEffectiveOperator();

		if (effectiveOp != Operator.NO_COMPONENT) {
			precedenceEdgeAdder.scheduleNewVertex(implementation,
					transactionManager, vertex, vertex);

			tvertexAdder.addAndScheduleTransferVertices(implementation, vertex);

		}
	}

	/**
	 * Edge scheduling vertices are added. Thus useless edge costs are removed
	 */
	@Override
	protected final void setEdgeCost(MapperDAGEdge edge) {

		edge.getTimingEdgeProperty().setCost(0);

		//Setting edge costs for special types
		super.setEdgeCost(edge);
	}

	@Override
	public EdgeSchedType getEdgeSchedType() {
		return edgeScheduler.getEdgeSchedType();
	}
}
