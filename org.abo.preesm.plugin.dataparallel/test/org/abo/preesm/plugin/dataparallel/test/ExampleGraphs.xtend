package org.abo.preesm.plugin.dataparallel.test

import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.SDFVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType
import org.abo.preesm.plugin.dataparallel.test.ExampleGraphs.SDFBuilder
import org.junit.Assert

/**
 * Construct example graphs. 
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
		
		public def SDFBuilder addEdge(String sourceName, String sourceOutName, String targetName, String targetInName, int prod, int cons, int delay) {
			val SDFAbstractVertex source = if(outputGraph.vertexSet.exists[node | node.name == sourceName]) 
										   		outputGraph.vertexSet.filter[node | node.name == sourceName].get(0) 
										   else {
												val src = new SDFVertex() => [name = sourceName]
												outputGraph.addVertex(src)
												src
											}

			val SDFAbstractVertex target = if(outputGraph.vertexSet.exists[node | node.name == targetName]) 
												outputGraph.vertexSet.filter[node | node.name == targetName].get(0) 
						 					else {
						 						val dst = new SDFVertex() => [name = targetName]
						 						outputGraph.addVertex(dst)
						 						dst
						 					}
						 	
			val outPort = new SDFSourceInterfaceVertex
			outPort.setName(sourceOutName)
			source.addSource(outPort)
			
			val inPort = new SDFSinkInterfaceVertex
			inPort.setName(targetInName)
			target.addSink(inPort)
			
			outputGraph.addEdge(source, outPort, target, inPort, new SDFIntEdgePropertyType(prod), new SDFIntEdgePropertyType(cons), new SDFIntEdgePropertyType(delay))				
			return this
		}
		
		public def SDFBuilder addEdge(String sourceName, String targetName, int prod, int cons, int delay) {
			return addEdge(sourceName, "output", targetName, "input", prod, cons, delay)
		}
		
		public def SDFGraph getOutputGraph() {
			return outputGraph
		}
	}
	
	/**
	 * Perform schedulability test on the graphs created
	 */
	@org.junit.Test
	public def void sdfIsSchedulable() {
		Util.provideAllGraphs.forEach[sdf |
			Assert.assertTrue(sdf.schedulable) 
		]
	}
	
	/**
	 * Create a network with an acylic and cyclic graph The graph is schedulable but not data-parallel The acyclic graph has 4 actors with following config Z(3)
	 * -(7)-> (6)C(2) --> (3)D(3) --> (2)E The cyclic graph has 3 actors with following config (3)A(2) -(4)-> (3)B(3) --> (2)C(1) C(1) -(1)-> (1)C The actor C is
	 * shared
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
	 * graph has 3 actors with following config (3)A(2) -(4)-> (3)B(3) --> (2)C(1) The actor C is shared
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
	 * Create strictly cyclic SDF containing 4 actors. None of the actor
	 * has enough tokens that all the instances can fire
	 * A(2) -(2)-> (3)B(3) -(3)-> (2)C(2) -(1)-> (2)D(2) -(2)-> (2)A
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
	 * A(2) -> (3)B(3) -(6)-> (2)C(2) -(4)-> (3)D(3) -> (2)A
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
	 * A(3) --> (5) B (5) -(7)-> (3)A 
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
	 * Create hierarchical graph containing two actors P where P contains A(3) -(7)-> (5)B
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
     * Create Acyclic graph containing two actors A (3) -(6)-> (5) B
     * 
     * @return sdf graph
     */
	public static def SDFGraph acyclicTwoActors() {
		return new SDFBuilder().addEdge("a", "b", 3, 5, 6).outputGraph
	}
}