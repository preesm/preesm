package org.ietr.preesm.plugin.codegen.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import net.sf.dftools.workflow.WorkflowException;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.parameters.InvalidExpressionException;

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
public class ImprovedCustomAllocator extends MemoryAllocator {

	/**
	 * Constructor of the allocator.
	 * 
	 * @param graph
	 *            the graph whose edges are to allocate in memory
	 */
	public ImprovedCustomAllocator(DirectedAcyclicGraph graph) {
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
	public ImprovedCustomAllocator(MemoryExclusionGraph memExclusionGraph) {
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
		SimpleGraph<MemoryExclusionGraphNode, DefaultEdge> inclusionGraph = exclusionGraph
				.getComplementary();

		// (9)
		int cliqueOffset = 0;

		// (8)
		while (!exclusionGraph.vertexSet().isEmpty()) {
			// (4)
			// TODO Remplacer par solver user define
			OstergardSolver<MemoryExclusionGraphNode, DefaultEdge> ostSolver;
			ostSolver = new OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>(
					inclusionGraph);
			// logger.log(Level.INFO, "3 - Stable Set");
			ostSolver.solve();
			HashSet<MemoryExclusionGraphNode> cliqueSet = ostSolver
					.getHeaviestClique();

			// Allocate Clique elements
			for (MemoryExclusionGraphNode node : cliqueSet) {
				// (10) Allocate clique elements
				allocation.put(node.getEdge(), cliqueOffset);
				memExNodeAllocation.put(node, cliqueOffset);
			}

			// This boolean is used to iterate over the list as long as a vertex
			// is added to an element of the list during an iteration
			boolean loopAgain = !cliqueSet.isEmpty(); // Loop only if clique is
														// not
														// empty (should always
														// be
														// true when reached...)

			// the cliqueWeight will store the weight of the current max element
			// of clique
			int cliqueWeight = Collections.max(cliqueSet).getWeight();
			int maximumSize = 2*cliqueWeight;



				ArrayList<MemoryExclusionGraphNode> nonAllocatedVertex;
				nonAllocatedVertex = new ArrayList<MemoryExclusionGraphNode>(
						exclusionGraph.vertexSet());
				Collections
						.sort(nonAllocatedVertex, Collections.reverseOrder());
				
				while (loopAgain) {
					loopAgain = false;

				for (MemoryExclusionGraphNode vertex : nonAllocatedVertex) {
					// Get vertex neighbors
					HashSet<MemoryExclusionGraphNode> neighbors = exclusionGraph
							.getAdjacentVertexOf(vertex);

					// The offset to begin the search
					int offset = cliqueOffset;

					// This boolean indicate that the offset chosen for the
					// vertex is
					// compatible with all the neighbors that are already
					// allocated.
					boolean validOffset = false;

					// Iterate until a valid offset is found (this offset will
					// be the
					// smallest possible)
					while (!validOffset) {
						validOffset = true;
						for (MemoryExclusionGraphNode neighbor : neighbors) {
							Integer neighborOffset;
							if ((neighborOffset = memExNodeAllocation
									.get(neighbor)) != null) {
								
								if (neighborOffset < (offset + vertex
										.getWeight())
										&& (neighborOffset + neighbor
												.getWeight()) > offset) {
									validOffset = false;
									offset += neighbor.getWeight();
									break;
								}
							}
						}
					}
					// Allocate the vertex at the resulting offset if the set is not enlarged by the weight of the node
					if ((offset-cliqueOffset) < cliqueWeight && ((offset-cliqueOffset)+vertex.getWeight()) < maximumSize) {
						memExNodeAllocation.put(vertex, offset);
						allocation.put(vertex.getEdge(), offset);
						cliqueSet.add(vertex);
						nonAllocatedVertex.remove(vertex);
						loopAgain = true;
						cliqueWeight = (((offset-cliqueOffset)+vertex.getWeight()) > cliqueWeight)?(offset-cliqueOffset)+vertex.getWeight() : cliqueWeight;
						break;
					}
				}
			}
			// ABORTED
			// // Improvement Try to add neighbors of cliqueSet
			// // neighbors are treated in decreasing weight order and added in
			// the best-fit fashion.
			// // However, the CWeight cannot be changed !
			//
			// // Sort the neighbors list in decreasing order of weight
			// Collections.sort(treatedNeighbors,Collections.reverseOrder());
			//
			// // Best-fit alloc aborted
			// for(MemoryExclusionGraphNode currentNeighbor : treatedNeighbors){
			// // retrieve the vertices of Ci that are neighbors of the current
			// neighbor.
			// HashSet<MemoryExclusionGraphNode> adjacent =
			// exclusionGraph.getAdjacentVertexOf(currentNeighbor);
			// adjacent.retainAll(cliqueSet);
			//
			// // Retrieve all the exclusions. Each node has a starting address
			// (its offset) and occupy memory up to (offset + size)
			// // The current neighbor cannot be allocated between those
			// addresses.
			// ArrayList<Integer> excludeFrom = new
			// ArrayList<Integer>(adjacent.size());
			// ArrayList<Integer> excludeTo = new
			// ArrayList<Integer>(adjacent.size());
			// for(MemoryExclusionGraphNode adjacentNode : adjacent){
			// excludeFrom.add(memExNodeAllocation.get(adjacentNode));
			// excludeTo.add(memExNodeAllocation.get(adjacentNode)+
			// adjacentNode.getWeight());
			// }
			//
			// // merge the two lists
			// Collections.sort(excludeFrom);
			// Collections.sort(excludeTo);
			// Iterator<Integer> iterFrom = excludeFrom.iterator();
			// Iterator<Integer> iterTo = excludeFrom.iterator();
			//
			//
			// int from = 0;
			// int to = 0;
			// int freeFrom = 0;
			// int offset = -1;
			// float occupation = (float)0.0; // Occupation rate of the best
			// fitted space (<=1)
			// int numberExcludingElements = 0;
			//
			// while(iterFrom.hasNext() && iterTo.hasNext()){
			// if(from <= to){
			// // If this is the end of a free space.
			// if(numberExcludingElements == 0 ){
			// // check if neighbor fits in the space left empty
			// if(currentNeighbor.getWeight() <= (freeFrom - from)){
			// // It fits ! But, does it BEST fits ?
			// if(((float)currentNeighbor.getWeight()/(float)(freeFrom - from))
			// > occupation){
			// // It does best fit !(yet)
			// occupation = ((float)currentNeighbor.getWeight()/(float)(freeFrom
			// - from));
			// offset = freeFrom;
			// }
			// }
			// }
			// numberExcludingElements++;
			// from = iterFrom.next();
			// }
			//
			// }
			//
			// }

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
	protected int weight(HashSet<MemoryExclusionGraphNode> set) {
		int result = 0;

		for (MemoryExclusionGraphNode vertex : set) {
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
			ArrayList<HashSet<MemoryExclusionGraphNode>> elementList) {
		// Define a comparator of list elements. The weight of an element is
		// used for comparison
		Comparator<HashSet<MemoryExclusionGraphNode>> comparator = new Comparator<HashSet<MemoryExclusionGraphNode>>() {
			public int compare(HashSet<MemoryExclusionGraphNode> o1,
					HashSet<MemoryExclusionGraphNode> o2) {
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
			ArrayList<HashSet<MemoryExclusionGraphNode>> elementList,
			boolean isOrdered) {
		int result = 0;

		// If the list has been ordered before, just return the weight of the
		// first element
		if (isOrdered && !elementList.isEmpty())
			return weight(elementList.get(0));
		// Else, search the list
		for (HashSet<MemoryExclusionGraphNode> element : elementList) {
			int temp = weight(element);
			if (temp > result)
				result = temp;
		}
		return result;
	}
}
