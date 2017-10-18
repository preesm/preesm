package org.abo.preesm.plugin.dataparallel.test.util

import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.operations.DependencyAnalysisOperations
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.SDFVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType
import org.junit.Assert
import org.junit.Test

/**
 * Manually construct example graphs for testing purposes 
 * 
 * @author Sudeep Kanur
 */
class ExampleGraphs {
	
	/**
	 * Helper SDF graph builder class
	 */
	static class SDFBuilder {
		val SDFGraph outputGraph
		
		new(){
			outputGraph = new SDFGraph()
		}
		
		/**
		 * Create a vertex based on the type passed as a String
		 * 
		 * @param type Type of the vertex as defined in the classes of each special vertices
		 * @return SDF vertex of appropriate type
		 */
		private def SDFAbstractVertex createVertex(String type) {			
			switch(type) {
				case SDFRoundBufferVertex.ROUND_BUFFER: {
					return new SDFRoundBufferVertex
				}
				case SDFBroadcastVertex.BROADCAST: {
					return new SDFBroadcastVertex
				}
				case SDFJoinVertex.JOIN : {
					return new SDFJoinVertex
				}
				case SDFForkVertex.FORK : {
					return new SDFForkVertex
				}
				case SDFVertex.VERTEX: {
					return new SDFVertex
				}
			}
		}
		
		/**
		 * Add an edge given source name, type, interface name and corresponding
		 * values for target with production, consumption and delay.
		 * <p>
		 * Function checks if a vertex with same name exists and creates one if there isn't. It
		 * adds the vertex to graph, creates interfaces and adds the edge.
		 * 
		 * @param sourceName Name of the source
		 * @param sourceType Type of vertex (broadcast, implode/explode, etc.)
		 * @param sourceOutName Output port name for the source
		 * @param targetName Name of the target
		 * @param targetType Type of vertex (broadcast, implode/explode, etc.)
		 * @param targetInName Input port name for the target
		 * @param prod Production rate for this edge
		 * @param cons Consumption rate of this edge
		 * @param delay Delay tokens on this edge
		 * 
		 * @return Builder instance to continue building
		 */
		public def SDFBuilder addEdge(String sourceName, String sourceType, String sourceOutName,
			String targetName, String targetType, String targetInName,
			int prod, int cons, int delay) {
				
			val SDFAbstractVertex source = if(outputGraph.vertexSet.exists[node | node.name == sourceName]) {
				outputGraph.vertexSet.filter[node | node.name == sourceName].get(0)	
			} else {
				val SDFAbstractVertex src = createVertex(sourceType)
				src.name = sourceName
				outputGraph.addVertex(src)
				src
			}
			val SDFAbstractVertex target = if(outputGraph.vertexSet.exists[node | node.name == targetName]) {
												outputGraph.vertexSet.filter[node | node.name == targetName].get(0)
				} else {
					val SDFAbstractVertex dst = createVertex(targetType)
					dst.name = targetName
					outputGraph.addVertex(dst)
					dst
				}
												
			val outPort = new SDFSinkInterfaceVertex
			outPort.setName(sourceOutName)
			source.addSink(outPort)
			
			val inPort = new SDFSourceInterfaceVertex
			inPort.setName(targetInName)
			target.addSource(inPort)
			
			outputGraph.addEdge(source, outPort, target, inPort, 
				new SDFIntEdgePropertyType(prod), 
				new SDFIntEdgePropertyType(cons), 
				new SDFIntEdgePropertyType(delay)
			)				
			return this
		}
		
		/**
		 * Add an edge given source name, type and corresponding
		 * values for target with production, consumption and delay. Assumes type of the vertex
		 * to be {@link SDFVertex} type.
		 * <p>
		 * Function checks if a vertex with same name exists and creates one if there isn't. It
		 * adds the vertex to graph, creates interfaces and adds the edge.
		 * 
		 * @param sourceName Name of the source
		 * @param sourceOutName Output port name for the source
		 * @param targetName Name of the target
		 * @param targetOutName Output port name for the target
		 * @param prod Production rate for this edge
		 * @param cons Consumption rate of this edge
		 * @param delay Delay tokens on this edge
		 * 
		 * @return Builder instance to continue building
		 */
		public def SDFBuilder addEdge(String sourceName, String sourceOutName, 
			String targetName, String targetInName, int prod, int cons, int delay) {
			return addEdge(sourceName, SDFVertex.VERTEX, sourceOutName,
						   targetName, SDFVertex.VERTEX, targetInName,
						   prod, cons, delay)
		}
		
		/**
		 * Add an edge given source name, type and corresponding
		 * values for target with production, consumption and delay. Assigns input and output port
		 * names as <code>input</code> and <code>output</code> respectively. Also assumes 
		 * <p>
		 * Function checks if a vertex with same name exists and creates one if there isn't. It
		 * adds the vertex to graph, creates interfaces and adds the edge.
		 * 
		 * @param sourceName Name of the source
		 * @param targetName Name of the target
		 * @param prod Production rate for this edge
		 * @param cons Consumption rate of this edge
		 * @param delay Delay tokens on this edge
		 * 
		 * @return Builder instance to continue building
		 */
		public def SDFBuilder addEdge(String sourceName, String targetName, 
			int prod, int cons, int delay) {
			return addEdge(sourceName, "output", targetName, "input", prod, cons, delay)
		}
		
		/**
		 * Return the built {@link SDFGraph}
		 * 
		 * @return The SDFGraph built
		 */
		public def SDFGraph getOutputGraph() {
			return outputGraph
		}
	}
	
	/**
	 * Perform schedulability test on the graphs created
	 */
	@Test
	public def void sdfIsSchedulable() {
		Util.provideAllGraphs.forEach[sdf |
			Assert.assertTrue(sdf.isSchedulable) 
		]
	}
	
	/**
	 * Check that the graphs can be cleaned
	 */
	@Test
	public def void sdfCanBeCleaned() {
		Util.provideAllGraphs.forEach[sdf |
			// Ideally this does not throw any exception
			sdf.clean()
		]		
	}
	
	/**
	 * Check if the graphs are dag independent. This is done
	 * manually to use the information as a reference in future
	 * tests
	 */
	@Test
	public def void dagInd() {
		val parameterArray = #[
			//#[sdf, ]
			#[acyclicTwoActors, Boolean.TRUE],
			#[twoActorSelfLoop, Boolean.FALSE],
			#[twoActorLoop, Boolean.FALSE],
			#[semanticallyAcyclicCycle, Boolean.TRUE],
			#[strictlyCyclic, Boolean.TRUE],
			#[strictlyCyclicDual, Boolean.TRUE],
			#[strictlyCyclic2, Boolean.TRUE],
			#[mixedNetwork1, Boolean.TRUE],
			#[mixedNetwork2, Boolean.FALSE],
			#[nestedStrongGraph, Boolean.TRUE],
			#[costStrongComponent, Boolean.FALSE]
		]
		
		parameterArray.forEach[row |
			val sdf = row.get(0) as SDFGraph
			val expected = row.get(1)
			
			val dagGen = new SDF2DAG(sdf) 
			val depOp = new DependencyAnalysisOperations
			dagGen.accept(depOp)
			
			Assert.assertEquals(depOp.isIndependent, expected)
		]
	}
	
	/**
	 * Strongly connected component of stereo vision application that gives error with SDF2DAG
	 * creation.
	 * Don't add this to Util as calculating branch sets using traditional method (as mentioned in
	 * the paper) will take a long long time to complete.
	 */
	 public static def SDFGraph costStrongComponent() {
		val height = 380
		val width = 434
		val size = height * width
		val rep = 19
		val sdf = new SDFBuilder()
				.addEdge("In", "out", "CostConstruction", "out", rep*size, size, 0)
				.addEdge("Broadcast5", SDFBroadcastVertex.BROADCAST, "back_0_0", 
						 "CostConstruction", SDFVertex.VERTEX, "back", 1, 1, 8)
				.addEdge("CostConstruction", "disparityError", "AggregateCost", "disparityError", 
					size, size, 0)
				.addEdge("AggregateCost", "aggregatedDisparity", "disparitySelect", "aggregatedDisparity", 
					size, size, 0)
				.addEdge("Broadcast5", SDFBroadcastVertex.BROADCAST, "out1_0_0", 
						 "disparitySelect", SDFVertex.VERTEX, "currentResult", 
						 size, size, size)
				.addEdge("disparitySelect", "backBestCost", "disparitySelect", "bestCostFeed", 
					(height*width+1), (height*width+1), size+1)
				.addEdge("disparitySelect", SDFVertex.VERTEX, "result", 
						 "Broadcast5", SDFBroadcastVertex.BROADCAST, "in", 
						 size, size, 0)
				.addEdge("Broadcast5", SDFBroadcastVertex.BROADCAST, "out0_0_0",
						 "Out", SDFVertex.VERTEX, "in", 
						 size, rep * size, 0)
				.outputGraph
		return sdf
	}
	
	/**
	 * Create a strongly connected graph. The graph is instance-independent, but not data-parallel.
	 * <p>
	 * Z (1) -(1)-> (1) A (2) -(3)-> (3) B (3) --> (2) C (1) --> (1) D
	 * <p>
	 * B (3) -(6)-> (2) A
	 * <p>
	 * C (1) -(3)-> (1) A
	 * 
	 * @return SDF graph 
	 */
	public static def SDFGraph nestedStrongGraph() {
		return new SDFBuilder()
				.addEdge("z", "a2", 1, 1, 1)
				.addEdge("a2", "b2", 2, 3, 3)
				.addEdge("b2", "c2", 3, 2, 0)
				.addEdge("c2", "d2", 1, 1, 0)
				.addEdge("b2", "outputA", "a2", "inputB", 3, 2, 6)
				.addEdge("c2", "outputA", "a2", "inputC", 1, 1, 3)
				.outputGraph
	}
	
	/**
	 * Create a network with an acylic and cyclic graph The graph is schedulable but not data-parallel 
	 * The acyclic graph has 4 actors with following config:
	 * <p>
	 * Z(3)-(7)-> (6)C(2) --> (3)D(3) --> (2)E 
	 * <p>
	 * The cyclic graph has 3 actors with following config:
	 * <p>
	 * (3)A(2) -(4)-> (3)B(3) --> (2)C(1) C(1) -(1)-> (1)C
	 * <p> 
	 * The actor C is shared
	 * 
	 * @return SDF graph
	 */
	public static def SDFGraph mixedNetwork2() {
		return new SDFBuilder()
				.addEdge("a", "b", 2, 3, 4)
				.addEdge("b", "c", 3, 2, 0)
				.addEdge("c", "a", 1, 1, 0)
				.addEdge("c", "outputC", "c", "inputC", 1, 1, 1)
				.addEdge("z", "output", "c", "inputZ", 3, 6, 7)
				.addEdge("c", "outputD", "d", "input", 2, 3, 0)
				.addEdge("d", "e", 3, 2, 0)
				.outputGraph	
	}
	
	/**
	 * Create a network with an acylic and cyclic graph The acyclic graph has 4 actors with following config Z(3) -(7)-> (6)C(2) --> (3)D(3) --> (2)E The cyclic
	 * graph has 3 actors with following config 
	 * <p>
	 * (3)A(2) -(4)-> (3)B(3) --> (2)C(1) 
	 * <p>
	 * The actor C is shared
	 * 
	 * @return SDF graph
	 */
	public static def SDFGraph mixedNetwork1() {
		return new SDFBuilder()
				.addEdge("a", "b", 2, 3, 4)
				.addEdge("b", "c", 3, 2, 3)
				.addEdge("c", "a", 1, 1, 0)
				.addEdge("z", "output", "c", "inputZ", 3, 6, 7)
				.addEdge("c", "outputD", "d", "input", 2, 3, 0)
				.addEdge("d", "e", 3, 2, 0)
				.outputGraph
	}
	
	/**
	 * Create two strictly cyclic SDFG, each containing 4 actors. None of the
	 * actors have enough tokens that all the instance can fire. Further, each
	 * cycle is connected through two actors
	 * 
	 * @return SDF Graph
	 */
	public static def SDFGraph strictlyCyclic2() {
		return new SDFBuilder()
					.addEdge("a0", "b0", 2, 3, 2) // Start cycle 1
					.addEdge("b0", "c0", 3, 2, 3)
					.addEdge("c0", "d0", 2, 3, 1)
					.addEdge("d0", "a0", 3, 2, 3) // End cycle 1
					.addEdge("c0", "outputE", "e", "input", 2, 2, 0) // Intermediate node to cycle 1
					.addEdge("e", "f", 2, 3, 6) // Intermediate node to intermediate node
					.addEdge("a1", "b1", 2, 3, 2) // Start cycle 2
					.addEdge("b1", "c1", 3, 2, 3)
					.addEdge("c1", "d1", 2, 3, 1)
					.addEdge("d1", "a1", 3, 2, 3) // End cycle 2
					.addEdge("f", "output", "d1", "inputF", 3, 3, 3) // Intermediate node to cycle 2
					.outputGraph
	}
	
	/**
	 * Create another configuration of strictly cyclic SDF containing 4 actors. None
	 * of the actor has enough tokens that all the instances can fire
	 * This is just the dual of the {@link ExampleGraphs#strictlyCyclic} graph
	 * <p>
	 * A(2) -(4)-> (3)B(3) -(1)-> (2)C(2) -(4)-> (2)D(2) -(1)-> (2)A
	 * 
	 * @return SDF Graph
	 */
	public static def SDFGraph strictlyCyclicDual() {
		return new SDFBuilder()
				.addEdge("a", "b", 2, 3, 4)
				.addEdge("b", "c", 3, 2, 1)
				.addEdge("c", "d", 2, 3, 4)
				.addEdge("d", "a", 3, 2, 1)
				.outputGraph
	}
	
	/**
	 * Create strictly cyclic SDF containing 4 actors. None of the actor
	 * has enough tokens that all the instances can fire
	 * <p>
	 * A(2) -(2)-> (3)B(3) -(3)-> (2)C(2) -(1)-> (2)D(2) -(2)-> (2)A
	 * 
	 * @return SDF Graph
	 */
	public static def SDFGraph strictlyCyclic() {
		return new SDFBuilder()
				.addEdge("a", "b", 2, 3, 2)
				.addEdge("b", "c", 3, 2, 3)
				.addEdge("c", "d", 2, 3, 1)
				.addEdge("d", "a", 3, 2, 3)
				.outputGraph
	}
	
	/**
	 * Create strictly cyclic SDF containing 4 actors. The DAG behaves as though
	 * it has no cycles
	 * <p>
	 * A(2) -> (3)B(3) -(6)-> (2)C(2) -(4)-> (3)D(3) -> (2)A
	 * 
	 * @return SDF Graph
	 */
	public static def SDFGraph semanticallyAcyclicCycle() {
		return new SDFBuilder()
				.addEdge("a", "b", 2, 3, 0)
				.addEdge("b", "c", 3, 2, 6)
				.addEdge("c", "d", 2, 3, 4) 
				.addEdge("d", "a", 3, 2, 0)
				.outputGraph
	}
	
	/**
	 * Create cyclic graph with two actors forming a loop
	 * <p>
	 * A(3) --> (5) B (5) -(7)-> (3)A 
	 * 
	 * @return SDF Graph
	 */
	public static def SDFGraph twoActorLoop() {
		return new SDFBuilder()
				.addEdge("a", "b", 3, 5, 0)
				.addEdge("b", "a", 5, 3, 7)
				.outputGraph
	}
	
	/**
	 * Create acyclic graph with two actors, one of which has a self loop
	 * 
	 * @return SDF Graph 
	 */
	public static def SDFGraph twoActorSelfLoop() {
		return new SDFBuilder()
				.addEdge("a", "b", 3, 5, 7)
				.addEdge("a", "outputA", "a", "inputA", 1, 1, 1)
				.outputGraph
	}
	
    /**
	 * Create hierarchical graph containing two actors P where P contains 
	 * <p>
	 * A(3) -(7)-> (5)B
	 * 
	 * @return SDF graph
	 */
	public static def SDFGraph acyclicHierarchicalTwoActors() {
    	val sdf = new SDFGraph();

    	val p = new SDFVertex();
    	p.setName("p");
    	p.setGraphDescription(acyclicTwoActors());
    	sdf.addVertex(p);

	    return sdf;
	}
	
    /**
     * Create Acyclic graph containing two actors:
     * <p>
     * A (3) -(6)-> (5) B
     * 
     * @return sdf graph
     */
	public static def SDFGraph acyclicTwoActors() {
		return new SDFBuilder().addEdge("a", "b", 3, 5, 6).outputGraph
	}
}