/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.mapper.abc.impl.latency;

import java.util.ArrayList;
import java.util.List;

import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.AbcType;
import org.ietr.preesm.mapper.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.mapper.abc.route.CommunicationRouter;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.PrecedenceEdgeAdder;
import org.ietr.preesm.mapper.params.AbcParameters;

/**
 * An approximately timed architecture simulator associates a complex cost to
 * each inter-core communication. This cost is composed of an overhead on the
 * sender, a transfer time on the medium and a reception time on the receiver.
 * Scheduling transfer vertices are added and mapped to the media architecture
 * components
 * 
 * @author mpelcat
 */
public class ApproximatelyTimedAbc extends LatencyAbc {

	List<Integer> types = null;

	/**
	 * Constructor of the simulator from a "blank" implementation where every
	 * vertex has not been mapped yet.
	 */
	public ApproximatelyTimedAbc(AbcParameters params, MapperDAG dag,
			Design archi, AbcType abcType, PreesmScenario scenario) {
		super(params, dag, archi, abcType, scenario);

		types = new ArrayList<Integer>();
		types.add(CommunicationRouter.transferType);
		types.add(CommunicationRouter.synchroType);
	}

	/**
	 * Called when a new vertex operator is set
	 */
	@Override
	protected void fireNewMappedVertex(MapperDAGVertex vertex,
			boolean updateRank) {

		super.fireNewMappedVertex(vertex, updateRank);

		ComponentInstance effectiveOp = vertex
				.getEffectiveOperator();

		if (effectiveOp != DesignTools.NO_COMPONENT_INSTANCE) {

			new PrecedenceEdgeAdder(orderManager, implementation)
					.scheduleVertex(vertex);
			comRouter.routeNewVertex(vertex, types);
		}
	}

	/**
	 * Edge scheduling vertices are added. Thus useless edge costs are removed
	 */
	@Override
	protected final void setEdgeCost(MapperDAGEdge edge) {

		edge.getTiming().setCost(0);

		// Setting edge costs for special types
		// super.setEdgeCost(edge);

	}

	@Override
	public EdgeSchedType getEdgeSchedType() {
		return edgeScheduler.getEdgeSchedType();
	}
}
