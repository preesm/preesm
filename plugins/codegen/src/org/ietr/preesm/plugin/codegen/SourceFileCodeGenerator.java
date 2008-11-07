package org.ietr.preesm.plugin.codegen;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.codegen.Buffer;
import org.ietr.preesm.core.codegen.BufferAllocation;
import org.ietr.preesm.core.codegen.CommunicationThreadDeclaration;
import org.ietr.preesm.core.codegen.ComputationThreadDeclaration;
import org.ietr.preesm.core.codegen.DataType;
import org.ietr.preesm.core.codegen.SchedulingOrderComparator;
import org.ietr.preesm.core.codegen.SourceFile;
import org.ietr.preesm.core.codegen.VertexType;
import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;
import org.ietr.preesm.core.codegen.sdfProperties.BufferProperties;
import org.ietr.preesm.core.log.PreesmLogger;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.dag.DirectedAcyclicGraph;

public class SourceFileCodeGenerator {

	SourceFile file;

	public SourceFileCodeGenerator(SourceFile file) {
		this.file = file;
	}

	/**
	 * Buffers belonging to SDF vertices in the given set are allocated here.
	 */
	public void allocateBuffers(Set<DAGVertex> ownVertices) {
		Iterator<DAGVertex> vIterator = ownVertices.iterator();

		// Iteration on own buffers
		while (vIterator.hasNext()) {
			DAGVertex vertex = vIterator.next();

			// Allocating all input buffers of vertex
			allocateVertexBuffers(vertex, true);

			// Allocating all output buffers of vertex
			allocateVertexBuffers(vertex, false);
		}
	}

	/**
	 * Allocates all the buffers retrieved from a given buffer aggregate. The
	 * boolean isInputBuffer is true if the aggregate belongs to an incoming
	 * edge and false if the aggregate belongs to an outgoing edge
	 */
	public void allocateEdgeBuffers(DAGEdge edge, boolean isInputBuffer) {

		BufferAggregate agg = (BufferAggregate) edge.getPropertyBean()
				.getValue(BufferAggregate.propertyBeanName);

		if (agg != null) {

			// allocates the aggregate
			Iterator<BufferProperties> iterator = agg.iterator();

			while (iterator.hasNext()) {
				BufferProperties def = iterator.next();

				// Creating the buffer
				Buffer buf = new Buffer(edge.getSource().getName(), edge
						.getTarget().getName(), def.getSourceOutputPortID(),
						def.getDestInputPortID(), def.getSize(), new DataType(
								def.getDataType()), agg);

				BufferAllocation allocation = new BufferAllocation(buf);

				// Adding the buffer allocation
				file.addBuffer(allocation);
			}
		} else {
			PreesmLogger.getLogger().log(
					Level.FINE,
					"No aggregate for edge " + edge.getSource().getId()
							+ edge.getTarget().getId());
		}
	}

	/**
	 * Route steps are allocated here. A route steps means that a receive and a
	 * send are called successively. The receive output is allocated.
	 */
	public void allocateRouteSteps(Set<DAGVertex> comVertices) {

		Iterator<DAGVertex> vIterator = comVertices.iterator();

		// Iteration on own buffers
		while (vIterator.hasNext()) {
			DAGVertex vertex = vIterator.next();

			if (VertexType.isIntermediateReceive(vertex)) {
				allocateVertexBuffers(vertex, false);
			}
		}
	}

	/**
	 * Allocates buffers belonging to vertex. If isInputBuffer is true,
	 * allocates the input buffers, otherwise allocates output buffers.
	 */
	public void allocateVertexBuffers(DAGVertex vertex, boolean isInputBuffer) {
		Iterator<DAGEdge> eIterator;
		Set<DAGEdge> edgeSet;

		if (isInputBuffer) {
			edgeSet = new HashSet<DAGEdge>(vertex.getBase().incomingEdgesOf(
					vertex));
			// Removes edges between two operators
			removeInterEdges(edgeSet);
		} else {
			edgeSet = new HashSet<DAGEdge>(vertex.getBase().outgoingEdgesOf(
					vertex));
			// Removes edges between two operators
			removeInterEdges(edgeSet);
		}

		eIterator = edgeSet.iterator();

		// Iteration on all the edges of each vertex belonging to ownVertices
		while (eIterator.hasNext()) {
			DAGEdge edge = eIterator.next();

			allocateEdgeBuffers(edge, isInputBuffer);
		}
	}

	/**
	 * Fills itself from an SDF and an architecture
	 */
	public void generateSource(DirectedAcyclicGraph algorithm,
			MultiCoreArchitecture architecture) {
		// Gets the task vertices allocated to the current operator in
		// scheduling order
		SortedSet<DAGVertex> ownTaskVertices = getOwnVertices(algorithm,
				VertexType.task);

		// Gets the communication vertices allocated to the current operator in
		// scheduling order
		SortedSet<DAGVertex> ownCommunicationVertices = getOwnVertices(
				algorithm, VertexType.send);
		ownCommunicationVertices.addAll(getOwnVertices(algorithm,
				VertexType.receive));

		// Buffers defined as global variables are retrieved here. They are
		// added globally to the file
		allocateBuffers(ownTaskVertices);

		// Allocation of route step buffers
		allocateRouteSteps(ownCommunicationVertices);

		// Creating computation thread in which all SDF function calls will be
		// placed
		ComputationThreadDeclaration computationThread = new ComputationThreadDeclaration(
				file);
		file.addThread(computationThread);

		// Adding all function calls corresponding do computation vertices
		computationThread.addUserFunctionCalls(ownTaskVertices);

		// Creating communication where communication processes are launched
		CommunicationThreadDeclaration communicationThread = new CommunicationThreadDeclaration(
				file);
		file.addThread(communicationThread);

		// Adds the code for send operations
		communicationThread.addSendsAndReceives(ownCommunicationVertices);

		// Adding all function calls corresponding do computation vertices
		computationThread.addSemaphorePends(ownTaskVertices);
		communicationThread.addSemaphores(ownCommunicationVertices);

		// Allocates the semaphores globally
		file.getSemaphoreContainer().allocateSemaphores();
	}

	/**
	 * Gets every task vertices allocated to the current operator in their
	 * scheduling order
	 */
	public SortedSet<DAGVertex> getOwnVertices(DirectedAcyclicGraph algorithm,
			VertexType currentType) {

		ConcurrentSkipListSet<DAGVertex> schedule = new ConcurrentSkipListSet<DAGVertex>(
				new SchedulingOrderComparator());
		Iterator<DAGVertex> iterator = algorithm.vertexSet().iterator();

		while (iterator.hasNext()) {
			DAGVertex vertex = iterator.next();

			// retrieving the operator where the vertex is allocated
			Operator vertexOperator = (Operator) vertex.getPropertyBean()
					.getValue(Operator.propertyBeanName);

			// retrieving the type of the vertex
			VertexType vertexType = (VertexType) vertex.getPropertyBean()
					.getValue(VertexType.propertyBeanName);

			// If the vertex is allocated on the current operator, we add it to
			// the set in scheduling order
			if (vertexOperator != null
					&& vertexOperator.equals(file.getOperator())
					&& vertexType != null && vertexType.equals(currentType)) {
				schedule.add(vertex);
			}
		}

		return schedule;
	}

	public void removeInterEdges(Set<DAGEdge> edgeSet) {
		Iterator<DAGEdge> eIterator = edgeSet.iterator();

		while (eIterator.hasNext()) {
			DAGEdge edge = eIterator.next();
			if (!edge.getSource().getPropertyBean().getValue(
					Operator.propertyBeanName).equals(
					edge.getTarget().getPropertyBean().getValue(
							Operator.propertyBeanName)))
				eIterator.remove();
		}
	}

}
