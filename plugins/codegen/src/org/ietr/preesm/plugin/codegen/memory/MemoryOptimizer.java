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
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.parameters.InvalidExpressionException;

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
		
				
		/*

		SimpleGraph<MemoryExclusionGraphNode, DefaultEdge> meminc = memex
				.GetComplementary();
		logger.log(Level.INFO, "Memory Inclusion Graph built");

		mclique = new OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>(
				meminc);

		logger.log(Level.INFO,
				"Maximum-Weight Stable Set: Starting Pre-Ordering");
		mclique.OrderVertexSet();

		logger.log(Level.INFO, "Maximum-Weight Stable Set: Starting Search");
		mclique.Wnew();

		logger.log(Level.INFO,
				"Maximum-Weight Stable is: " + mclique.GetHeaviestClique());
		logger.log(
				Level.INFO,
				"With weight :"
						+ mclique.sumWeight(mclique.GetHeaviestClique()));
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
		
		HybridSolver<MemoryExclusionGraphNode, DefaultEdge> hybrSolver;
		hybrSolver = new HybridSolver<MemoryExclusionGraphNode, DefaultEdge>(memex);
		
		
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
