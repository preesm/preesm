package org.ietr.preesm.plugin.codegen.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import net.sf.dftools.workflow.WorkflowException;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.sdf4j.iterators.DAGIterator;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.parameters.InvalidExpressionException;

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
		SimpleGraph<MemoryExclusionGraphNode, DefaultEdge> {

	/**
	 * Mandatory when extending SimpleGraph
	 */
	private static final long serialVersionUID = 6491894138235944107L;

	/**
	 * Each element of this list is a map containing all
	 * MemoryExclusionGraphNodes that sharing a common fork vertex ancestor.
	 * Each node is associated to a table of boolean which represents the
	 * branch(es) it belongs to. The index of the element in the ArrayList is
	 * the identifier of this fork.<br>
	 * <br>
	 * 
	 * <b> The ArrayList exclusionLists</b>
	 * <table border>
	 * <tr>
	 * <td colspan="2">Fork 0</td>
	 * <td colspan="2">Fork 1</td>
	 * <td colspan="2">...</td>
	 * <td colspan="2">Fork n</td>
	 * </tr>
	 * <tr>
	 * <td>Node AB</td>
	 * <td>010</td>
	 * <td>Node BC</td>
	 * <td>10</td>
	 * <td colspan="2">...</td>
	 * <td>Node XY</td>
	 * <td>branchID</td>
	 * </tr>
	 * <tr>
	 * <td>Node AC</td>
	 * <td>100</td>
	 * <td>Node BG</td>
	 * <td>01</td>
	 * <td colspan="2">...</td>
	 * <td>Node YZ</td>
	 * <td>branchID</td>
	 * </tr>
	 * <tr>
	 * <td>Node BC</td>
	 * <td>010</td>
	 * <td>Node GC</td>
	 * <td>01</td>
	 * <td colspan="2">...</td>
	 * <td>Node ZW</td>
	 * <td>branchID</td>
	 * </tr>
	 * </table>
	 */
	private ArrayList<HashMap<MemoryExclusionGraphNode, boolean[]>> exclusionLists;

	/**
	 * Each element of this list correspond to a DAGVertex and is a map
	 * containing the forks and branches to which this vertex belongs. The
	 * Integer is the fork fork identifier and the Boolean tab identify the
	 * branch(es) of this fork.<br>
	 * <br>
	 * 
	 * <b> The ArrayList verticesBranches</b>
	 * <table border>
	 * <tr>
	 * <td colspan="2">Vertex A</td>
	 * <td colspan="2">Vertex B</td>
	 * <td colspan="2">...</td>
	 * <td colspan="2">Vertex x</td>
	 * </tr>
	 * <tr>
	 * <td>Fork 1</td>
	 * <td>010</td>
	 * <td>Fork 1</td>
	 * <td>010</td>
	 * <td colspan="2">...</td>
	 * <td>Fork n</td>
	 * <td>branchID</td>
	 * </tr>
	 * <tr>
	 * <td colspan="2"></td>
	 * <td>Fork 2</td>
	 * <td>01</td>
	 * <td colspan="2">...</td>
	 * <td>Fork m</td>
	 * <td>branchID</td>
	 * </tr>
	 * </table>
	 */
	private ArrayList<HashMap<Integer, boolean[]>> verticesBranch;

	/**
	 * Default constructor
	 */
	public MemoryExclusionGraph() {
		super(DefaultEdge.class);
	}

	/**
	 * Method to apply bitwise-like operator OR to two boolean table.
	 * 
	 * @warning <b> The two table MUST have the same size </b>
	 * 
	 * @param a
	 *            first boolean table
	 * @param b
	 *            second boolean table
	 * @return the result of a OR b
	 */
	public static boolean[] or(boolean[] a, boolean[] b) throws Exception {
		if (a.length != b.length) {
			throw new Exception("or : Size of boolean tabs do not match.");
		}

		boolean c[] = new boolean[a.length];

		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] | b[i];
		}

		return c;
	}

	/**
	 * Method to apply bitwise-like operator AND to two boolean table.
	 * 
	 * @warning <b> The two table MUST have the same size </b>
	 * 
	 * @param a
	 *            first boolean table
	 * @param b
	 *            second boolean table
	 * @return the result of a AND b
	 */
	public static boolean[] and(boolean[] a, boolean[] b) throws Exception {
		if (a.length != b.length) {
			throw new Exception("and : Size of boolean tabs do not match.");
		}

		boolean c[] = new boolean[a.length];

		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] & b[i];
		}

		return c;
	}

	/**
	 * Method to check is all boolean of a tab are true.
	 * 
	 * @param a
	 *            the tab.
	 * @return true if all elements are true. false else.
	 */
	public static boolean allTrue(boolean[] a) {
		boolean res = true;

		for (boolean elem : a) {
			res = elem & res;
		}
		return res;
	}

	/**
	 * Method to check is all boolean of a tab are false.
	 * 
	 * @param a
	 *            the tab.
	 * @return true if all elements are false. false else.
	 */
	public static boolean allFalse(boolean[] a) {
		boolean res = false;

		for (boolean elem : a) {
			res = elem | res;
		}
		return !res;
	}

	/**
	 * This method add the node corresponding to the passed edge to the
	 * ExclusionGraph. If the source or targeted vertex isn't a task vertex,
	 * nothing is added.
	 * 
	 * @param edge
	 *            The memory transfer to add.
	 * @return the exclusion graph node created (or null)
	 */
	public MemoryExclusionGraphNode addNode(DAGEdge edge) {
		// If the target and source vertices are tasks,
		// add a node corresponding to the memory transfer
		// to the exclusion graph. Else, nothing

		MemoryExclusionGraphNode newNode = null;

		// As the non-task vertices are removed at the beginning of the build
		// function
		// This if statement could be removed.
		if (edge.getSource().getPropertyBean().getValue("vertexType")
				.toString().equals("task")
				&& edge.getTarget().getPropertyBean().getValue("vertexType")
						.toString().equals("task")) {
			try {
				newNode = new MemoryExclusionGraphNode(edge.getSource()
						.getName(), edge.getTarget().getName(), edge
						.getWeight().intValue());

				this.addVertex(newNode);

				// Add exclusion corresponding to all incoming edges of the
				// source vertex.
				for (DAGEdge incomingEdge : edge.getSource().incomingEdges()) {
					MemoryExclusionGraphNode memExGrNo = new MemoryExclusionGraphNode(
							incomingEdge.getSource().getName(), edge
									.getSource().getName(), incomingEdge
									.getWeight().intValue());

					this.addEdge(memExGrNo, newNode);
				}

			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return newNode;
	}

	/**
	 * Method to build the graph based on a DirectedAcyclicGraph
	 * 
	 * @param dag
	 *            This DirectedAcyclicGraph is analyzed to create the nodes and
	 *            edges of the MemoryExclusionGraph. The DAG used must be the
	 *            output of a scheduling process. This property ensures that all
	 *            preceding nodes of a "merge" node are treated before treating
	 *            the "merge" node.
	 * @throws InvalidExpressionException
	 * @throws WorkflowException
	 */
	public void BuildGraph(DirectedAcyclicGraph dag)
			throws InvalidExpressionException, WorkflowException {

		/*
		 * Declarations & initializations
		 */
		DAGIterator iter = new DAGIterator(dag); // Iterator on DAG vertices

		// Remove dag vertex of type other than "task"
		// And identify source-nodes
		HashSet<DAGVertex> toRemove = new HashSet<DAGVertex>();
		ArrayList<DAGVertex> sources = new ArrayList<DAGVertex>();
		int newOrder = 0;
		while (iter.hasNext()) {
			DAGVertex vert = iter.next();
			if (vert.getPropertyBean().getValue("vertexType").toString()
					.equals("task")) {
				vert.getPropertyBean().setValue("schedulingOrder", newOrder);
				newOrder++;

				if (vert.incomingEdges().size() == 0) {
					sources.add(vert);
				}
			} else {
				toRemove.add(vert);
			}
		}
		iter = new DAGIterator(dag); // Iterator on DAG vertices

		dag.removeAllVertices(toRemove);

		exclusionLists = new ArrayList<HashMap<MemoryExclusionGraphNode, boolean[]>>();
		verticesBranch = new ArrayList<HashMap<Integer, boolean[]>>(dag
				.vertexSet().size());

		// Initialize vertices Branch with empty HashMaps
		for (int i = 0; i < dag.vertexSet().size(); i++) {
			verticesBranch.add(new HashMap<Integer, boolean[]>());
		}

		// Create a fork with a branch for each source node (if more than one)
		if (sources.size() > 1) {
			// Creation of the new exclusion list
			HashMap<MemoryExclusionGraphNode, boolean[]> list = new HashMap<MemoryExclusionGraphNode, boolean[]>();
			exclusionLists.add(list);

			// Retrieve the index of the list which is also the unique ID of
			// the fork.
			int forkID = exclusionLists.size() - 1;

			int nbBranches = sources.size(); // Number of
												// outgoing
												// branches
			int branch = 0; // index of the current branch

			// For each source node
			for (DAGVertex sourceNode : sources) {

				// get a reference to the list of branches
				// to which the current DAG Vertex belongs
				HashMap<Integer, boolean[]> vertexBranch = verticesBranch
						.get((Integer) sourceNode.getPropertyBean().getValue(
								"schedulingOrder"));

				// Create the boolean table that identify the branch
				boolean[] branchID = new boolean[nbBranches];
				branchID[branch] = true;

				// Add the new branch to the list
				vertexBranch.put(forkID, branchID);
				
				branch++;
			}
		}

		/*
		 * Part 1: Scan of the DAG in order to: - create Exclusion Graph nodes.
		 * - create the exclusion lists - add exclusion between consecutive
		 * Memory Transfer
		 */
		while (iter.hasNext()) // For each vertex of the DAG
		{
			DAGVertex vertexDAG = iter.next(); // Retrieve the vertex to process
			int vertexID = (Integer) vertexDAG.getPropertyBean().getValue(
					"schedulingOrder"); // Retrieve the vertex unique ID

			/*
			 * Part 1.1: Merge vertex processing
			 */
			if ((vertexDAG.incomingEdges()).size() > 1) {

				HashMap<Integer, boolean[]> vertexBranch = verticesBranch
						.get(vertexID);

				// Just check if all branches from a node have merged.
				// if so, remove the fork from the branch list of the DAGVertex
				HashSet<Integer> removed = new HashSet<Integer>();
				for (int fork : vertexBranch.keySet()) {
					if (allTrue(vertexBranch.get(fork))) {
						removed.add(fork);
					}
				}

				for (int fork : removed) {
					vertexBranch.remove(fork);
				}

			}

			/*
			 * Part 1.2 Fork vertex processing
			 */
			if ((vertexDAG.outgoingEdges()).size() > 1) {

				// Creation of the new exclusion list
				HashMap<MemoryExclusionGraphNode, boolean[]> list = new HashMap<MemoryExclusionGraphNode, boolean[]>();
				exclusionLists.add(list);

				// Retrieve the index of the list which is also the unique ID of
				// the fork.
				int forkID = exclusionLists.size() - 1;

				int nbBranches = vertexDAG.outgoingEdges().size(); // Number of
																	// outgoing
																	// branches
				int branch = 0; // index of the current branch

				// For each outgoing edge
				for (DAGEdge edge : vertexDAG.outgoingEdges()) {

					// Retrieve a copy of the list of branches
					// to which the current DAG Vertex belongs
					HashMap<Integer, boolean[]> vertexBranch = new HashMap<Integer, boolean[]>(
							verticesBranch.get(vertexID));

					// Create the boolean table that identify the branch
					boolean[] branchID = new boolean[nbBranches];
					branchID[branch] = true;

					// Add the new branch to the list
					vertexBranch.put(forkID, branchID);

					// Add the node to the Exclusion Graph
					MemoryExclusionGraphNode newNode;
					if ((newNode = this.addNode(edge)) != null) {
						// If a node was added.
						// Add the node to exclusion lists it belongs to
						for (int fork : vertexBranch.keySet()) {
							HashMap<MemoryExclusionGraphNode, boolean[]> forkList = exclusionLists
									.get(fork);

							forkList.put(newNode, vertexBranch.get(fork));
						}
					}

					// Copy the list of branch of the current vertex (+ new fork
					// branches)
					// to the list of branch of the target vertex
					int targetVertexID = (Integer) edge.getTarget()
							.getPropertyBean().getValue("schedulingOrder");

					// Get the list of branch of target vertex
					HashMap<Integer, boolean[]> targetVertexBranch = verticesBranch
							.get(targetVertexID);

					for (int fork : vertexBranch.keySet()) {
						// If the target vertex already has a branch for
						// this fork
						if (targetVertexBranch.get(fork) != null) {
							// Merge the new branch with the previous
							try {
								boolean[] mergedBranch = or(
										targetVertexBranch.get(fork),
										vertexBranch.get(fork));
								targetVertexBranch.put(fork, mergedBranch);
							} catch (Exception e) {
								WorkflowException er = new WorkflowException(
										e.getLocalizedMessage());
								throw er;
							}
						} else {
							// We add the fork and the branch to the target
							// vertex
							targetVertexBranch
									.put(fork, vertexBranch.get(fork));
						}
					}

					// Increment index branch
					branch++;
				}

			} else {
				/*
				 * Part 1.3 Unique-output vertex processing
				 */
				if ((vertexDAG.outgoingEdges()).size() == 1) {

					DAGEdge edge = vertexDAG.outgoingEdges().iterator().next();

					// Retrieve the list of branches
					// to which the current DAG Vertex belongs
					HashMap<Integer, boolean[]> vertexBranch = verticesBranch
							.get(vertexID);

					// Add the node to the Exclusion Graph
					MemoryExclusionGraphNode newNode;
					if ((newNode = this.addNode(edge)) != null) {
						// If a node was added.
						// Add the node to exclusion lists it belongs to
						for (int fork : vertexBranch.keySet()) {
							HashMap<MemoryExclusionGraphNode, boolean[]> forkList = exclusionLists
									.get(fork);

							forkList.put(newNode, vertexBranch.get(fork));
						}
					}

					// Copy the list of branch of the current vertex
					// to the list of branch of the target vertex
					int targetVertexID = (Integer) edge.getTarget()
							.getPropertyBean().getValue("schedulingOrder");

					// Get the list of branch of target vertex
					HashMap<Integer, boolean[]> targetVertexBranch = verticesBranch
							.get(targetVertexID);

					for (int fork : vertexBranch.keySet()) {
						// If the target vertex already has a branch for
						// this fork
						if (targetVertexBranch.get(fork) != null) {
							// Merge the new branch with the previous
							try {
								boolean[] mergedBranch = or(
										targetVertexBranch.get(fork),
										vertexBranch.get(fork));
								targetVertexBranch.put(fork, mergedBranch);
							} catch (Exception e) {
								WorkflowException er = new WorkflowException(
										e.getLocalizedMessage());
								throw er;
							}
						} else {
							// We add the fork and the branch to the target
							// vertex
							targetVertexBranch
									.put(fork, vertexBranch.get(fork));
						}
					}
				}
			}
		}

		/*
		 * Part 2: Scan of the exclusion lists to add exclusion between
		 * potentially concurrent memory transfer.
		 */

		// For each exclusion list
		for (HashMap<MemoryExclusionGraphNode, boolean[]> list : exclusionLists) {
			Object[] keyList = (list.keySet().toArray());

			for (int i = 0; i < list.size() - 1; i++) {
				for (int j = i + 1; j < list.size(); j++) {
					try {
						if (allFalse(and(list.get(keyList[i]),
								list.get(keyList[j])))) {
							this.addEdge((MemoryExclusionGraphNode) keyList[i],
									(MemoryExclusionGraphNode) keyList[j]);
						}
					} catch (Exception e) {
						WorkflowException er = new WorkflowException(
								e.getLocalizedMessage());
						throw er;
					}

				}
			}

		}
	}
}
