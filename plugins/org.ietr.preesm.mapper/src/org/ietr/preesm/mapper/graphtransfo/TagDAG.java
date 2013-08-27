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

package org.ietr.preesm.mapper.graphtransfo;

import java.util.Iterator;

import net.sf.dftools.algorithm.model.AbstractEdge;
import net.sf.dftools.algorithm.model.PropertyBean;
import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.architecture.slam.Design;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.types.BufferAggregate;
import org.ietr.preesm.core.types.BufferProperties;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.core.types.VertexType;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.abc.edgescheduling.AbstractEdgeSched;
import org.ietr.preesm.mapper.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.abc.route.CommunicationRouter;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.ReceiveVertex;
import org.ietr.preesm.mapper.model.special.SendVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;

/**
 * Tags an SDF with the implementation information necessary for code
 * generation, and DAG exporting
 * 
 * @author pmenuet
 * @author mpelcat
 */
public class TagDAG {

	/**
	 * Main for test
	 */
	public static void main(String[] args) {

	}

	/**
	 * Constructor
	 */
	public TagDAG() {
		super();
	}

	/**
	 * tag adds the send and receive operations necessary to the code
	 * generation. It also adds the necessary properies.
	 * 
	 * @throws InvalidExpressionException
	 */
	public void tag(MapperDAG dag, Design architecture,
			PreesmScenario scenario, IAbc simu, EdgeSchedType edgeSchedType)
			throws InvalidExpressionException {

		PropertyBean bean = dag.getPropertyBean();
		bean.setValue(ImplementationPropertyNames.Graph_AbcReferenceType,
				simu.getType());
		bean.setValue(ImplementationPropertyNames.Graph_EdgeSchedReferenceType,
				edgeSchedType);
		bean.setValue(ImplementationPropertyNames.Graph_SdfReferenceGraph,
				dag.getReferenceSdfGraph());

		addSendReceive(dag, architecture, scenario);
		addProperties(dag, simu);
		addAllAggregates(dag, scenario);
	}

	/**
	 * Adds send and receive without scheduling them
	 */
	public void addSendReceive(MapperDAG dag, Design architecture,
			PreesmScenario scenario) {

		OrderManager orderMgr = new OrderManager(architecture);
		orderMgr.reconstructTotalOrderFromDAG(dag);

		CommunicationRouter comRouter = new CommunicationRouter(architecture,
				scenario, dag, AbstractEdgeSched.getInstance(
						EdgeSchedType.Simple, orderMgr), orderMgr);
		comRouter.routeAll(dag, CommunicationRouter.sendReceiveType);
		orderMgr.tagDAG(dag);
	}

	public void addProperties(MapperDAG dag, IAbc simu) {

		MapperDAGVertex currentVertex;

		Iterator<DAGVertex> iter = dag.vertexSet().iterator();

		// Updates graph time
		if (simu instanceof LatencyAbc) {
			((LatencyAbc) simu).updateTimings();
		}

		// Tagging the vertices with informations for code generation
		while (iter.hasNext()) {
			currentVertex = (MapperDAGVertex) iter.next();
			PropertyBean bean = currentVertex.getPropertyBean();

			if (currentVertex instanceof SendVertex) {

				MapperDAGEdge incomingEdge = (MapperDAGEdge) ((SendVertex) currentVertex)
						.incomingEdges().toArray()[0];

				// Setting the vertex type
				bean.setValue(ImplementationPropertyNames.Vertex_vertexType,
						VertexType.SEND);

				// Setting the operator on which vertex is executed
				bean.setValue(ImplementationPropertyNames.Vertex_Operator,
						((SendVertex) currentVertex).getRouteStep().getSender());

				// Setting the medium transmitting the current data
				AbstractRouteStep sendRs = ((SendVertex) currentVertex)
						.getRouteStep();
				bean.setValue(
						ImplementationPropertyNames.SendReceive_routeStep,
						sendRs);

				// Setting the size of the transmitted data
				bean.setValue(ImplementationPropertyNames.SendReceive_dataSize,
						incomingEdge.getInit().getDataSize());

				// Setting the name of the data sending vertex
				bean.setValue(ImplementationPropertyNames.Send_senderGraphName,
						incomingEdge.getSource().getName());

				// Setting the address of the operator on which vertex is
				// executed
				String baseAddress = DesignTools
						.getParameter(((SendVertex) currentVertex)
								.getRouteStep().getSender(),
								DesignTools.OPERATOR_BASE_ADDRESS);

				if (baseAddress != null) {
					bean.setValue(
							ImplementationPropertyNames.SendReceive_Operator_address,
							baseAddress);
				}

			} else if (currentVertex instanceof ReceiveVertex) {

				MapperDAGEdge outgoingEdge = (MapperDAGEdge) ((ReceiveVertex) currentVertex)
						.outgoingEdges().toArray()[0];

				// Setting the vertex type
				bean.setValue(ImplementationPropertyNames.Vertex_vertexType,
						VertexType.RECEIVE);

				// Setting the operator on which vertex is executed
				bean.setValue(ImplementationPropertyNames.Vertex_Operator,
						((ReceiveVertex) currentVertex).getRouteStep()
								.getReceiver());

				// Setting the medium transmitting the current data
				AbstractRouteStep rcvRs = ((ReceiveVertex) currentVertex)
						.getRouteStep();
				bean.setValue(
						ImplementationPropertyNames.SendReceive_routeStep,
						rcvRs);

				// Setting the size of the transmitted data
				bean.setValue(ImplementationPropertyNames.SendReceive_dataSize,
						outgoingEdge.getInit().getDataSize());

				// Setting the name of the data receiving vertex
				bean.setValue(
						ImplementationPropertyNames.Receive_receiverGraphName,
						outgoingEdge.getTarget().getName());

				// Setting the address of the operator on which vertex is
				// executed
				String baseAddress = DesignTools.getParameter(
						((ReceiveVertex) currentVertex).getRouteStep()
								.getReceiver(), "BaseAddress");

				if (baseAddress != null) {
					bean.setValue(
							ImplementationPropertyNames.SendReceive_Operator_address,
							baseAddress);
				}
			} else {

				// Setting the operator on which vertex is executed
				bean.setValue(ImplementationPropertyNames.Vertex_Operator,
						currentVertex.getMapping()
								.getEffectiveOperator());

				// Setting the vertex type
				bean.setValue(ImplementationPropertyNames.Vertex_vertexType,
						VertexType.TASK);

				bean.setValue(
						ImplementationPropertyNames.Vertex_originalVertexId,
						currentVertex.getCorrespondingSDFVertex().getId());

				// Setting the task duration
				ComponentInstance effectiveOperator = currentVertex
						.getMapping()
						.getEffectiveOperator();
				int singleRepeatTime = currentVertex.getInit()
						.getTime(effectiveOperator);
				int nbRepeat = currentVertex.getInit()
						.getNbRepeat();
				int totalTime = nbRepeat * singleRepeatTime;
				bean.setValue(ImplementationPropertyNames.Task_duration,
						totalTime);

				// Tags the DAG with vertex starttime when possible
				if (simu instanceof LatencyAbc) {
					long startTime = ((LatencyAbc) simu).getTLevel(
							currentVertex, false);
					bean.setValue(ImplementationPropertyNames.Start_time,
							startTime);
				}
			}

			// Setting the scheduling total order
			bean.setValue(ImplementationPropertyNames.Vertex_schedulingOrder,
					currentVertex.getTotalOrder());
		}
	}

	/**
	 * Loop on the edges to add aggregates.
	 * 
	 * @throws InvalidExpressionException
	 * @throws InvalidExpressionException
	 */
	public void addAllAggregates(MapperDAG dag, PreesmScenario scenario)
			throws InvalidExpressionException {

		MapperDAGEdge edge;

		Iterator<DAGEdge> iter = dag.edgeSet().iterator();

		// Tagging the vertices with informations for code generation
		while (iter.hasNext()) {
			edge = (MapperDAGEdge) iter.next();

			if (edge.getSource() instanceof TransferVertex
					|| edge.getTarget() instanceof TransferVertex) {
				addAggregateFromSDF(edge, scenario);
			} else {
				addAggregateFromSDF(edge, scenario);
			}
		}
	}

	/**
	 * Aggregate is imported from the SDF edge. An aggregate in SDF is a set of
	 * sdf edges that were merged into one DAG edge.
	 * 
	 * @throws InvalidExpressionException
	 */
	public void addAggregateFromSDF(MapperDAGEdge edge, PreesmScenario scenario)
			throws InvalidExpressionException {

		BufferAggregate agg = new BufferAggregate();

		// Iterating the SDF aggregates
		for (AbstractEdge<SDFGraph, SDFAbstractVertex> aggMember : edge
				.getAggregate()) {
			SDFEdge sdfAggMember = (SDFEdge) aggMember;

			DataType dataType = scenario.getSimulationManager().getDataType(
					sdfAggMember.getDataType().toString());
			BufferProperties props = new BufferProperties(dataType,
					sdfAggMember.getSourceInterface().getName(), sdfAggMember
							.getTargetInterface().getName(), sdfAggMember
							.getProd().intValue());

			agg.add(props);
		}
		edge.getPropertyBean().setValue(BufferAggregate.propertyBeanName, agg);
	}

}
