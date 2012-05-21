/*********************************************************
Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
Karol Desnos

[mpelcat,jnezan,kdesnos]@insa-rennes.fr

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

package org.ietr.preesm.memory.exclusiongraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.dftools.algorithm.iterators.DAGIterator;
import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.workflow.WorkflowException;

import org.ietr.preesm.core.types.BufferAggregate;
import org.ietr.preesm.core.types.BufferProperties;
import org.ietr.preesm.core.types.DataType;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * This class is used to handle the Memory Exclusion Graph
 * 
 * This Graph, created by analyzing a DAG, is composed of:
 * <ul>
 * <li>Vertices which represent the memory needed to transfer data from a Task
 * to another.</li>
 * <li>Undirected edges that signify that two memory transfers might be
 * concurrent, and thus can not share the same resource.</li>
 * </ul>
 * 
 * @author kdesnos
 * 
 */
public class MemoryExclusionGraph extends
		SimpleGraph<MemoryExclusionVertex, DefaultEdge> {

	/**
	 * Mandatory when extending SimpleGraph
	 */
	private static final long serialVersionUID = 6491894138235944107L;

	/**
	 * Backup the vertex adjacent to a given vertex for speed-up purposes.
	 */
	private HashMap<MemoryExclusionVertex, HashSet<MemoryExclusionVertex>> adjacentVerticesBackup;

	/**
	 * Each DAGVertex is associated to a list of MemoryExclusionVertex that have
	 * a precedence relationship with this DAGVertex. All successors of this
	 * DAGVertex will NOT have exclusion with MemoryExclusionVertex in this
	 * list. This list is built along the build of the MemEx, and used for
	 * subsequent updates. We use the name of the DAGVertex as a key.. Because
	 * using the DAG itself seems to be impossible.
	 */
	private HashMap<String, HashSet<MemoryExclusionVertex>> verticesPredecessors;

	/**
	 * Default constructor
	 */
	public MemoryExclusionGraph() {
		super(DefaultEdge.class);
		adjacentVerticesBackup = new HashMap<MemoryExclusionVertex, HashSet<MemoryExclusionVertex>>();
		dataTypes = new HashMap<String, DataType>();
	}

	private Map<String, DataType> dataTypes;

	/**
	 * This method add the node corresponding to the passed edge to the
	 * ExclusionGraph. If the source or targeted vertex isn't a task vertex,
	 * nothing is added. (should not happen)
	 * 
	 * @param edge
	 *            The memory transfer to add.
	 * @return the exclusion graph node created (or null)
	 */
	public MemoryExclusionVertex addNode(DAGEdge edge) {
		// If the target and source vertices are tasks,
		// add a node corresponding to the memory transfer
		// to the exclusion graph. Else, nothing
		MemoryExclusionVertex newNode = null;

		// As the non-task vertices are removed at the beginning of the build
		// function
		// This if statement could be removed.
		if (edge.getSource().getPropertyBean().getValue("vertexType")
				.toString().equals("task")
				&& edge.getTarget().getPropertyBean().getValue("vertexType")
						.toString().equals("task")) {
			newNode = new MemoryExclusionVertex(edge);

			// if datatype is defined, correct the vertex weight
			BufferAggregate buffers = (BufferAggregate) edge.getPropertyBean()
					.getValue("bufferAggregate");
			Iterator<BufferProperties> iter = buffers.iterator();
			int vertexWeight = 0;
			while (iter.hasNext()) {
				BufferProperties properties = iter.next();

				String dataType = properties.getDataType();
				DataType type = this.dataTypes.get(dataType);

				if (type != null) {
					vertexWeight += type.getSize() * properties.getSize();
				} else {
					vertexWeight += properties.getSize();
				}
			}
			
			newNode.setWeight(vertexWeight);

			this.addVertex(newNode);

		}
		return newNode;
	}

	/**
	 * Method to build the complementary graph based on a DirectedAcyclicGraph
	 * 
	 * @param dag
	 *            This DirectedAcyclicGraph is analyzed to create the nodes and
	 *            edges of the complementary MemoryExclusionGraph. This property
	 *            ensures that all preceding nodes of a "merge" node are treated
	 *            before treating the "merge" node. The DAG will be modified by
	 *            this function.
	 * @throws InvalidExpressionException
	 * @throws WorkflowException
	 */
	public void buildComplementaryGraph(DirectedAcyclicGraph dag)
			throws InvalidExpressionException, WorkflowException {
		/*
		 * Declarations & initializations
		 */
		DAGIterator iterDAGVertices = new DAGIterator(dag); // Iterator on DAG
															// vertices
		verticesPredecessors = new HashMap<String, HashSet<MemoryExclusionVertex>>();

		// Remove dag vertex of type other than "task"
		// And identify source vertices (vertices without predecessors)
		HashSet<DAGVertex> nonTaskVertices = new HashSet<DAGVertex>(); // Set of
																		// non-task
																		// vertices
		ArrayList<DAGVertex> sourcesVertices = new ArrayList<DAGVertex>(); // Set
																			// of
																			// source
																			// vertices
		int newOrder = 0;
		while (iterDAGVertices.hasNext()) {
			DAGVertex vert = iterDAGVertices.next();
			if (vert.getPropertyBean().getValue("vertexType").toString()
					.equals("task")) {
				// Set the scheduling Order which will be used as a unique ID
				// for each vertex
				vert.getPropertyBean().setValue("schedulingOrder", newOrder);
				newOrder++;

				if (vert.incomingEdges().size() == 0) {
					sourcesVertices.add(vert);
				}
			} else {
				nonTaskVertices.add(vert);
			}
		}
		dag.removeAllVertices(nonTaskVertices);

		iterDAGVertices = new DAGIterator(dag); // Iterator on DAG vertices

		// Each element of the "predecessors" list corresponds to a DagVertex
		// and
		// stores all its preceding ExclusionGraphNode except those
		// corresponding to incoming edges
		// The unique ID of the DAG vertices (their scheduling order) are used
		// as indexes in this list
		ArrayList<HashSet<MemoryExclusionVertex>> predecessors = new ArrayList<HashSet<MemoryExclusionVertex>>(
				dag.vertexSet().size());

		// Each element of the "incoming" list corresponds to a DAGVertex and
		// store only the ExclusionGraphNode that corresponds to its incoming
		// edges
		// The unique ID of the DAG vertices (their scheduling order) are used
		// as indexes in this list
		ArrayList<HashSet<MemoryExclusionVertex>> incoming = new ArrayList<HashSet<MemoryExclusionVertex>>(
				dag.vertexSet().size());

		// Initialize predecessors with empty HashSets
		for (int i = 0; i < dag.vertexSet().size(); i++) {
			predecessors.add(new HashSet<MemoryExclusionVertex>());
			incoming.add(new HashSet<MemoryExclusionVertex>());
		}

		/*
		 * Part 1: Scan of the DAG in order to: - create Exclusion Graph nodes.
		 * - add exclusion between consecutive Memory Transfer
		 */
		while (iterDAGVertices.hasNext()) // For each vertex of the DAG
		{
			DAGVertex vertexDAG = iterDAGVertices.next(); // Retrieve the vertex
															// to process
			int vertexID = (Integer) vertexDAG.getPropertyBean().getValue(
					"schedulingOrder"); // Retrieve the vertex unique ID

			// For each outgoing edge
			for (DAGEdge edge : vertexDAG.outgoingEdges()) {
				// Add the node to the Exclusion Graph
				MemoryExclusionVertex newNode;
				if ((newNode = this.addNode(edge)) != null) {
					// If a node was added.(It should always be the case)

					// Add inclusions with all predecessors of the current
					// vertex
					HashSet<MemoryExclusionVertex> inclusions = predecessors
							.get(vertexID);
					for (MemoryExclusionVertex inclusion : inclusions) {
						this.addEdge(newNode, inclusion);
					}

					// Add newNode to the incoming list of the consumer of this
					// edge
					incoming.get(
							(Integer) edge.getTarget().getPropertyBean()
									.getValue("schedulingOrder")).add(newNode);

					// Update the predecessor list of the consumer of this edge
					HashSet<MemoryExclusionVertex> predecessor;
					predecessor = predecessors.get((Integer) edge.getTarget()
							.getPropertyBean().getValue("schedulingOrder"));
					predecessor.addAll(inclusions);
					predecessor.addAll(incoming.get(vertexID));
				}
			}
			// Save predecessor list
			predecessors.get(vertexID).addAll(incoming.get(vertexID));
			verticesPredecessors.put(vertexDAG.getName(),
					predecessors.get(vertexID));

			// Clear useless lists
			predecessors.set(vertexID, null);
		}
	}

	/**
	 * Method to build the graph based on a DirectedAcyclicGraph
	 * 
	 * @param dag
	 *            This DirectedAcyclicGraph is analyzed to create the nodes and
	 *            edges of the MemoryExclusionGraph. The DAG used must be the
	 *            output of a scheduling process. This property ensures that all
	 *            preceding nodes of a "merge" node are treated before treating
	 *            the "merge" node. The DAG will be modified by this function.
	 * @throws InvalidExpressionException
	 * @throws WorkflowException
	 */
	public void buildGraph(DirectedAcyclicGraph dag)
			throws InvalidExpressionException, WorkflowException {
		/*
		 * Declarations & initializations
		 */
		DAGIterator iterDAGVertices = new DAGIterator(dag); // Iterator on DAG
															// vertices
		verticesPredecessors = new HashMap<String, HashSet<MemoryExclusionVertex>>();

		// Remove dag vertex of type other than "task"
		// And identify source vertices (vertices without predecessors)
		HashSet<DAGVertex> nonTaskVertices = new HashSet<DAGVertex>(); // Set of
																		// non-task
																		// vertices
		ArrayList<DAGVertex> sourcesVertices = new ArrayList<DAGVertex>(); // Set
																			// of
																			// source
																			// vertices
		int newOrder = 0;
		while (iterDAGVertices.hasNext()) {
			DAGVertex vert = iterDAGVertices.next();
			if (vert.getPropertyBean().getValue("vertexType").toString()
					.equals("task")) {
				// Set the scheduling Order which will be used as a unique ID
				// for each vertex
				vert.getPropertyBean().setValue("schedulingOrder", newOrder);
				newOrder++;

				if (vert.incomingEdges().size() == 0) {
					sourcesVertices.add(vert);
				}
			} else {
				nonTaskVertices.add(vert);
			}
		}
		dag.removeAllVertices(nonTaskVertices);

		iterDAGVertices = new DAGIterator(dag); // Iterator on DAG vertices

		// Each element of the "predecessors" list corresponds to a DagVertex
		// and
		// stores all its preceding ExclusionGraphNode except those
		// corresponding to incoming edges
		// The unique ID of the DAG vertices (their scheduling order) are used
		// as indexes in this list
		ArrayList<HashSet<MemoryExclusionVertex>> predecessors = new ArrayList<HashSet<MemoryExclusionVertex>>(
				dag.vertexSet().size());

		// Each element of the "incoming" list corresponds to a DAGVertex and
		// store only the ExclusionGraphNode that corresponds to its incoming
		// edges
		// The unique ID of the DAG vertices (their scheduling order) are used
		// as indexes in this list
		ArrayList<HashSet<MemoryExclusionVertex>> incoming = new ArrayList<HashSet<MemoryExclusionVertex>>(
				dag.vertexSet().size());

		// Initialize predecessors with empty HashSets
		for (int i = 0; i < dag.vertexSet().size(); i++) {
			predecessors.add(new HashSet<MemoryExclusionVertex>());
			incoming.add(new HashSet<MemoryExclusionVertex>());
		}

		/*
		 * Part 1: Scan of the DAG in order to: - create Exclusion Graph nodes.
		 * - add exclusion between consecutive Memory Transfer
		 */
		while (iterDAGVertices.hasNext()) // For each vertex of the DAG
		{
			DAGVertex vertexDAG = iterDAGVertices.next(); // Retrieve the vertex
															// to process
			int vertexID = (Integer) vertexDAG.getPropertyBean().getValue(
					"schedulingOrder"); // Retrieve the vertex unique ID

			// For each outgoing edge
			for (DAGEdge edge : vertexDAG.outgoingEdges()) {
				// Add the node to the Exclusion Graph
				MemoryExclusionVertex newNode;
				if ((newNode = this.addNode(edge)) != null) {
					// If a node was added.(It should always be the case)

					// Add Exclusions with all non-predecessors of the current
					// vertex
					HashSet<MemoryExclusionVertex> inclusions = predecessors
							.get(vertexID);
					HashSet<MemoryExclusionVertex> exclusions = new HashSet<MemoryExclusionVertex>(
							this.vertexSet());
					exclusions.remove(newNode);
					exclusions.removeAll(inclusions);
					for (MemoryExclusionVertex exclusion : exclusions) {
						this.addEdge(newNode, exclusion);
					}

					// Add newNode to the incoming list of the consumer of this
					// edge
					incoming.get(
							(Integer) edge.getTarget().getPropertyBean()
									.getValue("schedulingOrder")).add(newNode);

					// Update the predecessor list of the consumer of this edge
					HashSet<MemoryExclusionVertex> predecessor;
					predecessor = predecessors.get((Integer) edge.getTarget()
							.getPropertyBean().getValue("schedulingOrder"));
					predecessor.addAll(inclusions);
					predecessor.addAll(incoming.get(vertexID));
				} else {
					// If the node was not added.
					// Should never happen
					throw new WorkflowException(
							"The exclusion graph vertex corresponding to edge "
									+ edge.toString()
									+ " was not added to the graph.");
				}
			}
			// Save predecessor list, and include incoming to it.
			predecessors.get(vertexID).addAll(incoming.get(vertexID));
			verticesPredecessors.put(vertexDAG.getName(),
					predecessors.get(vertexID));

			// Clear useless lists
			predecessors.set(vertexID, null);
		}
	}

	/**
	 * Method to clear the adjacent vertices list. As the adjacent vertices
	 * lists are passed as references, their content might be corrupted if they
	 * are modified by the user of the class. Moreover, if a vertex is removed
	 * from the class without using the removeAllVertices overrode method, the
	 * list will still contain the vertice. Clearing the lists is left to the
	 * user's care. Indeed, a systematic clear to maintain the integrity of the
	 * lists would considerably reduce the performances of some algorithms. A
	 * solution to that problem would be to use a faster implementation of
	 * simpleGraphs that would provide fast methods to retrieve adjacent
	 * neighbors of a vertex !
	 * 
	 */
	public void clearAdjacentVerticesBackup() {
		adjacentVerticesBackup.clear();
	}

	/**
	 * @override
	 */
	public Object clone() {
		Object o = super.clone();
		((MemoryExclusionGraph) o).adjacentVerticesBackup = new HashMap<MemoryExclusionVertex, HashSet<MemoryExclusionVertex>>();

		return o;

	}

	/**
	 * This method is used to access all the neighbors of a given vertex.
	 * 
	 * @param vertex
	 *            the vertex whose neighbors are retrieved
	 * @return a set containing the neighbor vertices
	 */
	public HashSet<MemoryExclusionVertex> getAdjacentVertexOf(
			MemoryExclusionVertex vertex) {
		HashSet<MemoryExclusionVertex> result;

		// If this vertex was previously treated, simply access the backed-up
		// neighbors set.
		if ((result = adjacentVerticesBackup.get(vertex)) != null) {
			return result;
		}

		// Else create the list of neighbors of the vertex
		result = new HashSet<MemoryExclusionVertex>();

		// Add to result all vertices that have an edge with vertex
		Set<DefaultEdge> edges = this.edgesOf(vertex);
		for (DefaultEdge edge : edges) {
			result.add(this.getEdgeSource(edge));
			result.add(this.getEdgeTarget(edge));
		}

		// Remove vertex from result
		result.remove(vertex);

		// Back-up the resulting set
		adjacentVerticesBackup.put(vertex, result);

		// The following lines ensure that the vertices stored in the neighbors
		// list
		// belong to the graph.vertexSet.
		// Indeed, it may happen that several "equal" instances of
		// MemoryExclusionGaphNodes
		// are referenced in the same MemoryExclusionGraph : one in the vertex
		// set and one in the source/target of an edge.
		// If someone retrieves this vertex using the getEdgeSource(edge), then
		// modifies the vertex, the changes
		// might not be applied to the same vertex retrieved in the vertexSet()
		// of the graph.
		// The following lines ensures that the vertices returned in the
		// neighbors lists always belong to the vertexSet().
		HashSet<MemoryExclusionVertex> toAdd = new HashSet<MemoryExclusionVertex>();

		for (MemoryExclusionVertex vert : result) {
			for (MemoryExclusionVertex vertin : this.vertexSet()) {
				if (vert.equals(vertin)) {
					// Correct the reference
					toAdd.add(vertin);
					break;
				}
			}
		}

		result.clear();
		result.addAll(toAdd);

		return result;
	}

	/**
	 * Get the complementary graph of the exclusion graph. The complementary
	 * graph possess the same nodes but the complementary edges. i.e. if there
	 * is an edge between vi and vj in the exclusion graph, there will be no
	 * edge in the complementary.
	 * 
	 * @return
	 */
	public MemoryExclusionGraph getComplementary() {
		// Create a new Memory Exclusion Graph
		MemoryExclusionGraph result = new MemoryExclusionGraph();
		// Copy the vertices of the current graph
		for (MemoryExclusionVertex vertex : this.vertexSet()) {
			result.addVertex(vertex);
		}
		// Retrieve the vertices list
		MemoryExclusionVertex[] vertices = this.vertexSet().toArray(
				new MemoryExclusionVertex[0]);
		// For each pair of vertex, check if the corresponding edge exists in
		// the current graph.
		// If not, add an edge in the complementary graph
		for (int i = 0; i < this.vertexSet().size(); i++) {
			for (int j = i + 1; j < this.vertexSet().size(); j++) {
				if (!this.containsEdge(vertices[i], vertices[j])) {
					result.addEdge(vertices[i], vertices[j]);
				}
			}
		}
		return result;
	}

	/**
	 * @override
	 */
	public boolean removeAllVertices(
			Collection<? extends MemoryExclusionVertex> arg0) {
		boolean result = super.removeAllVertices(arg0);

		for (HashSet<MemoryExclusionVertex> backup : adjacentVerticesBackup
				.values()) {
			result |= backup.removeAll(arg0);
		}
		return result;
	}

	/**
	 * This function prepare the dag in order to update a MemEx that takes
	 * scheduling info into account.
	 * 
	 * @param dag
	 *            the dag to prepare (will not be modified)
	 */
	public void updateWithSchedule(DirectedAcyclicGraph dag) {

		// This map is used along the scan of the vertex of the dag.
		// Its purpose is to store the last vertex scheduled on each
		// component. This way, when a new vertex is executed on this
		// instance is encountered, an edge can be added between it and
		// the previous one.
		HashMap<ComponentInstance, DAGVertex> lastVerticesScheduled;
		lastVerticesScheduled = new HashMap<ComponentInstance, DAGVertex>();

		// Same a verticesPredecessors but only store predecessors that results
		// from scheduling info
		HashMap<String, HashSet<MemoryExclusionVertex>> newVerticesPredecessors;
		newVerticesPredecessors = new HashMap<String, HashSet<MemoryExclusionVertex>>();

		DAGIterator iterDAGVertices = new DAGIterator(dag); // Iterator on DAG
															// vertices

		// Create an array list of the DAGVertices, in scheduling order.
		// As the DAG are scanned in following the precedence order, the
		// computation needed to sort the list should not be too heavy.
		HashMap<Integer, DAGVertex> verticesMap = new HashMap<Integer, DAGVertex>();

		while (iterDAGVertices.hasNext()) {
			DAGVertex currentVertex = iterDAGVertices.next();

			if (currentVertex.getPropertyBean().getValue("vertexType")
					.toString().equals("task")) {
				int schedulingOrder = (Integer) currentVertex.getPropertyBean()
						.getValue("schedulingOrder");
				verticesMap.put(schedulingOrder, currentVertex);
			}
		}
		ArrayList<Integer> schedulingOrders = new ArrayList<Integer>(
				verticesMap.keySet());
		Collections.sort(schedulingOrders);

		// Scan the vertices in scheduling order
		for (int order : schedulingOrders) {
			DAGVertex currentVertex = verticesMap.get(order);

			// retrieve new predecessor list, if any.
			// else, create an empty one
			HashSet<MemoryExclusionVertex> newPredecessors = newVerticesPredecessors
					.get(currentVertex.getName());
			if (newPredecessors == null) {
				newPredecessors = new HashSet<MemoryExclusionVertex>();
				newVerticesPredecessors.put(currentVertex.getName(),
						newPredecessors);
			}

			// Retrieve component
			ComponentInstance comp = (ComponentInstance) currentVertex
					.getPropertyBean().getValue("Operator");

			// Retrieve last DAGVertex executed on this component
			DAGVertex lastScheduled = lastVerticesScheduled.get(comp);

			// If this is not the first time this component is encountered
			if (lastScheduled != null) {
				// update new predecessors of current vertex
				// with all predecessor (new and not new) of previous
				// DAGVertex executed on this component.
				newPredecessors.addAll(newVerticesPredecessors
						.get(lastScheduled.getName()));
				newPredecessors.addAll(verticesPredecessors.get(lastScheduled
						.getName()));
				// "old" predecessors will be excluded later
			}
			// Save currentVertex as lastScheduled on this component
			lastVerticesScheduled.put(comp, currentVertex);

			// Exclude all "old" predecessors from "new" list
			newPredecessors.removeAll(verticesPredecessors.get(currentVertex
					.getName()));

			if (!newPredecessors.isEmpty()) {
				// Remove exclusion between ExclusionVertices corresponding
				// to
				// outgoing edges, and ExclusionVertices in newPredecessors
				// list
				for (DAGEdge outgoingEdge : currentVertex.outgoingEdges()) {
					if (outgoingEdge.getTarget().getPropertyBean()
							.getValue("vertexType").toString().equals("task")) {
						MemoryExclusionVertex edgeVertex = new MemoryExclusionVertex(
								outgoingEdge);
						for (MemoryExclusionVertex newPredecessor : newPredecessors) {
							this.removeEdge(edgeVertex, newPredecessor);
						}

						// Update newPredecessor list of successors
						// DAGVertices
						HashSet<MemoryExclusionVertex> successorPredecessor;
						successorPredecessor = newVerticesPredecessors
								.get(outgoingEdge.getTarget().getName());
						if (successorPredecessor == null) {
							// if successor did not have a new predecessor
							// list, create one
							successorPredecessor = new HashSet<MemoryExclusionVertex>();
							newVerticesPredecessors.put(outgoingEdge
									.getTarget().getName(),
									successorPredecessor);
						}
						successorPredecessor.addAll(newPredecessors);
					}
				}
			}
		}

	}

	/**
	 * This method remove node A from the graph if:<br>
	 * - Node A and node B are <b>NOT</b> linked by an edge.<br>
	 * - Nodes A and B have ALL their neighbors in common.<br>
	 * - Nodes A has a lighter weight than node B.<br>
	 * Applying this method to an exclusion graph before a
	 * MaximumWeightCliqueSolver is executed remove nodes that will never be
	 * part of the maximum weight clique.<br>
	 * <br>
	 * This method also clears the adjacentverticesBackup lists.
	 * 
	 * @return A list of merged vertices
	 * @deprecated Not used anywhere
	 */
	public void removeLightestEquivalentNodes() {

		// Retrieve the list of nodes of the gaph
		ArrayList<MemoryExclusionVertex> nodes = new ArrayList<MemoryExclusionVertex>(
				this.vertexSet());

		// Sort it in descending order of weights
		Collections.sort(nodes, Collections.reverseOrder());

		HashSet<MemoryExclusionVertex> fusionned = new HashSet<MemoryExclusionVertex>();

		// Look for a pair of nodes with the properties exposed in method
		// comments
		for (MemoryExclusionVertex node : nodes) {
			if (!fusionned.contains(node)) {
				HashSet<MemoryExclusionVertex> nonAdjacentSet = new HashSet<MemoryExclusionVertex>(
						this.vertexSet());
				nonAdjacentSet.removeAll(getAdjacentVertexOf(node));
				nonAdjacentSet.remove(node);

				for (MemoryExclusionVertex notNeighbor : nonAdjacentSet) {
					if (getAdjacentVertexOf(notNeighbor).size() == getAdjacentVertexOf(
							node).size()) {
						if (getAdjacentVertexOf(notNeighbor).containsAll(
								getAdjacentVertexOf(node))) {

							// Keep only the one with the max weight
							fusionned.add(notNeighbor);

						}
					}
				}
				this.removeAllVertices(fusionned);
			}
		}
		adjacentVerticesBackup = new HashMap<MemoryExclusionVertex, HashSet<MemoryExclusionVertex>>();
	}

	/**
	 * This method is used to associate a map of data types to the memex graph.
	 * This map will be used when building the graph to give their weight to the
	 * graph vertices.
	 * 
	 * @param dataTypes
	 *            the map of DataType
	 */
	public void setDataTypes(Map<String, DataType> dataTypes) {
		if (dataTypes != null) {
			this.dataTypes = dataTypes;
		}

	}
}
