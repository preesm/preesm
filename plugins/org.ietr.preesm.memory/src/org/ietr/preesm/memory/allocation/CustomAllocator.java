package org.ietr.preesm.memory.allocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.workflow.WorkflowException;

import org.ietr.preesm.memory.bounds.OstergardSolver;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * This implementation of the MemoryAllocator mainly is based on a custom
 * algorithm. <br>
 * The algorithm used in this implementation is based on a coloring approach of
 * a MemoryExclusionGraph derived from the input graph. A description of the
 * algorithm is made in allocate() comments.
 * 
 * @author kdesnos
 * 
 */
public class CustomAllocator extends MemoryAllocator {

	/**
	 * Constructor of the allocator.
	 * 
	 * @param graph
	 *            the graph whose edges are to allocate in memory
	 */
	public CustomAllocator(DirectedAcyclicGraph graph) {
		super(graph);
	}

	/**
	 * Constructor of the allocator taking a Memory Exclusion Graph Node as a
	 * Parameter. <br>
	 * This constructor was created in order to avoid rebuilding a previously
	 * built exclusion graph.
	 * 
	 * @param memExclusionGraph
	 *            The exclusion graph whose vertices are to allocate in memory
	 */
	public CustomAllocator(MemoryExclusionGraph memExclusionGraph) {
		super(memExclusionGraph);
	}

	/**
	 * This implementation is based on a custom algorithm. Its major steps are
	 * the following :<br>
	 * <b>1 -</b> Build G<sub>exclu</sub>the MemoryExclusionGraph from
	 * this.graph<br>
	 * <b>2 -</b> Get G<sub>inclu</sub> the complementary to G<sub>exclu</sub>
	 * exclusion graph<br>
	 * <b>3 -</b> Let i := 0<br>
	 * <b>4 -</b> Find C<sub>i</sub> the maximum-weight clique in
	 * G<sub>inclu</sub>.(Each element <i>elt</i> of C<sub>i</sub> is then a
	 * vertex of the clique)<br>
	 * <b>5 -</b> Let CWeight :=
	 * maximum<sub>i</sub>(weight(<i>elt<sub>i</sub></i>))<br>
	 * <b>6 -</b> For each element <i>elt</i> of C<sub>i</sub> (in descending
	 * order of weights)<br>
	 * <b>6.1 -</b> For each neighbor <i>neigh</i> of <i>elt</i> (excluding
	 * neighbors from previous elements) in G<sub>exclu</sub> (in descending
	 * order of weights)<br>
	 * <b>6.1.1 -</b> Let NWeight := <i>neigh</i>.weight + weight(<i>elt</i>)<br>
	 * <b>6.1.2 -</b> If (NWeight < CWeight + <i>neigh</i>.weight) Then add
	 * <i>neigh</i> to elt and Goto(5)<br>
	 * <b>7 -</b> Remove all vertices of C<sub>i</sub> from G<sub>exclu</sub>
	 * and G<sub>inclu</sub><br>
	 * <b>8 -</b> If G<sub>exclu</sub> is not empty Then i = i+1 and Goto (4)<br>
	 * <b>9 -</b> Let cliqueOffset := 0<br>
	 * <b>10 -</b> For each C<sub>i</sub><br>
	 * <b>10.1 -</b>For each <i>elt</i> of C<sub>i</sub><br>
	 * <b>10.1.1 -</b>Let offset <i> o</i> := cliqueOffset<br>
	 * <b>10.1.2 -</b>For each vertex <i>v</i> of <i>elt</i><br>
	 * <b>10.1.2.1 -</b> Put <i>v</i> in allocation with offset <i>o</i><br>
	 * <b>10.1.2.2 -</b> Let o:=o+v.weight<br>
	 * <b>10.2 -</b> cliqueOffset := cliqueOffset +
	 * maximum<sub>i</sub>(weight(<i>elt<sub>i</sub></i>))<br>
	 * <br>
	 * G<sub>exclu</sub> := < V,E ><br>
	 * V := {vert<sub>1</sub>, vert<sub>2</sub>, ... ,vert<sub>n</sub>}<br>
	 * E := { (vert<sub>i</sub>,vert<sub>j</sub>); i!=j}<br>
	 * C<sub>i</sub> := { elt<sub>1</sub>, elt<sub>2</sub>, ...}<br>
	 * elt := {vert<sub>i</sub>, vert<sub>j</sub>,...}<br>
	 * weight(elt) := vert<sub>i</sub>.weight + vert<sub>j</sub>.weight + ...
	 */
	public void allocate() {
		// Logger logger = WorkflowLogger.getLogger();
		// (1)
		MemoryExclusionGraph exclusionGraph;
		if (inputExclusionGraph == null) {
			exclusionGraph = new MemoryExclusionGraph();
			try {
				// logger.log(Level.INFO, "1 - Building Graph");
				exclusionGraph.buildGraph(graph);
				inputExclusionGraph = (MemoryExclusionGraph) exclusionGraph
						.clone();
			} catch (InvalidExpressionException e) {
				e.printStackTrace();
			} catch (WorkflowException e) {
				e.printStackTrace();
			}
		} else {
			exclusionGraph = (MemoryExclusionGraph) inputExclusionGraph.clone();
		}

		// (2)
		// logger.log(Level.INFO, "2 - Get Complementary");
		SimpleGraph<MemoryExclusionVertex, DefaultEdge> inclusionGraph = exclusionGraph
				.getComplementary();

		// (9)
		int cliqueOffset = 0;

		// (8)
		while (!exclusionGraph.vertexSet().isEmpty()) {
			// (4)
			// TODO Remplacer par solver user define
			OstergardSolver<MemoryExclusionVertex, DefaultEdge> ostSolver;
			ostSolver = new OstergardSolver<MemoryExclusionVertex, DefaultEdge>(
					inclusionGraph);
			// logger.log(Level.INFO, "3 - Stable Set");
			ostSolver.solve();
			HashSet<MemoryExclusionVertex> cliqueSet = ostSolver
					.getHeaviestClique();

			// Convert cliqueSet to a list of elt (where elt is a set of
			// vertices)
			// logger.log(Level.INFO, "4 - Fill arrayList");
			ArrayList<HashSet<MemoryExclusionVertex>> clique = new ArrayList<HashSet<MemoryExclusionVertex>>();
			for (MemoryExclusionVertex node : cliqueSet) {
				HashSet<MemoryExclusionVertex> element = new HashSet<MemoryExclusionVertex>();
				element.add(node);
				clique.add(element);
				// (10) Allocate clique elements
				allocateMemoryObject(node, cliqueOffset);
			}

			// This boolean is used to iterate over the list as long as a vertex
			// is added to an element of the list during an iteration
			boolean loopAgain = !clique.isEmpty(); // Loop only if clique is not
													// empty (should always be
													// true when reached...)

			// the cliqueWeight will store the weight of the current clique Ci
			int cliqueWeight = 0;
			while (loopAgain) {
				loopAgain = false;

				// (5)
				// logger.log(Level.INFO, "5 - Order List");
				orderElementList(clique);
				cliqueWeight = maxElementWeight(clique, true);

				Iterator<HashSet<MemoryExclusionVertex>> iterElements;
				iterElements = clique.iterator();

				// List the neighbors of elements that were already tested.
				// This prevent the allocation of a neighbor in an elements once
				// it has been proved incompatible with a larger one.
				ArrayList<MemoryExclusionVertex> treatedNeighbors;
				treatedNeighbors = new ArrayList<MemoryExclusionVertex>();

				// (6)
				while (iterElements.hasNext()) {
					// The first iteration will never add any vertex to the
					// considered element. Indeed, as it is the largest element,
					// newWeight < cliqueWeight + neighbor.getWeight() will
					// always be false.
					// However, we keep the first iteration in order to fill the
					// treatedNeighbors List and thus make impossible the
					// allocation of first element neighbors in following
					// elements.
					HashSet<MemoryExclusionVertex> element = iterElements
							.next();
					int elementWeight = weight(element);
					// logger.log(Level.INFO, "6 - Get neighbors");
					// get all the neighbors of elements vertices
					ArrayList<MemoryExclusionVertex> neighbors;
					// The vertex are added to a set to avoid duplicates.
					// Then they will be stored in ArrayList neighbors
					HashSet<MemoryExclusionVertex> temporary = new HashSet<MemoryExclusionVertex>();
					// TODO Move adjacentVertex from solver to graph
					// Use a Solver to retrieve adjacent Vertex
					OstergardSolver<MemoryExclusionVertex, DefaultEdge> toolSolver;
					toolSolver = new OstergardSolver<MemoryExclusionVertex, DefaultEdge>(
							exclusionGraph);
					for (MemoryExclusionVertex vertex : element) {
						temporary.addAll(toolSolver.adjacentVerticesOf(vertex));
					}
					neighbors = new ArrayList<MemoryExclusionVertex>(temporary);

					// logger.log(Level.INFO, "7 - Sort neighbors");
					// (6.1)
					// Sort neighbors in descending order of weights
					Collections.sort(neighbors, Collections.reverseOrder());
					// Vertex already in cliqueSet are not considered.
					// As neighbors is ordered, a custom remove function might
					// be a good idea
					neighbors.removeAll(cliqueSet);
					neighbors.removeAll(treatedNeighbors);

					// logger.log(Level.INFO, "8 - Iterate Neighbors");
					for (MemoryExclusionVertex neighbor : neighbors) {
						// (6.1.1)
						// Compute the weight of the element if neighbor was
						// added to it
						int newWeight = elementWeight + neighbor.getWeight();
						if (newWeight < cliqueWeight + neighbor.getWeight()) {
							// logger.log(Level.INFO, "9 - new Element Found");
							element.add(neighbor);
							cliqueSet.add(neighbor);
							loopAgain = true;
							// (10)
							allocateMemoryObject(neighbor, cliqueOffset
									+ elementWeight);
							break; // break the neighbors loop (goto 5)
						}
					}

					// if the neighbors loop was broken, break the elements loop
					// too (goto (5))
					if (loopAgain) {
						break;
					}

					// else, neighbors loop was not broken, element iteration
					// continue.
					// Add the treated neighbors to the list
					treatedNeighbors.addAll(neighbors);
				}
			}
			// (7)
			// logger.log(Level.INFO, "10 - Remmoving vertex");
			// logger.log(Level.INFO, "Vertex  "+ cliqueSet);
			exclusionGraph.removeAllVertices(cliqueSet);
			inclusionGraph.removeAllVertices(cliqueSet);
			cliqueOffset += cliqueWeight;
		}
		// logger.log(Level.INFO, "11 - Over");
	}

	/**
	 * This method is used to retrieve the sum of weight of a set of vertices
	 * 
	 * @param set
	 *            the set of vertices to treat
	 * @return the sum of the vertices weight
	 */
	protected int weight(HashSet<MemoryExclusionVertex> set) {
		int result = 0;

		for (MemoryExclusionVertex vertex : set) {
			result += vertex.getWeight().intValue();
		}
		return result;
	}

	/**
	 * This method is used to order a list of elements wher each element is a
	 * set of vertices. The resulting list is ordered in decreasing weight
	 * order.
	 * 
	 * @param elementList
	 *            the list to order.
	 */
	protected void orderElementList(
			ArrayList<HashSet<MemoryExclusionVertex>> elementList) {
		// Define a comparator of list elements. The weight of an element is
		// used for comparison
		Comparator<HashSet<MemoryExclusionVertex>> comparator = new Comparator<HashSet<MemoryExclusionVertex>>() {
			public int compare(HashSet<MemoryExclusionVertex> o1,
					HashSet<MemoryExclusionVertex> o2) {
				return (weight(o2) - weight(o1));
			}
		};
		Collections.sort(elementList, comparator);
	}

	/**
	 * This method return the maximum weight of an element of the List. Each
	 * element of the list is a set of vertices.
	 * 
	 * @param elementList
	 *            the list of set of vertices
	 * @param isOrdered
	 *            true if the list has been ordered before
	 * @return the largest weight of an element
	 */
	protected int maxElementWeight(
			ArrayList<HashSet<MemoryExclusionVertex>> elementList,
			boolean isOrdered) {
		int result = 0;

		// If the list has been ordered before, just return the weight of the
		// first element
		if (isOrdered && !elementList.isEmpty())
			return weight(elementList.get(0));
		// Else, search the list
		for (HashSet<MemoryExclusionVertex> element : elementList) {
			int temp = weight(element);
			if (temp > result)
				result = temp;
		}
		return result;
	}
}
