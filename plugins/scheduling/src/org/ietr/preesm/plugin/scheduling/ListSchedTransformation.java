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

package org.ietr.preesm.plugin.scheduling;

import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.IOperator;
import org.ietr.preesm.core.architecture.parser.VLNV;
import org.ietr.preesm.core.architecture.route.MediumRouteStep;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.PreesmException;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.core.codegen.VertexType;
import org.ietr.preesm.plugin.scheduling.listsched.CombListSched;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.OperationDescriptor;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.OperatorDescriptor;
import org.ietr.preesm.plugin.scheduling.listsched.scheduler.AbstractScheduler;
import org.sdf4j.exceptions.CreateCycleException;
import org.sdf4j.exceptions.CreateMultigraphException;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.dag.EdgeAggregate;
import org.sdf4j.model.dag.types.DAGVertexPropertyType;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Class calling a tranformation for communication contentious list scheduling
 * 
 * @author pmu
 */
public class ListSchedTransformation extends AbstractScheduling {

	@Override
	public void transform(SDFGraph algorithm, SDFGraph transformedAlgorithm) {
		// TODO Auto-generated method stub

	}

	@Override
	public TaskResult transform(SDFGraph algorithm,
			MultiCoreArchitecture architecture, TextParameters textParameters,
			IScenario scenario, IProgressMonitor monitor)
			throws PreesmException {

		super.transform(algorithm, architecture, textParameters, scenario,
				monitor);

		System.out
				.println("Communication Contentious List Scheduling Transformation!");
		TaskResult result = new TaskResult();

		CombListSched scheduler = new CombListSched(algorithm, architecture,
				scenario);

		try {
			scheduler.schedule();
		} catch (InvalidExpressionException e) {
			// TODO Auto-generated catch block
			throw (new PreesmException(e.getMessage()));
		}

		result.setDAG(generateDAG(algorithm, architecture, scheduler
				.getBestScheduler()));

		return result;
	}

	private DirectedAcyclicGraph generateDAG(SDFGraph algorithm,
			MultiCoreArchitecture architecture, AbstractScheduler scheduler) {
		// TODO: Generate a DAG which will be used in code generation
		DirectedAcyclicGraph dag = new DirectedAcyclicGraph();

		AlgorithmDescriptor algo = scheduler.getAlgorithm();
		ArchitectureDescriptor archi = scheduler.getArchitecture();

		// Create a virtual routeStep
		VLNV vlnv = new VLNV();
		OperatorDefinition opdf = new OperatorDefinition(vlnv);
		MediumDefinition mediumdf = new MediumDefinition(vlnv);
		Operator sendOp = new Operator("send", opdf);
		Operator receiveOp = new Operator("receive", opdf);
		Medium medium = new Medium("medium", mediumdf);
		MediumRouteStep rs = new MediumRouteStep(sendOp, medium, receiveOp);

		for (ComputationDescriptor indexComputation : algo.getComputations()
				.values()) {
			// Create a vertex of type "task"
			if (indexComputation != algo.getTopComputation()
					&& indexComputation != algo.getBottomComputation()) {
				DAGVertex vertex = new DAGVertex();
				vertex.setName(indexComputation.getName());
				dag.addVertex(vertex);
			}
		}

		for (OperatorDescriptor indexOperator : archi.getAllOperators()
				.values()) {
			int order = 1;
			for (int i = 0; i < indexOperator.getOperations().size(); i++) {
				OperationDescriptor operation = indexOperator.getOperation(i);
				IOperator operator = (IOperator) architecture
						.getComponent(indexOperator.getId());
				if (operation instanceof ComputationDescriptor) {
					DAGVertex vertex = dag.getVertex(operation.getName());
					vertex.getPropertyBean().setValue("vertexType",
							VertexType.task);
					vertex.getPropertyBean().setValue("Operator", operator);
					vertex.getPropertyBean().setValue("SdfReferenceGraph",
							algorithm);
					vertex.setCorrespondingSDFVertex(algorithm
							.getVertex(operation.getName()));
					try {
						vertex.setNbRepeat(new DAGVertexPropertyType(vertex
								.getCorrespondingSDFVertex().getNbRepeat()));
					} catch (InvalidExpressionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					vertex.getPropertyBean().setValue("schedulingOrder", order);
					order++;
				} else if (operation instanceof CommunicationDescriptor) {
					if (operation != scheduler.getTopCommunication()
							&& operation != scheduler.getBottomCommunication()) {
						if (indexOperator.getSendCommunications().contains(
								((CommunicationDescriptor) operation))) {
							// Create a vertex of type "send"
							DAGVertex source = dag
									.getVertex(((CommunicationDescriptor) operation)
											.getOrigin());
							DAGVertex target = new DAGVertex();
							target.setName("send_" + operation.getName());
							dag.addVertex(target);
							try {
								dag.addDAGEdge(source, target);
							} catch (CreateMultigraphException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (CreateCycleException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							target.getPropertyBean().setValue("vertexType",
									VertexType.send);
							target.getPropertyBean().setValue("Operator",
									operator);
							target.getPropertyBean().setValue("routeStep", rs);
							target.getPropertyBean().setValue(
									"SdfReferenceGraph", algorithm);
							target.setCorrespondingSDFVertex(algorithm
									.getVertex(source.getName()));
							try {
								target.setNbRepeat(new DAGVertexPropertyType(
										target.getCorrespondingSDFVertex()
												.getNbRepeat()));
							} catch (InvalidExpressionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// target.getPropertyBean().setValue(
							// "SdfReferenceGraph",
							// algorithm.getVertex(source.getName()));
							target.getPropertyBean().setValue(
									"schedulingOrder", order);
							order++;
						} else if (indexOperator
								.getReceiveCommunications()
								.contains(((CommunicationDescriptor) operation))) {
							// Create a vertex of type "receive"
							DAGVertex source = new DAGVertex();
							source.setName("receive_" + operation.getName());
							dag.addVertex(source);
							DAGVertex target = dag
									.getVertex(((CommunicationDescriptor) operation)
											.getDestination());
							try {
								dag.addDAGEdge(source, target);
							} catch (CreateMultigraphException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (CreateCycleException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							source.getPropertyBean().setValue("vertexType",
									VertexType.receive);
							source.getPropertyBean().setValue("Operator",
									operator);
							source.getPropertyBean().setValue("routeStep", rs);
							source.getPropertyBean().setValue(
									"SdfReferenceGraph", algorithm);
							source.setCorrespondingSDFVertex(algorithm
									.getVertex(target.getName()));
							try {
								source.setNbRepeat(new DAGVertexPropertyType(
										source.getCorrespondingSDFVertex()
												.getNbRepeat()));
							} catch (InvalidExpressionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// source.getPropertyBean().setValue(
							// "SdfReferenceGraph",
							// algorithm.getVertex(target.getName()));
							source.getPropertyBean().setValue(
									"schedulingOrder", order);
							order++;
						}
					}
				}
			}
		}

		for (CommunicationDescriptor indexCommunication : algo
				.getCommunications().values()) {
			if (indexCommunication != scheduler.getTopCommunication()
					&& indexCommunication != scheduler.getBottomCommunication()
					&& algo.getComputation(indexCommunication.getOrigin()) != algo
							.getTopComputation()
					&& algo.getComputation(indexCommunication.getDestination()) != algo
							.getBottomComputation()) {
				// TODO: Create an edge
				if (!indexCommunication.isExist()) {
					DAGVertex source = dag.getVertex(indexCommunication
							.getOrigin());
					DAGVertex target = dag.getVertex(indexCommunication
							.getDestination());
					try {
						dag.addDAGEdge(source, target);
					} catch (CreateMultigraphException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CreateCycleException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DAGEdge edge = dag.getEdge(source, target);
					EdgeAggregate agg = new EdgeAggregate();
					agg.addAll(algorithm.getAllEdges(source
							.getCorrespondingSDFVertex(), target
							.getCorrespondingSDFVertex()));
					edge.setAggregate(agg);
				}
			}
		}

		for (OperatorDescriptor indexOperator : archi.getAllOperators()
				.values()) {
			for (CommunicationDescriptor indexCommunication : indexOperator
					.getSendCommunications()) {
				if (indexCommunication != scheduler.getTopCommunication()
						&& indexCommunication != scheduler
								.getBottomCommunication()) {
					DAGVertex source = dag.getVertex(indexCommunication
							.getOrigin());
					DAGVertex target = dag.getVertex(indexCommunication
							.getDestination());
					DAGVertex sendVertex = dag.getVertex("send_"
							+ indexCommunication.getName());
					DAGVertex receiveVertex = dag.getVertex("receive_"
							+ indexCommunication.getName());
					try {
						dag.addDAGEdge(sendVertex, receiveVertex);
					} catch (CreateMultigraphException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CreateCycleException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DAGEdge sendEdge = dag.getEdge(source, sendVertex);
					DAGEdge edge = dag.getEdge(sendVertex, receiveVertex);
					DAGEdge receiveEdge = dag.getEdge(receiveVertex, target);
					EdgeAggregate agg = new EdgeAggregate();
					agg.addAll(algorithm.getAllEdges(source
							.getCorrespondingSDFVertex(), target
							.getCorrespondingSDFVertex()));
					sendEdge.setAggregate(agg);
					edge.setAggregate(agg);
					receiveEdge.setAggregate(agg);
				}
			}
		}

		if (dag.vertexSet().size() == 0) {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"Can not map a DAG with no vertex.");
		} else {
			PreesmLogger.getLogger().log(Level.INFO, "Conversion finished.");
			PreesmLogger.getLogger().log(
					Level.INFO,
					"mapping a DAG with " + dag.vertexSet().size()
							+ " vertices and " + dag.edgeSet().size()
							+ " edges.");
		}
		return dag;
	}
}
