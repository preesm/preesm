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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.ietr.dftools.algorithm.iterators.DAGIterator;
import org.ietr.dftools.algorithm.model.CloneableProperty;
import org.ietr.dftools.algorithm.model.PropertyBean;
import org.ietr.dftools.algorithm.model.PropertyFactory;
import org.ietr.dftools.algorithm.model.PropertySource;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFEndVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFInitVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.core.types.BufferAggregate;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
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
		SimpleGraph<MemoryExclusionVertex, DefaultEdge> implements
		PropertySource {

	/**
	 * Mandatory when extending SimpleGraph
	 */
	private static final long serialVersionUID = 6491894138235944107L;

	/**
	 * Property to store a {@link Map} corresponding to the allocation of the
	 * {@link DAGEdge}.
	 */
	public static final String DAG_EDGE_ALLOCATION = "dag_edges_allocation";

	public static final String DAG_FIFO_ALLOCATION = "fifo_allocation";

	public static final String WORKING_MEM_ALLOCATION = "working_mem_allocation";

	public static final String SOURCE_DAG = "source_dag";
	
	/**
	 * Property to store the merged memory objects resulting from the script
	 * processing. The stored object is a:<br>
	 * <code> 
	 * Map&lt;MemoryExclusionVertex,Set&ltMemoryExclusionVertex&gt;&gt;
	 * </code><br>
	 * <br>
	 * This {@link Map} associates of {@link MemoryExclusionVertex} that contain
	 * merged {@link MemoryExclusionVertex} to the {@link Set} of contained
	 * {@link MemoryExclusionVertex}
	 * 
	 */
	public static final String HOST_MEMORY_OBJECT_PROPERTY = "host_memory_objects";

	/**
	 * Property to store an {@link Integer} corresponding to the amount of
	 * memory allocated.
	 */
	public static final String ALLOCATED_MEMORY_SIZE = "allocated_memory_size";

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
	 * using the DAG itself seems to be impossible.<br>
	 * If there are {@link MemoryExclusionVertex} corresponding to the working
	 * memory of {@link DAGVertex}, they will be added to the predecessor list
	 * of this vertex.
	 */
	private HashMap<String, HashSet<MemoryExclusionVertex>> verticesPredecessors;

	/**
	 * This {@link Map} is used to identify and store the list of
	 * {@link MemoryExclusionVertex Memory Exclusion Vertices} which are
	 * involved in an implode or an explode operation in the
	 * {@link DirectedAcyclicGraph DAG}. This information is stored to make it
	 * possible to process those vertices differently depending on the context.
	 * For example, when looking for the MaximumWeightClique in the graph, each
	 * vertex should be considered as a distinct memory object. On the contrary,
	 * when trying to allocate the MemEx Graph in memory, all memory objects of
	 * an implode/explode operation can (and must, for the codegen) be merged
	 * into a single memory object, in order to maximize the locality.
	 */
	// private HashMap<String, HashSet<MemoryExclusionVertex>>
	// implodeExplodeMap;

	/**
	 * {@link DirectedAcyclicGraph DAG} {@link DAGVertex vertices} in the
	 * scheduling order retrieved in
	 * {@link #updateWithSchedule(DirectedAcyclicGraph)}.
	 */
	protected List<DAGVertex> dagVerticesInSchedulingOrder = null;

	/**
	 * The {@link PropertyBean} that stores the properties of the
	 * {@link MemoryExclusionGraph}.
	 */
	protected PropertyBean properties;

	@SuppressWarnings("serial")
	protected static List<String> public_properties = new ArrayList<String>() {
		{
		}
	};

	/**
	 * Default constructor
	 */
	public MemoryExclusionGraph() {
		super(DefaultEdge.class);
		properties = new PropertyBean();
		adjacentVerticesBackup = new HashMap<MemoryExclusionVertex, HashSet<MemoryExclusionVertex>>();
	}

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
		// This if statement could probably be removed. (I keep it just in case)
		if (edge.getSource().getPropertyBean().getValue("vertexType")
				.toString().equals("task")
				&& edge.getTarget().getPropertyBean().getValue("vertexType")
						.toString().equals("task")) {
			newNode = new MemoryExclusionVertex(edge);

			boolean added = this.addVertex(newNode);
			// If false, this means that an equal node is already in the MemEx..
			// somehow..
			if (added == false) {
				// This may come from several edges belonging to an implodeSet
				System.out.println("Vertex not added : " + newNode.toString());
				newNode = null;
			}

		}
		return newNode;
	}

	@Override
	public DefaultEdge addEdge(MemoryExclusionVertex arg0,
			MemoryExclusionVertex arg1) {
		Set<MemoryExclusionVertex> set0 = adjacentVerticesBackup.get(arg0);
		if (set0 != null)
			set0.add(arg1);
		Set<MemoryExclusionVertex> set1 = adjacentVerticesBackup.get(arg1);
		if (set1 != null)
			set1.add(arg0);
		return super.addEdge(arg0, arg1);
	};

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

		final String localOrdering = "memExBuildingLocalOrdering";

		/*
		 * Declarations & initializations
		 */
		DAGIterator iterDAGVertices = new DAGIterator(dag); // Iterator on DAG
															// vertices
		// Be careful, DAGiterator does not seem to work well if dag is
		// modified throughout the iteration.
		// That's why we use first copy the ordered dag vertex set.
		LinkedHashSet<DAGVertex> dagVertices = new LinkedHashSet<DAGVertex>(dag
				.vertexSet().size());
		while (iterDAGVertices.hasNext()) {
			DAGVertex vert = iterDAGVertices.next();
			dagVertices.add(vert);
		}

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

		for (DAGVertex vert : dagVertices) {
			boolean isTask = vert.getPropertyBean().getValue("vertexType")
					.toString().equals("task");
			String vertKind = "";

			// Only task vertices have a kind
			if (isTask) {
				vertKind = vert.getPropertyBean().getValue("kind").toString();
			}

			if (vertKind.equals("dag_vertex")
					|| vertKind.equals("dag_broadcast_vertex") // roundbffers
																// covered
					|| vertKind.equals("dag_init_vertex")
					|| vertKind.equals("dag_end_vertex")
					|| vertKind.equals("dag_fork_vertex")
					|| vertKind.equals("dag_join_vertex")) {
				// If the dagVertex is a task (except implode/explode task), set
				// the scheduling Order which will be used as a unique ID for
				// each vertex
				vert.getPropertyBean().setValue(localOrdering, newOrder);
				newOrder++;

				if (vert.incomingEdges().size() == 0) {
					sourcesVertices.add(vert);
				}
			} else {
				// Send/Receive
				nonTaskVertices.add(vert);
			}
		}
		dag.removeAllVertices(nonTaskVertices);
		dagVertices.removeAll(nonTaskVertices);

		// iterDAGVertices = new DAGIterator(dag); // Iterator on DAG vertices

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

		// Scan of the DAG in order to:
		// - create Exclusion Graph nodes.
		// - add exclusion between consecutive Memory Transfer
		for (DAGVertex vertexDAG : dagVertices) // For each vertex of the DAG
		{
			// Processing is done in the following order:
			// 1. Fork/Join/Broadcast/RoundBuffer specific processing
			// 2. Working Memory specific Processing
			// 3. Outgoing Edges processing

			// Retrieve the vertex to process
			int vertexID = (Integer) vertexDAG.getPropertyBean().getValue(
					localOrdering); // Retrieve the vertex unique ID

			// 1. Fork/Join/Broadcast/Roundbuffer specific processing
			// Not usable yet ! Does not work because output edges are not
			// allocated
			// in the same order.. so overwrite are possible : inout needed here
			// !

			// String vertKind = vertexDAG.getPropertyBean().getValue("kind")
			// .toString();
			// if (vertKind.equals("dag_broadcast_vertex") // includes
			// roundbuffers
			// || vertKind.equals("dag_fork_vertex")
			// || vertKind.equals("dag_join_vertex")) {
			// // Add the incoming edges to the predecessor list so that there
			// // is no exclusion between input and output for these buffers
			// predecessors.get(vertexID).addAll(incoming.get(vertexID));
			// incoming.get(vertexID).clear();
			// }

			// Implicit Else if: broadcast/fork/join/roundBuffer have no working
			// mem
			// 2. Working Memory specific Processing
			// If the current vertex has some working memory, create the
			// associated MemoryExclusionGraphVertex
			Integer wMem = (Integer) vertexDAG.getCorrespondingSDFVertex()
					.getPropertyBean().getValue("working_memory");
			if (wMem != null) {
				MemoryExclusionVertex workingMemoryNode = new MemoryExclusionVertex(
						vertexDAG.getName(), vertexDAG.getName(), wMem);
				this.addVertex(workingMemoryNode);
				// Currently, there is no special alignment for working memory.
				// So we always assume a unitary typesize.
				workingMemoryNode.setPropertyValue(
						MemoryExclusionVertex.TYPE_SIZE, 1);

				// Add Exclusions with all non-predecessors of the current
				// vertex
				HashSet<MemoryExclusionVertex> inclusions = predecessors
						.get(vertexID);
				HashSet<MemoryExclusionVertex> exclusions = new HashSet<MemoryExclusionVertex>(
						this.vertexSet());
				exclusions.remove(workingMemoryNode);
				exclusions.removeAll(inclusions);
				for (MemoryExclusionVertex exclusion : exclusions) {
					this.addEdge(workingMemoryNode, exclusion);
				}

				// Add the node to the "incoming" list of the DAGVertex.
				// Like incoming edges, the working memory must have
				// exclusions
				// with all outgoing edges but not with successors
				incoming.get(vertexID).add(workingMemoryNode);
			}

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
									.getValue(localOrdering)).add(newNode);

					// Update the predecessor list of the consumer of this edge
					HashSet<MemoryExclusionVertex> predecessor;
					predecessor = predecessors.get((Integer) edge.getTarget()
							.getPropertyBean().getValue(localOrdering));
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
		}

		// Add the memory objects corresponding to the fifos.
		buildFifoMemoryObjects(dag);

		// Save the dag in the properties
		this.setPropertyValue(SOURCE_DAG, dag);
	}

	/**
	 * Build the memory objects corresponding to the fifos of the input
	 * {@link DirectedAcyclicGraph}. The new memory objects are added to the
	 * {@link MemoryExclusionGraph}. This method creates 1 or 2
	 * {@link MemoryExclusionVertex} for each pair of init/end {@link DAGVertex}
	 * encountered in the graph.
	 * 
	 * @param dag
	 *            the dag containing the fifos.
	 */
	protected void buildFifoMemoryObjects(DirectedAcyclicGraph dag) {
		// Scan the dag vertices
		for (DAGVertex vertex : dag.vertexSet()) {
			String vertKind = vertex.getPropertyBean().getValue("kind")
					.toString();

			// Process Init vertices only
			if (vertKind.equals("dag_init_vertex")) {

				DAGVertex dagInitVertex = vertex;

				// Retrieve the corresponding EndVertex
				SDFInitVertex sdfInitVertex = (SDFInitVertex) vertex
						.getPropertyBean().getValue(DAGVertex.SDF_VERTEX);
				SDFEndVertex sdfEndVertex = (SDFEndVertex) sdfInitVertex
						.getEndReference();
				DAGVertex dagEndVertex = dag.getVertex(sdfEndVertex.getName());

				// Create the Head Memory Object
				// Get the typeSize
				MemoryExclusionVertex headMemoryNode;
				int typeSize = 1; // (size of a token (from the scenario)
				{
					// TODO: Support the supprImplodeExplode option
					if (dag.outgoingEdgesOf(dagInitVertex).size() != 1) {
						throw new RuntimeException(
								"Init DAG vertex "
										+ dagInitVertex
										+ " has several outgoing edges.\n"
										+ "This is not supported by the MemEx builder.\n"
										+ "Set \"ImplodeExplodeSuppr\" and \"Suppr Fork/Join\""
										+ " options to false in the workflow tasks"
										+ " to get rid of this error.");
					}
					DAGEdge outgoingEdge = dag.outgoingEdgesOf(dagInitVertex)
							.iterator().next();
					BufferAggregate buffers = (BufferAggregate) outgoingEdge
							.getPropertyBean().getValue(
									BufferAggregate.propertyBeanName);
					if (buffers.size() != 1) {
						throw new RuntimeException(
								"DAGEdge "
										+ outgoingEdge
										+ " is equivalent to several SDFEdges.\n"
										+ "This is not supported by the MemEx builder.\n"
										+ "Please contact Preesm developers.");
					}
					DataType type = MemoryExclusionVertex._dataTypes
							.get(buffers.get(0).getDataType());
					if (type != null) {
						typeSize = type.getSize();
					}
					headMemoryNode = new MemoryExclusionVertex("FIFO_Head_"
							+ dagEndVertex.getName(), dagInitVertex.getName(),
							buffers.get(0).getSize() * typeSize);
					headMemoryNode.setPropertyValue(
							MemoryExclusionVertex.TYPE_SIZE, typeSize);
				}
				// Add the head node to the MEG
				this.addVertex(headMemoryNode);

				// Compute the list of all edges between init and end
				Set<DAGEdge> between;
				{
					Set<DAGEdge> endPredecessors = dag
							.getPredecessorEdgesOf(dagEndVertex);
					Set<DAGEdge> initSuccessors = dag
							.getSuccessorEdgesOf(vertex);
					between = (new HashSet<DAGEdge>(initSuccessors));
					between.retainAll(endPredecessors);
				}

				// Add exclusions between the head node and ALL MemoryObjects
				// that do not correspond to edges in the between list.
				for (MemoryExclusionVertex memObject : this.vertexSet()) {
					DAGEdge correspondingEdge = memObject.getEdge();
					if (memObject != headMemoryNode
							&& (correspondingEdge == null || !between
									.contains(correspondingEdge))) {
						this.addEdge(headMemoryNode, memObject);
					}
				}

				// No need to add exclusion between the head MObj and the
				// outgoing edge of the init or the incoming edge of the end.
				// (unless of course the init and the end have an empty
				// "between" list, but this will be handled by the previous
				// loop.)

				// Create the Memory Object for the remaining of the FIFO (if
				// any)
				int fifoDepth = sdfInitVertex.getInitSize();
				if (fifoDepth > headMemoryNode.getWeight() / typeSize) {
					MemoryExclusionVertex fifoMemoryNode = new MemoryExclusionVertex(
							"FIFO_Body_" + dagEndVertex.getName(),
							dagInitVertex.getName(), fifoDepth * typeSize
									- headMemoryNode.getWeight());
					fifoMemoryNode.setPropertyValue(
							MemoryExclusionVertex.TYPE_SIZE, typeSize);

					// Add to the graph and exclude with everyone
					this.addVertex(fifoMemoryNode);
					for (MemoryExclusionVertex mObj : this.vertexSet()) {
						if (mObj != fifoMemoryNode) {
							this.addEdge(fifoMemoryNode, mObj);
						}
					}
				}
			}
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

	@Override
	public void copyProperties(PropertySource props) {
		for (String key : props.getPropertyBean().keys()) {
			if (props.getPropertyBean().getValue(key) instanceof CloneableProperty) {
				this.getPropertyBean().setValue(
						key,
						((CloneableProperty) props.getPropertyBean().getValue(
								key)).clone());
			} else {
				this.getPropertyBean().setValue(key,
						props.getPropertyBean().getValue(key));
			}
		}
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
	 * @return the {@link #dagVerticesInSchedulingOrder} or <code>null</code> if
	 *         the {@link MemoryExclusionGraph MemEx} was not
	 *         {@link #updateWithSchedule(DirectedAcyclicGraph) updated with a
	 *         schedule}
	 */
	public List<DAGVertex> getDagVerticesInSchedulingOrder() {
		if (dagVerticesInSchedulingOrder == null) {
			return null;
		} else {
			return new ArrayList<DAGVertex>(dagVerticesInSchedulingOrder);
		}
	}

	@Override
	public PropertyFactory getFactoryForProperty(String propertyName) {
		return null;
	}

	/**
	 * Return the lifetime of the {@link MemoryExclusionVertex memory object}
	 * passed as a parameter. If the memory object corresponds to a fifo, the
	 * first {@link Integer} will have a greater value than the second. If the
	 * memory object always exists, its lifetime will be <code>(0L,0L)</code>
	 * 
	 * @param vertex
	 *            the vertex whose lifetime is searched
	 * @param dag
	 *            the scheduled {@link DirectedAcyclicGraph DAG} from which the
	 *            {@link MemoryExclusionVertex MemEx Vertex} is derived
	 * @return the lifetime of the memory object in the form of 2 integers
	 *         corresponding to its life beginning and end.
	 * @throws RuntimeException
	 *             if the {@link MemoryExclusionVertex} is not derived from the
	 *             given {@link DirectedAcyclicGraph DAG} or if the
	 *             {@link DirectedAcyclicGraph DAG} was not scheduled.
	 */
	protected Entry<Long, Long> getLifeTime(MemoryExclusionVertex vertex,
			DirectedAcyclicGraph dag) throws RuntimeException {

		// If the MemObject corresponds to an edge, its lifetime spans from the
		// execution start of its source until the execution end of its target
		DAGEdge edge = vertex.getEdge();
		if (edge != null) {
			DAGVertex source = edge.getSource();
			DAGVertex target = edge.getTarget();

			if (source == null || target == null) {
				throw new RuntimeException(
						"Cannot get lifetime of a memory object "
								+ vertex.toString()
								+ " because its corresponding DAGEdge has no valid source and/or target");
			}

			Object birth = source.getPropertyBean().getValue("TaskStartTime",
					Long.class);

			Object death = target.getPropertyBean().getValue("TaskStartTime",
					Long.class);

			if (death == null || birth == null) {
				throw new RuntimeException(
						"Cannot get lifetime of a memory object "
								+ vertex.toString()
								+ " because the source or target of its corresponding DAGEdge"
								+ " has no TaskStartTime property. Maybe the DAG was not sheduled.");
			}

			Object duration = target.getPropertyBean().getValue(
					ImplementationPropertyNames.Task_duration, Long.class);
			if (duration == null) {
				throw new RuntimeException(
						"Cannot get lifetime of a memory object "
								+ vertex
								+ " because the target of its corresponding DAGEdge"
								+ " has no duration property.");
			}

			return new AbstractMap.SimpleEntry<Long, Long>((Long) birth,
					(Long) death + (Long) duration);
		}

		// Else the memEx vertex corresponds to a working memory
		if (vertex.getSink().equals(vertex.getSource())) {
			DAGVertex dagVertex = dag.getVertex(vertex.getSink());
			if (dagVertex == null) {
				throw new RuntimeException(
						"Cannot get lifetime of working memory object "
								+ vertex
								+ " because its corresponding DAGVertex does not exist in the given DAG.");
			}

			Object birth = dagVertex.getPropertyBean().getValue(
					"TaskStartTime", Long.class);
			Object duration = dagVertex.getPropertyBean().getValue(
					ImplementationPropertyNames.Task_duration, Long.class);

			if (birth == null || duration == null) {
				throw new RuntimeException(
						"Cannot get lifetime of working memory object "
								+ vertex
								+ " because its DAGVertex has no TaskStartTime and/or duration property");
			}

			return new AbstractMap.SimpleEntry<Long, Long>((Long) birth,
					(Long) birth + (Long) duration);

		}

		if (vertex.getSource().startsWith("FIFO_")) {
			if (vertex.getSource().startsWith("FIFO_Body")) {
				return new AbstractMap.SimpleEntry<Long, Long>(0L, 0L);
			}

			// Working memory exists from the beginning of the End until the end
			// of the init.
			// Since there is no exclusion with edges connected to the init/end
			// vertices they will not be added (since updating only consists in
			// removing existing exclusions)
			if (vertex.getSource().startsWith("FIFO_Head_")) {
				DAGVertex dagEndVertex = dag.getVertex(vertex.getSource()
						.substring(("FIFO_Head_").length()));
				DAGVertex dagInitVertex = dag.getVertex(vertex.getSink());

				if (dagEndVertex == null || dagInitVertex == null) {
					throw new RuntimeException(
							"Cannot get lifetime of a memory object "
									+ vertex.toString()
									+ " because its corresponding DAGVertex could not be found in the DAG");
				}
				Object birth = dagEndVertex.getPropertyBean().getValue(
						"TaskStartTime", Long.class);

				Object death = dagInitVertex.getPropertyBean().getValue(
						"TaskStartTime", Long.class);

				if (death == null || birth == null) {
					throw new RuntimeException(
							"Cannot get lifetime of a memory object "
									+ vertex.toString()
									+ " because the source or target of its corresponding End/Init"
									+ " has no TaskStartTime property. Maybe the DAG was not sheduled.");
				}

				Object duration = dagInitVertex.getPropertyBean().getValue(
						ImplementationPropertyNames.Task_duration, Long.class);
				if (duration == null) {
					throw new RuntimeException(
							"Cannot get lifetime of a memory object "
									+ vertex
									+ " because the Init of its corresponding Fifo"
									+ " has no duration property.");
				}

				return new AbstractMap.SimpleEntry<Long, Long>((Long) death
						+ (Long) duration, (Long) birth);
			}
		}

		// the vertex does not come from an edge nor from working memory.
		// nor from a fifo
		// Error
		throw new RuntimeException("Cannot get lifetime of a memory object "
				+ "that is not derived from a scheduled DAG." + " (MemObject: "
				+ vertex.toString() + ")");

	}

	@Override
	public PropertyBean getPropertyBean() {
		return properties;
	}

	@Override
	public String getPropertyStringValue(String propertyName) {
		if (this.getPropertyBean().getValue(propertyName) != null) {
			return this.getPropertyBean().getValue(propertyName).toString();
		}
		return null;
	}

	@Override
	public List<String> getPublicProperties() {
		return public_properties;
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

	@Override
	public void setPropertyValue(String propertyName, Object value) {
		this.getPropertyBean().setValue(propertyName, value);
	}

	/**
	 * This function update a {@link MemoryExclusionGraph MemEx} by taking
	 * timing information contained in a {@link DirectedAcyclicGraph DAG} into
	 * account.
	 * 
	 * 
	 * @param dag
	 *            the {@link DirectedAcyclicGraph DAG} used which must be the
	 *            one from which the {@link MemoryExclusionGraph MemEx} graph is
	 *            {@link #buildGraph(DirectedAcyclicGraph) built} (will not be
	 *            modified)
	 */
	public void updateWithMemObjectLifetimes(DirectedAcyclicGraph dag) {

		Set<DefaultEdge> removedExclusions = new HashSet<>();

		// Scan the exclusions
		for (DefaultEdge exclusion : this.edgeSet()) {
			MemoryExclusionVertex memObject1 = this.getEdgeSource(exclusion);
			Entry<Long, Long> obj1Lifetime = getLifeTime(memObject1, dag);

			MemoryExclusionVertex memObject2 = this.getEdgeTarget(exclusion);
			Entry<Long, Long> obj2Lifetime = getLifeTime(memObject2, dag);

			// If one of the lifetime is a fifo_body (no need to update,
			// exclusions will remain)
			if ((obj1Lifetime.getKey() == 0L && obj1Lifetime.getValue() == 0L)
					|| (obj2Lifetime.getKey() == 0L && obj2Lifetime.getValue() == 0L)) {
				continue;
			}

			// If the two objects are fifo heads: exclusion hold
			if ((obj1Lifetime.getKey() > obj1Lifetime.getValue())
					&& (obj2Lifetime.getKey() > obj2Lifetime.getValue())) {
				continue;
			}

			// If one objects is fifo heads
			if ((obj1Lifetime.getKey() > obj1Lifetime.getValue())
					|| (obj2Lifetime.getKey() > obj2Lifetime.getValue())) {
				if (obj1Lifetime.getKey() > obj2Lifetime.getValue()
						&& obj1Lifetime.getValue() < obj2Lifetime.getKey()) {
					// Remove the exclution
					removedExclusions.add(exclusion);
				}
				continue;
			}

			// If this code is reached, the two objects are buffers or working
			// memory
			// If the lifetimes do not overlap
			if (!(obj1Lifetime.getKey() < obj2Lifetime.getValue() && obj2Lifetime
					.getKey() < obj1Lifetime.getValue())) {
				// Remove the exclution
				removedExclusions.add(exclusion);
			}
		}

		this.removeAllEdges(removedExclusions);
	}

	/**
	 * This function update a {@link MemoryExclusionGraph MemEx} by taking
	 * scheduling information contained in a {@link DirectedAcyclicGraph DAG}
	 * into account. <br>
	 * <br>
	 * kdesnos: This method could probably be accelerated a lot ! Instead of
	 * scanning the dag in scheduling order, the dag could be updated with new
	 * precedence edges. Then, scanning the exclusions and checking if they
	 * still hold (as is done with memory object lifetime) could be done to
	 * remove unnecessary exclusions.
	 * 
	 * @param dag
	 *            the {@link DirectedAcyclicGraph DAG} used (will not be
	 *            modified)
	 */
	public void updateWithSchedule(DirectedAcyclicGraph dag) {

		// Since the MemEx is modified, the AdjacentVerticesBackup will be
		// deprecated. Clear it !
		clearAdjacentVerticesBackup();

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
		// As the DAG are scanned following the precedence order, the
		// computation needed to sort the list should not be too heavy.
		HashMap<Integer, DAGVertex> verticesMap = new HashMap<Integer, DAGVertex>();

		while (iterDAGVertices.hasNext()) {
			DAGVertex currentVertex = iterDAGVertices.next();

			boolean isTask = currentVertex.getPropertyBean()
					.getValue("vertexType").toString().equals("task");

			String vertKind = "";

			// Only task vertices have a kind
			if (isTask) {
				vertKind = currentVertex.getPropertyBean().getValue("kind")
						.toString();
			}

			if (vertKind.equals("dag_vertex")
					|| vertKind.equals("dag_broadcast_vertex")
					|| vertKind.equals("dag_init_vertex")
					|| vertKind.equals("dag_end_vertex")
					|| vertKind.equals("dag_fork_vertex")
					|| vertKind.equals("dag_join_vertex")) {
				int schedulingOrder = (Integer) currentVertex
						.getPropertyBean()
						.getValue(
								ImplementationPropertyNames.Vertex_schedulingOrder);
				verticesMap.put(schedulingOrder, currentVertex);
			}
		}

		ArrayList<Integer> schedulingOrders = new ArrayList<Integer>(
				verticesMap.keySet());
		Collections.sort(schedulingOrders);

		dagVerticesInSchedulingOrder = new ArrayList<DAGVertex>();

		// Update the buffer exclusions
		// Scan the vertices in scheduling order
		for (int order : schedulingOrders) {
			DAGVertex currentVertex = verticesMap.get(order);
			dagVerticesInSchedulingOrder.add(currentVertex);

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
				// Remove exclusion between the Exclusion Vertex corresponding
				// to the working memory (if any) of the currentVertex and the
				// exclusion vertices in the newPredecessors list

				// Re-create the working memory exclusion vertex (weight does
				// not matter to find the vertex in the Memex)
				MemoryExclusionVertex wMemVertex = new MemoryExclusionVertex(
						currentVertex.getName(), currentVertex.getName(), 0);
				if (this.containsVertex(wMemVertex)) {
					for (MemoryExclusionVertex newPredecessor : newPredecessors) {
						if (this.removeEdge(wMemVertex, newPredecessor) == null) {
							throw new RuntimeException("Missing edge");
						}
					}
				}

				// Remove exclusion between ExclusionVertices corresponding
				// to outgoing edges of the currentVertex, and ExclusionVertices
				// in newPredecessors list
				for (DAGEdge outgoingEdge : currentVertex.outgoingEdges()) {
					if (outgoingEdge.getTarget().getPropertyBean()
							.getValue("vertexType").toString().equals("task")) {
						MemoryExclusionVertex edgeVertex = new MemoryExclusionVertex(
								outgoingEdge);
						for (MemoryExclusionVertex newPredecessor : newPredecessors) {
							if (this.removeEdge(edgeVertex, newPredecessor) == null) {
								/**
								 * Possible causes are: <br>
								 * -edgeVertex or newPredecessor no longer are
								 * in the graph <br>
								 * -this.verticesPredecessors was corrupted
								 * before calling updateWithSchedule() <br>
								 * -The exclusion or one of the vertex could not
								 * be found because the
								 * MemoryExclusionVertex.equals() method is
								 * corrupted -Explode Implode were removed when
								 * creating the MemEx but not when updating it.
								 */
								throw new RuntimeException(
										"Failed removing exclusion between "
												+ edgeVertex + " and "
												+ newPredecessor);
							}
						}

						// Update newPredecessor list of successors
						// DAGVertices (the target of the current edge)
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
						// Add the working memory object to the
						// successorPredecessor list
						if (this.containsVertex(wMemVertex)) {
							successorPredecessor.add(wMemVertex);
						}
					}
				}
			}
		}

		// Update the fifo exclusions
		updateFIFOMemObjectWithSchedule(dag);
	}

	/**
	 * Method used to update the exclusions between memory objects corresponding
	 * to Fifo heads and other memory objects of the
	 * {@link MemoryExclusionGraph}. Exclusions will be removed from the
	 * exclusion graph, but no exclusions will be added.
	 * 
	 * @param dag
	 *            the scheduled DAG
	 * 
	 */
	protected void updateFIFOMemObjectWithSchedule(DirectedAcyclicGraph inputDAG) {

		// Create a DAG with new edges from scheduling info
		DirectedAcyclicGraph scheduledDAG = (DirectedAcyclicGraph) inputDAG
				.clone();

		// Create an TreeMap of the DAGVertices, in scheduling order.
		TreeMap<Integer, DAGVertex> verticesMap = new TreeMap<Integer, DAGVertex>();

		DAGIterator iterDAGVertices = new DAGIterator(scheduledDAG); // Iterator
																		// on
																		// DAG
		// vertices

		// Get vertices in scheduling order and remove send/receive vertices
		// from the dag.
		// Also identify the init vertices
		Set<DAGVertex> removedVertices = new HashSet<DAGVertex>();
		Set<DAGVertex> initVertices = new HashSet<>();

		while (iterDAGVertices.hasNext()) {
			DAGVertex currentVertex = iterDAGVertices.next();

			boolean isTask = currentVertex.getPropertyBean()
					.getValue("vertexType").toString().equals("task");

			String vertKind = "";

			// Only task vertices have a kind
			if (isTask) {
				vertKind = currentVertex.getPropertyBean().getValue("kind")
						.toString();
			}

			if (vertKind.equals("dag_vertex")
					|| vertKind.equals("dag_broadcast_vertex")
					|| vertKind.equals("dag_init_vertex")
					|| vertKind.equals("dag_end_vertex")
					|| vertKind.equals("dag_fork_vertex")
					|| vertKind.equals("dag_join_vertex")) {
				int schedulingOrder = (Integer) currentVertex
						.getPropertyBean()
						.getValue(
								ImplementationPropertyNames.Vertex_schedulingOrder);
				verticesMap.put(schedulingOrder, currentVertex);

				if (vertKind.equals("dag_init_vertex")) {
					initVertices.add(currentVertex);
				}
			} else {
				removedVertices.add(currentVertex);
			}
		}

		if (initVertices.size() == 0) {
			// Nothing to update !
			return;
		}

		// Remove unwanted vertices from the scheduledDag
		scheduledDAG.removeAllVertices(removedVertices);

		// This map is used along the scan of the vertex of the dag.
		// Its purpose is to store the last vertex scheduled on each
		// component. This way, when a new vertex is executed on this
		// instance is encountered, an edge can be added between it and
		// the previous one.
		HashMap<ComponentInstance, DAGVertex> lastVerticesScheduled;
		lastVerticesScheduled = new HashMap<ComponentInstance, DAGVertex>();

		// Scan the dag and add new precedence edges caused by the schedule
		Set<DAGEdge> addedEdges = new HashSet<DAGEdge>();
		for (Entry<Integer, DAGVertex> entry : verticesMap.entrySet()) {
			DAGVertex currentVertex = entry.getValue();

			// Retrieve component
			ComponentInstance comp = (ComponentInstance) currentVertex
					.getPropertyBean().getValue("Operator");

			// Retrieve last DAGVertex executed on this component
			DAGVertex lastScheduled = lastVerticesScheduled.get(comp);

			// If this is not the first time this component is encountered
			if (lastScheduled != null) {

				// Add an edge between the last and the currend vertex
				// if there is not already one
				if (scheduledDAG.getEdge(lastScheduled, currentVertex) == null) {
					DAGEdge newEdge = scheduledDAG.addEdge(lastScheduled,
							currentVertex);
					addedEdges.add(newEdge);
				}
			}
			// Save currentVertex as lastScheduled on this component
			lastVerticesScheduled.put(comp, currentVertex);
		}

		// Now, remove fifo exclusion
		for (DAGVertex dagInitVertex : initVertices) {
			// Retrieve the corresponding EndVertex
			SDFInitVertex sdfInitVertex = (SDFInitVertex) dagInitVertex
					.getPropertyBean().getValue(DAGVertex.SDF_VERTEX);
			SDFEndVertex sdfEndVertex = (SDFEndVertex) sdfInitVertex
					.getEndReference();
			DAGVertex dagEndVertex = scheduledDAG.getVertex(sdfEndVertex
					.getName());

			// Compute the list of all edges between init and end
			Set<DAGEdge> edgesBetween;
			{
				Set<DAGEdge> endPredecessors = scheduledDAG
						.getPredecessorEdgesOf(dagEndVertex);
				Set<DAGEdge> initSuccessors = scheduledDAG
						.getSuccessorEdgesOf(dagInitVertex);
				edgesBetween = (new HashSet<DAGEdge>(initSuccessors));
				edgesBetween.retainAll(endPredecessors);
				edgesBetween.removeAll(addedEdges);
			}

			// Remove exclusions with all buffer in the list (if any)
			if (edgesBetween.size() != 0) {

				// retrieve the head MObj for current fifo
				// size does not matter ("that's what she said") to retrieve the
				// Memory object from the exclusion graph
				MemoryExclusionVertex headMemoryNode = new MemoryExclusionVertex(
						"FIFO_Head_" + dagEndVertex.getName(),
						dagInitVertex.getName(), 0);
				for (DAGEdge edge : edgesBetween) {
					MemoryExclusionVertex mObj = new MemoryExclusionVertex(edge);
					this.removeEdge(headMemoryNode, mObj);
				}
			}

			// Compute the list of all actors between
			// init and end
			Set<DAGVertex> verticesBetween = null;
			{
				Set<DAGVertex> endPredecessors = scheduledDAG
						.getPredecessorVerticesOf(dagEndVertex);
				Set<DAGVertex> initSuccessors = scheduledDAG
						.getSuccessorVerticesOf(dagInitVertex);
				verticesBetween = (new HashSet<DAGVertex>(initSuccessors));
				verticesBetween.retainAll(endPredecessors);
				verticesBetween.removeAll(addedEdges);
			}

			// retrieve the head MObj for current fifo
			// size does not matter ("that's what she said") to retrieve the
			// Memory object from the exclusion graph
			MemoryExclusionVertex headMemoryNode = new MemoryExclusionVertex(
					"FIFO_Head_" + dagEndVertex.getName(),
					dagInitVertex.getName(), 0);
			for (DAGVertex dagVertex : verticesBetween) {
				MemoryExclusionVertex wMemoryObj = new MemoryExclusionVertex(
						dagVertex.getName(), dagVertex.getName(), 0);
				if (this.containsVertex(wMemoryObj)) {
					this.removeEdge(headMemoryNode, wMemoryObj);
				}
			}

		}
	}

	/**
	 * This method returns the local copy of the {@link MemoryExclusionVertex}
	 * that is {@link MemoryExclusionVertex}{@link #equals(Object)} to the
	 * {@link MemoryExclusionVertex} passed as a parameter.
	 * 
	 * @param memObject
	 *            a {@link MemoryExclusionVertex} searched in the
	 *            {@link MemoryExclusionGraph}
	 * @return an equal {@link MemoryExclusionVertex} from the
	 *         {@link #vertexSet()}, null if there is no such vertex.
	 */
	public MemoryExclusionVertex getVertex(MemoryExclusionVertex memObject) {
		Iterator<MemoryExclusionVertex> iter = this.vertexSet().iterator();
		while (iter.hasNext()) {
			MemoryExclusionVertex vertex = iter.next();
			if (vertex.equals(memObject)) {
				return vertex;
			}
		}
		return null;
	}
}
