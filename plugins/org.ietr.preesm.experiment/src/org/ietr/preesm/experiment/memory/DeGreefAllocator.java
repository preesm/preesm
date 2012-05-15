package org.ietr.preesm.experiment.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

import net.sf.dftools.workflow.WorkflowException;

import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;

/**
 * In this class, an adapted version of the placement algorithm presented in <a
 * href=
 * "http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.47.3832&rep=rep1&type=pdf"
 * >this paper</a> is implemented.
 * 
 * @author kdesnos
 * 
 */
public class DeGreefAllocator extends MemoryAllocator {

	/**
	 * Constructor of the allocator
	 * 
	 * @param graph
	 *            the graph whose edges are to allocate
	 */
	public DeGreefAllocator(DirectedAcyclicGraph graph) {
		super(graph);
	}

	/**
	 * Constructor of the allocator
	 * 
	 * @param memEx
	 *            The exclusion graph whose vertices are to allocate
	 */
	public DeGreefAllocator(MemoryExclusionGraph memEx) {
		super(memEx);
	}

	@Override
	public void allocate() {
		// Build the MemoryExclusionGraph if necessary
		if (inputExclusionGraph == null) {
			inputExclusionGraph = new MemoryExclusionGraph();
			try {
				inputExclusionGraph.buildGraph(graph);
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WorkflowException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ArrayList<IntegerAndVertex> nonAllocatedVertices;
		nonAllocatedVertices = new ArrayList<IntegerAndVertex>();
		for (MemoryExclusionVertex vertex : inputExclusionGraph.vertexSet()) {
			nonAllocatedVertices.add(new IntegerAndVertex(0, vertex));
		}

		Collections.sort(nonAllocatedVertices);

		while (!nonAllocatedVertices.isEmpty()) {
			IntegerAndVertex currentPair = nonAllocatedVertices.remove(0);
			int offset = currentPair.getFirst();
			MemoryExclusionVertex vertex = currentPair.getSecond();

			@SuppressWarnings("unchecked")
			HashSet<MemoryExclusionVertex> neighbors = (HashSet<MemoryExclusionVertex>) inputExclusionGraph
					.getAdjacentVertexOf(vertex).clone();

			neighbors.retainAll(memExNodeAllocation.keySet());

			int newOffset = offset; // if the current offset is not posible,
									// this will store the new offset.
			boolean validOffset = false;

			// Iterate until a valid offset is found (this offset will be the
			// smallest possible)
			while (!validOffset) {
				validOffset = true;
				for (MemoryExclusionVertex neighbor : neighbors) {
					int neighborOffset = memExNodeAllocation.get(neighbor);
					if (neighborOffset < (newOffset + vertex.getWeight())
							&& (neighborOffset + neighbor.getWeight()) > newOffset) {
						validOffset = false;
						newOffset += neighbor.getWeight();
						break;
					}
				}
			}
			// If the offset was not valid
			if(newOffset != offset){
				nonAllocatedVertices.add(new IntegerAndVertex(newOffset,vertex));
				Collections.sort(nonAllocatedVertices);
			} else{
				memExNodeAllocation.put(vertex, offset);
				allocation.put(vertex.getEdge(), offset);
			}
		}
	}

	protected class IntegerAndVertex implements Comparable<IntegerAndVertex> {
		private Integer first;
		private MemoryExclusionVertex second;

		public IntegerAndVertex(Integer first, MemoryExclusionVertex second) {
			super();
			this.first = first;
			this.second = second;
		}

		public int hashCode() {
			int hashFirst = first != null ? first.hashCode() : 0;
			int hashSecond = second != null ? second.hashCode() : 0;

			return (hashFirst + hashSecond) * hashSecond + hashFirst;
		}

		public boolean equals(Object other) {
			if (other instanceof IntegerAndVertex) {
				IntegerAndVertex otherPair = (IntegerAndVertex) other;
				return ((this.first == otherPair.first || (this.first != null
						&& otherPair.first != null && this.first
							.equals(otherPair.first))) && (this.second == otherPair.second || (this.second != null
						&& otherPair.second != null && this.second
							.equals(otherPair.second))));
			}

			return false;
		}

		public String toString() {
			return "(" + first + ", " + second + ")";
		}

		public Integer getFirst() {
			return first;
		}

		public void setFirst(Integer first) {
			this.first = first;
		}

		public MemoryExclusionVertex getSecond() {
			return second;
		}

		public void setSecond(MemoryExclusionVertex second) {
			this.second = second;
		}

		@Override
		public int compareTo(IntegerAndVertex o) {
			// If the offsets are different, use them as a comparison
			if (first.compareTo(o.first) != 0) {
				return first.compareTo(o.first);
			}

			// Else, compare the vertices
			if (second.compareTo(o.second) != 0) {
				return second.compareTo(o.second);
			}

			// If the vertices weight and the offsets are equal, compare the
			// number of exclusion
			int nbExclusion = inputExclusionGraph.edgesOf(second).size();
			int nbExclusionO = inputExclusionGraph.edgesOf(o.second).size();
			return nbExclusion - nbExclusionO;
		}
	}
}
