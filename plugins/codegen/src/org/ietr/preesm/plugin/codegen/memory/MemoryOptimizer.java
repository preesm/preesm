/**
 * 
 */
package org.ietr.preesm.plugin.codegen.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jgrapht.graph.DefaultEdge;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;

/**
 * Testing methods to optimize DAG memory use
 * 
 * @author kdesnos
 */
public class MemoryOptimizer extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName) throws WorkflowException {

		// Copy the input dag to the output
		Map<String, Object> outputs = new HashMap<String, Object>();
		DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");
		outputs.put("DAG", dag);

		// Make a copy of the Input DAG for treatment
		DirectedAcyclicGraph localDAG = (DirectedAcyclicGraph) dag.clone();
		
//		// Test prog for MemoryAllocators 
//		localDAG = new DirectedAcyclicGraph();
//		DAGDefaultVertexPropertyType prop = new DAGDefaultVertexPropertyType(1);
//		
//		DAGVertex v1 = new DAGVertex("A",prop,prop);
//		v1.setName("A");
//		v1.getPropertyBean().setValue("vertexType", "task");
//		
//		
//		DAGVertex v2 = new DAGVertex("B",prop,prop);
//		v2.setName("B");
//		v2.getPropertyBean().setValue("vertexType", "task");
//		
//		DAGVertex v3 = new DAGVertex("C",prop,prop);
//		v3.setName("C");
//		v3.getPropertyBean().setValue("vertexType", "task");
//		
//		DAGVertex v4 = new DAGVertex("D",prop,prop);
//		v4.setName("D");
//		v4.getPropertyBean().setValue("vertexType", "task");
//		
//		DAGVertex v5 = new DAGVertex("E",prop,prop);
//		v5.setName("E");
//		v5.getPropertyBean().setValue("vertexType", "task");
//		
//		DAGVertex v6 = new DAGVertex("F",prop,prop);
//		v6.setName("F");
//		v6.getPropertyBean().setValue("vertexType", "task");
//		
//		localDAG.addVertex(v1);
//		localDAG.addVertex(v2);
//		localDAG.addVertex(v3);
//		localDAG.addVertex(v4);
//		localDAG.addVertex(v5);
//		localDAG.addVertex(v6);
//		
//		
//		(localDAG.addEdge(v1, v2)).setWeight(new DAGDefaultEdgePropertyType(200));
//		(localDAG.addEdge(v2, v3)).setWeight(new DAGDefaultEdgePropertyType(50));
//		(localDAG.addEdge(v1, v4)).setWeight(new DAGDefaultEdgePropertyType(50));
//		(localDAG.addEdge(v4, v3)).setWeight(new DAGDefaultEdgePropertyType(50));
//		(localDAG.addEdge(v3, v5)).setWeight(new DAGDefaultEdgePropertyType(150));
//		(localDAG.addEdge(v3, v6)).setWeight(new DAGDefaultEdgePropertyType(100));

		// Rem: Logger is used to display messages in the console
		Logger logger = WorkflowLogger.getLogger();
		logger.log(Level.INFO, "Memory exclusion graph : start building");

		
		MemoryExclusionGraph memex = new MemoryExclusionGraph();
		try {
			memex.buildGraph(localDAG);
		} catch (InvalidExpressionException e) {
			throw new WorkflowException(e.getLocalizedMessage());
		}
		logger.log(Level.INFO, "Memory exclusion graph : graph built");
		
		
		logger.log(Level.INFO, "There are " + memex.vertexSet().size()
				+ " memory transfers.");
		logger.log(Level.INFO, "There are " + memex.edgeSet().size()
				+ " exclusions.");

		double maxEdges = memex.vertexSet().size()
				* (memex.vertexSet().size() - 1) / 2.0;

		logger.log(Level.INFO, "The edge density of the graph is "
				+ memex.edgeSet().size() / maxEdges + ".");
		
		BasicAllocator baAlloc = new BasicAllocator(localDAG);
		
		baAlloc.allocate();
		
		logger.log(Level.INFO, "Maximum Memory Size is : "+baAlloc.getMemorySize());
		
		logger.log(Level.INFO, "Custom Alloc Start");
		CustomAllocator cuAlloc = new CustomAllocator(memex);
		
		cuAlloc.allocate();
		
		logger.log(Level.INFO, "Allocated Memory Size is : "+cuAlloc.getMemorySize());
		logger.log(Level.INFO, ""+((1.0 - (double)cuAlloc.getMemorySize()/(double)baAlloc.getMemorySize())*100.0) + "% was saved over worst allocation possible.");
		
		HybridSolver<MemoryExclusionGraphNode, DefaultEdge> hybrSolver;
		hybrSolver = new HybridSolver<MemoryExclusionGraphNode, DefaultEdge>(memex);
		hybrSolver.solve();
		
		logger.log(Level.INFO, ""+ ((double)cuAlloc.getMemorySize()/(double)hybrSolver.sumWeight(hybrSolver.getHeaviestClique())) + "x the minimum limit.");
		
		logger.log(Level.INFO, "Allocation is  : "+ cuAlloc.memExNodeAllocation);
		
		// Those two line will probably corrupt the allocation
		//DAGEdge edge  = cuAlloc.getAllocation().keySet().iterator().next();
		//cuAlloc.getAllocation().put(edge, cuAlloc.getAllocation().get(edge)+1);
		
		logger.log(Level.INFO,"Allocation check : " + ((cuAlloc.checkAllocation().isEmpty())?"OK":"Problem"));	
				
				
		/*

		SimpleGraph<MemoryExclusionGraphNode, DefaultEdge> meminc = memex
				.getComplementary();
		logger.log(Level.INFO, "Memory Inclusion Graph built");

		OstergardSolver<MemoryExclusionGraphNode, DefaultEdge> mclique = new OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>(
				meminc);

		logger.log(Level.INFO, "Maximum-Weight Stable Set: Starting Search");
		mclique.solve();

		logger.log(Level.INFO,
				"Maximum-Weight Stable is: " + mclique.getHeaviestClique());
		logger.log(
				Level.INFO,
				"With weight :"
						+ mclique.sumWeight(mclique.getHeaviestClique()));
						*/
						
		/*
		// Test prog for MaxCliqueProbSolv 
		memex = new MemoryExclusionGraph();
		MemoryExclusionGraphNode n1 = new MemoryExclusionGraphNode("A", "A", 1);
		MemoryExclusionGraphNode n2 = new MemoryExclusionGraphNode("B", "B", 2);
		MemoryExclusionGraphNode n3 = new MemoryExclusionGraphNode("C", "C", 2);
		MemoryExclusionGraphNode n4 = new MemoryExclusionGraphNode("D", "D", 4);
		MemoryExclusionGraphNode n5 = new MemoryExclusionGraphNode("E", "E", 7);

		memex.addVertex(n1); 
		memex.addVertex(n2); 
		memex.addVertex(n3);
		memex.addVertex(n4);
		memex.addVertex(n5);

		memex.addEdge(n1, n2);
		memex.addEdge(n1, n3);
		memex.addEdge(n1, n4);
		memex.addEdge(n1, n5);
		memex.addEdge(n2, n3);
		memex.addEdge(n3, n4);
		
		*/

				
		/*
		// Test program for HybridSolver
		HybridSolver<MemoryExclusionGraphNode, DefaultEdge> mcliqueHybr;
		mcliqueHybr = new HybridSolver<MemoryExclusionGraphNode, DefaultEdge>(memex);
		
		logger.log(Level.INFO, "Hybrid Start");
		mcliqueHybr.solve();
		logger.log(Level.INFO, "Hybrid Stop");
		
		logger.log(Level.INFO, "Hybrid" + mcliqueHybr.getHeaviestClique());
		logger.log(Level.INFO, "Hybrid" + mcliqueHybr.max);
		*/
		
		/*
		long tStart;
		long tFinish;
		
		WeightedGraphGenerator gGen = new WeightedGraphGenerator();
		
		//for(int aa=0; aa < 250 ; aa+=10){
		
		gGen.setNumberVertices(100);
		gGen.setEdgeDensity(0.9);
		gGen.setMinimumWeight(1001);
		gGen.setMaximumWeight(1010);
		
		
		//for(int ii = 0; ii< 10 ; ii++){
		//	logger.log(Level.INFO, "NODE : " + aa + " ITER  : " + ii);
		//memex = gGen.generateMemoryExclusionGraph();
		
		memex.saveToFile("C:/TEMP/graph.csv");


		OstergardSolver<MemoryExclusionGraphNode, DefaultEdge> ostSolver;
		ostSolver = new OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>(	memex);
		
		YamaguchiSolver<MemoryExclusionGraphNode, DefaultEdge> yamaSolver;
		yamaSolver = new YamaguchiSolver<MemoryExclusionGraphNode, DefaultEdge>(memex);
		
		//HybridSolver<MemoryExclusionGraphNode, DefaultEdge> hybrSolver;
		hybrSolver = new HybridSolver<MemoryExclusionGraphNode, DefaultEdge>(memex);
		
		/*
		logger.log(Level.INFO, "Yama Start");
		tStart = System.currentTimeMillis();
		yamaSolver.solve();
		tFinish = System.currentTimeMillis();
		
		logger.log(Level.INFO, "Yama [" + (tFinish - tStart) + "]");
		logger.log(Level.INFO, "Yama " + yamaSolver.sumWeight(yamaSolver.getHeaviestClique()));
		
		
		logger.log(Level.INFO, "Ost Start");
		tStart = System.currentTimeMillis();
		ostSolver.solve();
		tFinish = System.currentTimeMillis();
		
		logger.log(Level.INFO, "Ost [" + (tFinish - tStart) + "]");
		logger.log(Level.INFO, "Ost " + ostSolver.sumWeight(ostSolver.getHeaviestClique()));	
		
		
		
		logger.log(Level.INFO, "Hybrr Start");
		tStart = System.currentTimeMillis();
		//if(aa<70)
			hybrSolver.solve();
		tFinish = System.currentTimeMillis();
		
		logger.log(Level.INFO, "Hybr [" + (tFinish - tStart) + "]");
		logger.log(Level.INFO, "Hybr " + hybrSolver.sumWeight(hybrSolver.getHeaviestClique()));
		//}
		//}
		 
		 */
		
		
		
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Optimizing memory.";
	}
}
