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

import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.component.ComNode;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.AbcType;
import org.ietr.preesm.mapper.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.mapper.abc.taskscheduling.TaskSchedType;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.params.AbcParameters;
import org.ietr.preesm.mapper.tools.TLevelIterator;

/**
 * Simulates an architecture having as many cores as necessary to execute one
 * operation on one core. All core have the main operator definition. These
 * cores are all interconnected with media corresponding to the main medium
 * definition.
 * 
 * @author mpelcat
 */
public class InfiniteHomogeneousAbc extends LatencyAbc {

	/**
	 * Constructor
	 */
	public InfiniteHomogeneousAbc(AbcParameters params, MapperDAG dag,
			Design archi, PreesmScenario scenario) throws WorkflowException {
		this(params, dag, archi, TaskSchedType.Simple, scenario);
	}

	/**
	 * Constructor of the simulator from a "blank" implementation where every
	 * vertex has not been mapped yet.
	 */
	public InfiniteHomogeneousAbc(AbcParameters params, MapperDAG dag,
			Design archi, TaskSchedType taskSchedType, PreesmScenario scenario) throws WorkflowException {
		super(params, dag, archi, AbcType.InfiniteHomogeneous, scenario);
		this.getType().setTaskSchedType(taskSchedType);

		ComponentInstance mainComNode = DesignTools.getComponentInstance(archi,
				scenario.getSimulationManager().getMainComNodeName());

		ComponentInstance mainOperator = DesignTools.getComponentInstance(archi,
				scenario.getSimulationManager().getMainOperatorName());
		
		if (mainComNode != null) {
			WorkflowLogger.getLogger().info("Infinite homogeneous simulation");
		} else {
			WorkflowLogger
					.getLogger()
					.severe("Current architecture has no main communication node. Please set a main communication node.");
		}
		
		if (mainOperator == null) {
			WorkflowLogger
					.getLogger()
					.severe("Current architecture has no main operator. Please set a main operator.");
		}

		// The InfiniteHomogeneousArchitectureSimulator is specifically done
		// to map all vertices on the main operator definition but consider
		// as many cores as there are tasks.
		mapAllVerticesOnOperator(mainOperator);

		updateFinalCosts();
		orderManager.resetTotalOrder();
		TLevelIterator iterator = new TLevelIterator(implementation, true);

		while (iterator.hasNext()) {
			MapperDAGVertex v = iterator.next();
			orderManager.addLast(v);
		}

		retrieveTotalOrder();
	}

	@Override
	protected void fireNewMappedVertex(MapperDAGVertex vertex,
			boolean updateRank) {

		ComponentInstance effectiveOp = vertex
				.getMapping().getEffectiveOperator();

		/*
		 * mapping a vertex sets the cost of the current vertex and its edges
		 * 
		 * As we have an infinite homogeneous architecture, each communication
		 * is done through the unique type of medium
		 */
		if (effectiveOp == DesignTools.NO_COMPONENT_INSTANCE) {
			WorkflowLogger.getLogger().severe(
					"implementation of " + vertex.getName() + " failed. No operator was assigned.");

			vertex.getTiming().setCost(0);

		} else {

			// Setting vertex time
			long vertextime = vertex.getInit().getTime(
					effectiveOp);
			vertex.getTiming().setCost(vertextime);

			// Setting edges times

			setEdgesCosts(vertex.incomingEdges());
			setEdgesCosts(vertex.outgoingEdges());

			if (updateRank) {
				nTimeKeeper.updateTLevels();
				taskScheduler.insertVertex(vertex);
			} else {
				orderManager.insertGivenTotalOrder(vertex);
			}

		}
	}

	@Override
	protected void fireNewUnmappedVertex(MapperDAGVertex vertex) {

		// unmapping a vertex resets the cost of the current vertex
		// and its edges

		// Keeps the total order
		orderManager.remove(vertex, false);

		vertex.getTiming().reset();
		resetCost(vertex.incomingEdges());
		resetCost(vertex.outgoingEdges());

	}

	/**
	 * Asks the time keeper to update timings. Crucial and costly operation.
	 * Depending on the king of timings we want, calls the necessary updates.
	 */
	@Override
	public final void updateTimings() {
		nTimeKeeper.updateTandBLevels();
	}

	@Override
	protected void setEdgeCost(MapperDAGEdge edge) {

		long edgesize = edge.getInit().getDataSize();

		/**
		 * In a Infinite Homogeneous Architecture, each communication is
		 * supposed to be done on the main medium. The communication cost is
		 * simply calculated from the main medium speed.
		 */
		String mainComName = scenario.getSimulationManager()
				.getMainComNodeName();
		ComponentInstance mainCom = DesignTools.getComponentInstance(archi,
				mainComName);

		if (mainCom != null) {

			long cost = (long) (edgesize / ((ComNode) mainCom.getComponent())
					.getSpeed());

			edge.getTiming().setCost(cost);
		} else {
			Float speed = 1f;
			speed = edgesize * speed;
			edge.getTiming().setCost(speed.intValue());
		}
	}

	public EdgeSchedType getEdgeSchedType() {
		return null;
	}
}
