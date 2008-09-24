/**
 * 
 */
package org.ietr.preesm.plugin.abc.infinitehomogeneous;

import java.util.Iterator;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.MediumDefinition;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.model.dag.DAGEdge;

/**
 * Simulates an architecture having as many cores as necessary to
 * execute one operation on one core. All core have the main operator
 * definition. These cores are all interconnected with media
 * corresponding to the main medium definition.
 *         
 * @author mpelcat   
 */
public class InfiniteHomogeneousAbc extends
		AbstractAbc {

	/**
	 * Constructor of the simulator from a "blank" implantation where every
	 * vertex has not been implanted yet.
	 */
	public InfiniteHomogeneousAbc(MapperDAG dag,
			IArchitecture archi) {
		super(dag, archi);

		// The InfiniteHomogeneousArchitectureSimulator is specifically done
		// to implant all vertices on the main operator definition but consider
		// as many cores as there are tasks.
		implantAllVerticesOnOperator(archi.getMainOperator());

		updateTimings();
	}

	@Override
	protected void fireNewMappedVertex(MapperDAGVertex vertex) {

		Operator effectiveOp = vertex.getImplementationVertexProperty()
				.getEffectiveOperator();

		/*
		 * implanting a vertex sets the cost of the current vertex and its edges
		 * 
		 * As we have an infinite homogeneous architecture, each communication
		 * is done through the unique type of medium
		 */
		if (effectiveOp == Operator.NO_COMPONENT) {
			PreesmLogger.getLogger().severe(
					"implantation of " + vertex.getName() + " failed");
		} else {
			// Setting vertex time
			int vertextime = vertex.getInitialVertexProperty().getTime(
					effectiveOp);
			vertex.getTimingVertexProperty().setCost(vertextime);

			// Setting incoming edges times
			Iterator<DAGEdge> iterator = vertex.incomingEdges()
					.iterator();

			while (iterator.hasNext()) {
				MapperDAGEdge edge = (MapperDAGEdge) iterator.next();

				int edgesize = edge.getInitialEdgeProperty().getDataSize();

				/**
				 * In a Infinite Homogeneous Architecture, each communication is
				 * supposed to be done on the main medium. The communication
				 * cost is simply calculated from the main medium speed.
				 */

				if (archi.getMainMedium() != null) {
					MediumDefinition def = (MediumDefinition) archi
							.getMainMedium().getDefinition();
					Float speed = def.getMediumProperty().getInvSpeed();
					speed = edgesize * speed;
					edge.getTimingEdgeProperty().setCost(speed.intValue());
				} else {

					PreesmLogger
							.getLogger()
							.info(
									"current architecture has no main medium. infinite homogeneous simulator will use default speed");

					Float speed = 1f;
					speed = edgesize * speed;
					edge.getTimingEdgeProperty().setCost(speed.intValue());
				}
			}

			// Setting outgoing edges times
			iterator = vertex.outgoingEdges().iterator();

			while (iterator.hasNext()) {
				MapperDAGEdge edge = (MapperDAGEdge) iterator.next();

				int edgedatasize = edge.getInitialEdgeProperty().getDataSize();

				// medium is considered 1cycle/unit for the moment (test)
				edge.getTimingEdgeProperty().setCost(edgedatasize);
			}
		}
	}

	@Override
	protected void fireNewUnmappedVertex(MapperDAGVertex vertex) {

		Operator effectiveOp = vertex.getImplementationVertexProperty()
				.getEffectiveOperator();

		// unimplanting a vertex resets the cost of the current vertex
		// and its edges
		if (effectiveOp == Operator.NO_COMPONENT) {
			vertex.getTimingVertexProperty().resetCost();

			resetCost(vertex.incomingEdges());
			resetCost(vertex.outgoingEdges());
		} else {
			PreesmLogger.getLogger().severe(
					"unimplementation of " + vertex.getName() + " failed");
		}
	}

	/**
	 * Asks the time keeper to update timings. Crucial and costly operation.
	 * Depending on the king of timings we want, calls the necessary updates.
	 */
	@Override
	protected final void updateTimings() {

		if (dirtyTimings) {

			if (!dirtyVertices.isEmpty()) {
				Iterator<MapperDAGVertex> it = dirtyVertices.iterator();

				while (it.hasNext())
					timekeeper.updateTandBLevels(this.implementation, it.next());
				dirtyVertices.clear();
			} else {
				timekeeper.updateTandBLevels(this.implementation);
			}

			dirtyTimings = false;
		}
	}

	@Override
	protected final void setEdgeCost(MapperDAGEdge edge) {

	}
}
